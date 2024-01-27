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

package org.quiltmc.qsl.recipe.impl;

import java.util.Map;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.recipe.api.RecipeLoadingEvents;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeHolder;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;

@ApiStatus.Internal
final class ModifyRecipeHandlerImpl extends BasicRecipeHandlerImpl implements RecipeLoadingEvents.ModifyRecipesCallback.RecipeHandler {
	int counter = 0;

	ModifyRecipeHandlerImpl(RecipeManager recipeManager, Map<RecipeType<?>, Map<Identifier, RecipeHolder<?>>> recipes,
			Map<Identifier, RecipeHolder<?>> globalRecipes, DynamicRegistryManager registryManager) {
		super(recipeManager, recipes, globalRecipes, registryManager);
	}

	private void add(RecipeHolder<?> recipe) {
		Map<Identifier, RecipeHolder<?>> type = this.recipes.get(recipe.value().getType());

		if (type == null) {
			throw new IllegalStateException("The given recipe " + recipe.id()
					+ " does not have its recipe type " + recipe.value().getType() + " in the recipe manager.");
		}

		type.put(recipe.id(), recipe);
		this.globalRecipes.put(recipe.id(), recipe);
	}

	@Override
	public void replace(RecipeHolder<?> recipe) {
		RecipeType<?> oldType = this.getTypeOf(recipe.id());

		if (oldType == null) {
			if (RecipeManagerImpl.DEBUG_MODE) {
				RecipeManagerImpl.LOGGER.info("Add new recipe {} with type {} in modify phase.", recipe.id(), recipe.value().getType());
			}

			this.add(recipe);
		} else if (oldType == recipe.value().getType()) {
			if (RecipeManagerImpl.DEBUG_MODE) {
				RecipeManagerImpl.LOGGER.info("Replace recipe {} with same type {} in modify phase.", recipe.id(), recipe.value().getType());
			}

			this.recipes.get(oldType).put(recipe.id(), recipe);
			this.globalRecipes.put(recipe.id(), recipe);
		} else {
			if (RecipeManagerImpl.DEBUG_MODE) {
				RecipeManagerImpl.LOGGER.info("Replace new recipe {} with type {} (and old type {}) in modify phase.",
						recipe.id(), recipe.value().getType(), oldType);
			}

			this.recipes.get(oldType).remove(recipe.id());
			this.add(recipe);
		}

		this.counter++;
	}
}
