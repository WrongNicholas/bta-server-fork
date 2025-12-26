package net.minecraft.core.util.helper;

import net.minecraft.core.entity.Mob;

public enum Direction {
   NORTH(Axis.Z, 0, 0, -1, "north"),
   EAST(Axis.X, 1, 0, 0, "east"),
   SOUTH(Axis.Z, 0, 0, 1, "south"),
   WEST(Axis.X, -1, 0, 0, "west"),
   UP(Axis.Y, 0, 1, 0, "up"),
   DOWN(Axis.Y, 0, -1, 0, "down"),
   NONE(Axis.NONE, 0, 0, 0, "none");

   public static final Direction[] directions = new Direction[6];
   public static final Direction[] horizontalDirections = new Direction[4];
   private final Axis axis;
   private int id;
   private int horizontalIndex;
   private Direction opposite;
   private Side side;
   private final int offsetX;
   private final int offsetY;
   private final int offsetZ;
   private final String translationKey;
   @Deprecated
   public int index;

   private static void setId(Direction side, int id) {
      directions[id] = side;
      side.id = id;
   }

   private static void setHorizontal(Direction direction, int id) {
      horizontalDirections[id] = direction;
      direction.horizontalIndex = id;
   }

   private static void setOpposites(Direction side1, Direction side2) {
      side1.opposite = side2;
      side2.opposite = side1;
   }

   public static Direction getDirectionById(int i) {
      return i >= 0 && i < directions.length ? directions[i] : NONE;
   }

   private Direction(Axis axis, int offsetX, int offsetY, int offsetZ, String translationKey) {
      this.axis = axis;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.offsetZ = offsetZ;
      this.translationKey = "direction." + translationKey;
   }

   public int getOffsetX() {
      return this.offsetX;
   }

   public int getOffsetY() {
      return this.offsetY;
   }

   public int getOffsetZ() {
      return this.offsetZ;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   public Direction getOpposite() {
      return this.opposite;
   }

   public Side getSide() {
      return this.side;
   }

   public Axis getAxis() {
      return this.axis;
   }

   public int getId() {
      return this.id;
   }

   public boolean isVertical() {
      return this.getAxis().isVertical();
   }

   public boolean isHorizontal() {
      return !this.isVertical();
   }

   public Direction rotate(int amount) {
      if (this == UP) {
         return UP;
      } else {
         return this == DOWN ? DOWN : horizontalDirections[this.getHorizontalIndex() + amount & 3];
      }
   }

   public int getHorizontalIndex() {
      return this.horizontalIndex;
   }

   public static Direction getVerticalDirection(double rotationPitch) {
      return rotationPitch < 0.0 ? UP : DOWN;
   }

   public static Direction getVerticalDirection(Mob entity) {
      return entity.rotationLockVertical != null && entity.rotationLockVertical != NONE ? entity.rotationLockVertical : getVerticalDirection(entity.xRot);
   }

   public static Direction getHorizontalDirection(double rotationYaw) {
      return horizontalDirections[MathHelper.floor(rotationYaw / 90.0 + 0.5) + 2 & 3];
   }

   public static Direction getHorizontalDirection(Mob entity) {
      return entity.rotationLockHorizontal != null && entity.rotationLockHorizontal != NONE
         ? entity.rotationLockHorizontal
         : getHorizontalDirection(entity.yRot);
   }

   public static Direction getDirection(Mob entity) {
      if (entity.rotationLock != null && entity.rotationLock != NONE) {
         return entity.rotationLock;
      } else if (entity.xRot < -45.0F) {
         return UP;
      } else {
         return entity.xRot > 45.0F ? DOWN : getHorizontalDirection(entity.yRot);
      }
   }

   static {
      setId(DOWN, 0);
      setId(UP, 1);
      setId(NORTH, 2);
      setId(SOUTH, 3);
      setId(WEST, 4);
      setId(EAST, 5);
      NONE.id = -1;
      setHorizontal(NORTH, 0);
      setHorizontal(EAST, 1);
      setHorizontal(SOUTH, 2);
      setHorizontal(WEST, 3);
      setOpposites(EAST, WEST);
      setOpposites(UP, DOWN);
      setOpposites(NORTH, SOUTH);
      NONE.opposite = NONE;
      UP.side = Side.TOP;
      DOWN.side = Side.BOTTOM;
      NORTH.side = Side.NORTH;
      EAST.side = Side.EAST;
      SOUTH.side = Side.SOUTH;
      WEST.side = Side.WEST;
      NONE.side = Side.NONE;
      NORTH.index = 0;
      EAST.index = 1;
      SOUTH.index = 2;
      WEST.index = 3;
      UP.index = 4;
      DOWN.index = 5;
   }
}
