package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.message.StructuredDataMessage;

@Plugin(name = "sd", category = "Lookup")
public class StructuredDataLookup implements StrLookup {
   public static final String ID_KEY = "id";
   public static final String TYPE_KEY = "type";

   @Override
   public String lookup(final String key) {
      return null;
   }

   @Override
   public String lookup(final LogEvent event, final String key) {
      if (event != null && event.getMessage() instanceof StructuredDataMessage) {
         StructuredDataMessage msg = (StructuredDataMessage)event.getMessage();
         if ("id".equalsIgnoreCase(key)) {
            return msg.getId().getName();
         } else {
            return "type".equalsIgnoreCase(key) ? msg.getType() : msg.get(key);
         }
      } else {
         return null;
      }
   }
}
