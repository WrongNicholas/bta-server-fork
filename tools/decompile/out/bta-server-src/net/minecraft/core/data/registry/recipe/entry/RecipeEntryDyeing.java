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
import net.minecraft.core.data.registry.recipe.adapter.RecipeEntryDyeingJsonAdapter;
import net.minecraft.core.data.registry.recipe.adapter.RecipeJsonAdapter;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.ContainerCrafting;
import net.minecraft.core.util.helper.DyeColor;

public class RecipeEntryDyeing extends RecipeEntryCraftingDynamic implements HasJsonAdapter {
   public RecipeSymbol inputSymbol;
   public ItemStack output;
   public boolean useUpperMeta;
   public boolean useItemMeta;

   public RecipeEntryDyeing() {
   }

   public RecipeEntryDyeing(RecipeSymbol input, ItemStack output, boolean useUpperMeta, boolean useItemMeta) {
      this.inputSymbol = input;
      this.output = output;
      this.useUpperMeta = useUpperMeta;
      this.useItemMeta = useItemMeta;
   }

   @Override
   public boolean matches(ContainerCrafting containerCrafting) {
      boolean hasDye = false;
      boolean hasItem = false;

      for (int i = 0; i < 9; i++) {
         ItemStack stack = containerCrafting.getItem(i);
         if (stack != null && stack.getItem() == Items.DYE) {
            if (hasDye) {
               return false;
            }

            hasDye = true;
         } else {
            if (stack != null && !this.inputSymbol.matches(stack)) {
               return false;
            }

            if (this.inputSymbol.matches(stack)) {
               hasItem = true;
            }
         }
      }

      return hasDye && hasItem;
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
         for (int i = 0; i < 16; i++) {
            ItemStack output = new ItemStack(this.output.itemID, this.output.stackSize, this.getOutputMetaFromInputMeta(i));
            if (query.strict && output.getDisplayName().equalsIgnoreCase(query.query.getRight())) {
               return true;
            }

            if (!query.strict && output.getDisplayName().toLowerCase().contains(query.query.getRight().toLowerCase())) {
               return true;
            }
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
      int dyeMeta = 0;
      int items = 0;

      for (int i = 0; i < containerCrafting.getContainerSize(); i++) {
         ItemStack itemstack1 = containerCrafting.getItem(i);
         if (itemstack1 != null) {
            if (itemstack1.getItem() == Items.DYE) {
               dyeMeta = itemstack1.getMetadata();
            } else {
               items++;
            }
         }
      }

      return new ItemStack(this.output.getItem(), items, this.getOutputMetaFromInputMeta(dyeMeta));
   }

   public int getOutputMetaFromInputMeta(int meta) {
      DyeColor dyeColor = DyeColor.colorFromItemMeta(meta);
      if (this.useItemMeta) {
         return this.useUpperMeta ? dyeColor.itemMeta << 4 : dyeColor.itemMeta;
      } else {
         return this.useUpperMeta ? dyeColor.blockMeta << 4 : dyeColor.blockMeta;
      }
   }

   @Override
   public int getRecipeSize() {
      return 2;
   }

   @Override
   public ItemStack[] onCraftResult(ContainerCrafting containerCrafting) {
      ItemStack[] returnStack = new ItemStack[9];

      for (int i = 0; i < containerCrafting.getContainerSize(); i++) {
         containerCrafting.removeItem(i, 1);
      }

      return returnStack;
   }

   @Override
   public RecipeJsonAdapter<?> getAdapter() {
      return new RecipeEntryDyeingJsonAdapter();
   }
}
