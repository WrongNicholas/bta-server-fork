package org.apache.logging.log4j.core;

import java.io.Serializable;

public interface Appender extends LifeCycle {
   String ELEMENT_TYPE = "appender";
   Appender[] EMPTY_ARRAY = new Appender[0];

   void append(LogEvent event);

   String getName();

   Layout<? extends Serializable> getLayout();

   boolean ignoreExceptions();

   ErrorHandler getHandler();

   void setHandler(ErrorHandler handler);
}
