package net.minecraft.core.util.debug;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public class Debug {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static boolean enable = true;
   public static final Debug.Profiler baseProfiler = new Debug.Profiler("base", null);
   public static Debug.Profiler currentProfiler = baseProfiler;
   private static int fpsCount = 0;
   private static long lastSec = -1L;
   private static int fps = -1;

   public static void push(String string) {
      if (enable) {
         currentProfiler = currentProfiler.get(string);
         currentProfiler.startTime = now();
      }
   }

   public static void change(String string) {
      pop();
      push(string);
   }

   public static void reset() {
      if (currentProfiler != baseProfiler) {
         LOGGER.warn("Invalid profiler state: {}", currentProfiler != null ? currentProfiler.name : null);
         currentProfiler = baseProfiler;
      }

      reset(currentProfiler);
      long now = now();
      if (baseProfiler.startTime != -1L) {
         baseProfiler.time = now - baseProfiler.startTime;
      }

      baseProfiler.startTime = now;
      if (lastSec == -1L) {
         lastSec = now;
      }

      if (now > lastSec + 1000000000L) {
         lastSec = now;
         fps = fpsCount;
         fpsCount = 0;
      }

      fpsCount++;
   }

   private static void reset(Debug.Profiler profiler) {
      if (profiler != null) {
         profiler.time = 0L;

         for (int i = 0; i < profiler.profilers.size(); i++) {
            reset(profiler.allProfilers.get(i));
         }
      }
   }

   public static void pop() {
      if (enable) {
         if (currentProfiler.startTime == -1L) {
            throw new IllegalStateException();
         } else {
            currentProfiler.time = currentProfiler.time + (now() - currentProfiler.startTime);
            currentProfiler.startTime = -1L;
            currentProfiler = currentProfiler.parent;
            if (currentProfiler == null) {
               throw new NullPointerException();
            }
         }
      }
   }

   static long now() {
      return System.nanoTime();
   }

   public static int getFps() {
      return fps;
   }

   public static class Profiler {
      public final String name;
      public Debug.Profiler parent;
      public Map<String, Debug.Profiler> profilers = new HashMap<>();
      public List<Debug.Profiler> allProfilers = new ArrayList<>();
      public long startTime = -1L;
      public long time = 0L;

      public Profiler(String name, Debug.Profiler parent) {
         this.name = name;
         this.parent = parent;
      }

      protected Debug.Profiler get(String name) {
         Debug.Profiler profiler = this.profilers.get(name);
         if (profiler == null) {
            profiler = new Debug.Profiler(name, this);
            this.profilers.put(name, profiler);
            this.allProfilers.add(profiler);
         }

         return profiler;
      }

      @Override
      public int hashCode() {
         return this.name.hashCode();
      }
   }
}
