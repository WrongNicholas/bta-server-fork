package com.mojang.logging;

import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.spi.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.event.Level;

public class LogUtils {
   public static final String FATAL_MARKER_ID = "FATAL";
   public static final Marker FATAL_MARKER = MarkerFactory.getMarker("FATAL");

   public static boolean isLoggerActive() {
      LoggerContext loggerContext = LogManager.getContext();
      if (loggerContext instanceof LifeCycle) {
         LifeCycle lifeCycle = (LifeCycle)loggerContext;
         return !lifeCycle.isStopped();
      } else {
         return true;
      }
   }

   public static void configureRootLoggingLevel(Level level) {
      org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext)LogManager.getContext(false);
      Configuration config = ctx.getConfiguration();
      LoggerConfig loggerConfig = config.getLoggerConfig("");
      loggerConfig.setLevel(convertLevel(level));
      ctx.updateLoggers();
   }

   private static org.apache.logging.log4j.Level convertLevel(Level level) {
      switch (level) {
         case INFO:
            return org.apache.logging.log4j.Level.INFO;
         case WARN:
            return org.apache.logging.log4j.Level.WARN;
         case DEBUG:
            return org.apache.logging.log4j.Level.DEBUG;
         case ERROR:
            return org.apache.logging.log4j.Level.ERROR;
         case TRACE:
            return org.apache.logging.log4j.Level.TRACE;
         default:
            throw new IncompatibleClassChangeError();
      }
   }

   public static Object defer(final Supplier<Object> result) {
      class ToString {
         @Override
         public String toString() {
            return result.get().toString();
         }
      }

      return new ToString();
   }

   public static Logger getLogger() {
      return LoggerFactory.getLogger(getCallerClass());
   }

   private static Class<?> getCallerClass() {
      try {
         StackTraceElement[] stElements = Thread.currentThread().getStackTrace();

         for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(LogUtils.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
               return Class.forName(ste.getClassName());
            }
         }
      } catch (ClassNotFoundException var3) {
         System.err.print("Could not find calling class!");
      }

      return LogUtils.class;
   }
}
