package net.minecraft.core.world.type.overworld;

import net.minecraft.core.world.World;
import net.minecraft.core.world.type.WorldType;

public class WorldTypeOverworldParadise extends WorldTypeOverworld {
   public WorldTypeOverworldParadise(WorldType.Properties properties) {
      super(properties);
   }

   @Override
   public float getCelestialAngle(World world, long tick, float partialTick) {
      return 0.0F;
   }
}
