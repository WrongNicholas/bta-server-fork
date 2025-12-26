package org.apache.logging.log4j.util;

import java.util.Collection;
import java.util.Properties;
import java.util.Map.Entry;

public class PropertiesPropertySource implements PropertySource {
   private static final int DEFAULT_PRIORITY = 200;
   private static final String PREFIX = "log4j2.";
   private final Properties properties;
   private final int priority;

   public PropertiesPropertySource(final Properties properties) {
      this(properties, 200);
   }

   public PropertiesPropertySource(final Properties properties, final int priority) {
      this.properties = properties;
      this.priority = priority;
   }

   @Override
   public int getPriority() {
      return this.priority;
   }

   @Override
   public void forEach(final BiConsumer<String, String> action) {
      for (Entry<Object, Object> entry : this.properties.entrySet()) {
         action.accept((String)entry.getKey(), (String)entry.getValue());
      }
   }

   @Override
   public CharSequence getNormalForm(final Iterable<? extends CharSequence> tokens) {
      CharSequence camelCase = PropertySource.Util.joinAsCamelCase(tokens);
      return camelCase.length() > 0 ? "log4j2." + camelCase : null;
   }

   @Override
   public Collection<String> getPropertyNames() {
      return this.properties.stringPropertyNames();
   }

   @Override
   public String getProperty(String key) {
      return this.properties.getProperty(key);
   }

   @Override
   public boolean containsProperty(String key) {
      return this.getProperty(key) != null;
   }
}
