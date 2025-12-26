package net.minecraft.core.world.type;

import java.util.Arrays;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderSingleBiome;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.debug.ChunkGeneratorDebug;

public class WorldTypeDebug extends WorldType {
   public WorldTypeDebug(WorldType.Properties properties) {
      super(properties);
   }

   public static float[] createLightRamp() {
      float[] brightnessRamp = new float[32];
      Arrays.fill(brightnessRamp, 1.0F);
      return brightnessRamp;
   }

   @Override
   public BiomeProvider createBiomeProvider(World world) {
      return new BiomeProviderSingleBiome(Biomes.OVERWORLD_PLAINS, 1.0, 1.0, 1.0);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorDebug(world);
   }

   @Override
   public boolean isValidSpawn(World world, int x, int y, int z) {
      return true;
   }

   @Override
   public void getInitialSpawnLocation(World world) {
      world.getLevelData().setSpawn(0, 1, 0);
   }

   @Override
   public void getRespawnLocation(World world) {
   }

   @Override
   public float getCelestialAngle(World world, long tick, float partialTick) {
      return 0.0F;
   }

   @Override
   public int getSkyDarken(World world, long tick, float partialTick) {
      return 0;
   }
}
