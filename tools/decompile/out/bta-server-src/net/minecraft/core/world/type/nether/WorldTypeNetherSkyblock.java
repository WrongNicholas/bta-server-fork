package net.minecraft.core.world.type.nether;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.skyblock.ChunkGeneratorSkyblockNether;
import net.minecraft.core.world.type.WorldType;

public class WorldTypeNetherSkyblock extends WorldTypeNether {
   public WorldTypeNetherSkyblock(WorldType.Properties properties) {
      super(properties);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorSkyblockNether(world);
   }
}
