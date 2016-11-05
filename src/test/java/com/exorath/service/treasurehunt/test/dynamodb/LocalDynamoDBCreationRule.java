package com.exorath.service.treasurehunt.test.dynamodb;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Creates a local DynamoDB instance for testing.
 * See http://stackoverflow.com/a/37780083/1807588
 */
public class LocalDynamoDBCreationRule extends ExternalResource {

	private DynamoDBProxyServer server;
	private AmazonDynamoDB amazonDynamoDB;

	public LocalDynamoDBCreationRule() {
		// This one should be copied during test-compile time. If project's basedir does not contains a folder
		// named 'native-libs' please try '$ mvn clean install' from command line first
		System.setProperty("sqlite4java.library.path", "native-libs");
	}

	@Override
	protected void before() throws Throwable {
		try {
			final String port = getAvailablePort();
			this.server = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory", "-port", port});
			server.start();
			amazonDynamoDB = new AmazonDynamoDBClient(new BasicAWSCredentials("access", "secret"));
			amazonDynamoDB.setEndpoint("http://localhost:" + port);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void after() {
		if (server != null) {
			try {
				server.stop();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public AmazonDynamoDB getAmazonDynamoDB() {
		return amazonDynamoDB;
	}

	private String getAvailablePort() {
		try (final ServerSocket serverSocket = new ServerSocket(0)) {
			return String.valueOf(serverSocket.getLocalPort());
		} catch (IOException e) {
			throw new RuntimeException("Available port was not found", e);
		}
	}
}
