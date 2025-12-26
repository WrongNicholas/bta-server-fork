package net.minecraft.core.world.generate.chunk.classic;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.CavesLargeFeature;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;
import net.minecraft.core.world.generate.chunk.perlin.SurfaceGenerator;
import net.minecraft.core.world.generate.chunk.perlin.overworld.retro.ChunkDecoratorOverworldRetro;
import net.minecraft.core.world.generate.chunk.perlin.overworld.retro.SurfaceGeneratorOverworldRetro;
import net.minecraft.core.world.noise.CombinedPerlinNoise;
import net.minecraft.core.world.noise.PerlinNoise;

public class ChunkGeneratorClassic extends ChunkGenerator {
   private final CombinedPerlinNoise combinedA;
   private final CombinedPerlinNoise combinedB;
   private final CombinedPerlinNoise combinedC;
   private final CombinedPerlinNoise combinedD;
   private final PerlinNoise octavesA;
   private final PerlinNoise octavesB;
   private final SurfaceGenerator sg;
   private final CavesLargeFeature cg;

   public ChunkGeneratorClassic(World world) {
      super(world, new ChunkDecoratorOverworldRetro(world));
      long seed = world.getRandomSeed();
      this.combinedA = new CombinedPerlinNoise(new PerlinNoise(seed, 8, 0), new PerlinNoise(seed, 8, 8));
      this.combinedB = new CombinedPerlinNoise(new PerlinNoise(seed, 8, 16), new PerlinNoise(seed, 8, 24));
      this.combinedC = new CombinedPerlinNoise(new PerlinNoise(seed, 8, 32), new PerlinNoise(seed, 8, 40));
      this.combinedD = new CombinedPerlinNoise(new PerlinNoise(seed, 8, 48), new PerlinNoise(seed, 8, 56));
      this.octavesA = new PerlinNoise(seed, 6, 64);
      this.octavesB = new PerlinNoise(seed, 8, 70);
      this.sg = new SurfaceGeneratorOverworldRetro(world);
      this.cg = new CavesLargeFeature();
   }

   @Override
   protected ChunkGeneratorResult doBlockGeneration(Chunk chunk) {
      ChunkGeneratorResult result = new ChunkGeneratorResult();
      int chunkX = chunk.xPosition;
      int chunkZ = chunk.zPosition;
      float mod = 1.3F;
      int[] heightMap = new int[256];

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            double noiseA = this.combinedA.get((chunkX * 16 + x) * 1.3F, (chunkZ * 16 + z) * 1.3F) / 6.0 + -4.0;
            double noiseB = this.combinedB.get((chunkX * 16 + x) * 1.3F, (chunkZ * 16 + z) * 1.3F) / 5.0 + 10.0 + -4.0;
            if (this.octavesA.get(chunkX * 16 + x, chunkZ * 16 + z) / 8.0 > 0.0) {
               noiseB = noiseA;
            }

            double height;
            if ((height = Math.max(noiseA, noiseB) / 2.0) < 0.0) {
               height *= 0.8;
            }

            heightMap[x + z * 16] = (int)height;
         }
      }

      int[] newHeightMap = heightMap;

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            double val = this.combinedC.get(chunkX * 16 + x << 1, chunkZ * 16 + z << 1) / 8.0;
            int val2 = this.combinedD.get(chunkX * 16 + x << 1, chunkZ * 16 + z << 1) > 0.0 ? 1 : 0;
            if (val > 2.0) {
               int newHeight = ((newHeightMap[x + z * 16] - val2) / 2 << 1) + val2;
               newHeightMap[x + z * 16] = newHeight;
            }
         }
      }

      for (int x = 0; x < 16; x++) {
         for (int zx = 0; zx < 16; zx++) {
            int val = (int)(this.octavesB.get(chunkX * 16 + x, chunkZ * 16 + zx) / 24.0) - 4;
            int newHeight;
            int val2 = (newHeight = newHeightMap[x + zx * 16] + this.world.getWorldType().getOceanY()) + val;
            newHeightMap[x + zx * 16] = Math.max(newHeight, val2);
            if (newHeightMap[x + zx * 16] > this.world.getWorldType().getMaxY() - 2) {
               newHeightMap[x + zx * 16] = this.world.getWorldType().getMaxY() - 2;
            }

            if (newHeightMap[x + zx * 16] < 1) {
               newHeightMap[x + zx * 16] = 1;
            }

            for (int y = this.world.getWorldType().getMinY(); y < this.world.getWorldType().getMaxY(); y++) {
               int index = Chunk.makeBlockIndex(x, y, zx);
               int blockID = 0;
               if (y < newHeight) {
                  blockID = this.world.getWorldType().getFillerBlockId();
               } else if (y < this.world.getWorldType().getOceanY()) {
                  blockID = this.world.getWorldType().getOceanBlockId();
               }

               if (y == 0) {
                  blockID = Blocks.BEDROCK.id();
               }

               result.setBlock(x, y, zx, blockID);
            }
         }
      }

      this.sg.generateSurface(chunk, result);
      this.cg.generate(this.world, chunkX, chunkZ, result);
      return result;
   }
}
