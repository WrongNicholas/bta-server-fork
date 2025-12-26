package net.minecraft.core.world.biome.provider;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.world.biome.Biome;

public abstract class BiomeProvider {
   public Biome getBiome(int x, int y, int z) {
      Biome[] out = this.getBiomes(null, x, y, z, 1, 1, 1);
      return out[0];
   }

   public double getTemperature(int x, int z) {
      double[] out = this.getTemperatures(null, x, z, 1, 1);
      return out[0];
   }

   public double getHumidity(int x, int z) {
      double[] out = this.getHumidities(null, x, z, 1, 1);
      return out[0];
   }

   public double getVariety(int x, int z) {
      double[] out = this.getVarieties(null, x, z, 1, 1);
      return out[0];
   }

   public double getBiomeness(int x, int y, int z) {
      double[] out = this.getBiomenesses(null, x, y, z, 1, 1, 1);
      return out[0];
   }

   public Biome[] getBiomes(Biome[] biomes, int x, int y, int z, int xSize, int ySize, int zSize) {
      return this.getBiomes(biomes, null, null, null, x, y, z, xSize, ySize, zSize);
   }

   public abstract Biome[] getBiomes(Biome[] var1, double[] var2, double[] var3, double[] var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public abstract double[] getTemperatures(double[] var1, int var2, int var3, int var4, int var5);

   public abstract double[] getHumidities(double[] var1, int var2, int var3, int var4, int var5);

   public abstract double[] getVarieties(double[] var1, int var2, int var3, int var4, int var5);

   public abstract double[] getBiomenesses(double[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public abstract Biome lookupBiome(double var1, double var3, double var5, double var7);

   protected void generateBiomeDistributionStats() {
      int lastReport = 0;
      int sizeChunks = 2048;
      int chunksSampled = 0;
      Map<Biome, Integer> biomeOccurrenceMap = new HashMap<>();
      double tempAcc = 0.0;
      double humAcc = 0.0;
      System.out.println("Sampling " + sizeChunks * sizeChunks + " chunks for biome test...");

      for (int x = -sizeChunks / 2; x < sizeChunks / 2; x++) {
         for (int z = -sizeChunks / 2; z < sizeChunks / 2; z++) {
            int pctg = (int)((float)chunksSampled / (sizeChunks * sizeChunks) * 100.0F);
            if (pctg != lastReport) {
               System.out.println(pctg + "% complete...");
               lastReport = pctg;
            }

            tempAcc += this.getTemperature(x * 16, z * 16);
            humAcc += this.getHumidity(x * 16, z * 16);
            Biome biome = this.getBiome(x * 16, 64, z * 16);
            biomeOccurrenceMap.put(biome, biomeOccurrenceMap.getOrDefault(biome, 0) + 1);
            chunksSampled++;
         }
      }

      System.out.println("Seed analysis sampling " + sizeChunks * sizeChunks + " chunks:");
      System.out.println("    Average temperature: " + String.format("%.2f", tempAcc / (sizeChunks * sizeChunks)));
      System.out.println("    Average humidity: " + String.format("%.2f", humAcc / (sizeChunks * sizeChunks)));
      System.out.println("    Biome distribution:");

      for (Biome biome : Registries.BIOMES) {
         float pctg;
         if (biomeOccurrenceMap.containsKey(biome)) {
            pctg = (float)biomeOccurrenceMap.get(biome).intValue() / (sizeChunks * sizeChunks);
            pctg *= 100.0F;
         } else {
            pctg = 0.0F;
         }

         System.out.println("        " + Registries.BIOMES.getKey(biome) + ": " + String.format("%.2f", pctg) + "%");
      }
   }
}
