package net.minecraft.core.world.type.overworld;

import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderSingleBiome;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.skyblock.ChunkGeneratorSkyblock;
import net.minecraft.core.world.type.WorldType;

public class WorldTypeOverworldSkyblock extends WorldTypeOverworld {
   public WorldTypeOverworldSkyblock(WorldType.Properties properties) {
      super(properties);
   }

   @Override
   public BiomeProvider createBiomeProvider(World world) {
      return new BiomeProviderSingleBiome(Biomes.OVERWORLD_PLAINS, 1.0, 1.0, 1.0);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorSkyblock(world);
   }

   @Override
   public boolean isValidSpawn(World world, int x, int y, int z) {
      return true;
   }

   @Override
   public void getInitialSpawnLocation(World world) {
      world.getLevelData().setSpawn(0, 66, 0);
   }

   @Override
   public void getRespawnLocation(World world) {
   }
}
