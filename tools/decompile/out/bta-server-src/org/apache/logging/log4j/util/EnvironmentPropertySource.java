package org.apache.logging.log4j.util;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class EnvironmentPropertySource implements PropertySource {
   private static final String PREFIX = "LOG4J_";
   private static final int DEFAULT_PRIORITY = 100;

   @Override
   public int getPriority() {
      return 100;
   }

   private void logException(SecurityException e) {
      LowLevelLogUtil.logException("The system environment variables are not available to Log4j due to security restrictions: " + e, e);
   }

   @Override
   public void forEach(final BiConsumer<String, String> action) {
      Map<String, String> getenv;
      try {
         getenv = System.getenv();
      } catch (SecurityException var6) {
         this.logException(var6);
         return;
      }

      for (Entry<String, String> entry : getenv.entrySet()) {
         String key = entry.getKey();
         if (key.startsWith("LOG4J_")) {
            action.accept(key.substring("LOG4J_".length()), entry.getValue());
         }
      }
   }

   @Override
   public CharSequence getNormalForm(final Iterable<? extends CharSequence> tokens) {
      StringBuilder sb = new StringBuilder("LOG4J");
      boolean empty = true;

      for (CharSequence token : tokens) {
         empty = false;
         sb.append('_');

         for (int i = 0; i < token.length(); i++) {
            sb.append(Character.toUpperCase(token.charAt(i)));
         }
      }

      return empty ? null : sb.toString();
   }

   @Override
   public Collection<String> getPropertyNames() {
      try {
         return System.getenv().keySet();
      } catch (SecurityException var2) {
         this.logException(var2);
         return PropertySource.super.getPropertyNames();
      }
   }

   @Override
   public String getProperty(String key) {
      try {
         return System.getenv(key);
      } catch (SecurityException var3) {
         this.logException(var3);
         return PropertySource.super.getProperty(key);
      }
   }

   @Override
   public boolean containsProperty(String key) {
      try {
         return System.getenv().containsKey(key);
      } catch (SecurityException var3) {
         this.logException(var3);
         return PropertySource.super.containsProperty(key);
      }
   }
}
