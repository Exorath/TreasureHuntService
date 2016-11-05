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

package com.exorath.service.treasurehunt.res;

import com.google.gson.annotations.SerializedName;

/**
 * Wraps the success status of an operation.
 */
public class Result {
	@SerializedName("success")
	private boolean success;

	/**
	 * @param success Whether or not this {@code Result} represents a success.
	 */
	public Result(boolean success) {
		this.success = success;
	}

	/**
	 * @return true if the operation was a success, false otherwise.
	 */
	public boolean isSuccess() {
		return success;
	}
}
