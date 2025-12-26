package net.minecraft.core.player.inventory.container;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface Container {
   int getContainerSize();

   @Nullable
   ItemStack getItem(int var1);

   @Nullable
   ItemStack removeItem(int var1, int var2);

   void setItem(int var1, @Nullable ItemStack var2);

   String getNameTranslationKey();

   int getMaxStackSize();

   void setChanged();

   boolean stillValid(Player var1);

   void sortContainer();

   default boolean locked(int index) {
      return false;
   }
}
