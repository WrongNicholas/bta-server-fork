package net.minecraft.core.world.generate.chunk.skyblock;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;

public class ChunkGeneratorSkyblockNether extends ChunkGenerator {
   public ChunkGeneratorSkyblockNether(World world) {
      super(world, new ChunkDecoratorSkyblockNether());
   }

   @Override
   protected ChunkGeneratorResult doBlockGeneration(Chunk chunk) {
      ChunkGeneratorResult result = new ChunkGeneratorResult();

      for (int x = -1; x <= 1; x++) {
         for (int z = -1; z <= 1; z++) {
            this.tryPlace(chunk, result, x, 63, z, Blocks.GLOWSTONE.id());
            this.tryPlace(chunk, result, x, 64, z, Blocks.GLOWSTONE.id());
            this.tryPlace(chunk, result, x, 65, z, Blocks.GLOWSTONE.id());
         }
      }

      for (int z = -1; z <= 2; z++) {
         if (z != -1 && z != 2) {
            this.tryPlace(chunk, result, 2, 65, z, Blocks.OBSIDIAN.id());
            this.tryPlace(chunk, result, 2, 66, z, Blocks.PORTAL_NETHER.id());
            this.tryPlace(chunk, result, 2, 67, z, Blocks.PORTAL_NETHER.id());
            this.tryPlace(chunk, result, 2, 68, z, Blocks.PORTAL_NETHER.id());
            this.tryPlace(chunk, result, 2, 69, z, Blocks.OBSIDIAN.id());
         } else {
            this.tryPlace(chunk, result, 2, 65, z, Blocks.OBSIDIAN.id());
            this.tryPlace(chunk, result, 2, 66, z, Blocks.OBSIDIAN.id());
            this.tryPlace(chunk, result, 2, 67, z, Blocks.OBSIDIAN.id());
            this.tryPlace(chunk, result, 2, 68, z, Blocks.OBSIDIAN.id());
            this.tryPlace(chunk, result, 2, 69, z, Blocks.OBSIDIAN.id());
         }
      }

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
