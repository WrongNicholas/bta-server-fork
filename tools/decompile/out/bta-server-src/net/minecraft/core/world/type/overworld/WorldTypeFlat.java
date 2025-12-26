package net.minecraft.core.world.type.overworld;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderSingleBiome;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.flat.ChunkGeneratorFlat;
import net.minecraft.core.world.type.WorldType;

public class WorldTypeFlat extends WorldTypeOverworld {
   public WorldTypeFlat(WorldType.Properties properties) {
      super(properties);
   }

   @Override
   public BiomeProvider createBiomeProvider(World world) {
      return new BiomeProviderSingleBiome(Biomes.OVERWORLD_PLAINS, 0.5, 0.0, 0.0);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorFlat(world);
   }

   @Override
   public boolean isValidSpawn(World world, int x, int y, int z) {
      return world.getBlockId(x, y, z) == Blocks.GRASS.id();
   }

   @Override
   public void getRespawnLocation(World world) {
   }
}
