package net.minecraft.core.world.generate.chunk.perlin.paradise;

import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.perlin.DensityGenerator;
import net.minecraft.core.world.noise.PerlinNoise;

public class DensityGeneratorParadise implements DensityGenerator {
   private final World world;
   private final PerlinNoise minLimitNoise;
   private final PerlinNoise maxLimitNoise;
   private final PerlinNoise mainNoise;

   public DensityGeneratorParadise(World world) {
      this.world = world;
      this.minLimitNoise = new PerlinNoise(world.getRandomSeed(), 16, 0);
      this.maxLimitNoise = new PerlinNoise(world.getRandomSeed(), 16, 16);
      this.mainNoise = new PerlinNoise(world.getRandomSeed(), 8, 32);
   }

   @Override
   public double[] generateDensityMap(Chunk chunk) {
      int terrainHeight = this.world.getWorldType().getMaxY() + 1 - this.world.getWorldType().getMinY();
      int xSize = 5;
      int ySize = terrainHeight / 8 + 1;
      int zSize = 5;
      int x = chunk.xPosition * 4;
      int y = 0;
      int z = chunk.zPosition * 4;
      double[] densityMapArray = new double[xSize * ySize * zSize];
      double mainNoiseScaleX = 80.0;
      double mainNoiseScaleY = 120.0;
      double mainNoiseScaleZ = 80.0;
      double coordScale = 171.103;
      double heightScale = 342.206;
      double upperLimitScale = 512.0;
      double lowerLimitScale = 512.0;
      double[] mainNoiseArray = this.mainNoise.get(null, x, y, z, xSize, ySize, zSize, 2.1387875000000003, 2.851716666666667, 2.1387875000000003);
      double[] minLimitArray = this.minLimitNoise.get(null, x, y, z, xSize, ySize, zSize, 171.103, 342.206, 171.103);
      double[] maxLimitArray = this.maxLimitNoise.get(null, x, y, z, xSize, ySize, zSize, 171.103, 342.206, 171.103);
      int mainIndex = 0;
      int xSizeScale = 16 / xSize;

      for (int dx = 0; dx < xSize; dx++) {
         int ix = dx * xSizeScale + xSizeScale / 2;

         for (int dz = 0; dz < zSize; dz++) {
            int iz = dz * xSizeScale + xSizeScale / 2;
            double temperature = chunk.temperature[ix * 16 + iz];
            double humidity = chunk.humidity[ix * 16 + iz] * temperature;
            humidity = 1.0 - humidity;
            humidity *= humidity;
            humidity *= humidity;
            humidity = 1.0 - humidity;

            for (int dy = 0; dy < ySize; dy++) {
               double density = 0.0;
               double minDensity = minLimitArray[mainIndex] / 512.0;
               double maxDensity = maxLimitArray[mainIndex] / 512.0;
               double mainDensity = (mainNoiseArray[mainIndex] / 10.0 + 1.0) / 2.0;
               if (mainDensity < 0.0) {
                  density = minDensity;
               } else if (mainDensity > 1.0) {
                  density = maxDensity;
               } else {
                  density = minDensity + (maxDensity - minDensity) * mainDensity;
               }

               density -= 8.0;
               int upperLowerLimit = 8;
               if (dy > ySize - upperLowerLimit) {
                  double densityMod = (dy - (ySize - upperLowerLimit)) / (upperLowerLimit - 1.0F);
                  density = density * (1.0 - densityMod) + -30.0 * densityMod;
               }

               int var55 = 2;
               if (dy < var55) {
                  double densityMod = (var55 - dy) / (var55 - 1.0F);
                  density = density * (1.0 - densityMod) + -30.0 * densityMod;
               }

               densityMapArray[mainIndex] = density;
               mainIndex++;
            }
         }
      }

      return densityMapArray;
   }
}
