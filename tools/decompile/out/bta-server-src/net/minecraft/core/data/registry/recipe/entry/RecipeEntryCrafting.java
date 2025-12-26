package net.minecraft.core.data.registry.recipe.entry;

import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerCrafting;

public abstract class RecipeEntryCrafting<I, O> extends RecipeEntryBase<I, O, Void> {
   public RecipeEntryCrafting(I input, O output) {
      super(input, output, null);
   }

   public RecipeEntryCrafting() {
   }

   public Void getData() {
      return null;
   }

   public boolean containsData(Void data) {
      return false;
   }

   public abstract boolean matches(ContainerCrafting var1);

   public boolean matchesQueryIgnoreExceptions(SearchQuery query) {
      try {
         return this.matchesQuery(query);
      } catch (IllegalArgumentException | NullPointerException var3) {
         return false;
      }
   }

   public abstract boolean matchesQuery(SearchQuery var1);

   public abstract ItemStack getCraftingResult(ContainerCrafting var1);

   public abstract int getRecipeSize();

   public abstract ItemStack[] onCraftResult(ContainerCrafting var1);
}
