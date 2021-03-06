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

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.exorath.service.commons.dynamoDBProvider.DynamoDBProvider;
import com.exorath.service.treasurehunt.Service;
import com.exorath.service.treasurehunt.res.GetResult;
import com.exorath.service.treasurehunt.res.PutResult;
import com.exorath.service.treasurehunt.res.Treasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class DynamoDBService implements Service {
	private static final String TABLE_NAME = "Treasures";
	private static final String PRIM_KEY = "playerId";
	private static final String TREASURES_FIELD = "treasures";
	private static final Logger logger = LoggerFactory.getLogger(DynamoDBService.class);

	private Table table;

	public DynamoDBService(DynamoDBProvider provider) {
		try {
			table = provider.getDB().createTable(new CreateTableRequest()
					.withTableName(TABLE_NAME)
					.withKeySchema(new KeySchemaElement(PRIM_KEY, KeyType.HASH))
					.withAttributeDefinitions(new AttributeDefinition(PRIM_KEY, ScalarAttributeType.S))
					.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
			);
			logger.info("Created DynamoDB table " + TABLE_NAME + " with 1r/1w provisioning. Waiting for it to activate.");
		} catch (ResourceInUseException ex) {
			table = provider.getDB().getTable(TABLE_NAME);
			logger.info("DynamoDB table " + TABLE_NAME + " already existed. Waiting for it to activate.");
		}

		try {
			table.waitForActive();
		} catch (InterruptedException ex) {
			logger.error("DynamoDB table " + TABLE_NAME + " could not activate!\n" + ex.getMessage());
			System.exit(1);
		}
		logger.info("DynamoDB table " + TABLE_NAME + " active.");
	}

	@Override
	public GetResult getTreasures(UUID playerId) {
		GetItemSpec spec = new GetItemSpec().withPrimaryKey(PRIM_KEY, playerId.toString());
		Item item = table.getItem(spec);
		logger.info("Retrieved the following treasures for player " + playerId + ": " + item);
		if (item == null || !item.hasAttribute(TREASURES_FIELD)) {
			return new GetResult(new Treasure[0]);
		} else {
			List list = item.getList(TREASURES_FIELD);
			Treasure[] treasures = new Treasure[list.size()];
			int i = 0;
			for (Object treasure : list) {
				treasures[i++] = new Treasure(treasure.toString());
			}
			return new GetResult(treasures);
		}
	}

	@Override
	public PutResult setTreasure(UUID playerId, String treasureId) {
		UpdateItemSpec spec = new UpdateItemSpec()
				.withPrimaryKey(PRIM_KEY, playerId.toString())
				.withAttributeUpdate(new AttributeUpdate(TREASURES_FIELD).addElements(treasureId))
				.withReturnValues(ReturnValue.UPDATED_OLD);
		try {
			UpdateItemOutcome outcome = table.updateItem(spec);
			if (!outcome.getUpdateItemResult().toString().contains(treasureId)) {
				logger.info("Successfully set treasure " + treasureId + " for player " + playerId);
				return new PutResult();
			} else {
				logger.warn("Attempted to set treasure " + treasureId + " for player " + playerId + " a second time");
				return new PutResult("Treasure " + treasureId + " already set for player " + playerId);
			}
		} catch (ConditionalCheckFailedException ex) {
			logger.warn("Updated treasure " + treasureId + " for player " + playerId + " failed\n:" + ex.getMessage());
			return new PutResult(ex.getMessage());
		}
	}
}
