package net.minecraft.core.net;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.nio.file.Files;
import java.util.Properties;
import org.slf4j.Logger;

public class PropertyManager {
   public static Logger logger = LogUtils.getLogger();
   private Properties serverProperties = new Properties();
   private File serverPropertiesFile;

   public PropertyManager(File file) {
      this.serverPropertiesFile = file;
      if (file.exists()) {
         try {
            this.serverProperties.load(Files.newInputStream(file.toPath()));
         } catch (Exception var3) {
            logger.warn("Failed to load " + file, (Throwable)var3);
            this.generateNewProperties();
         }
      } else {
         logger.warn(file + " does not exist");
         this.generateNewProperties();
      }
   }

   public void generateNewProperties() {
      logger.info("Generating new properties file");
      this.saveProperties();
   }

   public void saveProperties() {
      try {
         this.serverProperties.store(Files.newOutputStream(this.serverPropertiesFile.toPath()), "Minecraft server properties");
      } catch (Exception var2) {
         logger.warn("Failed to save " + this.serverPropertiesFile, (Throwable)var2);
         this.generateNewProperties();
      }
   }

   public String getStringProperty(String key, String defaultValue) {
      if (!this.serverProperties.containsKey(key)) {
         this.serverProperties.setProperty(key, defaultValue);
         this.saveProperties();
      }

      return this.serverProperties.getProperty(key, defaultValue);
   }

   public int getIntProperty(String key, int value) {
      try {
         return Integer.parseInt(this.getStringProperty(key, Integer.toString(value)));
      } catch (Exception var4) {
         this.serverProperties.setProperty(key, Integer.toString(value));
         return value;
      }
   }

   public boolean getBooleanProperty(String key, boolean defaultValue) {
      try {
         return Boolean.parseBoolean(this.getStringProperty(key, Boolean.toString(defaultValue)));
      } catch (Exception var4) {
         this.serverProperties.setProperty(key, Boolean.toString(defaultValue));
         return defaultValue;
      }
   }

   public void setProperty(String key, boolean value) {
      this.serverProperties.setProperty(key, Boolean.toString(value));
      this.saveProperties();
   }

   public void setProperty(String key, int value) {
      this.serverProperties.setProperty(key, Integer.toString(value));
   }

   public void setProperty(String key, String value) {
      this.serverProperties.setProperty(key, value);
   }
}
