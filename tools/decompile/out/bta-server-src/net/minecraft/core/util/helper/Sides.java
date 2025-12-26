package net.minecraft.core.util.helper;

public class Sides {
   private static final int bottom = 0;
   private static final int top = 1;
   private static final int north = 2;
   private static final int south = 3;
   private static final int west = 4;
   private static final int east = 5;
   public static final int negY = 0;
   public static final int posY = 1;
   public static final int negZ = 2;
   public static final int posZ = 3;
   public static final int negX = 4;
   public static final int posX = 5;
   public static final int[] orientationLookUpHorizontal = new int[]{
      0, 1, 3, 2, 4, 5, 0, 1, 3, 2, 4, 5, 0, 1, 2, 3, 5, 4, 0, 1, 3, 2, 4, 5, 0, 1, 4, 5, 2, 3, 0, 1, 5, 4, 3, 2
   };
   public static final int[] orientationLookUp = new int[]{
      0, 1, 5, 4, 2, 3, 1, 0, 2, 3, 5, 4, 3, 2, 1, 0, 5, 4, 3, 2, 0, 1, 4, 5, 3, 2, 4, 5, 1, 0, 3, 2, 5, 4, 0, 1
   };
   public static final int[] orientationLookUpXYZAligned = new int[]{1, 0, 5, 4, 2, 3, 3, 2, 1, 0, 5, 4, 3, 2, 5, 4, 1, 0};
   public static final int[] orientationLookUpTrapdoorOpen = new int[]{3, 2, 1, 0, 5, 4, 3, 2, 0, 1, 4, 5, 3, 2, 4, 5, 1, 0, 3, 2, 5, 4, 0, 1};
}
