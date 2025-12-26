package net.minecraft.core.world.biome.data;

import java.util.Objects;

public final class BiomeRange {
   private final double minTemperature;
   private final double maxTemperature;
   private final double minHumidity;
   private final double maxHumidity;
   private final double minAltitude;
   private final double maxAltitude;
   private final double minVariety;
   private final double maxVariety;

   public BiomeRange(
      double minTemperature,
      double maxTemperature,
      double minHumidity,
      double maxHumidity,
      double minAltitude,
      double maxAltitude,
      double minVariety,
      double maxVariety
   ) {
      this.minTemperature = minTemperature;
      this.maxTemperature = maxTemperature;
      this.minHumidity = minHumidity;
      this.maxHumidity = maxHumidity;
      this.minAltitude = minAltitude;
      this.maxAltitude = maxAltitude;
      this.minVariety = minVariety;
      this.maxVariety = maxVariety;
   }

   public double getMinTemperature() {
      return this.minTemperature;
   }

   public double getMaxTemperature() {
      return this.maxTemperature;
   }

   public double getMinHumidity() {
      return this.minHumidity;
   }

   public double getMaxHumidity() {
      return this.maxHumidity;
   }

   public double getMinAltitude() {
      return this.minAltitude;
   }

   public double getMaxAltitude() {
      return this.maxAltitude;
   }

   public double getMinVariety() {
      return this.minVariety;
   }

   public double getMaxVariety() {
      return this.maxVariety;
   }

   public boolean contains(double temperature, double humidity, double altitude, double variety) {
      return temperature >= this.getMinTemperature()
         && temperature < this.getMaxTemperature()
         && humidity >= this.getMinHumidity()
         && humidity < this.getMaxHumidity()
         && altitude >= this.getMinAltitude()
         && altitude < this.getMaxAltitude()
         && variety >= this.getMinVariety()
         && variety < this.getMaxVariety();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BiomeRange that = (BiomeRange)o;
         return Double.compare(that.minTemperature, this.minTemperature) == 0
            && Double.compare(that.maxTemperature, this.maxTemperature) == 0
            && Double.compare(that.minHumidity, this.minHumidity) == 0
            && Double.compare(that.maxHumidity, this.maxHumidity) == 0
            && Double.compare(that.minAltitude, this.minAltitude) == 0
            && Double.compare(that.maxAltitude, this.maxAltitude) == 0
            && Double.compare(that.maxVariety, this.maxVariety) == 0;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.minTemperature, this.maxTemperature, this.minHumidity, this.maxHumidity, this.minAltitude, this.maxAltitude, this.minVariety, this.maxVariety
      );
   }
}
