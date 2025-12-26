package net.minecraft.core.world.generate.chunk.perlin.overworld;

import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.perlin.DensityGenerator;
import net.minecraft.core.world.noise.BasePerlinNoise;
import net.minecraft.core.world.noise.PerlinNoise;
import net.minecraft.core.world.type.WorldTypes;

public class DensityGeneratorOverworld implements DensityGenerator {
   private final World world;
   private final BasePerlinNoise<?> minLimitNoise;
   private final BasePerlinNoise<?> maxLimitNoise;
   private final BasePerlinNoise<?> mainNoise;
   private final BasePerlinNoise<?> scaleNoise;
   private final BasePerlinNoise<?> depthNoise;
   private final boolean generateRivers;
   private final BasePerlinNoise<?> smoothingRiverNoise;
   private final BasePerlinNoise<?> carvingRiverNoise;

   protected DensityGeneratorOverworld(
      World world,
      BasePerlinNoise<?> minLimitNoise,
      BasePerlinNoise<?> maxLimitNoise,
      BasePerlinNoise<?> mainNoise,
      BasePerlinNoise<?> scaleNoise,
      BasePerlinNoise<?> depthNoise,
      boolean generateRivers
   ) {
      this.world = world;
      this.minLimitNoise = minLimitNoise;
      this.maxLimitNoise = maxLimitNoise;
      this.mainNoise = mainNoise;
      this.scaleNoise = scaleNoise;
      this.depthNoise = depthNoise;
      this.generateRivers = generateRivers;
      this.smoothingRiverNoise = new PerlinNoise(world.getRandomSeed(), 1);
      this.carvingRiverNoise = new PerlinNoise(world.getRandomSeed() + 1L, 1);
   }

   public DensityGeneratorOverworld(World world) {
      this(
         world,
         new PerlinNoise(world.getRandomSeed(), 16, 0),
         new PerlinNoise(world.getRandomSeed(), 16, 16),
         new PerlinNoise(world.getRandomSeed(), 8, 32),
         new PerlinNoise(world.getRandomSeed(), 10, 48),
         new PerlinNoise(world.getRandomSeed(), 16, 58),
         true
      );
   }

   private void modifyDensityMapSmoothingRivers(int chunkX, int chunkZ, int terrainHeight, double[] densityMapArray) {
      int xSize = 5;
      int ySize = terrainHeight / 8 + 1;
      int zSize = 5;
      int x = chunkX * 4;
      int z = chunkZ * 4;
      double riverLevel = 0.0;
      double[] largeSmoothing = this.smoothingRiverNoise.get(null, x, z, xSize, zSize, 1.0 / (1000.0 / xSize), 1.0 / (1000.0 / zSize));
      double[] mediumSmoothing = this.smoothingRiverNoise.get(null, x, z, xSize, zSize, 1.0 / (50.0 / xSize), 1.0 / (50.0 / zSize));
      double[] smallSmoothing = this.smoothingRiverNoise.get(null, x, z, xSize, zSize, 1.0 / (10.0 / xSize), 1.0 / (10.0 / zSize));
      double[] riverRadiuses = new double[]{0.006, 0.024, 0.036};
      double[] riverMults = new double[]{20.0, 60.0, 60.0};

      for (int dx = 0; dx < xSize; dx++) {
         for (int dz = 0; dz < zSize; dz++) {
            double sample = largeSmoothing[dx * zSize + dz] + mediumSmoothing[dx * zSize + dz] * 0.04 + smallSmoothing[dx * zSize + dz] * 0.004;
            double finalSample = 0.0;

            for (int i = 0; i < riverRadiuses.length; i++) {
               if (!(sample < riverLevel - riverRadiuses[i]) && !(sample > riverLevel + riverRadiuses[i])) {
                  double modSample = sample - riverLevel;
                  modSample /= riverRadiuses[i];
                  modSample = Math.abs(modSample);
                  modSample = -modSample;
                  modSample = ++modSample * riverMults[i];
                  finalSample += modSample;
               }
            }

            for (int dy = 0; dy < ySize; dy++) {
               if (dy < ySize / 2) {
                  densityMapArray[dx * ySize * zSize + dz * ySize + dy] = densityMapArray[dx * ySize * zSize + dz * ySize + dy]
                     - finalSample * (dy / (ySize / 2.0)) * (dy / (ySize / 2.0));
               } else {
                  densityMapArray[dx * ySize * zSize + dz * ySize + dy] = densityMapArray[dx * ySize * zSize + dz * ySize + dy] - finalSample;
               }
            }
         }
      }
   }

