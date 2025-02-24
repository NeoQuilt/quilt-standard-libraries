/*
 * Copyright 2023 The Quilt Project
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

package org.quiltmc.qsl.registry.impl.sync.mod_protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quiltmc.qsl.registry.impl.RegistryConfig;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

public class ModProtocolImpl {
	public static boolean enabled = false;
	public static boolean disableQuery = false;
	public static String prioritizedId = "";
	public static ModProtocolDef prioritizedEntry;
	private static final Logger LOGGER = LogUtils.getLogger();

	private static final Map<String, ModProtocolDef> PROTOCOL_VERSIONS = new HashMap<>();
	public static final List<ModProtocolDef> REQUIRED = new ArrayList<>();
	public static final List<ModProtocolDef> ALL = new ArrayList<>();

	@SuppressWarnings("ConstantConditions")
	public static void loadVersions() {
		var config = RegistryConfig.INSTANCE.registry_sync;
		disableQuery = config.disable_mod_protocol_ping.value();

		if (config.mod_protocol_version.value() >= 0) {
			prioritizedEntry = new ModProtocolDef("global:" + config.mod_protocol_id.value(), config.mod_protocol_name.value(), IntList.of(config.mod_protocol_version.value()), false);
			prioritizedId = prioritizedEntry.id();
			add(prioritizedEntry);
		}

		for (var container : FabricLoader.getInstance().getAllMods()) {
			var data = container.getMetadata();
			var quiltRegistry = data.getCustomValue("quilt_registry");

			if (quiltRegistry == null) {
				continue;
			}

			if (quiltRegistry.getType() != CustomValue.CvType.OBJECT) {
				LOGGER.warn("Mod {} ({}) contains invalid 'quilt_registry' entry! Expected 'OBJECT', found '{}'", container.getMetadata().getName(), container.getMetadata().getId(), quiltRegistry.getType());
				continue;
			}

			var value = quiltRegistry.getAsObject().get("mod_protocol");

			if (value == null || value.getType() == CustomValue.CvType.NULL) {
				continue;
			}

			if (value.getType() == CustomValue.CvType.OBJECT) {
				var object = value.getAsObject();

				var optional = false;
				var optVal = object.get("optional");

				if (optVal != null) {
					if (optVal.getType() != CustomValue.CvType.BOOLEAN) {
						invalidEntryType(".optional", container, CustomValue.CvType.BOOLEAN, optVal.getType());
						continue;
					}

					optional = optVal.getAsBoolean();
				}

				var version = decodeVersion(".value", container, object.get("value"));

				if (version != null) {
					add(new ModProtocolDef("mod:" + data.getId(), data.getName(), version, optional));
				}
			} else {
				var version = decodeVersion("", container, value);
				if (version != null) {
					add(new ModProtocolDef("mod:" + data.getId(), data.getName(), version, false));
				}
			}
		}
	}

	private static IntList decodeVersion(String path, ModContainer container, CustomValue value) {
		if (value == null) {
			invalidEntryType(path, container, CustomValue.CvType.NUMBER, CustomValue.CvType.NULL);
			return null;
		} else if (value.getType() == CustomValue.CvType.NUMBER) {
			var i = value.getAsNumber().intValue();
			if (i < 0) {
				negativeEntry(path, container, i);
				return null;
			}

			return IntList.of(i);
		} else if (value.getType() == CustomValue.CvType.ARRAY) {
			var array = value.getAsArray();
			var versions = new IntArrayList(array.size());
			for (var i = 0; i < array.size(); i++) {
				var entry = array.get(i);
				if (entry.getType() == CustomValue.CvType.NUMBER) {
					var version = entry.getAsNumber().intValue();
					if (version < 0) {
						negativeEntry(path + "[" + i + "]", container, version);
						return null;
					}

					versions.add(version);
				} else {
					invalidEntryType(path + "[" + i + "]", container, CustomValue.CvType.NUMBER, entry.getType());
					return null;
				}
			}

			return versions;
		} else {
			invalidEntryType(path + ".optional", container, CustomValue.CvType.NUMBER, value.getType());
			return null;
		}
	}

	private static void invalidEntryType(String path, ModContainer c, CustomValue.CvType expected, CustomValue.CvType found) {
		LOGGER.warn("Mod {} ({}) contains invalid 'quilt_registry.mod_protocol{}' entry! Expected '{}', found '{}'", path, c.getMetadata().getName(), c.getMetadata().getId(), expected.name(), found.name());
	}

	private static void negativeEntry(String path, ModContainer c, int i) {
		LOGGER.warn("Mod {} ({}) contains invalid 'quilt_registry.mod_protocol{}' entry! Protocol requires non-negative integer, found '{}'!", path, c.getMetadata().getName(), c.getMetadata().getId(), i);
	}

	public static IntList getVersion(String string) {
		var x = PROTOCOL_VERSIONS.get(string);
		return x == null ? IntList.of() : x.versions();
	}

	public static void add(ModProtocolDef def) {
		PROTOCOL_VERSIONS.put(def.id(), def);

		if (!def.optional()) {
			REQUIRED.add(def);
		}

		ALL.add(def);
		enabled = true;
	}
}
