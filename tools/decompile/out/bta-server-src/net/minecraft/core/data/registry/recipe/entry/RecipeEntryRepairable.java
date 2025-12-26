package net.minecraft.core.data.registry.recipe.entry;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.HasJsonAdapter;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.data.registry.recipe.adapter.RecipeEntryRepairableJsonAdapter;
import net.minecraft.core.data.registry.recipe.adapter.RecipeJsonAdapter;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerCrafting;

public class RecipeEntryRepairable extends RecipeEntryCraftingDynamic implements HasJsonAdapter {
   public ItemStack inItem;
   public RecipeSymbol repairMaterial;

   public RecipeEntryRepairable() {
   }

   public RecipeEntryRepairable(ItemStack inItem, RecipeSymbol repairMaterial) {
      this.inItem = inItem;
      this.repairMaterial = repairMaterial;
   }

   @Override
   public boolean matches(ContainerCrafting containerCrafting) {
      int repairMaterialCount = 0;
      int inItemCount = 0;

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               if (this.repairMaterial.matches(stack)) {
                  repairMaterialCount++;
               } else {
                  if (this.inItem.itemID != stack.itemID) {
                     return false;
                  }

                  inItemCount++;
               }
            }
         }
      }

      return repairMaterialCount == 0 ? false : inItemCount == 1;
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
      ItemStack output = this.inItem;
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
      RecipeSymbol[] symbols = new RecipeSymbol[]{new RecipeSymbol(this.inItem), this.repairMaterial};

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
      int repairMaterialCount = 0;
      ItemStack inItemStack = null;

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               if (this.repairMaterial.matches(stack)) {
                  repairMaterialCount++;
               } else if (this.inItem.itemID == stack.itemID) {
                  inItemStack = stack.copy();
               }
            }
         }
      }

      assert inItemStack != null;

      for (int i = 0; i < repairMaterialCount; i++) {
         if (inItemStack.getMetadata() <= inItemStack.getMaxDamage()) {
            inItemStack.setMetadata(inItemStack.getMetadata() - inItemStack.getMaxDamage() / 8);
         }
      }

      if (inItemStack.getMetadata() < 0) {
         inItemStack.setMetadata(0);
      }

      return inItemStack;
   }

   @Override
   public ItemStack[] onCraftResult(ContainerCrafting containerCrafting) {
      ItemStack[] returnStack = new ItemStack[9];

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               ItemStack stack2;
               if (stack.stackSize - 1 == 0) {
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
      return new RecipeEntryRepairableJsonAdapter();
   }
}
