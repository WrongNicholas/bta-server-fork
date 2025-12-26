package net.minecraft.core.util.helper;

public enum Side {
   TOP(Axis.Y, 0, 1, 0, "top"),
   BOTTOM(Axis.Y, 0, -1, 0, "bottom"),
   NORTH(Axis.Z, 0, 0, -1, "north"),
   EAST(Axis.X, 1, 0, 0, "east"),
   SOUTH(Axis.Z, 0, 0, 1, "south"),
   WEST(Axis.X, -1, 0, 0, "west"),
   NONE(Axis.NONE, 0, 0, 0, "none");

   public static final Side posX = EAST;
   public static final Side negX = WEST;
   public static final Side posY = TOP;
   public static final Side negY = BOTTOM;
   public static final Side posZ = SOUTH;
   public static final Side negZ = NORTH;
   public static final Side[] sides = new Side[6];
   private final Axis axis;
   private int id;
   private Side opposite;
   private Direction direction;
   private final int offsetX;
   private final int offsetY;
   private final int offsetZ;
   private final String translationKey;

   private static void setId(Side side, int id) {
      sides[id] = side;
      side.id = id;
   }

   private static void setOpposites(Side side1, Side side2) {
      side1.opposite = side2;
      side2.opposite = side1;
   }

   public static Side getSideById(int i) {
      return i >= 0 && i < sides.length ? sides[i] : NONE;
   }

   private Side(Axis axis, int offsetX, int offsetY, int offsetZ, String translationKey) {
      this.axis = axis;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.offsetZ = offsetZ;
      this.translationKey = translationKey;
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

   public Side getOpposite() {
      return this.opposite;
   }

   public Direction getDirection() {
      return this.direction;
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

   static {
      setId(BOTTOM, 0);
      setId(TOP, 1);
      setId(NORTH, 2);
      setId(SOUTH, 3);
      setId(WEST, 4);
      setId(EAST, 5);
      NONE.id = -1;
      setOpposites(EAST, WEST);
      setOpposites(TOP, BOTTOM);
      setOpposites(NORTH, SOUTH);
      NONE.opposite = NONE;
      TOP.direction = Direction.UP;
      BOTTOM.direction = Direction.DOWN;
      NORTH.direction = Direction.NORTH;
      EAST.direction = Direction.EAST;
      SOUTH.direction = Direction.SOUTH;
      WEST.direction = Direction.WEST;
      NONE.direction = Direction.NONE;
   }
}
