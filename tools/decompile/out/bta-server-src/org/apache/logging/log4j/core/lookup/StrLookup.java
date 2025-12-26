package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;

public interface StrLookup {
   String CATEGORY = "Lookup";

   String lookup(String key);

   String lookup(LogEvent event, String key);

   default LookupResult evaluate(String key) {
      String value = this.lookup(key);
      return value == null ? null : new DefaultLookupResult(value);
   }

   default LookupResult evaluate(LogEvent event, String key) {
      String value = this.lookup(event, key);
      return value == null ? null : new DefaultLookupResult(value);
   }
}
