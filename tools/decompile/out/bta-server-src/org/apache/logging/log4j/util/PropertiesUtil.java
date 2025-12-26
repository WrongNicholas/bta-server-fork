package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public final class PropertiesUtil {
   private static final String LOG4J_PROPERTIES_FILE_NAME = "log4j2.component.properties";
   private static final String LOG4J_SYSTEM_PROPERTIES_FILE_NAME = "log4j2.system.properties";
   private static final PropertiesUtil LOG4J_PROPERTIES = new PropertiesUtil("log4j2.component.properties", false);
   private final PropertiesUtil.Environment environment;

   public PropertiesUtil(final Properties props) {
      this(new PropertiesPropertySource(props));
   }

   public PropertiesUtil(final String propertiesFileName) {
      this(propertiesFileName, true);
   }

   private PropertiesUtil(final String propertiesFileName, final boolean useTccl) {
      this(new PropertyFilePropertySource(propertiesFileName, useTccl));
   }

   PropertiesUtil(final PropertySource source) {
      this.environment = new PropertiesUtil.Environment(source);
   }

   static Properties loadClose(final InputStream in, final Object source) {
      Properties props = new Properties();
      if (null != in) {
         try {
            props.load(in);
         } catch (IOException var12) {
            LowLevelLogUtil.logException("Unable to read " + source, var12);
         } finally {
            try {
               in.close();
            } catch (IOException var11) {
               LowLevelLogUtil.logException("Unable to close " + source, var11);
            }
         }
      }

      return props;
   }

   public static PropertiesUtil getProperties() {
      return LOG4J_PROPERTIES;
   }

   public void addPropertySource(PropertySource propertySource) {
      if (this.environment != null) {
         this.environment.addPropertySource(propertySource);
      }
   }

   public boolean hasProperty(final String name) {
      return this.environment.containsKey(name);
   }

   public boolean getBooleanProperty(final String name) {
      return this.getBooleanProperty(name, false);
   }

   public boolean getBooleanProperty(final String name, final boolean defaultValue) {
      String prop = this.getStringProperty(name);
      return prop == null ? defaultValue : "true".equalsIgnoreCase(prop);
   }

   public boolean getBooleanProperty(final String name, final boolean defaultValueIfAbsent, final boolean defaultValueIfPresent) {
      String prop = this.getStringProperty(name);
      return prop == null ? defaultValueIfAbsent : (prop.isEmpty() ? defaultValueIfPresent : "true".equalsIgnoreCase(prop));
   }

   public Boolean getBooleanProperty(final String[] prefixes, String key, Supplier<Boolean> supplier) {
      for (String prefix : prefixes) {
         if (this.hasProperty(prefix + key)) {
            return this.getBooleanProperty(prefix + key);
         }
      }

      return supplier != null ? supplier.get() : null;
   }

   public Charset getCharsetProperty(final String name) {
      return this.getCharsetProperty(name, Charset.defaultCharset());
   }

   public Charset getCharsetProperty(final String name, final Charset defaultValue) {
      String charsetName = this.getStringProperty(name);
      if (charsetName == null) {
         return defaultValue;
      } else if (Charset.isSupported(charsetName)) {
         return Charset.forName(charsetName);
      } else {
         ResourceBundle bundle = getCharsetsResourceBundle();
         if (bundle.containsKey(name)) {
            String mapped = bundle.getString(name);
            if (Charset.isSupported(mapped)) {
               return Charset.forName(mapped);
            }
         }

         LowLevelLogUtil.log("Unable to get Charset '" + charsetName + "' for property '" + name + "', using default " + defaultValue + " and continuing.");
         return defaultValue;
      }
   }

   public double getDoubleProperty(final String name, final double defaultValue) {
      String prop = this.getStringProperty(name);
      if (prop != null) {
         try {
            return Double.parseDouble(prop);
         } catch (Exception var6) {
         }
      }

      return defaultValue;
   }

   public int getIntegerProperty(final String name, final int defaultValue) {
      String prop = this.getStringProperty(name);
      if (prop != null) {
         try {
            return Integer.parseInt(prop.trim());
         } catch (Exception var5) {
         }
      }

      return defaultValue;
   }

   public Integer getIntegerProperty(final String[] prefixes, String key, Supplier<Integer> supplier) {
      for (String prefix : prefixes) {
         if (this.hasProperty(prefix + key)) {
            return this.getIntegerProperty(prefix + key, 0);
         }
      }

      return supplier != null ? supplier.get() : null;
   }

   public long getLongProperty(final String name, final long defaultValue) {
      String prop = this.getStringProperty(name);
      if (prop != null) {
         try {
            return Long.parseLong(prop);
         } catch (Exception var6) {
         }
      }

      return defaultValue;
   }

   public Long getLongProperty(final String[] prefixes, String key, Supplier<Long> supplier) {
      for (String prefix : prefixes) {
         if (this.hasProperty(prefix + key)) {
            return this.getLongProperty(prefix + key, 0L);
         }
      }

      return supplier != null ? supplier.get() : null;
   }

   public Duration getDurationProperty(final String name, Duration defaultValue) {
      String prop = this.getStringProperty(name);
      return prop != null ? PropertiesUtil.TimeUnit.getDuration(prop) : defaultValue;
   }

   public Duration getDurationProperty(final String[] prefixes, String key, Supplier<Duration> supplier) {
      for (String prefix : prefixes) {
         if (this.hasProperty(prefix + key)) {
            return this.getDurationProperty(prefix + key, null);
         }
      }

      return supplier != null ? supplier.get() : null;
   }

   public String getStringProperty(final String[] prefixes, String key, Supplier<String> supplier) {
      for (String prefix : prefixes) {
         String result = this.getStringProperty(prefix + key);
         if (result != null) {
            return result;
         }
      }

      return supplier != null ? supplier.get() : null;
   }

   public String getStringProperty(final String name) {
      return this.environment.get(name);
   }

   public String getStringProperty(final String name, final String defaultValue) {
      String prop = this.getStringProperty(name);
      return prop == null ? defaultValue : prop;
   }

   public static Properties getSystemProperties() {
      try {
         return new Properties(System.getProperties());
      } catch (SecurityException var1) {
         LowLevelLogUtil.logException("Unable to access system properties.", var1);
         return new Properties();
      }
   }

   public void reload() {
      this.environment.reload();
   }

   public static Properties extractSubset(final Properties properties, final String prefix) {
      Properties subset = new Properties();
      if (prefix != null && prefix.length() != 0) {
         String prefixToMatch = prefix.charAt(prefix.length() - 1) != '.' ? prefix + '.' : prefix;
         List<String> keys = new ArrayList<>();

         for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefixToMatch)) {
               subset.setProperty(key.substring(prefixToMatch.length()), properties.getProperty(key));
               keys.add(key);
            }
         }

         for (String keyx : keys) {
            properties.remove(keyx);
         }

         return subset;
      } else {
         return subset;
      }
   }

   static ResourceBundle getCharsetsResourceBundle() {
      return ResourceBundle.getBundle("Log4j-charsets");
   }

   public static Map<String, Properties> partitionOnCommonPrefixes(final Properties properties) {
      return partitionOnCommonPrefixes(properties, false);
   }

   public static Map<String, Properties> partitionOnCommonPrefixes(final Properties properties, final boolean includeBaseKey) {
      Map<String, Properties> parts = new ConcurrentHashMap<>();

      for (String key : properties.stringPropertyNames()) {
         int idx = key.indexOf(46);
         if (idx < 0) {
            if (includeBaseKey) {
               if (!parts.containsKey(key)) {
                  parts.put(key, new Properties());
               }

               parts.get(key).setProperty("", properties.getProperty(key));
            }
         } else {
            String prefix = key.substring(0, idx);
            if (!parts.containsKey(prefix)) {
               parts.put(prefix, new Properties());
            }

            parts.get(prefix).setProperty(key.substring(idx + 1), properties.getProperty(key));
         }
      }

      return parts;
   }

   public boolean isOsWindows() {
      return this.getStringProperty("os.name", "").startsWith("Windows");
   }

   private static class Environment {
      private final Set<PropertySource> sources = new ConcurrentSkipListSet<>(new PropertySource.Comparator());
      private final Map<String, String> literal = new ConcurrentHashMap<>();
      private final Map<String, String> normalized = new ConcurrentHashMap<>();
      private final Map<List<CharSequence>, String> tokenized = new ConcurrentHashMap<>();

      private Environment(final PropertySource propertySource) {
         PropertyFilePropertySource sysProps = new PropertyFilePropertySource("log4j2.system.properties", false);

         try {
            sysProps.forEach((key, value) -> {
               if (System.getProperty(key) == null) {
                  System.setProperty(key, value);
               }
            });
         } catch (SecurityException var4) {
         }

         this.sources.add(propertySource);
         ServiceLoaderUtil.loadServices(PropertySource.class, MethodHandles.lookup(), false, false).forEach(this.sources::add);
         this.reload();
      }

      public void addPropertySource(PropertySource propertySource) {
         this.sources.add(propertySource);
      }

      private synchronized void reload() {
         this.literal.clear();
         this.normalized.clear();
         this.tokenized.clear();
         Set<String> keys = new HashSet<>();
         this.sources.stream().map(PropertySource::getPropertyNames).reduce(keys, (left, right) -> {
            left.addAll(right);
            return left;
         });
         keys.stream().filter(Objects::nonNull).forEach(key -> {
            List<CharSequence> tokens = PropertySource.Util.tokenize(key);
            boolean hasTokens = !tokens.isEmpty();
            this.sources.forEach(source -> {
               if (source.containsProperty(key)) {
                  String value = source.getProperty(key);
                  this.literal.putIfAbsent(key, value);
                  if (hasTokens) {
                     this.tokenized.putIfAbsent(tokens, value);
                  }
               }

               if (hasTokens) {
                  String normalKey = Objects.toString(source.getNormalForm(tokens), null);
                  if (normalKey != null && source.containsProperty(normalKey)) {
                     this.normalized.putIfAbsent(key, source.getProperty(normalKey));
                  }
               }
            });
         });
      }

      private String get(final String key) {
         if (this.normalized.containsKey(key)) {
            return this.normalized.get(key);
         } else if (this.literal.containsKey(key)) {
            return this.literal.get(key);
         } else {
            List<CharSequence> tokens = PropertySource.Util.tokenize(key);
            boolean hasTokens = !tokens.isEmpty();

            for (PropertySource source : this.sources) {
               if (hasTokens) {
                  String normalKey = Objects.toString(source.getNormalForm(tokens), null);
                  if (normalKey != null && source.containsProperty(normalKey)) {
                     return source.getProperty(normalKey);
                  }
               }

               if (source.containsProperty(key)) {
                  return source.getProperty(key);
               }
            }

            return this.tokenized.get(tokens);
         }
      }

      private boolean containsKey(final String key) {
         List<CharSequence> tokens = PropertySource.Util.tokenize(key);
         return this.normalized.containsKey(key)
            || this.literal.containsKey(key)
            || this.tokenized.containsKey(tokens)
            || this.sources.stream().anyMatch(s -> {
               CharSequence normalizedKey = s.getNormalForm(tokens);
               return s.containsProperty(key) || normalizedKey != null && s.containsProperty(normalizedKey.toString());
            });
      }
   }

   private static enum TimeUnit {
      NANOS("ns,nano,nanos,nanosecond,nanoseconds", ChronoUnit.NANOS),
      MICROS("us,micro,micros,microsecond,microseconds", ChronoUnit.MICROS),
      MILLIS("ms,milli,millis,millsecond,milliseconds", ChronoUnit.MILLIS),
      SECONDS("s,second,seconds", ChronoUnit.SECONDS),
      MINUTES("m,minute,minutes", ChronoUnit.MINUTES),
      HOURS("h,hour,hours", ChronoUnit.HOURS),
      DAYS("d,day,days", ChronoUnit.DAYS);

      private final String[] descriptions;
      private final ChronoUnit timeUnit;

      private TimeUnit(String descriptions, ChronoUnit timeUnit) {
         this.descriptions = descriptions.split(",");
         this.timeUnit = timeUnit;
      }

      ChronoUnit getTimeUnit() {
         return this.timeUnit;
      }

      static Duration getDuration(String time) {
         String value = time.trim();
         TemporalUnit temporalUnit = ChronoUnit.MILLIS;
         long timeVal = 0L;

         for (PropertiesUtil.TimeUnit timeUnit : values()) {
            for (String suffix : timeUnit.descriptions) {
               if (value.endsWith(suffix)) {
                  temporalUnit = timeUnit.timeUnit;
                  timeVal = Long.parseLong(value.substring(0, value.length() - suffix.length()));
               }
            }
         }

         return Duration.of(timeVal, temporalUnit);
      }
   }
}
