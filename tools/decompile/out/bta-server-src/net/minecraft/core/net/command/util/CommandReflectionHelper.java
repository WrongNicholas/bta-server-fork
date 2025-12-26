package net.minecraft.core.net.command.util;

import com.b100.utils.FileUtils;
import com.b100.utils.interfaces.Condition;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CommandReflectionHelper {
   public static List<Class<?>> getAllClasses(Condition<String> classNameCondition) {
      List<Class<?>> classes = new ArrayList<>();

      for (String classPathEntry : System.getProperty("java.class.path").split(";")) {
         File file = new File(classPathEntry);
         if (file.isDirectory()) {
            int l = file.getAbsolutePath().length() + 1;

            for (File file2 : FileUtils.getAllFiles(file)) {
               if (file2.getName().endsWith(".class")) {
                  tryAddClass(classes, file2.getAbsolutePath().substring(l), classNameCondition);
               }
            }
         }

         if (file.isFile()) {
            try {
               ZipFile zip = new ZipFile(file);
               Enumeration<? extends ZipEntry> entries = zip.entries();

               while (entries.hasMoreElements()) {
                  String entry = entries.nextElement().toString();
                  if (entry.endsWith(".class")) {
                     tryAddClass(classes, entry, classNameCondition);
                  }
               }

               zip.close();
            } catch (IOException var11) {
               var11.printStackTrace();
            }
         }
      }

      return classes;
   }

   private static void tryAddClass(List<Class<?>> classes, String fileName, Condition<String> classNameCondition) {
      String className = fileName.substring(0, fileName.length() - 6);
      className = className.replace('\\', '.');
      className = className.replace('/', '.');
      if (classNameCondition == null || classNameCondition.isTrue(className)) {
         Class<?> clazz = null;

         try {
            clazz = Class.forName(className);
         } catch (Throwable var6) {
            System.err.println(var6.getClass().getName() + ": " + var6.getMessage());
         }

         if (clazz != null) {
            classes.add(clazz);
         }
      }
   }
}
