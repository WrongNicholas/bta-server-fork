package net.minecraft.core.world.generate.chunk.perlin;

import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;

public interface TerrainGenerator {
   ChunkGeneratorResult generateTerrain(Chunk var1, double[] var2);

   DensityGenerator getDensityGenerator();
}
