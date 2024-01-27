/*
 * Copyright 2021 The Quilt Project
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

package org.quiltmc.qsl.crash.impl;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.crash.api.CrashReportEvents;

import net.minecraft.util.SystemDetails;

@ApiStatus.Internal
public final class CrashInfoImpl implements CrashReportEvents.SystemDetails {
	@Override
	public void addDetails(SystemDetails details) {
		//details.addSection("Quilt Mods",  "\n\t\t" + FabricLoader.getInstance().getAllMods().replace("\n", "\n\t\t"));
	}
}
