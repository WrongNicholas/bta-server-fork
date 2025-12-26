package org.apache.logging.log4j.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.async.AsyncLoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerContextSelector;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.config.properties.PropertiesConfiguration;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.impl.DefaultLogEventFactory;
import org.apache.logging.log4j.core.impl.LocationAware;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.core.impl.ReusableLogEventFactory;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Strings;

@Plugin(name = "logger", category = "Core", printObject = true)
public class LoggerConfig extends AbstractFilterable implements LocationAware {
   public static final String ROOT = "root";
   private static LogEventFactory LOG_EVENT_FACTORY = null;
   private List<AppenderRef> appenderRefs = new ArrayList<>();
   private final AppenderControlArraySet appenders = new AppenderControlArraySet();
   private final String name;
   private LogEventFactory logEventFactory;
   private Level level;
   private boolean additive = true;
   private boolean includeLocation = true;
   private LoggerConfig parent;
   private Map<Property, Boolean> propertiesMap;
   private final List<Property> properties;
   private final boolean propertiesRequireLookup;
   private final Configuration config;
   private final ReliabilityStrategy reliabilityStrategy;

   @PluginBuilderFactory
   public static <B extends LoggerConfig.Builder<B>> B newBuilder() {
      return new LoggerConfig.Builder<B>().asBuilder();
   }

   public LoggerConfig() {
      this.logEventFactory = LOG_EVENT_FACTORY;
      this.level = Level.ERROR;
      this.name = "";
      this.properties = null;
      this.propertiesRequireLookup = false;
      this.config = null;
      this.reliabilityStrategy = new DefaultReliabilityStrategy(this);
   }

   public LoggerConfig(final String name, final Level level, final boolean additive) {
      this.logEventFactory = LOG_EVENT_FACTORY;
      this.name = name;
      this.level = level;
      this.additive = additive;
      this.properties = null;
      this.propertiesRequireLookup = false;
      this.config = null;
      this.reliabilityStrategy = new DefaultReliabilityStrategy(this);
   }

