package net.minecraft.core.player.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.item.ItemStack;

public abstract class InventorySorter {
   public static void sortInventory(ItemStack[] inventory) {
      sortInventory(inventory, 0, inventory.length - 1);
   }

   public static void sortInventory(ItemStack[] inventory, int startIndex, int endIndex) {
      List<ItemStack> items = new ArrayList<>();

      for (int i = startIndex; i <= endIndex; i++) {
         if (inventory[i] != null) {
            items.add(inventory[i]);
            inventory[i] = null;
         }
      }

      items.sort((a, b) -> {
         int aId = a.itemID;
         int bId = b.itemID;
         if (aId == bId) {
            return 0;
         } else {
            return aId < bId ? -1 : 1;
         }
      });

      for (int ix = 0; ix < items.size(); ix++) {
         ItemStack stack = items.get(ix);
         if (stack.stackSize < stack.getMaxStackSize()) {
            int remaining = stack.getMaxStackSize() - stack.stackSize;

            for (int j = ix + 1; j < items.size(); j++) {
               ItemStack nextStack = items.get(j);
               if (stack.canStackWith(nextStack)) {
                  ix++;
                  if (remaining > 0) {
                     if (nextStack.stackSize <= remaining) {
                        remaining -= nextStack.stackSize;
                        items.remove(nextStack);
                        ix--;
                        j--;
                     } else {
                        nextStack.stackSize -= remaining;
                        remaining = 0;
                     }
                  }
               }
            }

            stack.stackSize = stack.getMaxStackSize() - remaining;
         }
      }

      items.sort((a, b) -> {
         int aId = a.itemID;
         int bId = b.itemID;
         if (aId == bId) {
            return a.getMetadata() == b.getMetadata() ? b.stackSize - a.stackSize : a.getMetadata() - b.getMetadata();
         } else {
            return aId < bId ? -1 : 1;
         }
      });

      for (int ixx = 0; ixx < items.size(); ixx++) {
         inventory[startIndex + ixx] = items.get(ixx);
      }
   }
}
