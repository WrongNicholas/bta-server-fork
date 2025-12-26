package net.minecraft.core.block;

import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;

public interface IPaintable {
   default boolean canBePainted() {
      return true;
   }

   void setColor(World var1, int var2, int var3, int var4, DyeColor var5);
}
