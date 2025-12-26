package net.minecraft.core.world.biome.provider;

import java.util.Arrays;
import net.minecraft.core.world.biome.Biome;

public class BiomeProviderSingleBiome extends BiomeProvider {
   private final Biome defaultBiome;
   private final double defaultTemperature;
   private final double defaultHumidity;
   private final double defaultVariety;

   public BiomeProviderSingleBiome(Biome defaultBiome, double defaultTemperature, double defaultHumidity, double defaultVariety) {
      this.defaultBiome = defaultBiome;
      this.defaultTemperature = defaultTemperature;
      this.defaultHumidity = defaultHumidity;
      this.defaultVariety = defaultVariety;
   }

   @Override
   public Biome[] getBiomes(
      Biome[] biomes, double[] temperatures, double[] humidities, double[] varieties, int x, int y, int z, int xSize, int ySize, int zSize
   ) {
      if (biomes == null || biomes.length != xSize * ySize * zSize) {
         biomes = new Biome[xSize * ySize * zSize];
      }

      Arrays.fill(biomes, this.defaultBiome);
      return biomes;
   }

   @Override
   public double[] getTemperatures(double[] temperatures, int x, int z, int xSize, int zSize) {
      if (temperatures == null || temperatures.length < xSize * zSize) {
         temperatures = new double[xSize * zSize];
      }

      Arrays.fill(temperatures, this.defaultTemperature);
      return temperatures;
   }

   @Override
   public double[] getHumidities(double[] humidities, int x, int z, int xSize, int zSize) {
      if (humidities == null || humidities.length < xSize * zSize) {
         humidities = new double[xSize * zSize];
      }

      Arrays.fill(humidities, this.defaultHumidity);
      return humidities;
   }

   @Override
   public double[] getVarieties(double[] varieties, int x, int z, int xSize, int zSize) {
      if (varieties == null || varieties.length < xSize * zSize) {
         varieties = new double[xSize * zSize];
      }

      Arrays.fill(varieties, this.defaultVariety);
      return varieties;
   }

   @Override
   public double[] getBiomenesses(double[] biomenesses, int x, int y, int z, int xSize, int ySize, int zSize) {
      if (biomenesses == null || biomenesses.length < xSize * ySize * zSize) {
         biomenesses = new double[xSize * ySize * zSize];
      }

      Arrays.fill(biomenesses, 1.0);
      return biomenesses;
   }

   @Override
   public Biome lookupBiome(double temperature, double humidity, double altitude, double variety) {
      return this.defaultBiome;
   }
}
