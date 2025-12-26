package net.minecraft.core.world.generate.chunk.perlin.overworld.woods;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.CavesLargeFeature;
import net.minecraft.core.world.generate.LargeFeature;
import net.minecraft.core.world.generate.chunk.perlin.ChunkGeneratorPerlin;
import net.minecraft.core.world.generate.chunk.perlin.overworld.SurfaceGeneratorOverworld;
import net.minecraft.core.world.generate.chunk.perlin.overworld.TerrainGeneratorOverworld;

public class ChunkGeneratorOverworldWoods extends ChunkGeneratorPerlin {
   public ChunkGeneratorOverworldWoods(World world) {
      super(
         world,
         new ChunkDecoratorOverworldWoods(world),
         new TerrainGeneratorOverworld(world),
         new SurfaceGeneratorOverworld(world),
         new LargeFeature[]{new CavesLargeFeature()}
      );
   }
}
