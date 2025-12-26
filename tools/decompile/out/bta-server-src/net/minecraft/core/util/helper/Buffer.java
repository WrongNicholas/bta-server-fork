package net.minecraft.core.util.helper;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class Buffer {
   public static ByteBuffer buffer = createBuffer(2097152);

   public static ByteBuffer createBuffer(int size) {
      return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
   }

   public static void checkBufferSize(int size) {
      if (buffer.capacity() < size) {
         buffer = createBuffer(size);
         System.out.println("Expanding buffer to " + size);
      }
   }

   public static void reset() {
      ((java.nio.Buffer)buffer).position(0);
      ((java.nio.Buffer)buffer).limit(buffer.capacity());
   }

   public static int getInt(int i) {
      int c = buffer.capacity() / 4;
      if (i >= c) {
         throw new IndexOutOfBoundsException(i + " / " + c);
      } else {
         return buffer.getInt(i);
      }
   }

   public static void put(byte[] a) {
      checkBufferSize(a.length);
      reset();
      buffer.put(a);
      ((java.nio.Buffer)buffer).limit(a.length);
      ((java.nio.Buffer)buffer).flip();
   }

   public static void put(BufferedImage image) {
      checkBufferSize(image.getWidth() * image.getHeight() * 4);
      reset();

      for (int i = 0; i < image.getWidth(); i++) {
         for (int j = 0; j < image.getHeight(); j++) {
            putARGB(j * image.getWidth() + i, image.getRGB(i, j));
         }
      }

      ((java.nio.Buffer)buffer).position(0);
      ((java.nio.Buffer)buffer).limit(image.getWidth() * image.getHeight() * 4);
   }

   public static void put4Bytes(int i, byte b0, byte b1, byte b2, byte b3) {
      buffer.put(i * 4 + 0, b0);
      buffer.put(i * 4 + 1, b1);
      buffer.put(i * 4 + 2, b2);
      buffer.put(i * 4 + 3, b3);
   }

   public static void putARGB(int i, int value) {
      byte a = (byte)(value >> 24 & 0xFF);
      byte r = (byte)(value >> 16 & 0xFF);
      byte g = (byte)(value >> 8 & 0xFF);
      byte b = (byte)(value >> 0 & 0xFF);
      put4Bytes(i, r, g, b, a);
   }

   public static void putColor(int i, Color color) {
      putARGB(i, color.value);
   }

   public static int getARGB(int i) {
      int a = buffer.get(i * 4 + 3) & 255;
      int r = buffer.get(i * 4 + 0) & 255;
      int g = buffer.get(i * 4 + 1) & 255;
      int b = buffer.get(i * 4 + 2) & 255;
      return (a << 24) + (r << 16) + (g << 8) + b;
   }
}
