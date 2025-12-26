package net.minecraft.core.world.generate.chunk.perlin.paradise;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.CavesLargeFeature;
import net.minecraft.core.world.generate.LargeFeature;
import net.minecraft.core.world.generate.chunk.perlin.ChunkGeneratorPerlin;
import net.minecraft.core.world.generate.chunk.perlin.overworld.ChunkDecoratorOverworld;
import net.minecraft.core.world.generate.chunk.perlin.overworld.SurfaceGeneratorOverworld;

public class ChunkGeneratorParadise extends ChunkGeneratorPerlin {
   public ChunkGeneratorParadise(World world) {
      super(
         world,
         new ChunkDecoratorOverworld(world),
         new TerrainGeneratorParadise(world),
         new SurfaceGeneratorOverworld(world),
         new LargeFeature[]{new CavesLargeFeature()}
      );
   }
}