   protected LoggerConfig(
      final String name,
      final List<AppenderRef> appenders,
      final Filter filter,
      final Level level,
      final boolean additive,
      final Property[] properties,
      final Configuration config,
      final boolean includeLocation
   ) {
      super(filter);
      this.logEventFactory = LOG_EVENT_FACTORY;
      this.name = name;
      this.appenderRefs = appenders;
      this.level = level;
      this.additive = additive;
      this.includeLocation = includeLocation;
      this.config = config;
      if (properties != null && properties.length > 0) {
         this.properties = Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(properties, properties.length)));
      } else {
         this.properties = null;
      }

      this.propertiesRequireLookup = containsPropertyRequiringLookup(properties);
      this.reliabilityStrategy = config.getReliabilityStrategy(this);
   }

   private static boolean containsPropertyRequiringLookup(final Property[] properties) {
      if (properties == null) {
         return false;
      } else {
         for (int i = 0; i < properties.length; i++) {
            if (properties[i].isValueNeedsLookup()) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public Filter getFilter() {
      return super.getFilter();
   }

   public String getName() {
      return this.name;
   }

   public void setParent(final LoggerConfig parent) {
      this.parent = parent;
   }

   public LoggerConfig getParent() {
      return this.parent;
   }

   public void addAppender(final Appender appender, final Level level, final Filter filter) {
      this.appenders.add(new AppenderControl(appender, level, filter));
   }

   public void removeAppender(final String name) {
      AppenderControl removed = null;

      while ((removed = this.appenders.remove(name)) != null) {
         this.cleanupFilter(removed);
      }
   }

   public Map<String, Appender> getAppenders() {
      return this.appenders.asMap();
   }

   protected void clearAppenders() {
      do {
         AppenderControl[] original = this.appenders.clear();

         for (AppenderControl ctl : original) {
            this.cleanupFilter(ctl);
         }
      } while (!this.appenders.isEmpty());
   }

   private void cleanupFilter(final AppenderControl ctl) {
      Filter filter = ctl.getFilter();
      if (filter != null) {
         ctl.removeFilter(filter);
         filter.stop();
      }
   }

   public List<AppenderRef> getAppenderRefs() {
      return this.appenderRefs;
   }

   public void setLevel(final Level level) {
      this.level = level;
   }

   public Level getLevel() {
      return this.level == null ? (this.parent == null ? Level.ERROR : this.parent.getLevel()) : this.level;
   }

   public Level getExplicitLevel() {
      return this.level;
   }

   public LogEventFactory getLogEventFactory() {
      return this.logEventFactory;
   }

   public void setLogEventFactory(final LogEventFactory logEventFactory) {
      this.logEventFactory = logEventFactory;
   }

   public boolean isAdditive() {
      return this.additive;
   }

   public void setAdditive(final boolean additive) {
      this.additive = additive;
   }

   public boolean isIncludeLocation() {
      return this.includeLocation;
   }

   @Deprecated
   public Map<Property, Boolean> getProperties() {
      if (this.properties == null) {
         return null;
      } else {
         if (this.propertiesMap == null) {
            Map<Property, Boolean> result = new HashMap<>(this.properties.size() * 2);

            for (int i = 0; i < this.properties.size(); i++) {
               result.put(this.properties.get(i), this.properties.get(i).isValueNeedsLookup());
            }

            this.propertiesMap = Collections.unmodifiableMap(result);
         }

         return this.propertiesMap;
      }
   }

   public List<Property> getPropertyList() {
      return this.properties;
   }

   public boolean isPropertiesRequireLookup() {
      return this.propertiesRequireLookup;
   }

   @PerformanceSensitive("allocation")
   public void log(final String loggerName, final String fqcn, final Marker marker, final Level level, final Message data, final Throwable t) {
      List<Property> props = this.getProperties(loggerName, fqcn, marker, level, data, t);
      LogEvent logEvent = this.logEventFactory.createEvent(loggerName, marker, fqcn, this.location(fqcn), level, data, props, t);

      try {
         this.log(logEvent, LoggerConfig.LoggerConfigPredicate.ALL);
      } finally {
         ReusableLogEventFactory.release(logEvent);
      }
   }

   private StackTraceElement location(String fqcn) {
      return this.requiresLocation() ? StackLocatorUtil.calcLocation(fqcn) : null;
   }

   @PerformanceSensitive("allocation")
   public void log(
      final String loggerName,
      final String fqcn,
      final StackTraceElement location,
      final Marker marker,
      final Level level,
      final Message data,
      final Throwable t
   ) {
      List<Property> props = this.getProperties(loggerName, fqcn, marker, level, data, t);
      LogEvent logEvent = this.logEventFactory.createEvent(loggerName, marker, fqcn, location, level, data, props, t);

      try {
         this.log(logEvent, LoggerConfig.LoggerConfigPredicate.ALL);
      } finally {
         ReusableLogEventFactory.release(logEvent);
      }
   }

   private List<Property> getProperties(
      final String loggerName, final String fqcn, final Marker marker, final Level level, final Message data, final Throwable t
   ) {
      List<Property> snapshot = this.properties;
      return snapshot != null && this.propertiesRequireLookup ? this.getPropertiesWithLookups(loggerName, fqcn, marker, level, data, t, snapshot) : snapshot;
   }

   private List<Property> getPropertiesWithLookups(
      final String loggerName, final String fqcn, final Marker marker, final Level level, final Message data, final Throwable t, final List<Property> props
   ) {
      List<Property> results = new ArrayList<>(props.size());
      LogEvent event = Log4jLogEvent.newBuilder()
         .setMessage(data)
         .setMarker(marker)
         .setLevel(level)
         .setLoggerName(loggerName)
         .setLoggerFqcn(fqcn)
         .setThrown(t)
         .build();

      for (int i = 0; i < props.size(); i++) {
         Property prop = props.get(i);
         String value = prop.evaluate(this.config.getStrSubstitutor());
         results.add(Property.createProperty(prop.getName(), prop.getRawValue(), value));
      }

      return results;
   }

   public void log(final LogEvent event) {
      this.log(event, LoggerConfig.LoggerConfigPredicate.ALL);
   }

   protected void log(final LogEvent event, final LoggerConfig.LoggerConfigPredicate predicate) {
      if (!this.isFiltered(event)) {
         this.processLogEvent(event, predicate);
      }
   }

   public ReliabilityStrategy getReliabilityStrategy() {
      return this.reliabilityStrategy;
   }

   private void processLogEvent(final LogEvent event, final LoggerConfig.LoggerConfigPredicate predicate) {
      event.setIncludeLocation(this.isIncludeLocation());
      if (predicate.allow(this)) {
         this.callAppenders(event);
      }

      this.logParent(event, predicate);
   }

   @Override
   public boolean requiresLocation() {
      if (!this.includeLocation) {
         return false;
      } else {
         AppenderControl[] controls = this.appenders.get();
         LoggerConfig loggerConfig = this;

         while (loggerConfig != null) {
            for (AppenderControl control : controls) {
               Appender appender = control.getAppender();
               if (appender instanceof LocationAware && ((LocationAware)appender).requiresLocation()) {
                  return true;
               }
            }

            if (!loggerConfig.additive) {
               break;
            }

            loggerConfig = loggerConfig.parent;
            if (loggerConfig != null) {
               controls = loggerConfig.appenders.get();
            }
         }

         return false;
      }
   }

   private void logParent(final LogEvent event, final LoggerConfig.LoggerConfigPredicate predicate) {
      if (this.additive && this.parent != null) {
         this.parent.log(event, predicate);
      }
   }

   @PerformanceSensitive("allocation")
   protected void callAppenders(final LogEvent event) {
      AppenderControl[] controls = this.appenders.get();

      for (int i = 0; i < controls.length; i++) {
         controls[i].callAppender(event);
      }
   }

   @Override
   public String toString() {
      return Strings.isEmpty(this.name) ? "root" : this.name;
   }

   @Deprecated
   public static LoggerConfig createLogger(
      final String additivity,
      final Level level,
      @PluginAttribute("name") final String loggerName,
      final String includeLocation,
      final AppenderRef[] refs,
      final Property[] properties,
      @PluginConfiguration final Configuration config,
      final Filter filter
   ) {
      if (loggerName == null) {
         LOGGER.error("Loggers cannot be configured without a name");
         return null;
      } else {
         List<AppenderRef> appenderRefs = Arrays.asList(refs);
         String name = loggerName.equals("root") ? "" : loggerName;
         boolean additive = Booleans.parseBoolean(additivity, true);
         return new LoggerConfig(name, appenderRefs, filter, level, additive, properties, config, includeLocation(includeLocation, config));
      }
   }

   @Deprecated
   public static LoggerConfig createLogger(
      @PluginAttribute(value = "additivity", defaultBoolean = true) final boolean additivity,
      @PluginAttribute("level") final Level level,
      @Required(message = "Loggers cannot be configured without a name") @PluginAttribute("name") final String loggerName,
      @PluginAttribute("includeLocation") final String includeLocation,
      @PluginElement("AppenderRef") final AppenderRef[] refs,
      @PluginElement("Properties") final Property[] properties,
      @PluginConfiguration final Configuration config,
      @PluginElement("Filter") final Filter filter
   ) {
      String name = loggerName.equals("root") ? "" : loggerName;
      return new LoggerConfig(name, Arrays.asList(refs), filter, level, additivity, properties, config, includeLocation(includeLocation, config));
   }

   protected static boolean includeLocation(final String includeLocationConfigValue) {
      return includeLocation(includeLocationConfigValue, null);
   }

   protected static boolean includeLocation(final String includeLocationConfigValue, final Configuration configuration) {
      if (includeLocationConfigValue == null) {
         LoggerContext context = null;
         if (configuration != null) {
            context = configuration.getLoggerContext();
         }

         return context != null ? !(context instanceof AsyncLoggerContext) : !AsyncLoggerContextSelector.isSelected();
      } else {
         return Boolean.parseBoolean(includeLocationConfigValue);
      }
   }

   protected final boolean hasAppenders() {
      return !this.appenders.isEmpty();
   }

   protected static LoggerConfig.LevelAndRefs getLevelAndRefs(Level level, AppenderRef[] refs, String levelAndRefs, Configuration config) {
      LoggerConfig.LevelAndRefs result = new LoggerConfig.LevelAndRefs();
      if (levelAndRefs != null) {
         if (config instanceof PropertiesConfiguration) {
            if (level != null) {
               LOGGER.warn("Level is ignored when levelAndRefs syntax is used.");
            }

            if (refs != null && refs.length > 0) {
               LOGGER.warn("Appender references are ignored when levelAndRefs syntax is used");
            }

            String[] parts = Strings.splitList(levelAndRefs);
            result.level = Level.getLevel(parts[0]);
            if (parts.length > 1) {
               List<AppenderRef> refList = new ArrayList<>();
               Arrays.stream(parts).skip(1L).forEach(ref -> refList.add(AppenderRef.createAppenderRef(ref, null, null)));
               result.refs = refList;
            }
         } else {
            LOGGER.warn("levelAndRefs are only allowed in a properties configuration. The value is ignored.");
            result.level = level;
            result.refs = (List<AppenderRef>)(refs != null ? Arrays.asList(refs) : new ArrayList<>());
         }
      } else {
         result.level = level;
         result.refs = (List<AppenderRef>)(refs != null ? Arrays.asList(refs) : new ArrayList<>());
      }

      return result;
   }

   static {
      String factory = PropertiesUtil.getProperties().getStringProperty("Log4jLogEventFactory");
      if (factory != null) {
         try {
            Class<?> clazz = Loader.loadClass(factory);
            if (clazz != null && LogEventFactory.class.isAssignableFrom(clazz)) {
               LOG_EVENT_FACTORY = (LogEventFactory)clazz.newInstance();
            }
         } catch (Exception var2) {
            LOGGER.error("Unable to create LogEventFactory {}", factory, var2);
         }
      }

      if (LOG_EVENT_FACTORY == null) {
         LOG_EVENT_FACTORY = (LogEventFactory)(Constants.ENABLE_THREADLOCALS ? new ReusableLogEventFactory() : new DefaultLogEventFactory());
      }
   }

   public static class Builder<B extends LoggerConfig.Builder<B>> implements org.apache.logging.log4j.core.util.Builder<LoggerConfig> {
      @PluginBuilderAttribute
      private Boolean additivity;
      @PluginBuilderAttribute
      private Level level;
      @PluginBuilderAttribute
      private String levelAndRefs;
      @PluginBuilderAttribute("name")
      @Required(message = "Loggers cannot be configured without a name")
      private String loggerName;
      @PluginBuilderAttribute
      private String includeLocation;
      @PluginElement("AppenderRef")
      private AppenderRef[] refs;
      @PluginElement("Properties")
      private Property[] properties;
      @PluginConfiguration
      private Configuration config;
      @PluginElement("Filter")
      private Filter filter;

      public boolean isAdditivity() {
         return this.additivity == null || this.additivity;
      }

      public B withAdditivity(boolean additivity) {
         this.additivity = additivity;
         return this.asBuilder();
      }

      public Level getLevel() {
         return this.level;
      }

      public B withLevel(Level level) {
         this.level = level;
         return this.asBuilder();
      }

      public String getLevelAndRefs() {
         return this.levelAndRefs;
      }

      public B withLevelAndRefs(String levelAndRefs) {
         this.levelAndRefs = levelAndRefs;
         return this.asBuilder();
      }

      public String getLoggerName() {
         return this.loggerName;
      }

      public B withLoggerName(String loggerName) {
         this.loggerName = loggerName;
         return this.asBuilder();
      }

      public String getIncludeLocation() {
         return this.includeLocation;
      }

      public B withIncludeLocation(String includeLocation) {
         this.includeLocation = includeLocation;
         return this.asBuilder();
      }

      public AppenderRef[] getRefs() {
         return this.refs;
      }

      public B withRefs(AppenderRef[] refs) {
         this.refs = refs;
         return this.asBuilder();
      }

      public Property[] getProperties() {
         return this.properties;
      }

      public B withProperties(Property[] properties) {
         this.properties = properties;
         return this.asBuilder();
      }

      public Configuration getConfig() {
         return this.config;
      }

      public B withConfig(Configuration config) {
         this.config = config;
         return this.asBuilder();
      }

      public Filter getFilter() {
         return this.filter;
      }

      public B withtFilter(Filter filter) {
         this.filter = filter;
         return this.asBuilder();
      }

      public LoggerConfig build() {
         String name = this.loggerName.equals("root") ? "" : this.loggerName;
         LoggerConfig.LevelAndRefs container = LoggerConfig.getLevelAndRefs(this.level, this.refs, this.levelAndRefs, this.config);
         boolean useLocation = LoggerConfig.includeLocation(this.includeLocation, this.config);
         return new LoggerConfig(name, container.refs, this.filter, container.level, this.isAdditivity(), this.properties, this.config, useLocation);
      }

      public B asBuilder() {
         return (B)this;
      }
   }

   protected static class LevelAndRefs {
      public Level level;
      public List<AppenderRef> refs;
   }

   protected static enum LoggerConfigPredicate {
      ALL {
         @Override
         boolean allow(final LoggerConfig config) {
            return true;
         }
      },
      ASYNCHRONOUS_ONLY {
         @Override
         boolean allow(final LoggerConfig config) {
            return config instanceof AsyncLoggerConfig;
         }
      },
      SYNCHRONOUS_ONLY {
         @Override
         boolean allow(final LoggerConfig config) {
            return !ASYNCHRONOUS_ONLY.allow(config);
         }
      };

      private LoggerConfigPredicate() {
      }

      abstract boolean allow(LoggerConfig config);
   }

   @Plugin(name = "root", category = "Core", printObject = true)
   public static class RootLogger extends LoggerConfig {
      @PluginBuilderFactory
      public static <B extends LoggerConfig.RootLogger.Builder<B>> B newRootBuilder() {
         return new LoggerConfig.RootLogger.Builder<B>().asBuilder();
      }

      @Deprecated
      public static LoggerConfig createLogger(
         @PluginAttribute("additivity") final String additivity,
         @PluginAttribute("level") final Level level,
         @PluginAttribute("includeLocation") final String includeLocation,
         @PluginElement("AppenderRef") final AppenderRef[] refs,
         @PluginElement("Properties") final Property[] properties,
         @PluginConfiguration final Configuration config,
         @PluginElement("Filter") final Filter filter
      ) {
         List<AppenderRef> appenderRefs = Arrays.asList(refs);
         Level actualLevel = level == null ? Level.ERROR : level;
         boolean additive = Booleans.parseBoolean(additivity, true);
         return new LoggerConfig("", appenderRefs, filter, actualLevel, additive, properties, config, includeLocation(includeLocation, config));
      }

      public static class Builder<B extends LoggerConfig.RootLogger.Builder<B>> implements org.apache.logging.log4j.core.util.Builder<LoggerConfig> {
         @PluginBuilderAttribute
         private boolean additivity;
         @PluginBuilderAttribute
         private Level level;
         @PluginBuilderAttribute
         private String levelAndRefs;
         @PluginBuilderAttribute
         private String includeLocation;
         @PluginElement("AppenderRef")
         private AppenderRef[] refs;
         @PluginElement("Properties")
         private Property[] properties;
         @PluginConfiguration
         private Configuration config;
         @PluginElement("Filter")
         private Filter filter;

         public boolean isAdditivity() {
            return this.additivity;
         }

         public B withAdditivity(boolean additivity) {
            this.additivity = additivity;
            return this.asBuilder();
         }

         public Level getLevel() {
            return this.level;
         }

         public B withLevel(Level level) {
            this.level = level;
            return this.asBuilder();
         }

         public String getLevelAndRefs() {
            return this.levelAndRefs;
         }

         public B withLevelAndRefs(String levelAndRefs) {
            this.levelAndRefs = levelAndRefs;
            return this.asBuilder();
         }

         public String getIncludeLocation() {
            return this.includeLocation;
         }

         public B withIncludeLocation(String includeLocation) {
            this.includeLocation = includeLocation;
            return this.asBuilder();
         }

         public AppenderRef[] getRefs() {
            return this.refs;
         }

         public B withRefs(AppenderRef[] refs) {
            this.refs = refs;
            return this.asBuilder();
         }

         public Property[] getProperties() {
            return this.properties;
         }

         public B withProperties(Property[] properties) {
            this.properties = properties;
            return this.asBuilder();
         }

         public Configuration getConfig() {
            return this.config;
         }

         public B withConfig(Configuration config) {
            this.config = config;
            return this.asBuilder();
         }

         public Filter getFilter() {
            return this.filter;
         }

         public B withtFilter(Filter filter) {
            this.filter = filter;
            return this.asBuilder();
         }

         public LoggerConfig build() {
            LoggerConfig.LevelAndRefs container = LoggerConfig.getLevelAndRefs(this.level, this.refs, this.levelAndRefs, this.config);
            return new LoggerConfig(
               "",
               container.refs,
               this.filter,
               container.level,
               this.additivity,
               this.properties,
               this.config,
               LoggerConfig.includeLocation(this.includeLocation, this.config)
            );
         }

         public B asBuilder() {
            return (B)this;
         }
      }
   }
}
