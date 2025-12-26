package com.b100.utils;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import javax.imageio.ImageIO;

public abstract class ImageUtils {
   public static BufferedImage loadExternalImage(String path) {
      return loadExternalImage(new File(path));
   }

   public static BufferedImage loadExternalImage(File file) {
      Utils.requireNonNull(file);
      if (!file.exists()) {
         throw new RuntimeException("File doesn't exist: " + file.getAbsolutePath());
      } else if (!file.isFile()) {
         throw new RuntimeException("Not a file: " + file.getAbsolutePath());
      } else {
         InputStream stream;
         try {
            stream = Files.newInputStream(file.toPath());
         } catch (Exception var10) {
            throw new RuntimeException("Could not open file", var10);
         }

         BufferedImage image;
         try {
            image = ImageIO.read(stream);
         } catch (Exception var8) {
            throw new RuntimeException("Could not read Image", var8);
         } finally {
            StreamUtils.close(stream);
         }

         return image;
      }
   }

   public static void saveExternalImage(BufferedImage image, String path) {
      saveExternalImage(image, new File(path));
   }

   public static void saveExternalImage(BufferedImage image, File file) {
      FileUtils.createNewFile(file);

      try {
         ImageIO.write(image, "png", file);
      } catch (IOException var3) {
         throw new RuntimeException("Error saving Image", var3);
      }
   }

   private static class TransferableImage implements Transferable {
      private Image image;

      public TransferableImage(Image image) {
         this.image = Utils.requireNonNull(image);
      }

      @Override
      public DataFlavor[] getTransferDataFlavors() {
         return new DataFlavor[]{DataFlavor.imageFlavor};
      }

      @Override
      public boolean isDataFlavorSupported(DataFlavor flavor) {
         return flavor == DataFlavor.imageFlavor;
      }

      @Override
      public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
         if (flavor == DataFlavor.imageFlavor) {
            return this.image;
         } else {
            throw new UnsupportedFlavorException(flavor);
         }
      }
   }
}
