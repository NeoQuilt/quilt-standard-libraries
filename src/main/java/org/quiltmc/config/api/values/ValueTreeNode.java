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
import org.quiltmc.config.api.metadata.MetadataContainer;
import org.quiltmc.config.api.metadata.MetadataType;

/**
 * An element in a configs value tree.
 *
 * <p>Will be either a {@link TrackedValue} or {@link Section}
 */
public interface ValueTreeNode extends MetadataContainer {
	ValueKey key();

	/**
	 * @return the metadata attached to this value for the specified type
	 */
	<M> M metadata(MetadataType<M, ?> type);

	/**
	 * @return whether or not this value has any metadata of the specified type
	 */
	<M> boolean hasMetadata(MetadataType<M, ?> type);

	/**
	 * A node that contains any number of child nodes.
	 */
	@ApiStatus.NonExtendable
	interface Section extends ValueTreeNode, Iterable<ValueTreeNode> {

	}
}
