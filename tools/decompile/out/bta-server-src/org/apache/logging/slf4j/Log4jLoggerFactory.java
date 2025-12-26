package org.apache.logging.slf4j;

import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.spi.AbstractLoggerAdapter;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class Log4jLoggerFactory extends AbstractLoggerAdapter<Logger> implements ILoggerFactory {
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private static final String SLF4J_PACKAGE = "org.slf4j";
   private static final Predicate<Class<?>> CALLER_PREDICATE = clazz -> !AbstractLoggerAdapter.class.equals(clazz) && !clazz.getName().startsWith("org.slf4j");
   private static final String TO_SLF4J_CONTEXT = "org.apache.logging.slf4j.SLF4JLoggerContext";
   private final Log4jMarkerFactory markerFactory;

   public Log4jLoggerFactory(final Log4jMarkerFactory markerFactory) {
      this.markerFactory = markerFactory;
   }

   protected Logger newLogger(final String name, final LoggerContext context) {
      String key = "ROOT".equals(name) ? "" : name;
      return new Log4jLogger(this.markerFactory, this.validateContext(context).getLogger(key), name);
   }

   @Override
   protected LoggerContext getContext() {
      Class<?> anchor = LogManager.getFactory().isClassLoaderDependent() ? StackLocatorUtil.getCallerClass(Log4jLoggerFactory.class, CALLER_PREDICATE) : null;
      LOGGER.trace("Log4jLoggerFactory.getContext() found anchor {}", anchor);
      return anchor == null ? LogManager.getContext(false) : this.getContext(anchor);
   }

   Log4jMarkerFactory getMarkerFactory() {
      return this.markerFactory;
   }

   private LoggerContext validateContext(final LoggerContext context) {
      if ("org.apache.logging.slf4j.SLF4JLoggerContext".equals(context.getClass().getName())) {
         throw new LoggingException("log4j-slf4j-impl cannot be present with log4j-to-slf4j");
      } else {
         return context;
      }
   }
}
