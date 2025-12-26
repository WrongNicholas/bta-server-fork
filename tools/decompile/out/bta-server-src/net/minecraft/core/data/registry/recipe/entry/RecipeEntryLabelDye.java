package net.minecraft.core.data.registry.recipe.entry;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.ContainerCrafting;

public class RecipeEntryLabelDye extends RecipeEntryCraftingDynamic {
   @Override
   public ItemStack getCraftingResult(ContainerCrafting containerCrafting) {
      ItemStack labelStack = null;
      ItemStack dyeStack = null;

      label47:
      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               if (stack.itemID == Items.LABEL.id && labelStack == null) {
                  labelStack = stack;
               } else if (stack.itemID == Items.DYE.id && dyeStack == null) {
                  dyeStack = stack;
               }

               if (labelStack != null && dyeStack != null) {
                  break label47;
               }
            }
         }
      }

      if (labelStack != null && dyeStack != null) {
         ItemStack outStack = labelStack.copy();
         outStack.setCustomColor((byte)(15 - dyeStack.getMetadata()));
         outStack.stackSize = 1;
         return outStack;
      } else {
         return null;
      }
   }

   @Override
   public int getRecipeSize() {
      return 2;
   }

   @Override
   public boolean matches(ContainerCrafting containerCrafting) {
      ItemStack labelStack = null;
      ItemStack dyeStack = null;

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               if (stack.itemID == Items.LABEL.id) {
                  if (labelStack != null) {
                     return false;
                  }

                  labelStack = stack;
               } else {
                  if (stack.itemID != Items.DYE.id) {
                     return false;
                  }

                  if (dyeStack != null) {
                     return false;
                  }

                  dyeStack = stack;
               }
            }
         }
      }

      return labelStack == null || dyeStack == null ? false : labelStack.hasCustomName() && !labelStack.hasCustomColor();
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
      ItemStack output = Items.LABEL.getDefaultStack();
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
      RecipeSymbol[] symbols = new RecipeSymbol[]{new RecipeSymbol(Items.LABEL.getDefaultStack()), new RecipeSymbol(new ItemStack(Items.DYE, 1, -1))};

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
   public ItemStack[] onCraftResult(ContainerCrafting containerCrafting) {
      ItemStack[] returnStack = new ItemStack[9];

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               stack.stackSize--;
               if (stack.stackSize <= 0) {
                  containerCrafting.setSlotContentsAt(x, y, null);
               }
            }
         }
      }

      return returnStack;
   }
}
