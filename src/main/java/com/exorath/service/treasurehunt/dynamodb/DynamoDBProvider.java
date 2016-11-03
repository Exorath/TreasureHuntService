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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamoDBProvider {
	private static final Logger logger = LoggerFactory.getLogger(DynamoDBProvider.class);

	private final DynamoDB db;

	public DynamoDBProvider(Regions region) {
		AWSCredentials credentials = new EnvironmentVariableCredentialsProvider().getCredentials();
		AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(region);
		db = new DynamoDB(client);
	}

	public DynamoDB getDB() {
		return db;
	}

	public Table getTable(String name) {
		return getDB().getTable(name);
	}

	public boolean hasTable(String name) {
		for (Table table : db.listTables()) {
			if (table.getTableName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public Table createTable(CreateTableRequest request) throws InterruptedException {
		Table table;
		try {
			table = db.createTable(request);
			String name = request.getTableName();
			long read = request.getProvisionedThroughput().getReadCapacityUnits();
			long write = request.getProvisionedThroughput().getWriteCapacityUnits();
			logger.info("Created DynamoDB table " + name + " with " + read + "r/" + write + "w provisioning. Waiting for it to activate.");
		} catch (ResourceInUseException ex) { // Table already exists
			table = db.getTable(request.getTableName());
			logger.warn("DynamoDB table " + request.getTableName() + " already exists. Waiting for it to activate.");
		}

		table.waitForActive();
		logger.info("DynamoDB table " + request.getTableName() + " is active.");
		return table;
	}
}
