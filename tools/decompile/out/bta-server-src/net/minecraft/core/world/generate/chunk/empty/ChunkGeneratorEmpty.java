package net.minecraft.core.world.generate.chunk.empty;

import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;

public class ChunkGeneratorEmpty extends ChunkGenerator {
   public ChunkGeneratorEmpty(World world) {
      super(world, new ChunkDecoratorEmpty());
   }

   @Override
   protected ChunkGeneratorResult doBlockGeneration(Chunk chunk) {
      return new ChunkGeneratorResult();
   }
}
