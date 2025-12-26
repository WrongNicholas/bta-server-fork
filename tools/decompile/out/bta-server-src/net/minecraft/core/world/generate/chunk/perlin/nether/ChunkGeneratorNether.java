package net.minecraft.core.world.generate.chunk.perlin.nether;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.CavesLargeFeature;
import net.minecraft.core.world.generate.LargeFeature;
import net.minecraft.core.world.generate.WarrensCavesLargeFeature;
import net.minecraft.core.world.generate.chunk.perlin.ChunkGeneratorPerlin;

public class ChunkGeneratorNether extends ChunkGeneratorPerlin {
   public ChunkGeneratorNether(World world) {
      super(
         world,
         new ChunkDecoratorNether(world),
         new TerrainGeneratorNether(world),
         new SurfaceGeneratorNether(world),
         new LargeFeature[]{new CavesLargeFeature(128, 256), new WarrensCavesLargeFeature(0, 128)}
      );
   }
}
