/*
 * Copyright 2022-2023 QuiltMC
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

package org.quiltmc.config.api.exceptions;

public final class ConfigFieldException extends RuntimeException {
	public ConfigFieldException() {
	}

	public ConfigFieldException(String message) {
		super(message);
	}

	public ConfigFieldException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigFieldException(Throwable cause) {
		super(cause);
	}
}
