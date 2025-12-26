package net.minecraft.core.world.generate.chunk.perlin.overworld.retro;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.chunk.perlin.overworld.TerrainGeneratorOverworld;

public class TerrainGeneratorOverworldRetro extends TerrainGeneratorOverworld {
   public TerrainGeneratorOverworldRetro(World world) {
      super(world, new DensityGeneratorOverworldRetro(world));
   }
}
