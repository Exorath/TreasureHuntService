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

import com.exorath.service.treasurehunt.res.PutResult;
import org.junit.Assert;
import org.junit.Test;

public class PutResultTest {

	@Test
	public void testIsSuccessEqualsConstructorSuccess() {
		PutResult result = new PutResult();
		Assert.assertTrue(result.isSuccess());

		String error = "This is an error";
		result = new PutResult(error);
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals(result.getError(), error);
	}
}