   private void modifyDensityMapCarvingRivers(int chunkX, int chunkZ, int terrainHeight, double[] densityMapArray) {
      int xSize = 5;
      int ySize = terrainHeight / 8 + 1;
      int zSize = 5;
      int x = chunkX * 4;
      int z = chunkZ * 4;
      int maxUndergroundRiverHeight = 2;
      double riverLevel = 0.0;
      double[] largeCarving = this.carvingRiverNoise.get(null, x, z, xSize, zSize, 1.0 / (1000.0 / xSize), 1.0 / (1000.0 / zSize));
      double[] mediumCarving = this.carvingRiverNoise.get(null, x, z, xSize, zSize, 1.0 / (50.0 / xSize), 1.0 / (50.0 / zSize));
      double[] smallCarving = this.carvingRiverNoise.get(null, x, z, xSize, zSize, 1.0 / (10.0 / xSize), 1.0 / (10.0 / zSize));
      double[] riverRadiuses = new double[]{0.012, 0.018, 0.024};
      double[] riverMults = new double[]{20.0, 60.0, 60.0};

      for (int dx = 0; dx < xSize; dx++) {
         for (int dz = 0; dz < zSize; dz++) {
            double sample = largeCarving[dx * zSize + dz] + mediumCarving[dx * zSize + dz] * 0.04 + smallCarving[dx * zSize + dz] * 0.004;
            double finalSample = 0.0;

            for (int i = 0; i < riverRadiuses.length; i++) {
               if (!(sample < riverLevel - riverRadiuses[i]) && !(sample > riverLevel + riverRadiuses[i])) {
                  double heightFactor = sample - riverLevel;
                  heightFactor /= riverRadiuses[i];
                  heightFactor = Math.abs(heightFactor);
                  heightFactor = -heightFactor;
                  heightFactor = ++heightFactor * riverMults[i];
                  finalSample += heightFactor;
               }
            }

            if (!(finalSample <= 0.0)) {
               for (int dy = ySize / 2 - maxUndergroundRiverHeight; dy < ySize / 2 + maxUndergroundRiverHeight; dy++) {
                  double heightFactor = dy;
                  heightFactor -= ySize / 2;
                  heightFactor /= maxUndergroundRiverHeight;
                  heightFactor = Math.abs(heightFactor);
                  heightFactor = -heightFactor;
                  heightFactor++;
                  densityMapArray[dx * ySize * zSize + dz * ySize + dy] = densityMapArray[dx * ySize * zSize + dz * ySize + dy] - finalSample * heightFactor;
               }
            }
         }
      }
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
      double mainNoiseScaleY = 160.0;
      double mainNoiseScaleZ = 80.0;
      double scaleNoiseScaleX = 1.121;
      double scaleNoiseScaleZ = 1.121;
      double depthNoiseScaleX = 200.0;
      double depthNoiseScaleZ = 200.0;
      double depthBaseSize = this.world.getWorldType() == WorldTypes.OVERWORLD_AMPLIFIED ? 8.5 : terrainHeight / 16.0 + 0.5;
      double coordScale = 684.412;
      double heightScale = 684.412;
      double heightStretch = 12.0;
      double upperLimitScale = this.world.getWorldType() == WorldTypes.OVERWORLD_AMPLIFIED ? 128.0 : 512.0;
      double lowerLimitScale = 512.0;
      double[] scaleArray = this.scaleNoise.get(null, x, z, xSize, zSize, 1.121, 1.121);
      double[] depthArray = this.depthNoise.get(null, x, z, xSize, zSize, 200.0, 200.0);
      double[] mainNoiseArray = this.mainNoise.get(null, x, y, z, xSize, ySize, zSize, 8.555150000000001, 4.277575000000001, 8.555150000000001);
      double[] minLimitArray = this.minLimitNoise.get(null, x, y, z, xSize, ySize, zSize, 684.412, 684.412, 684.412);
      double[] maxLimitArray = this.maxLimitNoise.get(null, x, y, z, xSize, ySize, zSize, 684.412, 684.412, 684.412);
      int mainIndex = 0;
      int depthScaleIndex = 0;
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
            double scale = (scaleArray[depthScaleIndex] + 256.0) / 512.0;
            scale *= humidity;
            if (scale > 1.0) {
               scale = 1.0;
            }

            double depth = depthArray[depthScaleIndex] / 8000.0;
            if (depth < 0.0) {
               depth = -depth * 0.3;
            }

            depth = depth * 3.0 - 2.0;
            if (depth < 0.0) {
               depth /= 2.0;
               if (depth < -1.0) {
                  depth = -1.0;
               }

               depth /= 1.4;
               depth /= 2.0;
               scale = 0.0;
            } else {
               if (depth > 1.0) {
                  depth = 1.0;
               }

               depth /= 8.0;
            }

            if (scale < 0.0) {
               scale = 0.0;
            }

            scale += 0.5;
            depth = depth * (depthBaseSize * 2.0) / 16.0;
            double offsetY = depthBaseSize + depth * 4.0;
            depthScaleIndex++;

            for (int dy = 0; dy < ySize; dy++) {
               double densityOffset = (dy - offsetY) * 12.0 / scale;
               if (densityOffset < 0.0) {
                  densityOffset *= 4.0;
               }

               double minDensity = minLimitArray[mainIndex] / upperLimitScale;
               double maxDensity = maxLimitArray[mainIndex] / 512.0;
               double mainDensity = (mainNoiseArray[mainIndex] / 10.0 + 1.0) / 2.0;
               double density;
               if (mainDensity < 0.0) {
                  density = minDensity;
               } else if (mainDensity > 1.0) {
                  density = maxDensity;
               } else {
                  density = minDensity + (maxDensity - minDensity) * mainDensity;
               }

               density -= densityOffset;
               if (dy > ySize - 4) {
                  double densityMod = (dy - (ySize - 4)) / 3.0F;
                  density = density * (1.0 - densityMod) + -10.0 * densityMod;
               }

               densityMapArray[mainIndex] = density;
               mainIndex++;
            }
         }
      }

      if (this.generateRivers) {
         this.modifyDensityMapSmoothingRivers(chunk.xPosition, chunk.zPosition, terrainHeight, densityMapArray);
         this.modifyDensityMapCarvingRivers(chunk.xPosition, chunk.zPosition, terrainHeight, densityMapArray);
      }

      return densityMapArray;
   }
}
