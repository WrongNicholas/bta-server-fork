package net.minecraft.core.world.biome.data;

import com.b100.utils.ImageUtils;
import com.mojang.logging.LogUtils;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.biome.Biome;
import org.slf4j.Logger;

public final class BiomeRangeMap {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<Biome, Set<BiomeRange>> ranges = new LinkedHashMap<>();
   private BiomeRangeLookup lookup = null;

   public void addRange(Biome biome, BiomeRange... ranges) {
      if (ranges != null && ranges.length != 0 && this.lookup == null) {
         Set<BiomeRange> set = this.ranges.getOrDefault(biome, new HashSet<>());
         set.addAll(Arrays.asList(ranges));
         this.ranges.put(biome, set);
      }
   }

   public void lock() {
      this.lookup = new BiomeRangeLookup(this, 50);
   }

   public Set<BiomeRange> getRanges(Biome biome) {
      Set<BiomeRange> ranges = this.ranges.get(biome);
      return ranges == null ? null : Collections.unmodifiableSet(ranges);
   }

   public void clear() {
      this.lookup = null;
      this.ranges.clear();
   }

   public boolean hasGaps() {
      for (double temperature = 0.0; temperature < 1.0; temperature += 0.01) {
         for (double humidity = 0.0; humidity < 1.0; humidity += 0.01) {
            for (double altitude = 0.0; altitude < 1.0; altitude += 0.01) {
               for (double variety = 0.0; variety < 1.0; variety += 0.01) {
                  Biome biome = this.lookupBiome(temperature, humidity, altitude, variety);
                  if (biome == null) {
                     LOGGER.warn("Gap found in biome map at:\nTemperature: {}\nHumidity: {}\nAltitude: {}", temperature, humidity, altitude);
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public Biome lookupBiome(double temperature, double humidity, double altitude, double variety) {
      temperature = MathHelper.clamp(temperature, 0.0, 1.0);
      humidity = MathHelper.clamp(humidity, 0.0, 1.0);
      altitude = MathHelper.clamp(altitude, 0.0, 1.0);
      variety = MathHelper.clamp(variety, 0.0, 1.0);
      return this.lookup.getBiome(temperature, humidity, altitude, variety);
   }

   Biome findBiome(double temperature, double humidity, double altitude, double variety) {
      temperature = MathHelper.clamp(temperature, 0.0, 1.0);
      humidity = MathHelper.clamp(humidity, 0.0, 1.0);
      altitude = MathHelper.clamp(altitude, 0.0, 1.0);
      variety = MathHelper.clamp(variety, 0.0, 1.0);
      Biome last = null;

      for (Entry<Biome, Set<BiomeRange>> entry : this.ranges.entrySet()) {
         for (BiomeRange range : entry.getValue()) {
            if (range.contains(temperature, humidity, altitude, variety)) {
               last = entry.getKey();
            }
         }
      }

      if (last == null) {
         LOGGER.warn("Warning: found NULL biome for T: {} H: {} A: {} V: {}", temperature, humidity, altitude, variety);
      }

      return last;
   }

   public void generateDebugImage(boolean debugText) {
      BufferedImage image = new BufferedImage(4800, 4800, 1);
      Graphics2D g = image.createGraphics();
      g.setColor(Color.black);
      g.fillRect(0, 0, 4800, 4800);

      for (int ti = 0; ti < 100; ti++) {
         for (int hi = 0; hi < 100; hi++) {
            double temperature = (100 - ti) / 99.0;
            double humidity = (100 - hi) / 99.0;
            temperature = Math.round(temperature * 100.0) / 100.0;
            humidity = Math.round(humidity * 100.0) / 100.0;
            Biome biome = this.lookupBiome(temperature, humidity, 0.5, 0.5);
            Color color;
            if (biome == null) {
               color = Color.magenta;
            } else {
               color = hex2Rgb(biome.color);
            }

            g.setColor(color);
            g.fillRect(ti * 48, hi * 48, 48, 48);
            if (debugText) {
               g.setColor(Color.black);
               g.drawRect(ti * 48, hi * 48, 48, 48);
               g.drawString("T: " + temperature, ti * 48, hi * 48 + 12);
               g.drawString("H: " + humidity, ti * 48, hi * 48 + 24);
            }

            if (biome != null) {
               g.drawString(String.format("%6.6s", Registries.BIOMES.getKey(biome).substring("minecraft:overworld.".length())), ti * 48, hi * 48 + 36);
            }
         }
      }

      Polygon cutOffPoint = new Polygon();
      cutOffPoint.addPoint(0, 0);
      cutOffPoint.addPoint(4800, 0);
      cutOffPoint.addPoint(4800, 4800);
      g.setColor(Color.gray);
      g.fillPolygon(cutOffPoint);
      g.dispose();

      try {
         ImageUtils.saveExternalImage(image, "BiomeLookup.png");
      } catch (Exception var12) {
         LOGGER.error("Failed to save BiomeLookup image!", (Throwable)var12);
      }
   }

   public static Color hex2Rgb(int colourHex) {
      int r = (colourHex & 0xFF0000) >> 16;
      int g = (colourHex & 0xFF00) >> 8;
      int b = colourHex & 0xFF;
      return new Color(r, g, b);
   }
}
