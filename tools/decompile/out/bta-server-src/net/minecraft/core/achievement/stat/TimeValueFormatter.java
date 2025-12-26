package net.minecraft.core.achievement.stat;

final class TimeValueFormatter implements StatValueFormatter {
   public static double UNIT_SECONDS_TO_MINUTES = 0.016666666666666666;
   public static double UNIT_MINUTES_TO_HOURS = 0.016666666666666666;
   public static double UNIT_HOURS_TO_DAYS = 0.041666666666666664;
   public static double UNIT_DAYS_TO_YEARS = 0.002737907006988508;

   @Override
   public String formatValue(int value) {
      double seconds = value / 20.0;
      double minutes = seconds * UNIT_SECONDS_TO_MINUTES;
      double hours = minutes * UNIT_MINUTES_TO_HOURS;
      double days = hours * UNIT_HOURS_TO_DAYS;
      double years = days * UNIT_DAYS_TO_YEARS;
      if (years > 0.5) {
         return Stat.getDecimalFormat().format(years) + " y";
      } else if (days > 0.5) {
         return Stat.getDecimalFormat().format(days) + " d";
      } else if (hours > 0.5) {
         return Stat.getDecimalFormat().format(hours) + " h";
      } else {
         return minutes > 0.5 ? Stat.getDecimalFormat().format(minutes) + " m" : seconds + " s";
      }
   }
}
