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

package org.quiltmc.config.api.values;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.config.impl.builders.ValueMapBuilderImpl;
import org.quiltmc.config.impl.util.ConfigUtils;

import java.util.Map;

@ApiStatus.NonExtendable
public interface ValueMap<T> extends Iterable<Map.Entry<String, T>>, Map<String, T>, CompoundConfigValue<T> {
	static <T> Builder<T> builder(T defaultValue) {
		ConfigUtils.assertValueType(defaultValue);

		return new ValueMapBuilderImpl<>(defaultValue);
	}

	@ApiStatus.NonExtendable
	interface Builder<T> {
		Builder<T> put(String key, T value);

		ValueMap<T> build();
	}

	interface TrackedBuilder<T> {
		TrackedBuilder<T> put(String key, T value);

		TrackedValue<ValueMap<T>> build();
	}
}
