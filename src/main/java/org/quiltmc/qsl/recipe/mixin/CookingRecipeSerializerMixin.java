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

package org.quiltmc.qsl.recipe.mixin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.quiltmc.qsl.recipe.api.serializer.QuiltRecipeSerializer;
import org.spongepowered.asm.mixin.Mixin;

import com.google.gson.JsonObject;

import net.minecraft.data.server.recipe.CookingRecipeJsonFactory.CookingRecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.CookingCategory;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.unmapped.C_unoypvme;
import net.minecraft.util.Identifier;

@Mixin(CookingRecipeSerializer.class)
public abstract class CookingRecipeSerializerMixin<T extends AbstractCookingRecipe> implements QuiltRecipeSerializer<T> {
	@Override
	public JsonObject toJson(T recipe, Identifier id) {
		CookingRecipeJsonProvider res;
		try {
			Constructor cons =   CookingRecipeJsonProvider.class.getConstructor(Identifier.class,String.class,CookingCategory.class,Ingredient.class,Item.class,float.class,int.class,C_unoypvme.class,RecipeSerializer.class);
cons.setAccessible(true);
res = (CookingRecipeJsonProvider)cons.newInstance(id, recipe.getGroup(), recipe.getCategory(),
			recipe.getIngredients().get(0), recipe.getResult(null).getItem(),
			recipe.getExperience(), recipe.getCookTime(), null, this);
return res 
		.toJson();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		return null;
		}
		



	}
}
