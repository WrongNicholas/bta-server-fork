package net.minecraft.core.world.type.overworld;

import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderSingleBiome;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.perlin.overworld.hell.ChunkGeneratorOverworldHell;
import net.minecraft.core.world.type.WorldType;

public class WorldTypeOverworldHell extends WorldTypeOverworld {
   public WorldTypeOverworldHell(WorldType.Properties properties) {
      super(properties);
   }

   @Override
   public void onWorldCreation(World world) {
      super.onWorldCreation(world);
      world.setWorldTime(576000L);
   }

   @Override
   public BiomeProvider createBiomeProvider(World world) {
      return new BiomeProviderSingleBiome(Biomes.OVERWORLD_HELL, 1.0, 1.0, 0.0);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorOverworldHell(world);
   }

   @Override
   public float getCelestialAngle(World world, long tick, float partialTick) {
      return 0.5F;
   }
}
