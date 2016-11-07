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

package com.exorath.service.treasurehunt;

import com.exorath.service.treasurehunt.res.GetResult;
import com.exorath.service.treasurehunt.res.PutResult;

import java.util.UUID;

/**
 * The treasure hunt {@code Service} allows for the setting and retrievel of a {@code Player}'s {@code Treasure}s.
 */
public interface Service {

	/**
	 * @param playerId The id of the {@code Player} for which to retrieve all treasures.
	 * @return A {@code Treasure} array containing all treasures the specified {@code Player} has found.
	 */
	GetResult getTreasures(UUID playerId);

	/**
	 * @param playerId   The id of the {@code Player} to which this treasure should be added.
	 * @param treasureId The id of the {@code Treasure} the specified {@code Player} has found.
	 * @return A {@code PutResult} with information regarding the success of the operation.
	 */
	PutResult setTreasure(UUID playerId, String treasureId);
}
