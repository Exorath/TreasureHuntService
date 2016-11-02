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

package com.exorath.service.treasurehunt.service;

import com.exorath.service.treasurehunt.Result;
import com.exorath.service.treasurehunt.Treasure;

import java.util.UUID;

public class DynamoDBService extends SimpleService {

	@Override
	public Treasure[] getTreasures(UUID playerId) {
		return new Treasure[0];
	}

	@Override
	public Result setTreasure(UUID playerId, String treasureId) {
		return new Result(false);
	}
}
