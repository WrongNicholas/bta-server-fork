package net.minecraft.core.world.generate.chunk.perlin.nether;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;
import net.minecraft.core.world.generate.chunk.perlin.SurfaceGenerator;
import net.minecraft.core.world.noise.PerlinNoise;

public class SurfaceGeneratorNether implements SurfaceGenerator {
   private final World world;
   private final PerlinNoise beachNoise;
   private final PerlinNoise soilNoise;

   public SurfaceGeneratorNether(World world) {
      this.world = world;
      this.beachNoise = new PerlinNoise(world.getRandomSeed(), 4, 40);
      this.soilNoise = new PerlinNoise(world.getRandomSeed(), 4, 44);
   }

   @Override
   public void generateSurface(Chunk chunk, ChunkGeneratorResult result) {
      int oceanY = this.world.getWorldType().getOceanY();
      int minY = this.world.getWorldType().getMinY();
      int maxY = this.world.getWorldType().getMaxY();
      int chunkX = chunk.xPosition;
      int chunkZ = chunk.zPosition;
      int oceanBlock = this.world.getWorldType().getOceanBlockId();
      int worldFillBlock = this.world.getWorldType().getFillerBlockId();
      Random rand = new Random(chunkX * 341873128712L + chunkZ * 132897987541L);
      double beachScale = 0.03125;
      double[] sandBeachNoise = this.beachNoise.get(null, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, beachScale, beachScale, 1.0);
      double[] gravelBeachNoise = this.beachNoise.get(null, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, beachScale, 1.0, beachScale);
      double[] soilThicknessNoise = this.soilNoise.get(null, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, beachScale * 2.0, beachScale * 2.0, beachScale * 2.0);

      for (int z = 0; z < 16; z++) {
         for (int x = 0; x < 16; x++) {
            boolean generateSandBeach = sandBeachNoise[z + x * 16] + rand.nextDouble() * 0.2 > 0.0;
            boolean generateGravelBeach = gravelBeachNoise[z + x * 16] + rand.nextDouble() * 0.2 > 0.0;
            int soilThickness = (int)(soilThicknessNoise[z + x * 16] / 3.0 + 3.0 + rand.nextDouble() * 0.25);
            int currentLayerDepth = -1;
            short topBlock = (short)worldFillBlock;
            short fillerBlock = (short)worldFillBlock;

            for (int y = maxY; y >= minY; y--) {
               int block = result.getBlock(x, y, z);
               if (block == 0) {
                  currentLayerDepth = -1;
               } else if (block == worldFillBlock) {
                  if (currentLayerDepth == -1) {
                     if (soilThickness <= 0) {
                        topBlock = 0;
                        fillerBlock = (short)worldFillBlock;
                     } else if (y >= minY + oceanY - 4 && y <= minY + oceanY + 1) {
                        topBlock = (short)worldFillBlock;
                        fillerBlock = (short)worldFillBlock;
                        if (generateGravelBeach) {
                           topBlock = (short)Blocks.GRAVEL.id();
                        }

                        if (generateGravelBeach) {
                           fillerBlock = (short)Blocks.COBBLE_NETHERRACK.id();
                        }

                        if (generateSandBeach) {
                           topBlock = (short)Blocks.SOULSAND.id();
                        }

                        if (generateSandBeach) {
                           fillerBlock = (short)Blocks.SOULSAND.id();
                        }
                     }

                     if (y < minY + oceanY && topBlock == 0) {
                        topBlock = (short)oceanBlock;
                     }

                     currentLayerDepth = soilThickness;
                     if (y >= minY + oceanY - 1) {
                        result.setBlock(x, y, z, topBlock);
                     } else {
                        result.setBlock(x, y, z, fillerBlock);
                     }
                  } else if (currentLayerDepth > 0) {
                     currentLayerDepth--;
                     result.setBlock(x, y, z, fillerBlock);
                  }
               }
            }
         }
      }
   }
}
