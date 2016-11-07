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
 * Wraps the success status of a put request.
 */
public class PutResult {
	@SerializedName("success")
	private boolean success;

	@SerializedName("err")
	private String err;

	/**
	 * Creates a new {@code PutResult} representing a success.
	 */
	public PutResult() {
		this.success = true;
		this.err = null;
	}

	/**
	 * Creates a new {@code PutResult} representing a failure.
	 *
	 * @param err The error associated with the put request this result represents.
	 */
	public PutResult(String err) {
		this.success = false;
		this.err = err;
	}

	/**
	 * @return true if the request was a success, false otherwise.
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @return The error message associated with the put request this result represents. This will only be set if {@code
	 * isSuccess} returns false.
	 */
	public String getError() {
		return err;
	}
}
