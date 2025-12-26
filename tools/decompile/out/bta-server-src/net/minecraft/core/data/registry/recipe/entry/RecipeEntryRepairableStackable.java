package net.minecraft.core.data.registry.recipe.entry;

import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerCrafting;

public class RecipeEntryRepairableStackable extends RecipeEntryCraftingDynamic {
   public Item inItem;
   public ItemStack materialStack;
   public ItemStack inItemStack;
   int originalDamage;
   int totalStackCount;

   public RecipeEntryRepairableStackable(Item inItem, ItemStack materialStack) {
      this.inItem = inItem;
      this.materialStack = materialStack;
      this.inItemStack = new ItemStack(inItem);
      this.totalStackCount = 0;
   }

   @Override
   public boolean matches(ContainerCrafting containerCrafting) {
      int materialStackCount = 0;
      int inItemCount = 0;

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               if (stack.itemID == this.materialStack.itemID) {
                  materialStackCount++;
               } else {
                  if (stack.itemID != this.inItem.id || stack.getMetadata() <= 0) {
                     return false;
                  }

                  inItemCount++;
               }
            }
         }
      }

      return materialStackCount == 0 ? false : inItemCount == 1;
   }

   @Override
   public boolean matchesQuery(SearchQuery query) {
      return false;
   }

   @Override
   public ItemStack getCraftingResult(ContainerCrafting containerCrafting) {
      ItemStack inItemStack = null;
      this.totalStackCount = 0;

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               if (stack.itemID == this.materialStack.itemID) {
                  this.totalStackCount = this.totalStackCount + stack.stackSize;
               } else if (stack.itemID == this.inItem.id) {
                  inItemStack = stack.copy();
                  this.originalDamage = inItemStack.getMetadata();
               }
            }
         }
      }

      if (inItemStack.getMetadata() - this.totalStackCount > 0) {
         inItemStack.setMetadata(inItemStack.getMetadata() - this.totalStackCount);
      } else {
         inItemStack.setMetadata(0);
      }

      if (inItemStack.getMetadata() < 0) {
         inItemStack.setMetadata(0);
      }

      return inItemStack;
   }

   @Override
   public ItemStack[] onCraftResult(ContainerCrafting containerCrafting) {
      int repairCost = this.originalDamage - this.inItemStack.getMetadata();
      ItemStack[] returnStack = new ItemStack[9];

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            ItemStack stack = containerCrafting.getItemStackAt(x, y);
            if (stack != null) {
               if (stack.getItem() == this.inItem) {
                  containerCrafting.setSlotContentsAt(x, y, null);
               }

               if (stack.getItem() == this.materialStack.getItem()) {
                  if (repairCost <= 0) {
                     return returnStack;
                  }

                  ItemStack replacementStack;
                  if (stack.stackSize - repairCost <= 0) {
                     replacementStack = null;
                  } else {
                     replacementStack = new ItemStack(stack.getItem(), stack.stackSize - repairCost);
                  }

                  repairCost -= stack.stackSize;
                  containerCrafting.setSlotContentsAt(x, y, replacementStack);
               }
            }
         }
      }

      return returnStack;
   }

   @Override
   public int getRecipeSize() {
      return 2;
   }
}
