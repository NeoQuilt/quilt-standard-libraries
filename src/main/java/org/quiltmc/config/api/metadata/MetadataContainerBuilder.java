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

package org.quiltmc.config.api.metadata;

import java.util.function.Consumer;

public interface MetadataContainerBuilder<SELF extends MetadataContainerBuilder<SELF>> {
	/**
	 * Create or configure a piece of metadata
	 *
	 * @param type the type of metadata to configure
	 * @param builderConsumer the modifications to be made to the piece of metadata
	 * @return this
	 */
	<M, B extends MetadataType.Builder<M>> SELF metadata(MetadataType<M, B> type, Consumer<B> builderConsumer);
}
