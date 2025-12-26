package net.minecraft.core.world.biome.data;

import com.mojang.logging.LogUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.world.biome.Biome;
import org.slf4j.Logger;

public class BiomeRangeLookup {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final byte[] lookup;
   private final int span;

   public BiomeRangeLookup(BiomeRangeMap map, int span) {
      this.lookup = new byte[span * span * span * span];
      this.span = span;
      ExecutorService service = Executors.newFixedThreadPool(10);
      Future<?>[] tasks = new Future[span * span];

      for (int iTemperature = 0; iTemperature < span; iTemperature++) {
         for (int iHumidity = 0; iHumidity < span; iHumidity++) {
            int finalITemperature = iTemperature;
            int finalIHumidity = iHumidity;
            tasks[iTemperature * span + iHumidity] = service.submit(() -> this.setupSubTask(map, finalITemperature, finalIHumidity));
         }
      }

      for (int i = 0; i < tasks.length; i++) {
         try {
            tasks[i].get();
         } catch (ExecutionException | InterruptedException var9) {
            throw new RuntimeException(var9);
         }
      }

      service.shutdown();
   }

   private void setupSubTask(BiomeRangeMap map, int iTemperature, int iHumidity) {
      for (int iAltitude = 0; iAltitude < this.span; iAltitude++) {
         for (int iVariety = 0; iVariety < this.span; iVariety++) {
            Biome biome = map.findBiome(
               (double)iTemperature / this.span, (double)iHumidity / this.span, (double)iAltitude / this.span, (double)iVariety / this.span
            );
            if (biome == null) {
               throw new RuntimeException(
                  "Biome at T: "
                     + (float)iTemperature / this.span
                     + " H: "
                     + (float)iHumidity / this.span
                     + " A: "
                     + (float)iAltitude / this.span
                     + " V: "
                     + (float)iVariety / this.span
                     + " is null!"
               );
            }

            this.setBiome(iTemperature, iHumidity, iAltitude, iVariety, biome);
         }
      }
   }

   private void setBiome(int temperature, int humidity, int altitude, int variety, Biome biome) {
      this.lookup[variety * this.span * this.span * this.span + altitude * this.span * this.span + humidity * this.span + temperature] = (byte)Registries.BIOMES
         .getNumericIdOfItem(biome);
   }

   public Biome getBiome(double temperature, double humidity, double altitude, double variety) {
      int iTemperature = this.integerize(temperature);
      int iHumidity = this.integerize(humidity);
      int iAltitude = this.integerize(altitude);
      int iVariety = this.integerize(variety);
      return Registries.BIOMES
         .getItemByNumericId(
            this.lookup[iVariety * this.span * this.span * this.span + iAltitude * this.span * this.span + iHumidity * this.span + iTemperature]
         );
   }

   private int integerize(double d) {
      int i = (int)(d * this.span);
      if (i < 0) {
         i = 0;
      }

      if (i >= this.span) {
         i = this.span - 1;
      }

      return i;
   }
}
