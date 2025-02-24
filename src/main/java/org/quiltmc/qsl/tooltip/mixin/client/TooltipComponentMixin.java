/*
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

package org.quiltmc.qsl.tooltip.mixin.client;

import org.quiltmc.qsl.tooltip.api.client.TooltipComponentCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;

@Environment(EnvType.CLIENT)
@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {
	@Inject(
			method = "of(Lnet/minecraft/client/item/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void onConversion(TooltipData data, CallbackInfoReturnable<TooltipComponent> cir) {
		var component = TooltipComponentCallback.EVENT.invoker().getComponent(data);

		if (component != null) {
			cir.setReturnValue(component);
		}
	}
}
