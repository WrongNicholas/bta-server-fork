package net.minecraft.core.player.inventory.slot;

import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SlotCreative extends Slot {
   public ItemStack item;

   public SlotCreative(int index, int x, int y, ItemStack item) {
      super(null, index, x, y);
      this.item = item;
   }

   @Nullable
   @Override
   public ItemStack remove(int i) {
      return this.item == null ? null : this.item.copy();
   }

   @Override
   public boolean hasItem() {
      return this.item != null;
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   @Nullable
   @Override
   public ItemStack getItemStack() {
      return this.item == null ? null : this.item.copy();
   }

   @Override
   public void onTake(ItemStack itemstack) {
   }

   @Override
   public void setChanged() {
   }

   @Override
   public void set(@Nullable ItemStack itemstack) {
   }

   @Override
   public boolean enableDragAndPickup() {
      return false;
   }

   @Override
   public boolean allowItemInteraction() {
      return false;
   }
}
