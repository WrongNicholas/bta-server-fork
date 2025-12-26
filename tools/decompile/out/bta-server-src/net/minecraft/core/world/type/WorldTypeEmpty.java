package net.minecraft.core.world.type;

import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderSingleBiome;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.empty.ChunkGeneratorEmpty;
import net.minecraft.core.world.weather.Weather;

public class WorldTypeEmpty extends WorldType {
   public WorldTypeEmpty(WorldType.Properties properties) {
      super(properties);
   }

   public static float[] createLightRamp() {
      float[] brightnessRamp = new float[32];
      float f = 0.05F;

      for (int i = 0; i <= 31; i++) {
         float f1 = 1.0F - i / 15.0F;
         if (i > 15) {
            f1 = 0.0F;
         }

         brightnessRamp[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
      }

      return brightnessRamp;
   }

   @Override
   public BiomeProvider createBiomeProvider(World world) {
      return new BiomeProviderSingleBiome(Biomes.OVERWORLD_PLAINS, 1.0, 1.0, 1.0);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorEmpty(world);
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
      return this.getTimeOfDay(world, tick, partialTick);
   }

   @Override
   public int getSkyDarken(World world, long tick, float partialTick) {
      float f1 = this.getCelestialAngle(world, tick, partialTick);
      float f2 = 1.0F - (MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      float weatherOffset = 0.0F;
      Weather currentWeather = world.getCurrentWeather();
      if (currentWeather != null) {
         weatherOffset = currentWeather.subtractLightLevel * world.weatherManager.getWeatherIntensity() * world.weatherManager.getWeatherPower();
      }

      return (int)(f2 * (11.0F - weatherOffset) + weatherOffset);
   }
}
