package net.minecraft.core.data.registry.recipe.entry;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.HasJsonAdapter;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.data.registry.recipe.adapter.RecipeCraftingShapedJsonAdapter;
import net.minecraft.core.data.registry.recipe.adapter.RecipeJsonAdapter;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerCrafting;

public class RecipeEntryCraftingShaped extends RecipeEntryCrafting<RecipeSymbol[], ItemStack> implements HasJsonAdapter {
   public static final boolean DEFAULT_CONSUME_CONTAINER = false;
   public static final boolean DEFAULT_ALLOW_MIRROR = true;
   public boolean consumeContainerItem;
   public boolean allowMirrored;
   public final int recipeWidth;
   public final int recipeHeight;

   public RecipeEntryCraftingShaped(int recipeWidth, int recipeHeight, RecipeSymbol[] input, ItemStack output) {
      super(input, output);
      this.recipeWidth = recipeWidth;
      this.recipeHeight = recipeHeight;
      this.consumeContainerItem = false;
      this.allowMirrored = true;
   }

   public RecipeEntryCraftingShaped(int recipeWidth, int recipeHeight, RecipeSymbol[] input, ItemStack output, boolean consumeContainerItem) {
      super(input, output);
      this.consumeContainerItem = consumeContainerItem;
      this.allowMirrored = true;
      this.recipeWidth = recipeWidth;
      this.recipeHeight = recipeHeight;
   }

   public RecipeEntryCraftingShaped(
      int recipeWidth, int recipeHeight, RecipeSymbol[] input, ItemStack output, boolean consumeContainerItem, boolean allowMirrored
   ) {
      super(input, output);
      this.consumeContainerItem = consumeContainerItem;
      this.allowMirrored = allowMirrored;
      this.recipeWidth = recipeWidth;
      this.recipeHeight = recipeHeight;
   }

   public RecipeEntryCraftingShaped() {
      this.recipeWidth = 0;
      this.recipeHeight = 0;
      this.consumeContainerItem = false;
      this.allowMirrored = true;
   }

   @Override
   public boolean matches(ContainerCrafting containerCrafting) {
      for (int _w = 0; _w <= 3 - this.recipeWidth; _w++) {
         for (int _h = 0; _h <= 3 - this.recipeHeight; _h++) {
            if (this.allowMirrored && this.isRecipeMatching(containerCrafting, _w, _h, true)) {
               return true;
            }

            if (this.isRecipeMatching(containerCrafting, _w, _h, false)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean matchesQuery(SearchQuery query) {
      switch (query.mode) {
         case ALL:
            if ((this.matchesRecipe(query) || this.matchesUsage(query)) && this.matchesScope(query)) {
               return true;
            }
            break;
         case RECIPE:
            if (this.matchesRecipe(query) && this.matchesScope(query)) {
               return true;
            }
            break;
         case USAGE:
            if (this.matchesUsage(query) && this.matchesScope(query)) {
               return true;
            }
      }

      return false;
   }

   public boolean matchesScope(SearchQuery query) {
      if (query.scope.getLeft() == SearchQuery.SearchScope.NONE) {
         return true;
      } else {
         if (query.scope.getLeft() == SearchQuery.SearchScope.NAMESPACE) {
            RecipeNamespace namespace = Registries.RECIPES.getItem(query.scope.getRight());
            if (namespace == this.parent.getParent()) {
               return true;
            }
         } else if (query.scope.getLeft() == SearchQuery.SearchScope.NAMESPACE_GROUP) {
            RecipeGroup group;
            try {
               group = Registries.RECIPES.getGroupFromKey(query.scope.getRight());
            } catch (IllegalArgumentException var4) {
               group = null;
            }

            if (group == this.parent) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean matchesRecipe(SearchQuery query) {
      if (query.query.getLeft() == SearchQuery.QueryType.NAME) {
         if (query.strict && this.getOutput().getDisplayName().equalsIgnoreCase(query.query.getRight())) {
            return true;
         }

         if (!query.strict && this.getOutput().getDisplayName().toLowerCase().contains(query.query.getRight().toLowerCase())) {
            return true;
         }
      } else if (query.query.getLeft() == SearchQuery.QueryType.GROUP && !Objects.equals(query.query.getRight(), "")) {
         List<ItemStack> groupStacks = new RecipeSymbol(query.query.getRight()).resolve();
         if (groupStacks == null) {
            return false;
         }

         return groupStacks.contains(this.getOutput());
      }

      return false;
   }

   public boolean matchesUsage(SearchQuery query) {
      RecipeSymbol[] symbols = this.getInput();

      for (RecipeSymbol symbol : symbols) {
         if (symbol != null) {
            List<ItemStack> stacks = symbol.resolve();
            if (query.query.getLeft() == SearchQuery.QueryType.NAME) {
               for (ItemStack stack : stacks) {
                  if (query.strict && stack.getDisplayName().equalsIgnoreCase(query.query.getRight())) {
                     return true;
                  }

                  if (!query.strict && stack.getDisplayName().toLowerCase().contains(query.query.getRight().toLowerCase())) {
                     return true;
                  }
               }
            } else if (query.query.getLeft() == SearchQuery.QueryType.GROUP && !Objects.equals(query.query.getRight(), "")) {
               List<ItemStack> groupStacks = new RecipeSymbol(query.query.getRight()).resolve();
               if (groupStacks == null) {
                  return false;
               }

               if (stacks.stream().anyMatch(groupStacks::contains)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean isRecipeMatching(ContainerCrafting inventory, int x, int y, boolean mirror) {
      for (int k = 0; k < 3; k++) {
         for (int l = 0; l < 3; l++) {
            int i1 = k - x;
            int j1 = l - y;
            RecipeSymbol symbol = null;
            if (i1 >= 0 && j1 >= 0 && i1 < this.recipeWidth && j1 < this.recipeHeight) {
               if (mirror) {
                  symbol = this.getInput()[this.recipeWidth - i1 - 1 + j1 * this.recipeWidth];
               } else {
                  symbol = this.getInput()[i1 + j1 * this.recipeWidth];
               }
            }

            ItemStack itemstack1 = inventory.getItemStackAt(k, l);
            if (itemstack1 != null || symbol != null) {
               if (itemstack1 == null || symbol == null) {
                  return false;
               }

               if (!symbol.matches(itemstack1)) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   @Override
   public ItemStack getCraftingResult(ContainerCrafting containerCrafting) {
      return this.getOutput().copy();
   }

   @Override
   public ItemStack[] onCraftResult(ContainerCrafting containerCrafting) {
      ItemStack[] returnStack = new ItemStack[9];

      for (int i = 0; i < containerCrafting.getContainerSize(); i++) {
         ItemStack itemstack1 = containerCrafting.getItem(i);
         if (itemstack1 != null) {
            containerCrafting.removeItem(i, 1);
            if (!this.consumeContainerItem && itemstack1.getItem().hasContainerItem()) {
               containerCrafting.setItem(i, new ItemStack(itemstack1.getItem().getContainerItem()));
            }
         }
      }

      return returnStack;
   }

   @Override
   public int getRecipeSize() {
      return this.recipeWidth * this.recipeHeight;
   }

   @Override
   public RecipeJsonAdapter<?> getAdapter() {
      return new RecipeCraftingShapedJsonAdapter();
   }
}
