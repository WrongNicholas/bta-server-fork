package net.minecraft.core.world.generate.chunk.perlin.overworld.floating;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.CavesLargeFeature;
import net.minecraft.core.world.generate.LargeFeature;
import net.minecraft.core.world.generate.chunk.perlin.ChunkGeneratorPerlin;
import net.minecraft.core.world.generate.chunk.perlin.overworld.ChunkDecoratorOverworld;
import net.minecraft.core.world.generate.chunk.perlin.overworld.SurfaceGeneratorOverworld;
import net.minecraft.core.world.generate.chunk.perlin.paradise.TerrainGeneratorParadise;

public class ChunkGeneratorOverworldFloating extends ChunkGeneratorPerlin {
   public ChunkGeneratorOverworldFloating(World world) {
      super(
         world,
         new ChunkDecoratorOverworld(world),
         new TerrainGeneratorParadise(world),
         new SurfaceGeneratorOverworld(world),
         new LargeFeature[]{new CavesLargeFeature()}
      );
   }
}
