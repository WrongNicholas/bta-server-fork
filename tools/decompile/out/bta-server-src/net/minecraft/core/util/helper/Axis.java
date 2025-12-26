package net.minecraft.core.util.helper;

public enum Axis {
   X,
   Y,
   Z,
   NONE;

   public boolean isVertical() {
      return this == Y;
   }
}
