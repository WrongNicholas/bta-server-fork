package net.minecraft.core.world.generate.chunk.perlin.nether;

import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.perlin.DensityGenerator;
import net.minecraft.core.world.noise.PerlinNoise;

public class DensityGeneratorNether implements DensityGenerator {
   private final World world;
   private final PerlinNoise minLimitNoise;
   private final PerlinNoise maxLimitNoise;
   private final PerlinNoise mainNoise;

   public DensityGeneratorNether(World world) {
      this.world = world;
      this.minLimitNoise = new PerlinNoise(world.getRandomSeed(), 16, 0);
      this.maxLimitNoise = new PerlinNoise(world.getRandomSeed(), 16, 16);
      this.mainNoise = new PerlinNoise(world.getRandomSeed(), 8, 32);
   }

   @Override
   public double[] generateDensityMap(Chunk chunk) {
      int terrainHeight = this.world.getWorldType().getMaxY() + 1 - this.world.getWorldType().getMinY();
      int halfTerrainHeight = terrainHeight / 2;
      int xSize = 5;
      int ySize = terrainHeight / 8 + 1;
      int halfYSize = halfTerrainHeight / 8 + 1;
      int zSize = 5;
      int x = chunk.xPosition * 4;
      int y = 0;
      int z = chunk.zPosition * 4;
      double[] densityMapArray = new double[xSize * ySize * zSize];
      double mainNoiseScaleX = 80.0;
      double mainNoiseScaleY = 60.0;
      double mainNoiseScaleZ = 80.0;
      double coordScale = 684.412;
      double heightScale = 2053.236;
      double upperLimitScale = 512.0;
      double lowerLimitScale = 512.0;
      double[] mainNoiseArray = this.mainNoise.get(null, x, y, z, xSize, halfYSize, zSize, 8.555150000000001, 34.2206, 8.555150000000001);
      double[] minLimitArray = this.minLimitNoise.get(null, x, y, z, xSize, halfYSize, zSize, 684.412, 2053.236, 684.412);
      double[] maxLimitArray = this.maxLimitNoise.get(null, x, y, z, xSize, halfYSize, zSize, 684.412, 2053.236, 684.412);
      double[] densityOffsetArray = new double[halfYSize];

      for (int dy = 0; dy < halfYSize; dy++) {
         densityOffsetArray[dy] = Math.cos(dy * Math.PI * 6.0 / halfYSize) * 2.0;
         double densityOffset = dy;
         if (dy > halfYSize / 2) {
            densityOffset = halfYSize - 1 - dy;
         }

         if (densityOffset < 4.0) {
            densityOffset = 4.0 - densityOffset;
            densityOffsetArray[dy] -= densityOffset * densityOffset * densityOffset * 10.0;
         }
      }

      for (int dx = 0; dx < xSize; dx++) {
         for (int dz = 0; dz < zSize; dz++) {
            for (int dy = 0; dy < halfYSize; dy++) {
               int halfIndex = dx * halfYSize * zSize + dz * halfYSize + dy;
               int offsetIndex = dx * ySize * zSize + dz * ySize + (dy + halfYSize - 1);
               double densityOffsetx = densityOffsetArray[dy];
               double minDensity = minLimitArray[halfIndex] / 512.0;
               double maxDensity = maxLimitArray[halfIndex] / 512.0;
               double mainDensity = (mainNoiseArray[halfIndex] / 10.0 + 1.0) / 2.0;
               double density;
               if (mainDensity < 0.0) {
                  density = minDensity;
               } else if (mainDensity > 1.0) {
                  density = maxDensity;
               } else {
                  density = minDensity + (maxDensity - minDensity) * mainDensity;
               }

               density -= densityOffsetx;
               if (dy > halfYSize - 4) {
                  double d11 = (dy - (halfYSize - 4)) / 3.0F;
                  density = density * (1.0 - d11) + -10.0 * d11;
               }

               densityMapArray[offsetIndex] = density;
            }
         }
      }

      return densityMapArray;
   }
}
