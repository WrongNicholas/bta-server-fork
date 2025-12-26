package com.b100.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import net.minecraft.core.net.CertificateHelper;

public abstract class StringUtils {
   public static String getFileContentAsString(String path) {
      validateStringNotEmpty(path);
      return getFileContentAsString(new File(path));
   }

   public static String getFileContentAsString(File file) {
      FileUtils.validateFileExists(file);

      try {
         return readInputString(Files.newInputStream(file.toPath()));
      } catch (Exception var2) {
         throw new RuntimeException("Error while reading file", var2);
      }
   }

   public static void saveStringToFile(String path, String content) {
      validateStringNotEmpty(path);
      validateStringNotEmpty(content);
      saveStringToFile(new File(path).getAbsoluteFile(), content);
   }

   public static void saveStringToFile(File file, String content) {
      FileUtils.createNewFile(file);
      validateStringNotEmpty(content);
      if (file == null) {
         throw new NullPointerException();
      } else if (content == null) {
         throw new NullPointerException();
      } else {
         try {
            FileUtils.createNewFile(file);
            FileWriter fw = new FileWriter(file);
            fw.write(content);
            fw.close();
         } catch (Exception var3) {
            throw new RuntimeException(file.getAbsolutePath(), var3);
         }
      }
   }

   public static String readInputString(InputStream inputStream) {
      if (inputStream == null) {
         throw new NullPointerException();
      } else {
         try {
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String line = null;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
               if (firstLine) {
                  firstLine = false;
               } else {
                  line = "\n" + line;
               }

               builder.append(line);
            }

            br.close();
            reader.close();
            return builder.toString();
         } catch (Exception var6) {
            throw new RuntimeException(var6);
         }
      }
   }

   public static String getWebsiteContentAsString(String url) {
      validateStringNotEmpty(url);
      URL u = null;
      InputStream is = null;

      try {
         u = new URL(url);
      } catch (Exception var4) {
         throw new RuntimeException(url, var4);
      }

      try {
         URLConnection connection = u.openConnection();
         if (connection instanceof HttpsURLConnection) {
            connection.setDoInput(true);
            connection.setDoOutput(false);
            CertificateHelper.install((HttpsURLConnection)connection);
         }

         connection.connect();
         is = connection.getInputStream();
      } catch (Exception var5) {
         throw new RuntimeException(u.toString(), var5);
      }

      return readInputString(is);
   }

   public static boolean isStringEmpty(String string) {
      return string == null || string.length() == 0;
   }

   public static void validateStringNotEmpty(String string) {
      if (string == null) {
         throw new NullPointerException();
      } else if (string.length() == 0) {
         throw new RuntimeException("Empty String");
      }
   }

   public static String[] toArray(List<String> list) {
      String[] array = new String[list.size()];

      for (int i = 0; i < array.length; i++) {
         array[i] = list.get(i);
      }

      return array;
   }

   public static String getResourceAsString(String string) {
      return readInputString(StringUtils.class.getResourceAsStream(string));
   }

   public static String substring(String string, int beginIndex, int endIndex) {
      if (beginIndex > string.length()) {
         return "";
      } else {
         return endIndex > string.length() ? string.substring(beginIndex) : string.substring(beginIndex, endIndex);
      }
   }
}
