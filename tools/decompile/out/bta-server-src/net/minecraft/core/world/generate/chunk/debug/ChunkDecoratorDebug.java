package net.minecraft.core.world.generate.chunk.debug;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;

public class ChunkDecoratorDebug implements ChunkDecorator {
   @Override
   public void decorate(Chunk chunk) {
      int chunkX = chunk.xPosition;
      int chunkZ = chunk.zPosition;
      int chunkBlockOffset = chunkX * 8;
      int chunkMetaOffset = chunkZ * 8;

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            int blockBlockOffset = x / 2;
            int blockMetaOffset = z / 2;
            if (x % 2 == 0 && z % 2 == 0) {
               int blockId = chunkBlockOffset + blockBlockOffset;
               int meta = chunkMetaOffset + blockMetaOffset;
               if (blockId >= 0 && blockId < Blocks.blocksList.length && Blocks.blocksList[blockId] != null) {
                  chunk.setBlockIDWithMetadata(x, 0, z, blockId, meta);
               }
            }
         }
      }
   }
}
