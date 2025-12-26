package net.minecraft.core.world.generate.chunk.perlin.overworld;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.CavesLargeFeature;
import net.minecraft.core.world.generate.LargeFeature;
import net.minecraft.core.world.generate.chunk.perlin.ChunkGeneratorPerlin;

public class ChunkGeneratorOverworld extends ChunkGeneratorPerlin {
   public ChunkGeneratorOverworld(World world) {
      super(
         world,
         new ChunkDecoratorOverworld(world),
         new TerrainGeneratorOverworld(world),
         new SurfaceGeneratorOverworld(world),
         new LargeFeature[]{new CavesLargeFeature()}
      );
   }
}
