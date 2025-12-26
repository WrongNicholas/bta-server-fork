package org.apache.logging.log4j.core.lookup;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;

public final class PropertiesLookup implements StrLookup {
   private final Map<String, String> contextProperties;
   private final Map<String, PropertiesLookup.ConfigurationPropertyResult> configurationProperties;

   public PropertiesLookup(final Property[] configProperties, final Map<String, String> contextProperties) {
      this.contextProperties = contextProperties == null ? Collections.emptyMap() : contextProperties;
      this.configurationProperties = configProperties == null ? Collections.emptyMap() : createConfigurationPropertyMap(configProperties);
   }

   public PropertiesLookup(final Map<String, String> properties) {
      this(Property.EMPTY_ARRAY, properties);
   }

   @Override
   public String lookup(final LogEvent event, final String key) {
      return this.lookup(key);
   }

   @Override
   public String lookup(final String key) {
      LookupResult result = this.evaluate(key);
      return result == null ? null : result.value();
   }

   @Override
   public LookupResult evaluate(String key) {
      if (key == null) {
         return null;
      } else {
         LookupResult configResult = this.configurationProperties.get(key);
         if (configResult != null) {
            return configResult;
         } else {
            String contextResult = this.contextProperties.get(key);
            return contextResult == null ? null : new PropertiesLookup.ContextPropertyResult(contextResult);
         }
      }
   }

   @Override
   public LookupResult evaluate(final LogEvent event, final String key) {
      return this.evaluate(key);
   }

   @Override
   public String toString() {
      return "PropertiesLookup{contextProperties=" + this.contextProperties + ", configurationProperties=" + this.configurationProperties + '}';
   }

   private static Map<String, PropertiesLookup.ConfigurationPropertyResult> createConfigurationPropertyMap(Property[] props) {
      Map<String, PropertiesLookup.ConfigurationPropertyResult> result = new HashMap<>(props.length);

      for (Property property : props) {
         result.put(property.getName(), new PropertiesLookup.ConfigurationPropertyResult(property.getRawValue()));
      }

      return result;
   }

   private static final class ConfigurationPropertyResult implements LookupResult {
      private final String value;

      ConfigurationPropertyResult(String value) {
         this.value = Objects.requireNonNull(value, "value is required");
      }

      @Override
      public String value() {
         return this.value;
      }

      @Override
      public boolean isLookupEvaluationAllowedInValue() {
         return true;
      }

      @Override
      public String toString() {
         return "ConfigurationPropertyResult{'" + this.value + "'}";
      }
   }

   private static final class ContextPropertyResult implements LookupResult {
      private final String value;

      ContextPropertyResult(String value) {
         this.value = Objects.requireNonNull(value, "value is required");
      }

      @Override
      public String value() {
         return this.value;
      }

      @Override
      public boolean isLookupEvaluationAllowedInValue() {
         return false;
      }

      @Override
      public String toString() {
         return "ContextPropertyResult{'" + this.value + "'}";
      }
   }
}
