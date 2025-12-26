package net.minecraft.core.player.inventory.slot;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.jetbrains.annotations.Nullable;

public class Slot {
   protected final int slot;
   protected final Container container;
   public int index;
   public int x;
   public int y;
   protected boolean discovered;

   public Slot(Container container, int index, int x, int y) {
      this.container = container;
      this.slot = index;
      this.x = x;
      this.y = y;
      this.discovered = true;
   }

   public void sortSlotInventory() {
      if (this.container != null) {
         this.container.sortContainer();
      }
   }

   @Nullable
   public ItemStack remove(int i) {
      return this.container.locked(this.slot) ? null : this.container.removeItem(this.slot, i);
   }

   @Nullable
   public String getItemIcon() {
      return null;
   }

   public boolean hasItem() {
      return this.getItemStack() != null;
   }

   public int getMaxStackSize() {
      return this.container.getMaxStackSize();
   }

   @Nullable
   public ItemStack getItemStack() {
      return this.container.getItem(this.slot);
   }

   public boolean isAt(Container container, int i) {
      return container == this.container && i == this.slot;
   }

   public boolean mayPlace(ItemStack itemstack) {
      return this.container != null && !this.isLocked();
   }

   public void onTake(ItemStack itemstack) {
      this.setChanged();
   }

   public void setChanged() {
      this.container.setChanged();
   }

   public Container getContainer() {
      return this.container;
   }

   public void set(@Nullable ItemStack itemstack) {
      this.container.setItem(this.slot, itemstack);
      this.setChanged();
      if (this.container instanceof ContainerInventory) {
         ContainerInventory inventory = (ContainerInventory)this.container;
         if (itemstack != null) {
            inventory.player.addStat(itemstack.getItem().getStat("stat_picked_up"), 1);
         }
      }
   }

   public boolean enableDragAndPickup() {
      return !this.isLocked();
   }

   public boolean allowItemInteraction() {
      return !this.isLocked();
   }

   public boolean getIsDiscovered(Player player) {
      return this.discovered;
   }

   public boolean isLocked() {
      return this.container != null && this.container.locked(this.slot);
   }
}
