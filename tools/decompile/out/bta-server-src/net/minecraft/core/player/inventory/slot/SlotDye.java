package net.minecraft.core.player.inventory.slot;

import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.Container;

public class SlotDye extends Slot {
   public SlotDye(Container inventory, int id, int x, int y) {
      super(inventory, id, x, y);
   }

   @Override
   public boolean mayPlace(ItemStack itemstack) {
      return itemstack != null && itemstack.itemID == Items.DYE.id;
   }

   @Override
   public int getMaxStackSize() {
      return 1;
   }

   @Override
   public boolean allowItemInteraction() {
      return false;
   }
}
