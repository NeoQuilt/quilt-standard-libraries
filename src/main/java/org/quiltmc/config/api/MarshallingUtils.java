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

package org.quiltmc.config.api;

import org.quiltmc.config.api.exceptions.ConfigParseException;
import org.quiltmc.config.api.values.ConfigSerializableObject;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Utility class that serializers can use to convert intermediary representations of config values to values that
 * conform to the types of the {@link TrackedValue}s they represent.
 */
public final class MarshallingUtils {
	private MarshallingUtils() { }

	/**
	 * Converts a serialized object into a value object to be stored in a {@link TrackedValue}
	 *
	 * @param object some object to convert
	 * @param to the default value of the object for the given type
	 * @param valueMapCreator a function that converts an arbitrary object into a ValueMap with the given default
	 * @param valueListCreator a function that converts an arbitrary object into a ValueList with the given default
	 * @return some value
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private static <M, L> Object coerce(Object object, Object to, BiFunction<M, ValueMap<?>, ValueMap<?>> valueMapCreator, BiFunction<L, ValueList<?>, ValueList<?>> valueListCreator) {
		if (to instanceof Integer) {
			return ((Number) object).intValue();
		} else if (to instanceof Long) {
			return ((Number) object).longValue();
		} else if (to instanceof Float) {
			return ((Number) object).floatValue();
		} else if (to instanceof Double) {
			return ((Number) object).doubleValue();
		} else if (to instanceof String) {
			return object;
		} else if (to instanceof Boolean) {
			return object;
		} else if (to instanceof ConfigSerializableObject) {
			return ((ConfigSerializableObject) to).convertFrom(coerce(object, ((ConfigSerializableObject<?>) to).getRepresentation(), valueMapCreator, valueListCreator));
		} else if (to instanceof ValueMap) {
			return valueMapCreator.apply((M) object, (ValueMap<?>) to);
		} else if (to instanceof ValueList) {
			return valueListCreator.apply((L) object, (ValueList<?>) to);
		} else if (to.getClass().isEnum()) {
			for (Object o : to.getClass().getEnumConstants()) {
				if (((Enum<?>) o).name().equalsIgnoreCase((String) object)) {
					return o;
				}
			}

			throw new ConfigParseException("Unexpected value '" + object + "' for enum class '" + to.getClass() + "'");
		} else {
			throw new ConfigParseException("Unexpected value type: " + to.getClass());
		}
	}

	public static <M, L> Object coerce(Object object, Object to, ValueMapCreator<M> creator) {
		MapCoercer<M> mapCoercer = new MapCoercer<>(creator);

		return coerce(object, to, mapCoercer, new ListCoercer<>(mapCoercer));
	}

	public interface ValueMapCreator<M> {
		void create(M object, MapEntryConsumer entryConsumer);
	}

	public interface MapEntryConsumer {
		void put(String key, Object value);
	}

	@SuppressWarnings("rawtypes")
	private static final class MapCoercer<M> implements BiFunction<M, ValueMap<?>, ValueMap<?>> {
		private final ValueMapCreator<M> creator;

		private MapCoercer(ValueMapCreator<M> creator) {
			this.creator = creator;
		}

		@Override
		@SuppressWarnings({"unchecked"})
		public ValueMap<?> apply(M object, ValueMap<?> defaultValue) {
			ValueMap.Builder builder = ValueMap.builder(defaultValue.getDefaultValue());

			this.creator.create(object, (key, value) ->
					builder.put(key, coerce(value, defaultValue.getDefaultValue(), this.creator)));

			return builder.build();
		}
	}

	private static final class ListCoercer<M> implements BiFunction<List<?>, ValueList<?>, ValueList<?>> {
		private final BiFunction<M, ValueMap<?>, ValueMap<?>> valueMapCreator;

		private ListCoercer(BiFunction<M, ValueMap<?>, ValueMap<?>> valueMapCreator) {
			this.valueMapCreator = valueMapCreator;
		}

		@Override
		public ValueList<?> apply(List<?> list, ValueList<?> defaultValue) {
			Object[] values = list.toArray();

			for (int i = 0; i < values.length; ++i) {
				values[i] = coerce(values[i], defaultValue.getDefaultValue(), this.valueMapCreator, this);
			}

			return ValueList.create(defaultValue.getDefaultValue(), values);
		}
	}
}
