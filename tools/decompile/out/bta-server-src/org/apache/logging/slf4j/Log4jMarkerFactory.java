package org.apache.logging.slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.status.StatusLogger;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;

public class Log4jMarkerFactory implements IMarkerFactory {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final ConcurrentMap<String, Marker> markerMap = new ConcurrentHashMap<>();

   @Override
   public Marker getMarker(final String name) {
      if (name == null) {
         throw new IllegalArgumentException("Marker name must not be null");
      } else {
         Marker marker = this.markerMap.get(name);
         if (marker != null) {
            return marker;
         } else {
            org.apache.logging.log4j.Marker log4jMarker = MarkerManager.getMarker(name);
            return this.addMarkerIfAbsent(name, log4jMarker);
         }
      }
   }

   private Marker addMarkerIfAbsent(final String name, final org.apache.logging.log4j.Marker log4jMarker) {
      Marker marker = new Log4jMarker(this, log4jMarker);
      Marker existing = this.markerMap.putIfAbsent(name, marker);
      return existing == null ? marker : existing;
   }

   public Marker getMarker(final Marker marker) {
      if (marker == null) {
         throw new IllegalArgumentException("Marker must not be null");
      } else {
         Marker m = this.markerMap.get(marker.getName());
         return m != null ? m : this.addMarkerIfAbsent(marker.getName(), convertMarker(marker));
      }
   }

   org.apache.logging.log4j.Marker getLog4jMarker(final Marker marker) {
      if (marker == null) {
         return null;
      } else {
         return marker instanceof Log4jMarker ? ((Log4jMarker)marker).getLog4jMarker() : ((Log4jMarker)this.getMarker(marker)).getLog4jMarker();
      }
   }

   static org.apache.logging.log4j.Marker convertMarker(final Marker original) {
      if (original == null) {
         throw new IllegalArgumentException("Marker must not be null");
      } else {
         return convertMarker(original, new ArrayList<>());
      }
   }

   private static org.apache.logging.log4j.Marker convertMarker(final Marker original, final Collection<Marker> visited) {
      org.apache.logging.log4j.Marker marker = MarkerManager.getMarker(original.getName());
      if (original.hasReferences()) {
         for (Marker next : original) {
            if (visited.contains(next)) {
               LOGGER.warn("Found a cycle in Marker [{}]. Cycle will be broken.", next.getName());
            } else {
               visited.add(next);
               marker.addParents(convertMarker(next, visited));
            }
         }
      }

      return marker;
   }

   @Override
   public boolean exists(final String name) {
      return this.markerMap.containsKey(name);
   }

   @Override
   public boolean detachMarker(final String name) {
      return false;
   }

   @Override
   public Marker getDetachedMarker(final String name) {
      LOGGER.warn("Log4j does not support detached Markers. Returned Marker [{}] will be unchanged.", name);
      return this.getMarker(name);
   }
}
