package net.minecraft.core.world.generate.chunk.indev;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;
import net.minecraft.core.world.noise.CombinedPerlinNoise;
import net.minecraft.core.world.noise.RetroPerlinNoise;

public final class LevelGenerator {
   private final int sizeX;
   private final int sizeY;
   private final int sizeZ;
   private final IndevWorldType worldType;
   private final IndevWorldTheme worldTheme;

   public LevelGenerator(int sizeX, int sizeY, int sizeZ, IndevWorldType worldType, IndevWorldTheme worldTheme) {
      this.sizeX = sizeX;
      this.sizeY = sizeY;
      this.sizeZ = sizeZ;
      this.worldType = worldType;
      this.worldTheme = worldTheme;
   }

   public void generate(World world, ChunkGeneratorResult result, int xOffset, int zOffset) {
      int waterLevel = world.getWorldType().getOceanY();
      long seed = world.getRandomSeed();
      int layers = 1;
      if (this.worldType == IndevWorldType.FLOATING) {
         layers = (this.sizeY - 64) / 48 + 1;
      }

      for (int layer = 0; layer < layers; layer++) {
         int[] heightMap = new int[256];
         if (this.worldType == IndevWorldType.FLAT) {
            for (int i = 0; i < heightMap.length; i++) {
               heightMap[i] = 0;
            }
         } else {
            CombinedPerlinNoise heightNoiseGeneratorA = new CombinedPerlinNoise(new RetroPerlinNoise(seed, 8, 0), new RetroPerlinNoise(seed, 8, 8));
            CombinedPerlinNoise heightNoiseGeneratorB = new CombinedPerlinNoise(new RetroPerlinNoise(seed, 8, 16), new RetroPerlinNoise(seed, 8, 24));
            RetroPerlinNoise heightNoiseGeneratorSelector = new RetroPerlinNoise(seed, 6, 32);
            RetroPerlinNoise edgeNoiseGenerator = new RetroPerlinNoise(seed, 2, 38);

            for (int x = 0; x < 16; x++) {
               double xDistanceFromCenter = Math.abs(((x + xOffset) / (this.sizeX - 1.0) - 0.5) * 2.0);

               for (int z = 0; z < 16; z++) {
                  double zDistanceFromCenter = Math.abs(((z + zOffset) / (this.sizeZ - 1.0) - 0.5) * 2.0);
                  double heightNoiseA = heightNoiseGeneratorA.get((x + xOffset) * 1.3F, (z + zOffset) * 1.3F) / 6.0 + -4.0;
                  double heightNoiseB = heightNoiseGeneratorB.get((x + xOffset) * 1.3F, (z + zOffset) * 1.3F) / 5.0 + 10.0 + -4.0;
                  double heightNoiseToUse = heightNoiseGeneratorSelector.get(x + xOffset, z + zOffset) / 8.0;
                  if (heightNoiseToUse > 0.0) {
                     heightNoiseB = heightNoiseA;
                  }

                  double height = Math.max(heightNoiseA, heightNoiseB) / 2.0;
                  if (this.worldType == IndevWorldType.ISLAND) {
                     double distanceFromCenter = Math.sqrt(xDistanceFromCenter * xDistanceFromCenter + zDistanceFromCenter * zDistanceFromCenter) * 1.2F;
                     double edgeNoise = edgeNoiseGenerator.get((x + xOffset) * 0.05F, (z + zOffset) * 0.05F) / 4.0 + 1.0;
                     distanceFromCenter = Math.min(distanceFromCenter, edgeNoise);
                     distanceFromCenter = Math.max(distanceFromCenter, Math.max(xDistanceFromCenter, zDistanceFromCenter));
                     if (distanceFromCenter > 1.0) {
                        distanceFromCenter = 1.0;
                     }

                     if (distanceFromCenter < 0.0) {
                        distanceFromCenter = 0.0;
                     }

                     distanceFromCenter *= distanceFromCenter;
                     height = height * (1.0 - distanceFromCenter) - distanceFromCenter * 10.0 + 5.0;
                     if (height < 0.0) {
                        height -= height * height * 0.2F;
                     }
                  } else if (height < 0.0) {
                     height *= 0.8;
                  }

                  heightMap[x + z * 16] = (int)height;
               }
            }

            CombinedPerlinNoise erosionPowerNoiseGenerator = new CombinedPerlinNoise(new RetroPerlinNoise(seed, 8, 40), new RetroPerlinNoise(seed, 8, 48));
            CombinedPerlinNoise erosionAmountNoiseGenerator = new CombinedPerlinNoise(new RetroPerlinNoise(seed, 8, 56), new RetroPerlinNoise(seed, 8, 64));

            for (int x = 0; x < 16; x++) {
               for (int z = 0; z < 16; z++) {
                  double erosionPower = erosionPowerNoiseGenerator.get(x + xOffset << 1, z + zOffset << 1) / 8.0;
                  int erosionAmount = erosionAmountNoiseGenerator.get(x + xOffset << 1, z + zOffset << 1) > 0.0 ? 1 : 0;
                  if (erosionPower > 2.0) {
                     int height = heightMap[x + z * 16];
                     height = ((height - erosionAmount) / 2 << 1) + erosionAmount;
                     heightMap[x + z * 16] = height;
                  }
               }
            }
         }

         RetroPerlinNoise soilNoiseGenerator = new RetroPerlinNoise(seed, 8, 72);
         RetroPerlinNoise floatingLayerNoiseGenerator = new RetroPerlinNoise(seed, 8, 80);

         for (int x = 0; x < 16; x++) {
            double xDistanceFromCenter = Math.abs(((x + xOffset) / (this.sizeX - 1.0) - 0.5) * 2.0);

            for (int zx = 0; zx < 16; zx++) {
               double zDistanceFromCenterx = Math.abs(((zx + zOffset) / (this.sizeZ - 1.0) - 0.5) * 2.0);
               double distanceFromCenterx = Math.max(xDistanceFromCenter, zDistanceFromCenterx);
               distanceFromCenterx = distanceFromCenterx * distanceFromCenterx * distanceFromCenterx;
               int soilNoise = (int)(soilNoiseGenerator.get(x + xOffset, zx + zOffset) / 24.0) - 4;
               int height = heightMap[x + zx * 16] + waterLevel;
               int soilHeight = height + soilNoise;
               heightMap[x + zx * 16] = Math.max(height, soilHeight);
               if (heightMap[x + zx * 16] > this.sizeY - 2) {
                  heightMap[x + zx * 16] = this.sizeY - 2;
               }

               if (heightMap[x + zx * 16] <= 0) {
                  heightMap[x + zx * 16] = 1;
               }

               double floatingLayerNoise = floatingLayerNoiseGenerator.get((x + xOffset) * 2.3, (zx + zOffset) * 2.3) / 24.0;
               int floatingLayerMinY = (int)(Math.sqrt(Math.abs(floatingLayerNoise)) * Math.signum(floatingLayerNoise) * 20.0) + waterLevel;
               floatingLayerMinY = (int)(floatingLayerMinY * (1.0 - distanceFromCenterx) + distanceFromCenterx * this.sizeY);
               if (floatingLayerMinY > waterLevel) {
                  floatingLayerMinY = this.sizeY;
               }

               for (int y = 0; y < this.sizeY; y++) {
                  int id = 0;
                  if (y <= height) {
                     id = Blocks.STONE.id();
                  }

                  if (this.worldType == IndevWorldType.FLOATING && y < floatingLayerMinY) {
                     id = 0;
                  }

                  if (result.getBlock(x, y, zx) == 0) {
                     result.setBlock(x, y, zx, id);
                  }
               }
            }
         }

         RetroPerlinNoise sandNoiseGenerator = new RetroPerlinNoise(seed, 8, 88);
         RetroPerlinNoise gravelNoiseGenerator = new RetroPerlinNoise(seed, 8, 96);
         int beachHeight = waterLevel - 1;
         if (this.worldTheme == IndevWorldTheme.PARADISE) {
            beachHeight += 2;
         }

         for (int x = 0; x < 16; x++) {
            for (int zx = 0; zx < 16; zx++) {
               boolean isSand = sandNoiseGenerator.get(x + xOffset, zx + zOffset) > 8.0;
               boolean isGravel = gravelNoiseGenerator.get(x + xOffset, zx + zOffset) > 12.0;
               if (this.worldType == IndevWorldType.ISLAND) {
                  isSand = sandNoiseGenerator.get(x + xOffset, zx + zOffset) > -8.0;
               }

               if (this.worldTheme == IndevWorldTheme.PARADISE) {
                  isSand = sandNoiseGenerator.get(x + xOffset, zx + zOffset) > -32.0;
               }

               if (this.worldTheme == IndevWorldTheme.HELL || this.worldTheme == IndevWorldTheme.WOODS) {
                  isSand = sandNoiseGenerator.get(x + xOffset, zx + zOffset) > -8.0;
               }

               int heightx = heightMap[x + zx * 16];
               int topBlock = result.getBlock(x, heightx + 1, zx);
               if ((topBlock == Blocks.FLUID_WATER_FLOWING.id() || topBlock == Blocks.FLUID_WATER_STILL.id() || topBlock == 0)
                  && heightx <= waterLevel - 1
                  && isGravel) {
                  result.setBlock(x, heightx, zx, Blocks.GRAVEL.id());
               }

               if (topBlock == 0) {
                  int idx = -1;
                  if (heightx <= beachHeight && isSand) {
                     idx = Blocks.SAND.id();
                     if (this.worldTheme == IndevWorldTheme.HELL) {
                        idx = Blocks.GRASS.id();
                     }
                  }

                  if (result.getBlock(x, heightx, zx) != 0 && idx > 0) {
                     result.setBlock(x, heightx, zx, idx);
                  }
               }
            }
         }
      }

      if (this.worldType != IndevWorldType.FLOATING) {
         for (int x = 0; x < 16; x++) {
            for (int zx = 0; zx < 16; zx++) {
               for (int y = 0; y < this.sizeY; y++) {
                  int i = Chunk.makeBlockIndex(x, y, zx);
                  int idxx = result.getBlock(x, y, zx);
                  if (idxx == 0 && y < waterLevel) {
                     result.setBlock(x, y, zx, Blocks.FLUID_WATER_STILL.id());
                  }
               }
            }
         }
      }
   }

