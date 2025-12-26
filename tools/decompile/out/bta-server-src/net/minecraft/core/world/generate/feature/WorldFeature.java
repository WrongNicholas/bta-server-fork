package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.world.World;

public abstract class WorldFeature {
   public abstract boolean place(World var1, Random var2, int var3, int var4, int var5);

   public void init(double d, double d1, double d2) {
   }
}
