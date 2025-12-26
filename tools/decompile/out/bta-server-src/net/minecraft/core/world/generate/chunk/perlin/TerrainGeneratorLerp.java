package net.minecraft.core.world.generate.chunk.perlin;

import java.util.Random;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;

public abstract class TerrainGeneratorLerp implements TerrainGenerator {
   protected final World world;
   protected final Random rand;

   public TerrainGeneratorLerp(World world) {
      this.world = world;
      this.rand = new Random();
   }

   protected abstract int getBlockAt(int var1, int var2, int var3, double var4);

   @Override
   public ChunkGeneratorResult generateTerrain(Chunk chunk, double[] densityMap) {
      int terrainHeight = this.world.getWorldType().getMaxY() + 1 - this.world.getWorldType().getMinY();
      this.rand.setSeed(chunk.xPosition * 341873128712L + chunk.zPosition * 132897987541L);
      int xBlocksPerChunk = 16;
      int zBlocksPerChunk = 16;
      byte xBlocksPerBlob = 4;
      byte yBlocksPerBlob = 8;
      byte zBlocksPerBlob = 4;
      int xBlobsPerChunk = 4;
      int yBlobsPerChunk = terrainHeight / 8;
      int zBlobsPerChunk = 4;
      int xDensitiesPerChunk = 5;
      int yDensitiesPerChunk = yBlobsPerChunk + 1;
      int zDensitiesPerChunk = 5;
      double xLerpInterval = 0.25;
      double yLerpInterval = 0.125;
      double zLerpInterval = 0.25;
      ChunkGeneratorResult result = new ChunkGeneratorResult();

      for (int blobX = 0; blobX < 4; blobX++) {
         for (int blobZ = 0; blobZ < 4; blobZ++) {
            for (int blobY = 0; blobY < yBlobsPerChunk; blobY++) {
               double densityMinXMinYMinZ = densityMap[(blobX * 5 + blobZ) * yDensitiesPerChunk + blobY];
               double densityMinXMinYMaxZ = densityMap[(blobX * 5 + blobZ + 1) * yDensitiesPerChunk + blobY];
               double densityMinXMaxYMinZ = densityMap[(blobX * 5 + blobZ) * yDensitiesPerChunk + blobY + 1];
               double densityMinXMaxYMaxZ = densityMap[(blobX * 5 + blobZ + 1) * yDensitiesPerChunk + blobY + 1];
               double densityMaxXMinYMinZ = densityMap[((blobX + 1) * 5 + blobZ) * yDensitiesPerChunk + blobY];
               double densityMaxXMinYMaxZ = densityMap[((blobX + 1) * 5 + blobZ + 1) * yDensitiesPerChunk + blobY];
               double densityMaxXMaxYMinZ = densityMap[((blobX + 1) * 5 + blobZ) * yDensitiesPerChunk + blobY + 1];
               double densityMaxXMaxYMaxZ = densityMap[((blobX + 1) * 5 + blobZ + 1) * yDensitiesPerChunk + blobY + 1];
               double yLerpAmountMinXMinZ = (densityMinXMaxYMinZ - densityMinXMinYMinZ) * 0.125;
               double yLerpAmountMinXMaxZ = (densityMinXMaxYMaxZ - densityMinXMinYMaxZ) * 0.125;
               double yLerpAmountMaxXMinZ = (densityMaxXMaxYMinZ - densityMaxXMinYMinZ) * 0.125;
               double yLerpAmountMaxXMaxZ = (densityMaxXMaxYMaxZ - densityMaxXMinYMaxZ) * 0.125;
               double yLerpDensityMinXMinZ = densityMinXMinYMinZ;
               double yLerpDensityMinXMaxZ = densityMinXMinYMaxZ;
               double yLerpDensityMaxXMinZ = densityMaxXMinYMinZ;
               double yLerpDensityMaxXMaxZ = densityMaxXMinYMaxZ;

               for (int yInBlob = 0; yInBlob < 8; yInBlob++) {
                  double xLerpAmountMinZ = (yLerpDensityMaxXMinZ - yLerpDensityMinXMinZ) * 0.25;
                  double xLerpAmountMaxZ = (yLerpDensityMaxXMaxZ - yLerpDensityMinXMaxZ) * 0.25;
                  double xLerpDensityMinZ = yLerpDensityMinXMinZ;
                  double xLerpDensityMaxZ = yLerpDensityMinXMaxZ;

                  for (int xInBlob = 0; xInBlob < 4; xInBlob++) {
                     double zLerpAmount = (xLerpDensityMaxZ - xLerpDensityMinZ) * 0.25;
                     double zLerpDensity = xLerpDensityMinZ;

                     for (int zInBlob = 0; zInBlob < 4; zInBlob++) {
                        int blockX = xInBlob + blobX * 4;
                        int blockY = yInBlob + blobY * 8 + this.world.worldType.getMinY();
                        int blockZ = zInBlob + blobZ * 4;
                        int id = this.getBlockAt(blockX, blockY, blockZ, zLerpDensity);
                        result.setBlock(blockX, blockY, blockZ, id);
                        zLerpDensity += zLerpAmount;
                     }

                     xLerpDensityMinZ += xLerpAmountMinZ;
                     xLerpDensityMaxZ += xLerpAmountMaxZ;
                  }

                  yLerpDensityMinXMinZ += yLerpAmountMinXMinZ;
                  yLerpDensityMinXMaxZ += yLerpAmountMinXMaxZ;
                  yLerpDensityMaxXMinZ += yLerpAmountMaxXMinZ;
                  yLerpDensityMaxXMaxZ += yLerpAmountMaxXMaxZ;
               }
            }
         }
      }

      return result;
   }
}
