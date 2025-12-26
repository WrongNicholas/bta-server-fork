package net.minecraft.core.world.generate.chunk.debug;

import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;

public class ChunkGeneratorDebug extends ChunkGenerator {
   public ChunkGeneratorDebug(World world) {
      super(world, new ChunkDecoratorDebug());
   }

   @Override
   protected ChunkGeneratorResult doBlockGeneration(Chunk chunk) {
      return new ChunkGeneratorResult();
   }
}
