package net.minecraft.core.world.type.overworld;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderOverworld;
import net.minecraft.core.world.config.season.SeasonConfig;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.perlin.overworld.ChunkGeneratorOverworld;
import net.minecraft.core.world.season.Seasons;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.weather.Weather;

public class WorldTypeOverworld extends WorldType {
   public WorldTypeOverworld(WorldType.Properties properties) {
      super(properties);
   }

   @Override
   public void onWorldCreation(World world) {
      super.onWorldCreation(world);
      world.setWorldTime(75000L);
   }

   public static WorldType.Properties defaultProperties(String translationKey) {
      return WorldType.Properties.of(translationKey)
         .brightnessRamp(createLightRamp())
         .seasonConfig(
            SeasonConfig.builder()
               .withSeasonInCycle(Seasons.OVERWORLD_SPRING, 14)
               .withSeasonInCycle(Seasons.OVERWORLD_SUMMER, 14)
               .withSeasonInCycle(Seasons.OVERWORLD_FALL, 14)
               .withSeasonInCycle(Seasons.OVERWORLD_WINTER, 14)
               .build()
         )
         .oceanBlock(Blocks.FLUID_WATER_STILL)
         .fillerBlock(Blocks.STONE)
         .allowRespawn();
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
      return new BiomeProviderOverworld(world.getRandomSeed(), this);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorOverworld(world);
   }

   @Override
   public boolean isValidSpawn(World world, int x, int y, int z) {
      return world.getBlockId(x, y, z) == Blocks.SAND.id();
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
