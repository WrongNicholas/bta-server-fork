package org.apache.logging.slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.MarkerManager;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;

class Log4jMarker implements Marker {
   public static final long serialVersionUID = 1590472L;
   private final IMarkerFactory factory;
   private final org.apache.logging.log4j.Marker marker;

   public Log4jMarker(final IMarkerFactory markerFactory, final org.apache.logging.log4j.Marker marker) {
      this.factory = markerFactory;
      this.marker = marker;
   }

   @Override
   public void add(final Marker marker) {
      if (marker == null) {
         throw new IllegalArgumentException();
      } else {
         Marker m = this.factory.getMarker(marker.getName());
         this.marker.addParents(((Log4jMarker)m).getLog4jMarker());
      }
   }

   @Override
   public boolean contains(final Marker marker) {
      if (marker == null) {
         throw new IllegalArgumentException();
      } else {
         return this.marker.isInstanceOf(marker.getName());
      }
   }

   @Override
   public boolean contains(final String s) {
      return s != null ? this.marker.isInstanceOf(s) : false;
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (!(obj instanceof Log4jMarker)) {
         return false;
      } else {
         Log4jMarker other = (Log4jMarker)obj;
         return Objects.equals(this.marker, other.marker);
      }
   }

   public org.apache.logging.log4j.Marker getLog4jMarker() {
      return this.marker;
   }

   @Override
   public String getName() {
      return this.marker.getName();
   }

   @Override
   public boolean hasChildren() {
      return this.marker.hasParents();
   }

   @Override
   public int hashCode() {
      return 31 + Objects.hashCode(this.marker);
   }

   @Override
   public boolean hasReferences() {
      return this.marker.hasParents();
   }

   @Override
   public Iterator<Marker> iterator() {
      org.apache.logging.log4j.Marker[] log4jParents = this.marker.getParents();
      List<Marker> parents = new ArrayList<>(log4jParents.length);

      for (org.apache.logging.log4j.Marker m : log4jParents) {
         parents.add(this.factory.getMarker(m.getName()));
      }

      return parents.iterator();
   }

   @Override
   public boolean remove(final Marker marker) {
      return marker != null ? this.marker.remove(MarkerManager.getMarker(marker.getName())) : false;
   }
}
