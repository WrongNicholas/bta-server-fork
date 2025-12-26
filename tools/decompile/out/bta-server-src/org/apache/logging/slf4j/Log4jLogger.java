package org.apache.logging.slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;
import org.slf4j.spi.LoggingEventBuilder;
import org.slf4j.spi.NOPLoggingEventBuilder;

public class Log4jLogger implements LocationAwareLogger, Serializable {
   public static final String FQCN = Log4jLogger.class.getName();
   private static final long serialVersionUID = 7869000638091304316L;
   private transient ExtendedLogger logger;
   private final String name;
   private transient Log4jMarkerFactory markerFactory;

   public Log4jLogger(final Log4jMarkerFactory markerFactory, final ExtendedLogger logger, final String name) {
      this.markerFactory = markerFactory;
      this.logger = logger;
      this.name = name;
   }

   @Override
   public void trace(final String format) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, format);
   }

   @Override
   public void trace(final String format, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, format, o);
   }

   @Override
   public void trace(final String format, final Object arg1, final Object arg2) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, format, arg1, arg2);
   }

   @Override
   public void trace(final String format, final Object... args) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, format, args);
   }

   @Override
   public void trace(final String format, final Throwable t) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, format, t);
   }

   @Override
   public boolean isTraceEnabled() {
      return this.logger.isEnabled(Level.TRACE, null, null);
   }

   @Override
   public boolean isTraceEnabled(final Marker marker) {
      return this.logger.isEnabled(Level.TRACE, this.markerFactory.getLog4jMarker(marker), null);
   }

   @Override
   public void trace(final Marker marker, final String s) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, this.markerFactory.getLog4jMarker(marker), s);
   }

   @Override
   public void trace(final Marker marker, final String s, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, this.markerFactory.getLog4jMarker(marker), s, o);
   }

   @Override
   public void trace(final Marker marker, final String s, final Object o, final Object o1) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, this.markerFactory.getLog4jMarker(marker), s, o, o1);
   }

   @Override
   public void trace(final Marker marker, final String s, final Object... objects) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, this.markerFactory.getLog4jMarker(marker), s, objects);
   }

   @Override
   public void trace(final Marker marker, final String s, final Throwable throwable) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, this.markerFactory.getLog4jMarker(marker), s, throwable);
   }

   @Override
   public void debug(final String format) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, format);
   }

   @Override
   public void debug(final String format, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, format, o);
   }

   @Override
   public void debug(final String format, final Object arg1, final Object arg2) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, format, arg1, arg2);
   }

   @Override
   public void debug(final String format, final Object... args) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, format, args);
   }

   @Override
   public void debug(final String format, final Throwable t) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, format, t);
   }

   @Override
   public boolean isDebugEnabled() {
      return this.logger.isEnabled(Level.DEBUG, null, null);
   }

   @Override
   public boolean isDebugEnabled(final Marker marker) {
      return this.logger.isEnabled(Level.DEBUG, this.markerFactory.getLog4jMarker(marker), null);
   }

   @Override
   public void debug(final Marker marker, final String s) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, this.markerFactory.getLog4jMarker(marker), s);
   }

   @Override
   public void debug(final Marker marker, final String s, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, this.markerFactory.getLog4jMarker(marker), s, o);
   }

   @Override
   public void debug(final Marker marker, final String s, final Object o, final Object o1) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, this.markerFactory.getLog4jMarker(marker), s, o, o1);
   }

   @Override
   public void debug(final Marker marker, final String s, final Object... objects) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, this.markerFactory.getLog4jMarker(marker), s, objects);
   }

   @Override
   public void debug(final Marker marker, final String s, final Throwable throwable) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, this.markerFactory.getLog4jMarker(marker), s, throwable);
   }

   @Override
   public void info(final String format) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, format);
   }

   @Override
   public void info(final String format, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, format, o);
   }

   @Override
   public void info(final String format, final Object arg1, final Object arg2) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, format, arg1, arg2);
   }

   @Override
   public void info(final String format, final Object... args) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, format, args);
   }

   @Override
   public void info(final String format, final Throwable t) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, format, t);
   }

   @Override
   public boolean isInfoEnabled() {
      return this.logger.isEnabled(Level.INFO, null, null);
   }

   @Override
   public boolean isInfoEnabled(final Marker marker) {
      return this.logger.isEnabled(Level.INFO, this.markerFactory.getLog4jMarker(marker), null);
   }

   @Override
   public void info(final Marker marker, final String s) {
      this.logger.logIfEnabled(FQCN, Level.INFO, this.markerFactory.getLog4jMarker(marker), s);
   }

   @Override
   public void info(final Marker marker, final String s, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.INFO, this.markerFactory.getLog4jMarker(marker), s, o);
   }

   @Override
   public void info(final Marker marker, final String s, final Object o, final Object o1) {
      this.logger.logIfEnabled(FQCN, Level.INFO, this.markerFactory.getLog4jMarker(marker), s, o, o1);
   }

   @Override
   public void info(final Marker marker, final String s, final Object... objects) {
      this.logger.logIfEnabled(FQCN, Level.INFO, this.markerFactory.getLog4jMarker(marker), s, objects);
   }

   @Override
   public void info(final Marker marker, final String s, final Throwable throwable) {
      this.logger.logIfEnabled(FQCN, Level.INFO, this.markerFactory.getLog4jMarker(marker), s, throwable);
   }

   @Override
   public void warn(final String format) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, format);
   }

   @Override
   public void warn(final String format, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, format, o);
   }

   @Override
   public void warn(final String format, final Object arg1, final Object arg2) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, format, arg1, arg2);
   }

   @Override
   public void warn(final String format, final Object... args) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, format, args);
   }

   @Override
   public void warn(final String format, final Throwable t) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, format, t);
   }

   @Override
   public boolean isWarnEnabled() {
      return this.logger.isEnabled(Level.WARN, null, null);
   }

   @Override
   public boolean isWarnEnabled(final Marker marker) {
      return this.logger.isEnabled(Level.WARN, this.markerFactory.getLog4jMarker(marker), null);
   }

   @Override
   public void warn(final Marker marker, final String s) {
      this.logger.logIfEnabled(FQCN, Level.WARN, this.markerFactory.getLog4jMarker(marker), s);
   }

   @Override
   public void warn(final Marker marker, final String s, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.WARN, this.markerFactory.getLog4jMarker(marker), s, o);
   }

   @Override
   public void warn(final Marker marker, final String s, final Object o, final Object o1) {
      this.logger.logIfEnabled(FQCN, Level.WARN, this.markerFactory.getLog4jMarker(marker), s, o, o1);
   }

   @Override
   public void warn(final Marker marker, final String s, final Object... objects) {
      this.logger.logIfEnabled(FQCN, Level.WARN, this.markerFactory.getLog4jMarker(marker), s, objects);
   }

   @Override
   public void warn(final Marker marker, final String s, final Throwable throwable) {
      this.logger.logIfEnabled(FQCN, Level.WARN, this.markerFactory.getLog4jMarker(marker), s, throwable);
   }

   @Override
   public void error(final String format) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, format);
   }

   @Override
   public void error(final String format, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, format, o);
   }

   @Override
   public void error(final String format, final Object arg1, final Object arg2) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, format, arg1, arg2);
   }

   @Override
   public void error(final String format, final Object... args) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, format, args);
   }

   @Override
   public void error(final String format, final Throwable t) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, format, t);
   }

   @Override
   public boolean isErrorEnabled() {
      return this.logger.isEnabled(Level.ERROR, null, null);
   }

   @Override
   public boolean isErrorEnabled(final Marker marker) {
      return this.logger.isEnabled(Level.ERROR, this.markerFactory.getLog4jMarker(marker), null);
   }

   @Override
   public void error(final Marker marker, final String s) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, this.markerFactory.getLog4jMarker(marker), s);
   }

   @Override
   public void error(final Marker marker, final String s, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, this.markerFactory.getLog4jMarker(marker), s, o);
   }

   @Override
   public void error(final Marker marker, final String s, final Object o, final Object o1) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, this.markerFactory.getLog4jMarker(marker), s, o, o1);
   }

   @Override
   public void error(final Marker marker, final String s, final Object... objects) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, this.markerFactory.getLog4jMarker(marker), s, objects);
   }

   @Override
   public void error(final Marker marker, final String s, final Throwable throwable) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, this.markerFactory.getLog4jMarker(marker), s, throwable);
   }

   @Override
   public void log(final Marker marker, final String fqcn, final int level, final String message, final Object[] params, final Throwable throwable) {
      Level log4jLevel = getLevel(level);
      org.apache.logging.log4j.Marker log4jMarker = this.markerFactory.getLog4jMarker(marker);
      if (this.logger.isEnabled(log4jLevel, log4jMarker, message, params)) {
         Message msg;
         Throwable actualThrowable;
         if (params == null) {
            msg = new SimpleMessage(message);
            actualThrowable = throwable;
         } else {
            msg = new ParameterizedMessage(message, params, throwable);
            actualThrowable = throwable != null ? throwable : msg.getThrowable();
         }

         this.logger.logMessage(fqcn, log4jLevel, log4jMarker, msg, actualThrowable);
      }
   }

   @Override
   public String getName() {
      return this.name;
   }

   private void readObject(final ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
      aInputStream.defaultReadObject();
      this.logger = LogManager.getContext().getLogger(this.name);
      this.markerFactory = ((Log4jLoggerFactory)LoggerFactory.getILoggerFactory()).getMarkerFactory();
   }

   private void writeObject(final ObjectOutputStream aOutputStream) throws IOException {
      aOutputStream.defaultWriteObject();
   }

   private static Level getLevel(final int i) {
      switch (i) {
         case 0:
            return Level.TRACE;
         case 10:
            return Level.DEBUG;
         case 20:
            return Level.INFO;
         case 30:
            return Level.WARN;
         case 40:
            return Level.ERROR;
         default:
            return Level.ERROR;
      }
   }

   @Override
   public LoggingEventBuilder makeLoggingEventBuilder(org.slf4j.event.Level level) {
      Level log4jLevel = getLevel(level.toInt());
      return (LoggingEventBuilder)(this.logger.isEnabled(log4jLevel)
         ? new Log4jEventBuilder(this.markerFactory, this.logger.atLevel(log4jLevel))
         : NOPLoggingEventBuilder.singleton());
   }

   @Override
   public LoggingEventBuilder atTrace() {
      return (LoggingEventBuilder)(this.logger.isTraceEnabled()
         ? new Log4jEventBuilder(this.markerFactory, this.logger.atTrace())
         : NOPLoggingEventBuilder.singleton());
   }

   @Override
   public LoggingEventBuilder atDebug() {
      return (LoggingEventBuilder)(this.logger.isDebugEnabled()
         ? new Log4jEventBuilder(this.markerFactory, this.logger.atDebug())
         : NOPLoggingEventBuilder.singleton());
   }

   @Override
   public LoggingEventBuilder atInfo() {
      return (LoggingEventBuilder)(this.logger.isInfoEnabled()
         ? new Log4jEventBuilder(this.markerFactory, this.logger.atInfo())
         : NOPLoggingEventBuilder.singleton());
   }

   @Override
   public LoggingEventBuilder atWarn() {
      return (LoggingEventBuilder)(this.logger.isWarnEnabled()
         ? new Log4jEventBuilder(this.markerFactory, this.logger.atWarn())
         : NOPLoggingEventBuilder.singleton());
   }

   @Override
   public LoggingEventBuilder atError() {
      return (LoggingEventBuilder)(this.logger.isErrorEnabled()
         ? new Log4jEventBuilder(this.markerFactory, this.logger.atError())
         : NOPLoggingEventBuilder.singleton());
   }

   @Override
   public boolean isEnabledForLevel(org.slf4j.event.Level level) {
      return this.logger.isEnabled(getLevel(level.toInt()));
   }
}
