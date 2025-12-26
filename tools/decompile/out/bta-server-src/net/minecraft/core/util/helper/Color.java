package net.minecraft.core.util.helper;

import com.b100.utils.InvalidCharacterException;
import com.b100.utils.StringReader;

public class Color {
   public static final int SHIFT_ALPHA = 24;
   public static final int SHIFT_RED = 16;
   public static final int SHIFT_GREEN = 8;
   public static final int SHIFT_BLUE = 0;
   public static final int MASK_CHANNEL = 255;
   public int value;

   public Color setRGBA(int r, int g, int b, int a) {
      r &= 255;
      g &= 255;
      b &= 255;
      a &= 255;
      this.value = (a << 24) + (r << 16) + (g << 8) + (b << 0);
      return this;
   }

   public Color setARGB(int value) {
      this.value = value;
      return this;
   }

   public Color setRGB(int r, int g, int b) {
      return this.setRGBA(r, g, b, 255);
   }

   public void parse(String colorString) {
      try {
         int red = 0;
         int green = 0;
         int blue = 0;
         int alpha = 0;
         StringReader reader = new StringReader(colorString);

         while (true) {
            char c = reader.get();
            reader.expectOne("rgba");
            reader.next();
            reader.expectAndSkip('=');
            if (c == 'r') {
               red = Integer.parseInt(reader.readUntilCharacter(','));
            } else if (c == 'g') {
               green = Integer.parseInt(reader.readUntilCharacter(','));
            } else if (c == 'b') {
               blue = Integer.parseInt(reader.readUntilCharacter(','));
            } else {
               if (c != 'a') {
                  throw new InvalidCharacterException(reader);
               }

               alpha = Integer.parseInt(reader.readUntilCharacter(','));
            }

            if (reader.remainingCharacters() == 0) {
               this.setRGBA(red, green, blue, alpha);
               return;
            }

            reader.expectAndSkip(',');
         }
      } catch (Exception var8) {
         throw new RuntimeException("Invalid Color String: \"" + colorString + "\"", var8);
      }
   }

   public int getARGB() {
      return this.value;
   }

   public int getAlpha() {
      return this.value >> 24 & 0xFF;
   }

   public int getRed() {
      return this.value >> 16 & 0xFF;
   }

   public int getGreen() {
      return this.value >> 8 & 0xFF;
   }

   public int getBlue() {
      return this.value >> 0 & 0xFF;
   }

   public String toPropertiesString() {
      return "r=" + this.getRed() + ",g=" + this.getGreen() + ",b=" + this.getBlue() + ",a=" + this.getAlpha();
   }

   public String toHexRGBA() {
      return "0x" + Utils.toHex(this.getRed(), 2) + Utils.toHex(this.getGreen(), 2) + Utils.toHex(this.getBlue(), 2) + Utils.toHex(this.getAlpha(), 2);
   }

   @Override
   public String toString() {
      return this.toHexRGBA();
   }

   public void setRGB(Color newColor) {
      this.setRGBA(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), this.getAlpha());
   }

   public void setARGB(Color newColor) {
      this.value = newColor.value;
   }

   public static int byteToIntARGB(byte a, byte r, byte g, byte b) {
      return ((a & 0xFF) << 24) + ((r & 0xFF) << 16) + ((g & 0xFF) << 8) + ((b & 0xFF) << 0);
   }

   public static int shortToIntARGB(short a, short r, short g, short b) {
      return ((a & 0xFF) << 24) + ((r & 0xFF) << 16) + ((g & 0xFF) << 8) + ((b & 0xFF) << 0);
   }

   public static int intToIntARGB(int a, int r, int g, int b) {
      return ((a & 0xFF) << 24) + ((r & 0xFF) << 16) + ((g & 0xFF) << 8) + ((b & 0xFF) << 0);
   }

   public static int floatToIntARGB(float a, float r, float g, float b) {
      return intToIntARGB((int)(a * 255.0F), (int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F));
   }

   public static int doubleToIntARGB(double a, double r, double g, double b) {
      return intToIntARGB((int)(a * 255.0), (int)(r * 255.0), (int)(g * 255.0), (int)(b * 255.0));
   }

   public static int alphaFromInt(int value) {
      return value >> 24 & 0xFF;
   }

   public static int redFromInt(int value) {
      return value >> 16 & 0xFF;
   }

   public static int greenFromInt(int value) {
      return value >> 8 & 0xFF;
   }

   public static int blueFromInt(int value) {
      return value >> 0 & 0xFF;
   }
}
