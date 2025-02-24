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

package org.quiltmc.qsl.worldgen.surface_rule.impl;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.worldgen.surface_rule.api.SurfaceRuleEvents;
import org.quiltmc.qsl.worldgen.surface_rule.api.codec.AddMaterialRuleCallback;

import net.fabricmc.loader.api.ModContainer;

@ApiStatus.Internal
public class SurfaceRuleDataInitializer implements ModInitializer {
	@Override
	public void onInitialize(ModContainer mod) {
		SurfaceRuleEvents.MODIFY_OVERWORLD_CODECS.register(AddMaterialRuleCallback.CODEC_ID, AddMaterialRuleCallback.CODEC);
		SurfaceRuleEvents.MODIFY_NETHER_CODECS.register(AddMaterialRuleCallback.CODEC_ID, AddMaterialRuleCallback.CODEC);
		SurfaceRuleEvents.MODIFY_THE_END_CODECS.register(AddMaterialRuleCallback.CODEC_ID, AddMaterialRuleCallback.CODEC);
		SurfaceRuleEvents.MODIFY_GENERIC_CODECS.register(AddMaterialRuleCallback.CODEC_ID, AddMaterialRuleCallback.CODEC);
	}
}
