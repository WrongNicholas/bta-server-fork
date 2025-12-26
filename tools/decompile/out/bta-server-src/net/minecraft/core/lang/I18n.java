package net.minecraft.core.lang;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;

public class I18n {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static I18n INSTANCE;
   private Language currentLanguage;

   public static void initialize(String languageCode) {
      INSTANCE = new I18n(languageCode);
   }

   private I18n(String languageCode) {
      if (!LanguageSeeker.LANGUAGE_DIR.exists()) {
         LanguageSeeker.LANGUAGE_DIR.mkdirs();
      }

      this.reload(languageCode);
   }

   public void reload(String languageCode) {
      this.reload(languageCode, false);
   }

   public void reload(String languageCode, boolean save) {
      if (this.currentLanguage != null) {
         this.currentLanguage.onReload();
      }

      try {
         this.currentLanguage = LanguageSeeker.seek(languageCode);
         if (this.currentLanguage == null) {
            this.currentLanguage = Language.Default.INSTANCE;
         }
      } catch (IOException var4) {
         this.currentLanguage = Language.Default.INSTANCE;
      }

      LOGGER.info("{} Translation Keys", this.currentLanguage.keySize());
   }

   public static I18n getInstance() {
      return INSTANCE;
   }

   public String translateKey(String s) {
      return this.currentLanguage.translateKey(s);
   }

   public String translateKeyAndFormat(String formatKey, Object... args) {
      return this.currentLanguage.translateKeyAndFormat(formatKey, args);
   }

   public String translateNameKey(String s) {
      return this.translateKey(s + ".name");
   }

   public String translateDescKey(String s) {
      return this.translateKey(s + ".desc");
   }

   public Language getCurrentLanguage() {
      return this.currentLanguage;
   }

   public static InputStream getResourceAsStream(String path) {
      return I18n.class.getResourceAsStream(path);
   }

   public static String[] getFilesInDirectory(String directory) {
      List<String> paths = new ArrayList<>();
      if (!directory.endsWith("/")) {
         directory = directory + "/";
      }

      try {
         URI uri = I18n.class.getResource(directory).toURI();
         FileSystem fileSystem = null;
         Path myPath;
         if (uri.getScheme().equals("jar")) {
            try {
               fileSystem = FileSystems.getFileSystem(uri);
            } catch (Exception var9) {
               fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            }

            myPath = fileSystem.getPath(directory);
         } else {
            myPath = Paths.get(uri);
         }

         Stream<Path> walk = Files.walk(myPath, 1);

         try {
            Iterator<Path> it = walk.iterator();
            it.next();

            while (it.hasNext()) {
               paths.add(directory + it.next().getFileName().toString());
            }
         } catch (Throwable var10) {
            if (walk != null) {
               try {
                  walk.close();
               } catch (Throwable var8) {
                  var10.addSuppressed(var8);
               }
            }

            throw var10;
         }

         if (walk != null) {
            walk.close();
         }

         if (fileSystem != null) {
            fileSystem.close();
         }
      } catch (Exception var11) {
         LOGGER.error("Exception while loading all files in directory '{}' in translator!", directory, var11);
      }

      return paths.toArray(new String[0]);
   }
}
