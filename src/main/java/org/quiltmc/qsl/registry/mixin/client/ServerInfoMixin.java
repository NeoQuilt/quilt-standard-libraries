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

package org.quiltmc.qsl.registry.mixin.client;

import java.util.Map;

import org.quiltmc.qsl.registry.impl.sync.mod_protocol.ModProtocolContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;

@Environment(EnvType.CLIENT)
@Mixin(ServerInfo.class)
public class ServerInfoMixin implements ModProtocolContainer {
	@Unique
	private Map<String, IntList> quilt$modProtocol;

	@Override
	public void quilt$setModProtocol(Map<String, IntList> map) {
		this.quilt$modProtocol = map;
	}

	@Override
	public Map<String, IntList> quilt$getModProtocol() {
		return this.quilt$modProtocol;
	}
}
