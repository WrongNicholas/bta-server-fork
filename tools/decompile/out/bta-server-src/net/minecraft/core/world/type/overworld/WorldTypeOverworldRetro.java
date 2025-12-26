package net.minecraft.core.world.type.overworld;

import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderSingleBiome;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.perlin.overworld.retro.ChunkGeneratorOverworldRetro;
import net.minecraft.core.world.type.WorldType;

public class WorldTypeOverworldRetro extends WorldTypeOverworld {
   public WorldTypeOverworldRetro(WorldType.Properties properties) {
      super(properties);
   }

   @Override
   public BiomeProvider createBiomeProvider(World world) {
      return new BiomeProviderSingleBiome(Biomes.OVERWORLD_RETRO, 1.0, 1.0, 1.0);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorOverworldRetro(world);
   }
}
