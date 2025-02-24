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

package org.quiltmc.config.impl;

import org.jetbrains.annotations.NotNull;
import org.quiltmc.config.api.*;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.builders.ConfigBuilderImpl;
import org.quiltmc.config.impl.builders.ReflectiveConfigCreator;
import org.quiltmc.config.impl.builders.WrappedConfigCreator;
import org.quiltmc.config.impl.tree.Trie;
import org.quiltmc.config.impl.util.ImmutableIterable;
import org.quiltmc.config.implementor_api.ConfigEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ConfigImpl extends AbstractMetadataContainer implements Config {
	private final ConfigEnvironment environment;
	private final String family, id;
	private final Path path;
	private final List<UpdateCallback> callbacks;
	private final Trie values;
	private final String defaultFileType;

	public ConfigImpl(ConfigEnvironment environment, String id, Path path, Map<MetadataType<?, ?>, Object> metadata, String family, List<UpdateCallback> callbacks, Trie values, String defaultFileType) {
		super(metadata);
		this.environment = environment;
		this.family = family;
		this.id = id;
		this.path = path;
		this.callbacks = callbacks;
		this.values = values;
		this.defaultFileType = defaultFileType;
	}

	@Override
	public String family() {
		return this.family;
	}

	@Override
	public String id() {
		return this.id;
	}

	@Override
	public Path savePath() {
		return this.path;
	}

	@Override
	public void registerCallback(UpdateCallback callback) {
		this.callbacks.add(callback);
	}

	public String getDefaultFileType() {
		return this.defaultFileType;
	}

	public ConfigEnvironment getEnvironment() {
		return this.environment;
	}

	private Path getPath() {
		return this.environment.getSaveDir().resolve(this.family).resolve(this.path).resolve(this.id + "." + this.environment.getSerializer(this.defaultFileType).getFileExtension());
	}

	@Override
	public void save() {
		Path path = this.getPath();

		try {
			Files.createDirectories(path.getParent());
			this.environment.getSerializer(this.defaultFileType).serialize(this, Files.newOutputStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void invokeCallbacks() {
		for (UpdateCallback callback : this.callbacks) {
			callback.onUpdate(this);
		}
	}

	public Iterable<TrackedValue<?>> values() {
		return new Iterable<TrackedValue<?>>() {
			@NotNull
			@Override
			public Iterator<TrackedValue<?>> iterator() {
				return new Iterator<TrackedValue<?>>() {
					private final Iterator<ValueTreeNode> itr = ConfigImpl.this.values.leaves().iterator();
					private ValueTreeNode next;

					@Override
					public boolean hasNext() {
						// Consume non-leaf nodes
						while (this.itr.hasNext() && !(this.next instanceof TrackedValue)) {
							this.next = this.itr.next();
						}

						return next != null;
					}

					@Override
					public TrackedValue<?> next() {
						TrackedValue<?> value = (TrackedValue<?>) this.next;

						this.next = null;

						return value;
					}
				};
			}
		};
	}

	@Override
	public TrackedValue<?> getValue(Iterable<String> key) {
		return this.values.get(key);
	}

	public Iterable<ValueTreeNode> nodes() {
		return new ImmutableIterable<>(this.values.nodes());
	}

	public ValueTreeNode getNode(Iterable<String> key) {
		return this.values.getNode(key);
	}

	public static Config create(ConfigEnvironment environment, String familyId, String id, Creator... creators) {
		return create(environment, familyId, id, Paths.get(""), creators);
	}

	public static Config create(ConfigEnvironment environment, String familyId, String id, Path path, Creator... creators) {
		ConfigBuilderImpl builder = new ConfigBuilderImpl(environment, familyId, id, path);

		for (Creator creator : creators) {
			creator.create(builder);
		}

		return builder.build();
	}

	@Deprecated
	public static <C extends WrappedConfig> C create(ConfigEnvironment environment, String familyId, String id, Path path, Creator before, Class<C> configCreatorClass, Creator after) {
		WrappedConfigCreator<C> creator = WrappedConfigCreator.of(configCreatorClass);
		Config config = create(environment, familyId, id, path, before, creator, after);
		C c = creator.getInstance();

		c.setWrappedConfig(config);

		return c;
	}

	public static <C extends ReflectiveConfig> C createReflective(ConfigEnvironment environment, String familyId, String id, Path path, Creator before, Class<C> configCreatorClass, Creator after) {
		ReflectiveConfigCreator<C> creator = ReflectiveConfigCreator.of(configCreatorClass);
		Config config = create(environment, familyId, id, path, before, creator, after);
		C c = creator.getInstance();

		InternalsHelper.setWrappedConfig(c, config);

		return c;
	}
}
