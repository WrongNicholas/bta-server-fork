package com.b100.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public abstract class FileUtils {
   public static void createFolder(File file) {
      try {
         if (file == null) {
            throw new NullPointerException();
         } else {
            if (file.exists()) {
               if (file.isDirectory()) {
                  return;
               }

               file.delete();
            }

            if (!file.mkdirs()) {
               throw new RuntimeException("Folder not created!");
            }
         }
      } catch (Exception var2) {
         throw new RuntimeException("Creating Folder: " + file);
      }
   }

   public static void createFolderForFile(File file) {
      if (file == null) {
         throw new NullPointerException();
      } else {
         File parent = file.getParentFile();
         if (parent != null) {
            createFolder(parent);
         }
      }
   }

   public static File createNewFile(File file) {
      if (file == null) {
         throw new NullPointerException();
      } else {
         if (file.exists()) {
            file.delete();
         }

         createFile(file);
         return file;
      }
   }

   public static void createFile(File file) {
      if (file == null) {
         throw new NullPointerException();
      } else if (!file.exists()) {
         try {
            createFolderForFile(file);
            if (!file.createNewFile()) {
               throw new RuntimeException("File not created!");
            }
         } catch (Exception var2) {
            throw new RuntimeException("Creating File: " + file, var2);
         }
      }
   }

   public static String getFileExtension(File file) {
      if (file == null) {
         throw new NullPointerException();
      } else {
         String path = file.getAbsoluteFile().getAbsolutePath();
         String ext = "";

         for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '/' || c == '\\') {
               ext = "";
            } else if (c == '.') {
               ext = "";
            } else {
               ext = ext + c;
            }
         }

         return ext;
      }
   }

   public static String getFileExtension(String path) {
      StringUtils.validateStringNotEmpty(path);
      return getFileExtension(new File(path));
   }

   public static void copyAll(String from, String to) {
      StringUtils.validateStringNotEmpty(from);
      StringUtils.validateStringNotEmpty(to);
      copyAll(new File(from), new File(to));
   }

   public static void copy(String from, String to) {
      StringUtils.validateStringNotEmpty(from);
      StringUtils.validateStringNotEmpty(to);
      copy(new File(from), new File(to));
   }

   public static void copyAll(File from, File to) {
      if (from == null) {
         throw new NullPointerException();
      } else if (to == null) {
         throw new NullPointerException();
      } else {
         List<File> files = getAllFiles(from);
         String fromPath = from.getAbsolutePath();
         String toPath = to.getAbsolutePath();

         for (File oldFile : files) {
            String oldPath = oldFile.getAbsolutePath();
            if (!oldPath.startsWith(fromPath)) {
               throw new RuntimeException(oldFile.getAbsolutePath());
            }

            String newPath = toPath + oldPath.substring(fromPath.length());
            File newFile = new File(newPath);
            copy(oldFile, newFile);
         }
      }
   }

   public static void copy(File from, File to) {
      if (from == null) {
         throw new NullPointerException();
      } else if (to == null) {
         throw new NullPointerException();
      } else if (!from.exists()) {
         throw new RuntimeException("File doesn't exist: " + from.getAbsolutePath());
      } else {
         try {
            createNewFile(to);
            InputStream in = Files.newInputStream(from.toPath());
            OutputStream out = Files.newOutputStream(to.toPath());
            StreamUtils.transferData(in, out);
            in.close();
            out.close();
         } catch (Exception var4) {
            throw new RuntimeException("Could not copy from " + from.getAbsolutePath() + " to " + to.getAbsolutePath(), var4);
         }
      }
   }

   public static List<File> getAllFiles(File folder) {
      return getAllFiles(folder, false);
   }

   public static List<File> getAllFiles(List<File> files, File folder) {
      return getAllFiles(files, folder, false);
   }

   public static List<File> getAllFiles(File folder, boolean includeFolders) {
      if (folder == null) {
         throw new NullPointerException();
      } else {
         return getAllFiles(new ArrayList<>(), folder, includeFolders);
      }
   }

   public static List<File> getAllFiles(List<File> files, File folder, boolean includeFolders) {
      if (files == null) {
         throw new NullPointerException();
      } else if (folder == null) {
         throw new NullPointerException();
      } else {
         File[] files2 = folder.listFiles();

         for (File file : files2) {
            if (file.isFile()) {
               files.add(file);
            }

            if (file.isDirectory()) {
               getAllFiles(files, file, includeFolders);
               if (includeFolders) {
                  files.add(file);
               }
            }
         }

         return files;
      }
   }

   public static void validateFileExists(File file) {
      if (file == null) {
         throw new NullPointerException();
      } else if (!file.exists()) {
         throw new RuntimeException("File " + file + " doesn't exist!");
      } else if (!file.isFile()) {
         throw new RuntimeException("Not a file: " + file);
      }
   }

   public static void validateFolderExists(File folder) {
      if (folder == null) {
         throw new NullPointerException();
      } else if (!folder.exists()) {
         throw new RuntimeException("Folder " + folder + " doesn't exist!");
      } else if (!folder.isDirectory()) {
         throw new RuntimeException("Not a folder: " + folder);
      }
   }

   public static void downloadFile(String url, File file) throws IOException {
      downloadFile(new URL(url), file);
   }

   public static void downloadFile(URL url, File file) throws IOException {
      createNewFile(file);
      BufferedInputStream in = new BufferedInputStream(url.openStream());
      BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
      StreamUtils.transferDataAndClose(in, out);
   }
}
