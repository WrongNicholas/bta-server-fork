package net.minecraft.core.item;

import java.util.Random;
import net.minecraft.core.world.World;

public interface IDispensable {
   void onDispensed(ItemStack var1, World var2, double var3, double var5, double var7, int var9, int var10, int var11, Random var12);

   default boolean isRemovedOnDispense() {
      return true;
   }
}
