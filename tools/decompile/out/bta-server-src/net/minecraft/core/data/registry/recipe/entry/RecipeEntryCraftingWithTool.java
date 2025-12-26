package net.minecraft.core.data.registry.recipe.entry;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.HasJsonAdapter;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.data.registry.recipe.adapter.RecipeEntryCraftingWithToolJsonAdapter;
import net.minecraft.core.data.registry.recipe.adapter.RecipeJsonAdapter;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerCrafting;

public class RecipeEntryCraftingWithTool extends RecipeEntryCraftingDynamic implements HasJsonAdapter {
   public RecipeSymbol inputSymbol;
   public RecipeSymbol toolToUse;
   public ItemStack output;

   public RecipeEntryCraftingWithTool() {
   }

   public RecipeEntryCraftingWithTool(RecipeSymbol inputSymbol, RecipeSymbol toolToUse, ItemStack output) {
      this.inputSymbol = inputSymbol;
      this.toolToUse = toolToUse;
      this.output = output;
   }

   @Override
   public boolean matches(ContainerCrafting containerCrafting) {
      boolean toolMatch = false;
      boolean itemMatch = false;

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               if (this.toolToUse.matches(stack)) {
                  toolMatch = true;
               } else {
                  if (!this.inputSymbol.matches(stack)) {
                     return false;
                  }

                  itemMatch = true;
               }
            }
         }
      }

      return toolMatch && itemMatch;
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
      RecipeSymbol[] symbols = new RecipeSymbol[]{this.inputSymbol, this.toolToUse};

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
      return new ItemStack(this.output.itemID, this.output.stackSize, this.output.getMetadata());
   }

   @Override
   public ItemStack[] onCraftResult(ContainerCrafting containerCrafting) {
      ItemStack[] returnStack = new ItemStack[9];

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               ItemStack stack2;
               if (this.toolToUse.matches(new ItemStack(stack))) {
                  if (stack.getMetadata() + 1 > stack.getMaxDamage()) {
                     stack2 = null;
                  } else {
                     stack2 = new ItemStack(stack.getItem(), stack.stackSize, stack.getMetadata() + 1);
                  }
               } else if (stack.stackSize - 1 == 0) {
                  stack2 = null;
               } else {
                  stack2 = new ItemStack(stack.getItem(), stack.stackSize - 1);
               }

               containerCrafting.setSlotContentsAt(x, y, stack2);
            }
         }
      }

      return returnStack;
   }

   @Override
   public int getRecipeSize() {
      return 2;
   }

   @Override
   public RecipeJsonAdapter<?> getAdapter() {
      return new RecipeEntryCraftingWithToolJsonAdapter();
   }
}
