package net.minecraft.core.world.type.overworld;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.perlin.overworld.woods.ChunkGeneratorOverworldWoods;
import net.minecraft.core.world.type.WorldType;

public class WorldTypeOverworldWoods extends WorldTypeOverworld {
   public WorldTypeOverworldWoods(WorldType.Properties properties) {
      super(properties);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorOverworldWoods(world);
   }
}
