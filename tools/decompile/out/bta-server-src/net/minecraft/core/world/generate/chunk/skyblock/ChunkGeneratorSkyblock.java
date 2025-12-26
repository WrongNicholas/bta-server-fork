package net.minecraft.core.world.generate.chunk.skyblock;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;

public class ChunkGeneratorSkyblock extends ChunkGenerator {
   public ChunkGeneratorSkyblock(World world) {
      super(world, new ChunkDecoratorSkyblock());
   }

   @Override
   protected ChunkGeneratorResult doBlockGeneration(Chunk chunk) {
      ChunkGeneratorResult result = new ChunkGeneratorResult();

      for (int x = -1; x <= 1; x++) {
         for (int z = -1; z <= 1; z++) {
            this.tryPlace(chunk, result, x, 64, z, Blocks.DIRT.id());
            this.tryPlace(chunk, result, x, 65, z, Blocks.DIRT.id());
            this.tryPlace(chunk, result, x, 66, z, Blocks.GRASS.id());
         }
      }

      for (int x = -1; x <= 1; x++) {
         for (int z = -4; z <= -2; z++) {
            this.tryPlace(chunk, result, x, 64, z, Blocks.DIRT.id());
            this.tryPlace(chunk, result, x, 65, z, Blocks.DIRT.id());
            this.tryPlace(chunk, result, x, 66, z, Blocks.GRASS.id());
         }
      }

      for (int x = 2; x <= 4; x++) {
         for (int z = -4; z <= -2; z++) {
            this.tryPlace(chunk, result, x, 64, z, Blocks.DIRT.id());
            this.tryPlace(chunk, result, x, 65, z, Blocks.DIRT.id());
            this.tryPlace(chunk, result, x, 66, z, Blocks.GRASS.id());
         }
      }

      this.tryPlace(chunk, result, 0, 64, -3, Blocks.BEDROCK.id());

      for (int x = -68; x <= -66; x++) {
         for (int z = -1; z <= 1; z++) {
            this.tryPlace(chunk, result, x, 64, z, Blocks.SAND.id());
            this.tryPlace(chunk, result, x, 65, z, Blocks.SAND.id());
            this.tryPlace(chunk, result, x, 66, z, Blocks.SAND.id());
         }
      }

      this.tryPlace(chunk, result, -68, 67, 1, Blocks.CACTUS.id());
      return result;
   }

   private boolean contains(Chunk chunk, int xBlock, int zBlock) {
      return chunk.xPosition == xBlock >> 4 && chunk.zPosition == zBlock >> 4;
   }

   private void tryPlace(Chunk chunk, ChunkGeneratorResult result, int xBlock, int yBlock, int zBlock, int blockId) {
      if (this.contains(chunk, xBlock, zBlock)) {
         result.setBlock(xBlock & 15, yBlock, zBlock & 15, blockId);
      }
   }
}
