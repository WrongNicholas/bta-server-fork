package net.minecraft.core.util.helper;

public class Time {
   private static long lastTick;
   public static double delta;

   public static void reset() {
      lastTick = now();
   }

   public static void tick() {
      long now = now();
      delta = (now - lastTick) / 1000.0;
      lastTick = now;
   }

   public static long now() {
      return System.currentTimeMillis();
   }
}
