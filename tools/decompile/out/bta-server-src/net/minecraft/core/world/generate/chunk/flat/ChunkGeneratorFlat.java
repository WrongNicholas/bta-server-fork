package net.minecraft.core.world.generate.chunk.flat;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;

public class ChunkGeneratorFlat extends ChunkGenerator {
   public ChunkGeneratorFlat(World world) {
      super(world, new ChunkDecoratorFlat());
   }

   @Override
   protected ChunkGeneratorResult doBlockGeneration(Chunk chunk) {
      ChunkGeneratorResult result = new ChunkGeneratorResult();

      for (int z = 0; z < 16; z++) {
         for (int x = 0; x < 16; x++) {
            result.setBlock(x, 0, z, Blocks.BEDROCK.id());

            for (int y = 1; y < 4; y++) {
               result.setBlock(x, y, z, Blocks.STONE.id());
            }

            for (int y = 4; y < 6; y++) {
               result.setBlock(x, y, z, Blocks.DIRT.id());
            }

            result.setBlock(x, 6, z, Blocks.GRASS.id());
         }
      }

      return result;
   }
}
