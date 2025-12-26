package net.minecraft.core.world.type.overworld;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.type.WorldType;

public class WorldTypeOverworldInland extends WorldTypeOverworld {
   public WorldTypeOverworldInland(WorldType.Properties properties) {
      super(properties);
   }

   @Override
   public boolean isValidSpawn(World world, int x, int y, int z) {
      return world.getBlockId(x, y, z) == Blocks.GRASS.id();
   }
}
