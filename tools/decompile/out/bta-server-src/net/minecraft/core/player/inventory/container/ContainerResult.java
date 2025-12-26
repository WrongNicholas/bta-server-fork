package net.minecraft.core.player.inventory.container;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ContainerResult implements Container {
   private ItemStack[] items = new ItemStack[1];

   @Override
   public int getContainerSize() {
      return 1;
   }

   @Nullable
   @Override
   public ItemStack getItem(int index) {
      return this.items[index];
   }

   @Override
   public String getNameTranslationKey() {
      return "container.result.name";
   }

   @Nullable
   @Override
   public ItemStack removeItem(int index, int takeAmount) {
      if (this.items[index] != null) {
         ItemStack itemstack = this.items[index];
         this.items[index] = null;
         return itemstack;
      } else {
         return null;
      }
   }

   @Override
   public void setItem(int index, @Nullable ItemStack itemstack) {
      this.items[index] = itemstack;
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
   }
}
