package net.minecraft.core.world.type.overworld;

import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderOverworld;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.perlin.overworld.floating.ChunkGeneratorOverworldFloating;
import net.minecraft.core.world.type.WorldType;

public class WorldTypeOverworldFloating extends WorldTypeOverworld {
   public WorldTypeOverworldFloating(WorldType.Properties properties) {
      super(properties);
   }

   @Override
   public BiomeProvider createBiomeProvider(World world) {
      return new BiomeProviderOverworld(world.getRandomSeed(), this)
         .withTemperatureScales(0.00625, 0.00625, 0.25, 0.01)
         .withHumidityScales(0.0125, 0.0125, 0.3, 0.01)
         .withFuzzinessScales(8.0, 8.0, 0.025);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorOverworldFloating(world);
   }

   @Override
   public boolean isValidSpawn(World world, int x, int y, int z) {
      Block<?> block = world.getBlock(x, y, z);
      return world.getHeightValue(x, z) > 0 && block != null && block.isSolidRender();
   }
}
