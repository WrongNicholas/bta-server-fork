package net.minecraft.core.util.helper;

public class MathHelper {
   private static final float[] SIN_TABLE = new float[65536];
   public static final float PI = (float) Math.PI;
   public static final float DEG_TO_RAD = (float)Math.toRadians(1.0);
   public static final float RAD_TO_DEG = (float)Math.toDegrees(1.0);

   public static float sin(float f) {
      return SIN_TABLE[(int)(f * 10430.38F) & 65535];
   }

   public static float cos(float f) {
      return SIN_TABLE[(int)(f * 10430.38F + 16384.0F) & 65535];
   }

   public static float sqrt_float(float f) {
      return (float)Math.sqrt(f);
   }

   public static float sqrt(double d) {
      return (float)Math.sqrt(d);
   }

   public static int floor_float(float f) {
      int i = (int)f;
      return f >= i ? i : i - 1;
   }

   public static int floor(double d) {
      int truncated = (int)d;
      return d >= truncated ? truncated : truncated - 1;
   }

   public static int ceil(double d) {
      int truncated = (int)d;
      return d == truncated ? truncated : truncated + 1;
   }

   public static int round(double d) {
      int truncated = (int)d;
      return d <= truncated + 0.5 ? truncated : truncated + 1;
   }

   public static float abs(float f) {
      return f < 0.0F ? -f : f;
   }

   public static double abs_max(double d, double d1) {
      if (d < 0.0) {
         d = -d;
      }

      if (d1 < 0.0) {
         d1 = -d1;
      }

      return Math.max(d, d1);
   }

   public static int bucketInt(int value, int bucket) {
      return value < 0 ? -((-value - 1) / bucket) - 1 : value / bucket;
   }

   public static double clamp(double value, double min, double max) {
      return value > max ? max : Math.max(value, min);
   }

   public static float clamp(float value, float min, float max) {
      return value > max ? max : Math.max(value, min);
   }

   public static int clamp(int value, int min, int max) {
      return value > max ? max : Math.max(value, min);
   }

   public static byte clamp(byte value, byte min, byte max) {
      return value > max ? max : (byte)Math.max((int)value, (int)min);
   }

   public static float lerp(float a, float b, float amount) {
      return a + (b - a) * amount;
   }

   public static double lerp(double a, double b, double amount) {
      return a + (b - a) * amount;
   }

   public static float getInterpolate(float[] array, float index) {
      int index0 = (int)index;
      if (index0 == index) {
         return array[index0];
      } else {
         int index1 = index0 + 1;
         float val0 = array[index0];
         float val1 = array[index1];
         return lerp(val0, val1, index - index0);
      }
   }

   public static boolean stringNullOrLengthZero(String s) {
      return s == null || s.length() == 0;
   }

   public static int ceilInt(int num, int denom) {
      return (int)Math.ceil((float)num / denom);
   }

   public static double normalizeRotation(double rotation) {
      double rotNorm = unsignedMod(rotation, 360.0);
      return rotNorm < 180.0 ? rotNorm : rotNorm - 360.0;
   }

   public static float normalizeRotation(float rotation) {
      float rotNorm = (float)unsignedMod(rotation, 360.0);
      return rotNorm < 180.0F ? rotNorm : rotNorm - 360.0F;
   }

   public static double deltaAngle(double a, double b) {
      return unsignedMod(a - b + 180.0, 360.0) - 180.0;
   }

   public static double unsignedMod(double value, double n) {
      return value - Math.floor(value / n) * n;
   }

   public static float toRadians(float angleDegrees) {
      return angleDegrees * DEG_TO_RAD;
   }

   public static float toDegrees(float angleRadians) {
      return angleRadians * RAD_TO_DEG;
   }

   public static int maxAbs(int a, int b) {
      return Math.abs(a) > Math.abs(b) ? a : b;
   }

   static {
      for (int i = 0; i < 65536; i++) {
         SIN_TABLE[i] = (float)Math.sin(i * Math.PI * 2.0 / 65536.0);
      }
   }
}
