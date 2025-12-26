package net.minecraft.core.world.generate.chunk.perlin;

import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;

public interface SurfaceGenerator {
   void generateSurface(Chunk var1, ChunkGeneratorResult var2);
}
