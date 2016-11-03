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

import com.google.gson.Gson;
import spark.Route;

import java.util.UUID;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;

public class Transport {
	private static final String PLAYER_ID = "playerId";
	private static final String TREASURE_ID = "treasureId";
	private static final Gson Gson = new Gson();

	public static void setup(Service srv, PortProvider provider) {
		port(provider.getPort());
		get("/players/:" + PLAYER_ID + "/treasures/", Transport.getGetTreasuresRoute(srv), Gson::toJson);
		put("/players/:" + PLAYER_ID + "/treasures/:" + TREASURE_ID, Transport.getSetTreasureRoute(srv), Gson::toJson);
	}

	public static Route getGetTreasuresRoute(Service srv) {
		return (req, res) -> srv.getTreasures(UUID.fromString(req.params(PLAYER_ID)));
	}

	public static Route getSetTreasureRoute(Service srv) {
		return (req, res) -> srv.setTreasure(UUID.fromString(req.params(PLAYER_ID)), req.params(TREASURE_ID));
	}
}
