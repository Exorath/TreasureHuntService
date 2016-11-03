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

package com.exorath.service.treasurehunt.test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.exorath.service.commons.dynamoDBProvider.DynamoDBProvider;
import com.exorath.service.treasurehunt.Service;
import com.exorath.service.treasurehunt.Treasure;
import com.exorath.service.treasurehunt.dynamodb.DynamoDBService;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class DynamoDBServiceTest {

	@Test
	public void testGetTreasuresEqualsSetTreasures() {
		UUID playerId = UUID.randomUUID();
		Service srv = new DynamoDBService(getDynamoDBProvider());
		srv.setTreasure(playerId, "testTreasure1");
		srv.setTreasure(playerId, "testTreasure2");
		srv.setTreasure(playerId, "testTreasure3");
		srv.setTreasure(playerId, "testTreasure4");

		Treasure[] treasures = srv.getTreasures(playerId);
		Assert.assertEquals(treasures.length, 4);

		boolean testTreasuse1AccountedFor = false;
		boolean testTreasuse2AccountedFor = false;
		boolean testTreasuse3AccountedFor = false;
		boolean testTreasuse4AccountedFor = false;
		for (Treasure t : treasures) {
			if (t.getId().equals("testTreasure1")) {
				testTreasuse1AccountedFor = true;
			} else if (t.getId().equals("testTreasure2")) {
				testTreasuse2AccountedFor = true;
			} else if (t.getId().equals("testTreasure3")) {
				testTreasuse3AccountedFor = true;
			} else if (t.getId().equals("testTreasure4")) {
				testTreasuse4AccountedFor = true;
			}
		}
		Assert.assertTrue(testTreasuse1AccountedFor && testTreasuse2AccountedFor && testTreasuse3AccountedFor && testTreasuse4AccountedFor);
	}

	private DynamoDBProvider getDynamoDBProvider() {
		AmazonDynamoDBClient client = new AmazonDynamoDBClient().withEndpoint("http://localhost:8000");
		DynamoDB db = new DynamoDB(client);
		return () -> db;
	}
}
