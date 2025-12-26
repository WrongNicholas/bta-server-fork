package net.minecraft.core.world.generate;

import java.util.Random;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;

public class LargeFeature {
   protected int radiusChunk = 8;
   protected static Random rand = new Random();
   protected World worldObj;

   public void generate(World world, int baseChunkX, int baseChunkZ, ChunkGeneratorResult result) {
      int radius = this.radiusChunk;
      rand.setSeed(world.getRandomSeed());
      this.worldObj = world;
      long randX = rand.nextLong() / 2L * 2L + 1L;
      long randZ = rand.nextLong() / 2L * 2L + 1L;

      for (int chunkX = baseChunkX - radius; chunkX <= baseChunkX + radius; chunkX++) {
         for (int chunkZ = baseChunkZ - radius; chunkZ <= baseChunkZ + radius; chunkZ++) {
            rand.setSeed(chunkX * randX + chunkZ * randZ ^ world.getRandomSeed());
            this.doGeneration(world, chunkX, chunkZ, baseChunkX, baseChunkZ, result);
         }
      }
   }

   protected void doGeneration(World world, int chunkX, int chunkZ, int baseChunkX, int baseChunkZ, ChunkGeneratorResult result) {
   }
}
