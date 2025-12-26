package net.minecraft.core.world.wind;

import net.minecraft.core.world.World;

public abstract class WindProvider {
   public abstract float getWindDirection(World var1, float var2, float var3, float var4);

   public abstract float getWindIntensity(World var1, float var2, float var3, float var4);
}