   public static void generateHouse(World world) {
      int xSpawn = world.getLevelData().getSpawnX();
      int ySpawn = world.getLevelData().getSpawnY();
      int zSpawn = world.getLevelData().getSpawnZ();

      for (int x = xSpawn - 3; x <= xSpawn + 3; x++) {
         for (int y = ySpawn - 2; y <= ySpawn + 2; y++) {
            for (int z = zSpawn - 3; z <= zSpawn + 3; z++) {
               int id = y < ySpawn - 1 ? Blocks.OBSIDIAN.id() : 0;
               if (x == xSpawn - 3 || z == zSpawn - 3 || x == xSpawn + 3 || z == zSpawn + 3 || y == ySpawn - 2 || y == ySpawn + 2) {
                  id = Blocks.STONE.id();
                  if (y >= ySpawn - 1) {
                     id = Blocks.PLANKS_OAK.id();
                  }
               }

               if (z == zSpawn - 3 && x == xSpawn && y >= ySpawn - 1 && y <= ySpawn) {
                  id = 0;
               }

               world.setBlockWithNotify(x, y + 2, z, id);
            }
         }
      }

      world.setBlockWithNotify(xSpawn - 3 + 1, ySpawn + 2, zSpawn, Blocks.TORCH_COAL.id());
      world.setBlockWithNotify(xSpawn + 3 - 1, ySpawn + 2, zSpawn, Blocks.TORCH_COAL.id());
   }
}
