package net.minecraft.core.data.registry.recipe.entry;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.HasJsonAdapter;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.data.registry.recipe.adapter.RecipeEntryScrapJsonAdapter;
import net.minecraft.core.data.registry.recipe.adapter.RecipeJsonAdapter;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerCrafting;

public class RecipeEntryScrap extends RecipeEntryCraftingDynamic implements HasJsonAdapter {
   public Item itemToScrap;
   public Item scrapYield;
   public ItemStack scrapYieldStack;
   public int maxYield;

   public RecipeEntryScrap() {
   }

   public RecipeEntryScrap(Item itemToScrap, Item scrapYield, int maxYield) {
      this.itemToScrap = itemToScrap;
      this.scrapYield = scrapYield;
      this.maxYield = maxYield;
      this.scrapYieldStack = new ItemStack(scrapYield);
   }

   @Override
   public boolean matches(ContainerCrafting containerCrafting) {
      int itemToScrapCount = 0;

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               if (stack.itemID != this.itemToScrap.id) {
                  return false;
               }

               itemToScrapCount++;
            }
         }
      }

      return itemToScrapCount == 1;
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
      ItemStack output = this.scrapYield.getDefaultStack();
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

         return groupStacks.contains(this.getOutput());
      }

      return false;
   }

   public boolean matchesUsage(SearchQuery query) {
      RecipeSymbol[] symbols = new RecipeSymbol[]{new RecipeSymbol(this.itemToScrap.getDefaultStack())};

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
      float itemToScrapDamage = 0.0F;
      float itemMaxDamage = 0.0F;
      ItemStack scrapYieldStack = null;

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null && stack.itemID == this.itemToScrap.id) {
               itemToScrapDamage = stack.getMetadata();
               itemMaxDamage = stack.getMaxDamage();
            }
         }
      }

      float scrapYieldResult = (itemMaxDamage - itemToScrapDamage) / itemMaxDamage * (this.maxYield - 1) + 1.0F;
      return scrapYieldResult >= 1.0F ? new ItemStack(this.scrapYield, (int)scrapYieldResult) : null;
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
      return new RecipeEntryScrapJsonAdapter();
   }
}
