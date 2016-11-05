/*
 * Copyright 2016 Exorath
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.exorath.service.treasurehunt.dynamodb;

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
