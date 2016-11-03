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

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.exorath.service.commons.dynamoDBProvider.DynamoDBProvider;
import com.exorath.service.treasurehunt.Result;
import com.exorath.service.treasurehunt.Service;
import com.exorath.service.treasurehunt.Treasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DynamoDBService implements Service {
	private static final String TABLE_NAME = "Treasures";
	private static final String TABLE_ID = "playerId";
	private static final Logger logger = LoggerFactory.getLogger(DynamoDBService.class);

	private Table table;

	public DynamoDBService() {
		DynamoDBProvider provider = DynamoDBProvider.getEnvironmentDynamoDBProvider();
		try {
			table = getTable(TABLE_NAME, provider.getDB());
		} catch (InterruptedException ex) {
			logger.error("DynamoDB table " + TABLE_NAME + " could not activate.");
			System.exit(1);
		}
	}

	private Table getTable(String name, DynamoDB db) throws InterruptedException {
		Table table;
		try {
			table = db.createTable(new CreateTableRequest()
					.withTableName(name)
					.withKeySchema(new KeySchemaElement(TABLE_ID, KeyType.HASH))
					.withAttributeDefinitions(new AttributeDefinition(TABLE_ID, ScalarAttributeType.S))
					.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
			);
			logger.info("Created DynamoDB table " + name + " with 1r/1w provisioning. Waiting for it to activate");
			table.waitForActive();
		} catch (ResourceInUseException e) { //table exists, let's make sure it's active
			table = db.getTable(TABLE_NAME);
			table.waitForActive();
		}
		return table;
	}

	@Override
	public Treasure[] getTreasures(UUID playerId) {
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("playerId", playerId);
		Item outcome;
		try {
			outcome = table.getItem(spec);
			logger.info("getTreasures(" + playerId + ") succeeded:\n" + outcome);
		} catch (Exception ex) {
			logger.error("getTreasures(" + playerId + ") failed:\n" + ex.getMessage());
			return new Treasure[0];
		}
		Treasure[] treasures = new Treasure[outcome.numberOfAttributes()];
		int i = 0;
		for (Object o : outcome.asMap().values()) {
			treasures[i++] = new Treasure(o.toString());
		}
		return treasures;
	}

	@Override
	public Result setTreasure(UUID playerId, String treasureId) {
		try {
			PutItemOutcome outcome = table.putItem(new Item()
					.withPrimaryKey("playerId", playerId, "treasureId", treasureId));
			logger.info("setTreasure(" + playerId + ", " + treasureId + ") succeeded:\n" + outcome.getPutItemResult());
		} catch (Exception ex) {
			logger.error("setTreasure(" + playerId + ", " + treasureId + ") failed:\n" + ex.getMessage());
			return new Result(false);
		}
		return new Result(true);
	}
}
