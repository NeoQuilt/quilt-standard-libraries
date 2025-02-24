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

import org.quiltmc.config.api.metadata.MetadataContainer;
import org.quiltmc.config.api.metadata.MetadataType;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractMetadataContainer implements MetadataContainer {
	public Map<MetadataType<?, ?>, Object> metadata;

	protected AbstractMetadataContainer(Map<MetadataType<?, ?>, Object> metadata) {
		this.metadata = metadata;
	}

	@SuppressWarnings("unchecked")
	public <M> M metadata(MetadataType<M, ?> type) {
		if (this.metadata.containsKey(type)) {
			return (M) this.metadata.get(type);
		} else {
			Optional<M> defaultValue = type.getDefaultValue(this);

			if (defaultValue.isPresent()) {
				this.metadata.put(type, defaultValue.get());
				return (M) this.metadata.get(type);
			} else {
				return null;
			}
		}
	}

	public <M> boolean hasMetadata(MetadataType<M, ?> type) {
		return this.metadata.containsKey(type) || type.getDefaultValue(this).isPresent();
	}
}
