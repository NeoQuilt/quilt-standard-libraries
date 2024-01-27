/*
 * Copyright 2016, 2017, 2018, 2019 FabricMC
 * Copyright 2022 The Quilt Project
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

package org.quiltmc.qsl.screen.mixin.client;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.screen.api.client.QuiltScreen;
import org.quiltmc.qsl.screen.impl.client.ButtonList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
abstract class ScreenMixin implements QuiltScreen {
	@Shadow
	@Final
	private List<Selectable> selectables;

	@Shadow
	@Final
	private List<Element> children;

	@Shadow
	@Final
	private List<Drawable> drawables;

	@Shadow
	@Nullable
	protected MinecraftClient client;

	@Shadow
	private boolean initialized;

	@Shadow
	protected TextRenderer textRenderer;

	@Unique
	private ButtonList quilt$quiltButtons = null;




	@Override
	public TextRenderer getTextRenderer() {
		return this.textRenderer;
	}

	@Override
	public MinecraftClient getClient() {
		return this.client;
	}
}
