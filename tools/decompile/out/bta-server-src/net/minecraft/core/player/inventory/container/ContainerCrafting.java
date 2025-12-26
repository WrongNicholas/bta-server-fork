package net.minecraft.core.player.inventory.container;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import org.jetbrains.annotations.Nullable;

public class ContainerCrafting implements Container {
   private ItemStack[] items;
   private int width;
   private MenuAbstract menu;

   public ContainerCrafting(MenuAbstract container, int width, int n) {
      int k = width * n;
      this.items = new ItemStack[k];
      this.menu = container;
      this.width = width;
   }

   @Override
   public int getContainerSize() {
      return this.items.length;
   }

   @Nullable
   @Override
   public ItemStack getItem(int index) {
      return index >= this.getContainerSize() ? null : this.items[index];
   }

   public ItemStack getItemStackAt(int i, int j) {
      if (i >= 0 && i < this.width) {
         int k = i + j * this.width;
         return this.getItem(k);
      } else {
         return null;
      }
   }

   public void setSlotContentsAt(int i, int j, ItemStack itemStack) {
      if (i >= 0 && i < this.width) {
         int k = i + j * this.width;
         this.setItem(k, itemStack);
      }
   }

   @Override
   public String getNameTranslationKey() {
      return "container.crafting.name";
   }

   @Nullable
   @Override
   public ItemStack removeItem(int index, int takeAmount) {
      if (this.items[index] != null) {
         if (this.items[index].stackSize <= takeAmount) {
            ItemStack itemstack = this.items[index];
            this.items[index] = null;
            this.menu.slotsChanged(this);
            return itemstack;
         } else {
            ItemStack itemstack1 = this.items[index].splitStack(takeAmount);
            if (this.items[index].stackSize <= 0) {
               this.items[index] = null;
            }

            this.menu.slotsChanged(this);
            return itemstack1;
         }
      } else {
         return null;
      }
   }

   @Override
   public void setItem(int index, @Nullable ItemStack itemstack) {
      this.items[index] = itemstack;
      this.menu.slotsChanged(this);
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   @Override
   public void setChanged() {
      this.menu.slotsChanged(this);
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return true;
   }

   @Override
   public void sortContainer() {
   }
}
