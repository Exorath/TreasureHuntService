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
import com.exorath.service.commons.dynamoDBProvider.DynamoDBProvider;
import com.exorath.service.treasurehunt.Service;
import com.exorath.service.treasurehunt.res.GetResult;
import com.exorath.service.treasurehunt.res.Treasure;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.UUID;

public class DynamoDBServiceTest {

	@ClassRule
	public static final LocalDynamoDBCreationRule client = new LocalDynamoDBCreationRule();

	@Test
	public void testGetTreasuresEqualsSetTreasures() {
		UUID playerId = UUID.randomUUID();
		UUID playerId2 = UUID.randomUUID();
		Service srv = new DynamoDBService(getDynamoDBProvider());
		srv.setTreasure(playerId, "testTreasure1");
		srv.setTreasure(playerId2, "testTreasure3");
		srv.setTreasure(playerId, "testTreasure2");
		srv.setTreasure(playerId, "testTreasure2");
		srv.setTreasure(playerId, "testTreasure3");
		srv.setTreasure(playerId, "testTreasure4");
		srv.setTreasure(playerId2, "testTreasure6");

		GetResult result1 = srv.getTreasures(playerId);
		Assert.assertEquals(result1.getCount(), 4);
		Assert.assertEquals(result1.getTreasures().length, 4);

		GetResult result2 = srv.getTreasures(playerId2);
		Assert.assertEquals(result2.getCount(), 2);
		Assert.assertEquals(result2.getTreasures().length, 2);

		boolean testTreasure1AccountedFor = false;
		boolean testTreasure2AccountedFor = false;
		boolean testTreasure3AccountedFor = false;
		boolean testTreasure4AccountedFor = false;
		boolean testTreasure5AccountedFor = false;
		for (Treasure t : result1.getTreasures()) {
			switch (t.getId()) {
				case "testTreasure1":
					testTreasure1AccountedFor = true;
					break;
				case "testTreasure2":
					testTreasure2AccountedFor = true;
					break;
				case "testTreasure3":
					testTreasure3AccountedFor = true;
					break;
				case "testTreasure4":
					testTreasure4AccountedFor = true;
					break;
				case "testTreasure5":
					testTreasure5AccountedFor = true;
					break;
			}
		}
		Assert.assertTrue(testTreasure1AccountedFor &&
				testTreasure2AccountedFor &&
				testTreasure3AccountedFor &&
				testTreasure4AccountedFor &&
				!testTreasure5AccountedFor);

		boolean testTreasure3AccountedFor2 = false;
		boolean testTreasure6AccountedFor2 = false;
		for (Treasure t : result2.getTreasures()) {
			switch (t.getId()) {
				case "testTreasure3":
					testTreasure3AccountedFor2 = true;
					break;
				case "testTreasure6":
					testTreasure6AccountedFor2 = true;
					break;
			}
		}
		Assert.assertTrue(testTreasure3AccountedFor2 &&
				testTreasure6AccountedFor2);
	}

	private DynamoDBProvider getDynamoDBProvider() {
		DynamoDB db = new DynamoDB(client.getAmazonDynamoDB());
		return () -> db;
	}
}
