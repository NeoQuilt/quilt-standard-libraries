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
import org.jetbrains.annotations.NotNull;
import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.metadata.MetadataContainerBuilder;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.impl.builders.TrackedValueBuilderImpl;
import org.quiltmc.config.impl.tree.TrackedValueImpl;
import org.quiltmc.config.impl.util.ConfigUtils;
import org.quiltmc.config.impl.values.ValueKeyImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A value in a config tree.
 */
@ApiStatus.NonExtendable
public interface TrackedValue<T> extends ValueTreeNode {
	ValueKey key();

	/**
	 * Returns the current value being tracked, or the override value if this value is being overridden
	 *
	 * @return some value
	 */
	T value();

	/**
	 * @return whether or not this value is being overridden
	 */
	boolean isBeingOverridden();

	/**
	 * @return the real value being tracked, even if it's being overridden
	 */
	T getRealValue();

	/**
	 * @param newValue the value to set
	 * @param serialize whether or not to serialize this value's backing config file. Should be false only when deserializing
	 * @return the old value that's been replaced
	 */
	T setValue(@NotNull T newValue, boolean serialize);

	/**
	 * Sets an override for this value to be returned by {@link #value} that is not serialized to disk
	 *
	 * @param newValue some value
	 */
	void setOverride(T newValue);

	void removeOverride();

	T getDefaultValue();

	/**
	 * Adds a listener to this {@link TrackedValue} that's called whenever it's updated
	 *
	 * @param callback an update listener
	 */
	void registerCallback(UpdateCallback<T> callback);

	/**
	 * @return the metadata attached to this value for the specified type
	 */
	<M> M metadata(MetadataType<M, ?> type);

	/**
	 * @return whether or not this value has any metadata of the specified type
	 */
	<M> boolean hasMetadata(MetadataType<M, ?> type);

	/**
	 * @return all constraints on this value
	 */
	Iterable<Constraint<T>> constraints();

	/**
	 * Checks the given value against all of this {@link TrackedValue}'s constraints
	 *
	 * @param value the value to check
	 * @return a list of error messages produced by any failing constraints, or an empty {@link Optional} if all passed
	 */
	Optional<Iterable<String>> checkForFailingConstraints(T value);

	/**
	 * Add a config value to be tracked.
	 *
	 * Config values can be one of the following types:
	 * <ul>
	 *     <li>A basic type (int, long, float, double, boolean, String, or enum)</li>
	 *     <li>A complex type (a {@link ValueList} or {@link ValueMap} of basic or complex types or a {@link org.quiltmc.config.api.values.ConfigSerializableObject})</li>
	 * </ul>
	 *
	 * @param defaultValue the default value of the new {@link TrackedValue} to create
	 * @param key0 the first element of the key for the new {@link TrackedValue}
	 * @param keys any number of additional keys for the new {@link TrackedValue}
	 * @return a new {@link TrackedValue}
	 */
	static <T> TrackedValue<T> create(@NotNull T defaultValue, @NotNull String key0, String... keys) {
		ConfigUtils.assertValueType(defaultValue);
		Objects.requireNonNull(key0);
		return new TrackedValueImpl<>(new ValueKeyImpl(key0, keys), defaultValue, new LinkedHashMap<>(0), new ArrayList<>(0), new ArrayList<>(0));
	}

	/**
	 * Add a config value to be tracked.
	 *
	 * Config values can be one of the following types:
	 * <ul>
	 *     <li>A basic type (int, long, float, double, boolean, String, or enum)</li>
	 *     <li>A complex type (a {@link ValueList} or {@link ValueMap} of basic or complex types or a {@link org.quiltmc.config.api.values.ConfigSerializableObject})</li>
	 * </ul>
	 *
	 * @param defaultValue the default value of the new {@link TrackedValue} to create
	 * @param key0 the first element of the key for the new {@link TrackedValue}
	 * @param creator a function that allows adding additional metadata to fields
	 * @return a new {@link TrackedValue}
	 */
	static <T> TrackedValue<T> create(@NotNull T defaultValue, String key0, Consumer<Builder<T>> creator) {
		TrackedValueBuilderImpl<T> builder = new TrackedValueBuilderImpl<>(defaultValue, key0);

		creator.accept(builder);

		return builder.build();
	}

	void invokeCallbacks();

	void serializeAndInvokeCallbacks();

	interface UpdateCallback<T> {
		void onUpdate(TrackedValue<T> trackedValue);
	}

	@ApiStatus.NonExtendable
	interface Builder<T> extends MetadataContainerBuilder<Builder<T>> {
		/**
		 * @return the default value for this builder
		 */
		T getDefaultValue();

		/**
		 * Adds an additional key to this values key
		 *
		 * e.g. if this {@link TrackedValue}'s current key is "appearance.gui", calling this method with
		 * "inventory" would result in a key of "appearance.gui.inventory".
		 *
		 * @param key the key to append
		 * @return this
		 */
		Builder<T> key(String key);

		/**
		 * Create or configure a piece of metadata
		 *
		 * @param type the type of metadata to configure
		 * @param builderConsumer the modifications to be made to the piece of metadata
		 * @return this
		 */
		<M, B extends MetadataType.Builder<M>> Builder<T> metadata(MetadataType<M, B> type, Consumer<B> builderConsumer);

		/**
		 * @param constraint a constraint that this value must satisfy
		 * @return this
		 */
		Builder<T> constraint(Constraint<T> constraint);

		/**
		 * Adds a default listener to the resulting {@link TrackedValue} that's called whenever it's updated
		 *
		 * @param callback an update listener
		 * @return this
		 */
		Builder<T> callback(UpdateCallback<T> callback);
	}
}
