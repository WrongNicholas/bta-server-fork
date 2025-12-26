package net.minecraft.core.data.registry.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.data.registry.Registry;

public class RecipeNamespace extends Registry<RecipeGroup<? extends RecipeEntryBase<?, ?, ?>>> {
   public List<RecipeEntryBase<?, ?, ?>> getAllRecipes() {
      ArrayList<RecipeEntryBase<?, ?, ?>> recipes = new ArrayList<>();

      for (RecipeGroup<? extends RecipeEntryBase<?, ?, ?>> recipeGroup : this) {
         for (RecipeEntryBase<?, ?, ?> recipeEntry : recipeGroup) {
            recipes.add(recipeEntry);
         }
      }

      return Collections.unmodifiableList(recipes);
   }
}
