package net.minecraft.core.data.registry.recipe.entry;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.HasJsonAdapter;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.data.registry.recipe.adapter.RecipeEntryUndyeingJsonAdapter;
import net.minecraft.core.data.registry.recipe.adapter.RecipeJsonAdapter;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerCrafting;

public class RecipeEntryUndyeing extends RecipeEntryCraftingDynamic implements HasJsonAdapter {
   public RecipeSymbol inputSymbol;
   public ItemStack output;

   public RecipeEntryUndyeing() {
   }

   public RecipeEntryUndyeing(RecipeSymbol input, ItemStack output) {
      this.inputSymbol = input;
      this.output = output;
   }

   @Override
   public boolean matches(ContainerCrafting containerCrafting) {
      boolean hasSponge = false;
      boolean hasItem = false;

      for (int i = 0; i < 9; i++) {
         ItemStack stack = containerCrafting.getItem(i);
         if (stack != null && stack.getItem() == Blocks.SPONGE_WET.asItem()) {
            if (hasSponge) {
               return false;
            }

            hasSponge = true;
         } else {
            if (stack != null && !this.inputSymbol.matches(stack)) {
               return false;
            }

            if (this.inputSymbol.matches(stack)) {
               hasItem = true;
            }
         }
      }

      return hasSponge && hasItem;
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
         if (query.strict && this.output.getDisplayName().equalsIgnoreCase(query.query.getRight())) {
            return true;
         }

         if (!query.strict && this.output.getDisplayName().toLowerCase().contains(query.query.getRight().toLowerCase())) {
            return true;
         }
      } else if (query.query.getLeft() == SearchQuery.QueryType.GROUP && !Objects.equals(query.query.getRight(), "")) {
         List<ItemStack> groupStacks = new RecipeSymbol(query.query.getRight()).resolve();
         if (groupStacks == null) {
            return false;
         }

         return groupStacks.contains(this.output);
      }

      return false;
   }

   public boolean matchesUsage(SearchQuery query) {
      RecipeSymbol[] symbols = new RecipeSymbol[]{this.inputSymbol, new RecipeSymbol(Blocks.SPONGE_WET.getDefaultStack())};

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

   @Override
   public ItemStack getCraftingResult(ContainerCrafting containerCrafting) {
      int items = 0;

      for (int i = 0; i < containerCrafting.getContainerSize(); i++) {
         ItemStack itemstack1 = containerCrafting.getItem(i);
         if (itemstack1 != null && itemstack1.getItem() != Blocks.SPONGE_WET.asItem()) {
            items++;
         }
      }

      return new ItemStack(this.output.getItem(), items, this.output.getMetadata());
   }

   @Override
   public int getRecipeSize() {
      return 2;
   }

   @Override
   public ItemStack[] onCraftResult(ContainerCrafting containerCrafting) {
      ItemStack[] returnStack = new ItemStack[9];

      for (int i = 0; i < containerCrafting.getContainerSize(); i++) {
         ItemStack itemstack1 = containerCrafting.getItem(i);
         if (itemstack1 != null && itemstack1.getItem() != Blocks.SPONGE_WET.asItem()) {
            containerCrafting.removeItem(i, 1);
         }
      }

      return returnStack;
   }

   @Override
   public RecipeJsonAdapter<?> getAdapter() {
      return new RecipeEntryUndyeingJsonAdapter();
   }
}
