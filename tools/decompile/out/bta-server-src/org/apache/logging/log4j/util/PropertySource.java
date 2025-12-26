package org.apache.logging.log4j.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface PropertySource {
   int getPriority();

   default void forEach(BiConsumer<String, String> action) {
   }

   default Collection<String> getPropertyNames() {
      return Collections.emptySet();
   }

   default CharSequence getNormalForm(Iterable<? extends CharSequence> tokens) {
      return null;
   }

   default String getProperty(String key) {
      return null;
   }

   default boolean containsProperty(String key) {
      return false;
   }

   public static class Comparator implements java.util.Comparator<PropertySource>, Serializable {
      private static final long serialVersionUID = 1L;

      public int compare(final PropertySource o1, final PropertySource o2) {
         return Integer.compare(Objects.requireNonNull(o1).getPriority(), Objects.requireNonNull(o2).getPriority());
      }
   }

   public static final class Util {
      private static final Pattern PREFIX_PATTERN = Pattern.compile("(^log4j2?[-._/]?|^org\\.apache\\.logging\\.log4j\\.)|(?=AsyncLogger(Config)?\\.)", 2);
      private static final Pattern PROPERTY_TOKENIZER = Pattern.compile("([A-Z]*[a-z0-9]+|[A-Z0-9]+)[-._/]?");
      private static final Map<CharSequence, List<CharSequence>> CACHE = new ConcurrentHashMap<>();

      public static List<CharSequence> tokenize(final CharSequence value) {
         if (CACHE.containsKey(value)) {
            return CACHE.get(value);
         } else {
            List<CharSequence> tokens = new ArrayList<>();
            int start = 0;
            Matcher prefixMatcher = PREFIX_PATTERN.matcher(value);
            if (prefixMatcher.find(start)) {
               start = prefixMatcher.end();

               for (Matcher matcher = PROPERTY_TOKENIZER.matcher(value); matcher.find(start); start = matcher.end()) {
                  tokens.add(matcher.group(1).toLowerCase());
               }
            }

            CACHE.put(value, tokens);
            return tokens;
         }
      }

      public static CharSequence joinAsCamelCase(final Iterable<? extends CharSequence> tokens) {
         StringBuilder sb = new StringBuilder();
         boolean first = true;

         for (CharSequence token : tokens) {
            if (first) {
               sb.append(token);
            } else {
               sb.append(Character.toUpperCase(token.charAt(0)));
               if (token.length() > 1) {
                  sb.append(token.subSequence(1, token.length()));
               }
            }

            first = false;
         }

         return sb.toString();
      }

      private Util() {
      }

      static {
         CACHE.put("disableThreadContext", Arrays.asList("disable", "thread", "context"));
         CACHE.put("disableThreadContextStack", Arrays.asList("disable", "thread", "context", "stack"));
         CACHE.put("disableThreadContextMap", Arrays.asList("disable", "thread", "context", "map"));
         CACHE.put("isThreadContextMapInheritable", Arrays.asList("is", "thread", "context", "map", "inheritable"));
      }
   }
}
