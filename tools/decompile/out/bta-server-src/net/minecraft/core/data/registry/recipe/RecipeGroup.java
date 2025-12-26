package net.minecraft.core.data.registry.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.data.registry.Registry;

public class RecipeGroup<T extends RecipeEntryBase<?, ?, ?>> extends Registry<T> {
   private final RecipeSymbol machine;

   public RecipeGroup(RecipeSymbol machine) {
      this.machine = machine;
   }

   public void register(String key, T item) {
      super.register(key, item);
      item.parent = this;
   }

   public RecipeSymbol getMachine() {
      return this.machine;
   }

   public List<T> getAllRecipes() {
      ArrayList<T> recipes = new ArrayList<>();

      for (T recipeEntry : this) {
         recipes.add(recipeEntry);
      }

      return Collections.unmodifiableList(recipes);
   }
}
