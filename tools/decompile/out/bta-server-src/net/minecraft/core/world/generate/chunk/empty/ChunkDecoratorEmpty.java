package net.minecraft.core.world.generate.chunk.empty;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;

public class ChunkDecoratorEmpty implements ChunkDecorator {
   @Override
   public void decorate(Chunk chunk) {
      if (chunk.xPosition == 0 && chunk.zPosition == 0) {
         chunk.setBlockID(0, 0, 0, Blocks.BEDROCK.id());
      }
   }
}
