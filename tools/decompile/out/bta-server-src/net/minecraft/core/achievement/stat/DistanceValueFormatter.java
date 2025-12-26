package net.minecraft.core.achievement.stat;

final class DistanceValueFormatter implements StatValueFormatter {
   public static double UNIT_CENTIMETER_TO_METER = 0.01;
   public static double UNIT_METER_TO_KILOMETER = 0.001;

   @Override
   public String formatValue(int value) {
      double meters = value * UNIT_CENTIMETER_TO_METER;
      double kilometers = meters * UNIT_METER_TO_KILOMETER;
      if (kilometers > 0.5) {
         return Stat.getDecimalFormat().format(kilometers) + " km";
      } else {
         return meters > 0.5 ? Stat.getDecimalFormat().format(meters) + " m" : value + " cm";
      }
   }
}
