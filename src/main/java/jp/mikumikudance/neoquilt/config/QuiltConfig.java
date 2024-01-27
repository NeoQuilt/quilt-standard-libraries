/*
 * Copyright 2023 QuiltMC
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

package jp.mikumikudance.neoquilt.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.implementor_api.ConfigFactory;

public final class QuiltConfig {


	public static <C extends ReflectiveConfig> C create(String family, String id, Path path, Config.Creator before, Class<C> configCreatorClass, Config.Creator after) {
		return ConfigFactory.create(null, family, id, path, before, configCreatorClass, after);
	}
	public static <C extends ReflectiveConfig> C create(String family, String id, Class<C> configCreatorClass) {
		return create(family, id, Paths.get(""), builder -> {}, configCreatorClass, builder -> {});
	}

	private QuiltConfig() {
	}
}
