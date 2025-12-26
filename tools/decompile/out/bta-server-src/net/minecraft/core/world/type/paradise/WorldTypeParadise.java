package net.minecraft.core.world.type.paradise;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderSingleBiome;
import net.minecraft.core.world.config.season.SeasonConfig;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.perlin.paradise.ChunkGeneratorParadise;
import net.minecraft.core.world.season.Seasons;
import net.minecraft.core.world.type.WorldType;

public class WorldTypeParadise extends WorldType {
   public WorldTypeParadise(WorldType.Properties properties) {
      super(properties);
   }

   public static WorldType.Properties defaultProperties(String translationKey) {
      return WorldType.Properties.of(translationKey)
         .brightnessRamp(createLightRamp())
         .seasonConfig(SeasonConfig.builder().withSeasonInCycle(Seasons.PARADISE_GOLD, 28).withSeasonInCycle(Seasons.PARADISE_SILVER, 28).build())
         .fillerBlock(Blocks.MARBLE)
         .dayNightCycleTicks(1344000)
         .bounds(0, 255, 0);
   }

   private static float[] createLightRamp() {
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
   public void onWorldCreation(World world) {
      super.onWorldCreation(world);
      world.setWorldTime(576000L);
   }

   @Override
   public BiomeProvider createBiomeProvider(World world) {
      return new BiomeProviderSingleBiome(Biomes.PARADISE_PARADISE, 0.5, 0.0, 0.0);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorParadise(world);
   }

   @Override
   public boolean isValidSpawn(World world, int x, int y, int z) {
      return true;
   }

   @Override
   public float getCelestialAngle(World world, long tick, float partialTick) {
      float dayProgress = this.getTimeOfDay(world, tick, partialTick);
      dayProgress -= 0.25F;
      float var8 = 1.0F - (float)((Math.cos(dayProgress * Math.PI) + 1.0) / 2.0);
      return dayProgress + (var8 - dayProgress) / 3.0F;
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
      if (world.getCurrentWeather() != null) {
         weatherOffset = world.getCurrentWeather().subtractLightLevel * world.weatherManager.getWeatherIntensity() * world.weatherManager.getWeatherPower();
      }

      int subtracted = (int)(f2 * (11.0F - weatherOffset) + weatherOffset);
      if (subtracted > 8) {
         subtracted = 8;
      }

      return subtracted;
   }
}
