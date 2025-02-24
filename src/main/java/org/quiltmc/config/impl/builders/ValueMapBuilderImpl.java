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

package org.quiltmc.config.impl.builders;

import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueMap;
import org.quiltmc.config.impl.values.ValueMapImpl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ValueMapBuilderImpl<T> implements ValueMap.Builder<T> {
	private final T defaultValue;
	private final Map<String, T> values = new LinkedHashMap<>();

	public ValueMapBuilderImpl(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public ValueMap.Builder<T> put(String key, T value) {
		this.values.put(key, value);

		return this;
	}

	@Override
	public ValueMap<T> build() {
		return new ValueMapImpl<>(this.defaultValue, this.values);
	}


	public static class TrackedValueMapBuilderImpl<T> implements ValueMap.TrackedBuilder<T> {

		private final T defaultValue;
		private final Map<String, T> values = new LinkedHashMap<>();
		private final Function<ValueMap<T>, TrackedValue<ValueMap<T>>> trackedValueFactory;
		public TrackedValueMapBuilderImpl(T defaultValue, Function<ValueMap<T>, TrackedValue<ValueMap<T>>> trackedValueFactory) {
			this.defaultValue = defaultValue;
			this.trackedValueFactory = trackedValueFactory;
		}

		@Override
		public ValueMap.TrackedBuilder<T> put(String key, T value) {
			this.values.put(key, value);

			return this;
		}

		@Override
		public TrackedValue<ValueMap<T>> build() {
			return trackedValueFactory.apply(new ValueMapImpl<>(this.defaultValue, this.values));
		}

	}
}
