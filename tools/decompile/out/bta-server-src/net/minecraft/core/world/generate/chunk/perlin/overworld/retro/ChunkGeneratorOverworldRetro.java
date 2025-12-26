package net.minecraft.core.world.generate.chunk.perlin.overworld.retro;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.CavesLargeFeature;
import net.minecraft.core.world.generate.LargeFeature;
import net.minecraft.core.world.generate.chunk.perlin.ChunkGeneratorPerlin;

public class ChunkGeneratorOverworldRetro extends ChunkGeneratorPerlin {
   public ChunkGeneratorOverworldRetro(World world) {
      super(
         world,
         new ChunkDecoratorOverworldRetro(world),
         new TerrainGeneratorOverworldRetro(world),
         new SurfaceGeneratorOverworldRetro(world),
         new LargeFeature[]{new CavesLargeFeature()}
      );
   }
}
