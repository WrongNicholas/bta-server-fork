package org.apache.logging.log4j;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.spi.StandardLevel;
import org.apache.logging.log4j.util.Strings;

public final class Level implements Comparable<Level>, Serializable {
   private static final Level[] EMPTY_ARRAY = new Level[0];
   private static final ConcurrentMap<String, Level> LEVELS = new ConcurrentHashMap<>();
   public static final Level OFF = new Level("OFF", StandardLevel.OFF.intLevel());
   public static final Level FATAL = new Level("FATAL", StandardLevel.FATAL.intLevel());
   public static final Level ERROR = new Level("ERROR", StandardLevel.ERROR.intLevel());
   public static final Level WARN = new Level("WARN", StandardLevel.WARN.intLevel());
   public static final Level INFO = new Level("INFO", StandardLevel.INFO.intLevel());
   public static final Level DEBUG = new Level("DEBUG", StandardLevel.DEBUG.intLevel());
   public static final Level TRACE = new Level("TRACE", StandardLevel.TRACE.intLevel());
   public static final Level ALL = new Level("ALL", StandardLevel.ALL.intLevel());
   public static final String CATEGORY = "Level";
   private static final long serialVersionUID = 1581082L;
   private final String name;
   private final int intLevel;
   private final StandardLevel standardLevel;

   private Level(final String name, final int intLevel) {
      if (Strings.isEmpty(name)) {
         throw new IllegalArgumentException("Illegal null or empty Level name.");
      } else if (intLevel < 0) {
         throw new IllegalArgumentException("Illegal Level int less than zero.");
      } else {
         this.name = name;
         this.intLevel = intLevel;
         this.standardLevel = StandardLevel.getStandardLevel(intLevel);
         if (LEVELS.putIfAbsent(toUpperCase(name.trim()), this) != null) {
            throw new IllegalStateException("Level " + name + " has already been defined.");
         }
      }
   }

   public int intLevel() {
      return this.intLevel;
   }

   public StandardLevel getStandardLevel() {
      return this.standardLevel;
   }

   public boolean isInRange(final Level minLevel, final Level maxLevel) {
      return this.intLevel >= minLevel.intLevel && this.intLevel <= maxLevel.intLevel;
   }

   public boolean isLessSpecificThan(final Level level) {
      return this.intLevel >= level.intLevel;
   }

   public boolean isMoreSpecificThan(final Level level) {
      return this.intLevel <= level.intLevel;
   }

   public Level clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException();
   }

   public int compareTo(final Level other) {
      return this.intLevel < other.intLevel ? -1 : (this.intLevel > other.intLevel ? 1 : 0);
   }

   @Override
   public boolean equals(final Object other) {
      return other instanceof Level && other == this;
   }

   public Class<Level> getDeclaringClass() {
      return Level.class;
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   public String name() {
      return this.name;
   }

   @Override
   public String toString() {
      return this.name;
   }

   public static Level forName(final String name, final int intValue) {
      if (Strings.isEmpty(name)) {
         throw new IllegalArgumentException("Illegal null or empty Level name.");
      } else {
         String normalizedName = toUpperCase(name.trim());
         Level level = LEVELS.get(normalizedName);
         if (level != null) {
            return level;
         } else {
            try {
               return new Level(name, intValue);
            } catch (IllegalStateException var5) {
               return LEVELS.get(normalizedName);
            }
         }
      }
   }

   public static Level getLevel(final String name) {
      if (Strings.isEmpty(name)) {
         throw new IllegalArgumentException("Illegal null or empty Level name.");
      } else {
         return LEVELS.get(toUpperCase(name.trim()));
      }
   }

   public static Level toLevel(final String level) {
      return toLevel(level, DEBUG);
   }

   public static Level toLevel(final String name, final Level defaultLevel) {
      if (name == null) {
         return defaultLevel;
      } else {
         Level level = LEVELS.get(toUpperCase(name.trim()));
         return level == null ? defaultLevel : level;
      }
   }

   private static String toUpperCase(final String name) {
      return name.toUpperCase(Locale.ENGLISH);
   }

   public static Level[] values() {
      return LEVELS.values().toArray(EMPTY_ARRAY);
   }

   public static Level valueOf(final String name) {
      Objects.requireNonNull(name, "No level name given.");
      String levelName = toUpperCase(name.trim());
      Level level = LEVELS.get(levelName);
      if (level != null) {
         return level;
      } else {
         throw new IllegalArgumentException("Unknown level constant [" + levelName + "].");
      }
   }

   public static <T extends Enum<T>> T valueOf(final Class<T> enumType, final String name) {
      return Enum.valueOf(enumType, name);
   }

   protected Object readResolve() {
      return valueOf(this.name);
   }
}
