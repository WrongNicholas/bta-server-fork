package net.minecraft.core.player.inventory.container;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.InventorySorter;
import org.jetbrains.annotations.Nullable;

public class ContainerCompound implements Container {
   private String name;
   private Container c1;
   private Container c2;

   public ContainerCompound(String s, Container c1, Container c2) {
      this.name = s;
      this.c1 = c1;
      this.c2 = c2;
   }

   @Override
   public int getContainerSize() {
      return this.c1.getContainerSize() + this.c2.getContainerSize();
   }

   @Override
   public String getNameTranslationKey() {
      return this.name;
   }

   @Nullable
   @Override
   public ItemStack getItem(int index) {
      return index >= this.c1.getContainerSize() ? this.c2.getItem(index - this.c1.getContainerSize()) : this.c1.getItem(index);
   }

   @Nullable
   @Override
   public ItemStack removeItem(int index, int takeAmount) {
      return index >= this.c1.getContainerSize() ? this.c2.removeItem(index - this.c1.getContainerSize(), takeAmount) : this.c1.removeItem(index, takeAmount);
   }

   @Override
   public void setItem(int index, @Nullable ItemStack itemstack) {
      if (index >= this.c1.getContainerSize()) {
         this.c2.setItem(index - this.c1.getContainerSize(), itemstack);
      } else {
         this.c1.setItem(index, itemstack);
      }
   }

   @Override
   public int getMaxStackSize() {
      return this.c1.getMaxStackSize();
   }

   @Override
   public void setChanged() {
      this.c1.setChanged();
      this.c2.setChanged();
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return this.c1.stillValid(entityplayer) && this.c2.stillValid(entityplayer);
   }

   @Override
   public void sortContainer() {
      ItemStack[] itemStacks = new ItemStack[this.getContainerSize()];

      for (int i = 0; i < itemStacks.length; i++) {
         itemStacks[i] = this.getItem(i);
      }

      InventorySorter.sortInventory(itemStacks);

      for (int i = 0; i < itemStacks.length; i++) {
         this.setItem(i, itemStacks[i]);
      }
   }
}
