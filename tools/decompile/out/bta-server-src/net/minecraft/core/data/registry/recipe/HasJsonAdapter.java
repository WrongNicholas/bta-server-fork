package net.minecraft.core.data.registry.recipe;

import net.minecraft.core.data.registry.recipe.adapter.RecipeJsonAdapter;

public interface HasJsonAdapter {
   RecipeJsonAdapter<?> getAdapter();
}
