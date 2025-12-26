package net.minecraft.core.world.generate.chunk.perlin;

import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

public interface DensityGenerator {
   static double readDensity(World world, double[] densities, int x, int y, int z) {
      int sizeX = 5;
      int sizeY = world.getHeightBlocks() / 8 + 1;
      int sizeZ = 5;
      return densities[x * sizeY * sizeZ + z * sizeY + y];
   }

   double[] generateDensityMap(Chunk var1);
}
