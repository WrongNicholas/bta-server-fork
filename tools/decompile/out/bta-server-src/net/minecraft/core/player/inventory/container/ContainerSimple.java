package net.minecraft.core.player.inventory.container;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.InventorySorter;
import org.jetbrains.annotations.Nullable;

public class ContainerSimple implements Container {
   private String name;
   private int size;
   private ItemStack[] items;

   public ContainerSimple(String name, int size) {
      this.name = name;
      this.size = size;
      this.items = new ItemStack[size];
   }

   @Nullable
   @Override
   public ItemStack getItem(int index) {
      return this.items[index];
   }

   @Nullable
   @Override
   public ItemStack removeItem(int index, int takeAmount) {
      if (this.items[index] != null) {
         if (this.items[index].stackSize <= takeAmount) {
            ItemStack itemstack = this.items[index];
            this.items[index] = null;
            this.setChanged();
            return itemstack;
         } else {
            ItemStack itemstack1 = this.items[index].splitStack(takeAmount);
            if (this.items[index].stackSize <= 0) {
               this.items[index] = null;
            }

            this.setChanged();
            return itemstack1;
         }
      } else {
         return null;
      }
   }

   @Override
   public void setItem(int index, @Nullable ItemStack itemstack) {
      this.items[index] = itemstack;
      if (itemstack != null && itemstack.stackSize > this.getMaxStackSize()) {
         itemstack.stackSize = this.getMaxStackSize();
      }

      this.setChanged();
   }

   @Override
   public int getContainerSize() {
      return this.size;
   }

   @Override
   public String getNameTranslationKey() {
      return this.name;
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   @Override
   public void setChanged() {
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return true;
   }

   @Override
   public void sortContainer() {
      InventorySorter.sortInventory(this.items);
   }
}
