package net.minecraft.core.block.logic;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public enum RailDirection {
   NONE(-1, false, false, false, 0, 0, 0, 0, 0, 0),
   STRAIGHT_NS(0, false, true, false, 0, 0, 1, 0, 0, -1),
   STRAIGHT_EW(1, false, true, false, -1, 0, 0, 1, 0, 0),
   SLOPE_E(2, true, false, false, -1, 0, 0, 1, 1, 0),
   SLOPE_W(3, true, false, false, -1, 1, 0, 1, 0, 0),
   SLOPE_N(4, true, false, false, 0, 0, 1, 0, 1, -1),
   SLOPE_S(5, true, false, false, 0, 1, 1, 0, 0, -1),
   TURN_ES(6, false, false, true, 0, 0, 1, 1, 0, 0),
   TURN_WS(7, false, false, true, 0, 0, 1, -1, 0, 0),
   TURN_WN(8, false, false, true, 0, 0, -1, -1, 0, 0),
   TURN_EN(9, false, false, true, 0, 0, -1, 1, 0, 0);

   private static final Map<Integer, RailDirection> directionMap = new HashMap<>();
   public final int meta;
   private final boolean sloped;
   private final boolean straight;
   private final boolean curved;
   private final int offX1;
   private final int offY1;
   private final int offZ1;
   private final int offX2;
   private final int offY2;
   private final int offZ2;

   private RailDirection(int meta, boolean sloped, boolean straight, boolean curved, int offX1, int offY1, int offZ1, int offX2, int offY2, int offZ2) {
      this.meta = meta;
      this.sloped = sloped;
      this.straight = straight;
      this.curved = curved;
      this.offX1 = offX1;
      this.offY1 = offY1;
      this.offZ1 = offZ1;
      this.offX2 = offX2;
      this.offY2 = offY2;
      this.offZ2 = offZ2;
   }

   public int getNextRailX() {
      return this.offX1;
   }

   public int getNextRailY() {
      return this.offY1;
   }

   public int getNextRailZ() {
      return this.offZ1;
   }

   public int getPrevRailX() {
      return this.offX2;
   }

   public int getPrevRailY() {
      return this.offY2;
   }

   public int getPrevRailZ() {
      return this.offZ2;
   }

   public boolean isSloped() {
      return this.sloped;
   }

   public boolean isStraight() {
      return this.straight;
   }

   public boolean isCurved() {
      return this.curved;
   }

   @NotNull
   public static RailDirection getFromMeta(int meta) {
      return directionMap.containsKey(meta) ? directionMap.get(meta) : NONE;
   }

   static {
      for (RailDirection d : values()) {
         directionMap.put(d.meta, d);
      }
   }
}
