package net.minecraft.core.util.helper;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public abstract class Utils {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String hexString = "0123456789ABCDEF";

   public static String toHex(int value, int length) {
      int[] arr = new int[length];

      for (int i = 0; i < length; i++) {
         arr[i] = value & 15;
         value >>= 4;
      }

      StringBuilder str = new StringBuilder();

      for (int i = arr.length - 1; i >= 0; i--) {
         str.append("0123456789ABCDEF".charAt(arr[i]));
      }

      return str.toString();
   }

   public static int percentRound(double d) {
      return (int)Math.round(d * 100.0);
   }

   public static int percent(double d) {
      return (int)(d * 100.0);
   }

   public static double floor10(double d) {
      return (long)(d * 10.0) / 10.0;
   }

   public static double floor100(double d) {
      return (long)(d * 100.0) / 100.0;
   }
}
