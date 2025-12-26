package net.minecraft.core.util.helper;

import com.b100.utils.FileUtils;
import com.b100.utils.interfaces.Condition;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipFile;
import org.slf4j.Logger;

public abstract class ReflectionHelper {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static List<Class<?>> getAllClasses(Condition<String> classNameCondition) {
      ClassLoader classLoader = ClassLoader.getSystemClassLoader();
      List<Class<?>> classes = new ArrayList<>();
      String[] classPathEntries = System.getProperty("java.class.path").split(";");

      for (String classPathEntry : classPathEntries) {
         File file = new File(classPathEntry);
         if (file.isDirectory()) {
            int l = file.getAbsolutePath().length() + 1;

            for (File file2 : FileUtils.getAllFiles(file)) {
               if (file2.getName().endsWith(".class")) {
                  tryAddClass(classes, classLoader, file2.getAbsolutePath().substring(l), classNameCondition);
               }
            }
         }

         if (file.isFile()) {
            try {
               ZipFile zip = new ZipFile(file);
               Enumeration<?> entries = zip.entries();

               while (entries.hasMoreElements()) {
                  String entry = entries.nextElement().toString();
                  if (entry.endsWith(".class")) {
                     tryAddClass(classes, classLoader, entry, classNameCondition);
                  }
               }

               zip.close();
            } catch (IOException var13) {
               LOGGER.error("Exception loading class entries from file '{}'!", file.getPath(), var13);
            }
         }
      }

      return classes;
   }

   private static void tryAddClass(List<Class<?>> classes, ClassLoader classLoader, String fileName, Condition<String> classNameCondition) {
      String className = fileName.substring(0, fileName.length() - 6);
      className = className.replace('\\', '.');
      className = className.replace('/', '.');
      if (classNameCondition == null || classNameCondition.isTrue(className)) {
         Class<?> clazz = null;

         try {
            clazz = classLoader.loadClass(className);
         } catch (Throwable var7) {
            System.err.println(var7.getClass().getName() + ": " + var7.getMessage());
         }

         if (clazz != null) {
            classes.add(clazz);
         }
      }
   }
}
