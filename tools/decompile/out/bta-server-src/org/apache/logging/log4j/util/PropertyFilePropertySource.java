package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertyFilePropertySource extends PropertiesPropertySource {
   public PropertyFilePropertySource(final String fileName) {
      this(fileName, true);
   }

   public PropertyFilePropertySource(final String fileName, final boolean useTccl) {
      super(loadPropertiesFile(fileName, useTccl));
   }

   private static Properties loadPropertiesFile(final String fileName, final boolean useTccl) {
      Properties props = new Properties();

      for (URL url : LoaderUtil.findResources(fileName, useTccl)) {
         try (InputStream in = url.openStream()) {
            props.load(in);
         } catch (IOException var18) {
            LowLevelLogUtil.logException("Unable to read " + url, var18);
         }
      }

      return props;
   }
}
