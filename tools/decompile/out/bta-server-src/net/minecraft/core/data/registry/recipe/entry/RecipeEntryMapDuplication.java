package net.minecraft.core.data.registry.recipe.entry;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemMap;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.ContainerCrafting;

public class RecipeEntryMapDuplication extends RecipeEntryCraftingDynamic {
   @Override
   public ItemStack getCraftingResult(ContainerCrafting containerCrafting) {
      ItemStack map = null;
      int paperAmount = 0;

      for (int i = 0; i < containerCrafting.getContainerSize(); i++) {
         ItemStack s = containerCrafting.getItem(i);
         if (s != null) {
            Item item = s.getItem();
            if (item == Items.MAP) {
               map = s;
            } else if (item == Items.PAPER) {
               paperAmount++;
            }
         }
      }

      assert map != null;

      ItemStack output = map.copy();
      output.stackSize += paperAmount;
      return output;
   }

   @Override
   public int getRecipeSize() {
      return 2;
   }

   @Override
   public ItemStack[] onCraftResult(ContainerCrafting containerCrafting) {
      for (int i = 0; i < containerCrafting.getContainerSize(); i++) {
         containerCrafting.setItem(i, null);
      }

      return new ItemStack[0];
   }

   @Override
   public boolean matches(ContainerCrafting containerCrafting) {
      ItemStack mapStack = null;
      boolean hasPaper = false;

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               if (stack.itemID == Items.MAP.id) {
                  if (mapStack != null) {
                     return false;
                  }

                  mapStack = stack;
               } else {
                  if (stack.itemID != Items.PAPER.id) {
                     return false;
                  }

                  hasPaper = true;
               }
            }
         }
      }

      if (mapStack == null) {
         return false;
      } else {
         return !ItemMap.hasInitialized(mapStack) ? false : hasPaper;
      }
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
      ItemStack output = Items.MAP.getDefaultStack();
      if (query.query.getLeft() == SearchQuery.QueryType.NAME) {
         if (query.strict && output.getDisplayName().equalsIgnoreCase(query.query.getRight())) {
            return true;
         }

         if (!query.strict && output.getDisplayName().toLowerCase().contains(query.query.getRight().toLowerCase())) {
            return true;
         }
      } else if (query.query.getLeft() == SearchQuery.QueryType.GROUP && !Objects.equals(query.query.getRight(), "")) {
         List<ItemStack> groupStacks = new RecipeSymbol(query.query.getRight()).resolve();
         if (groupStacks == null) {
            return false;
         }

         return groupStacks.contains(output);
      }

      return false;
   }

   public boolean matchesUsage(SearchQuery query) {
      RecipeSymbol[] symbols = new RecipeSymbol[]{new RecipeSymbol(Items.MAP.getDefaultStack())};

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
}
