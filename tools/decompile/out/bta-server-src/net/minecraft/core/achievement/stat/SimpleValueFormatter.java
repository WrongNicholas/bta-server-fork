package net.minecraft.core.achievement.stat;

final class SimpleValueFormatter implements StatValueFormatter {
   @Override
   public String formatValue(int value) {
      return Stat.getNumberFormat().format((long)value);
   }
}
