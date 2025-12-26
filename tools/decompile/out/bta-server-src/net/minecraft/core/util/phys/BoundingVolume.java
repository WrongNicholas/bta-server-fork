package net.minecraft.core.util.phys;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class BoundingVolume {
   private static final int SIZE = 16;
   private final boolean[] volume = new boolean[4096];
   private Float[] bakedLineCoords = null;

   private BoundingVolume() {
   }

   public static BoundingVolume newFilledVolume() {
      return new BoundingVolume();
   }

   public static BoundingVolume newEmptyVolume() {
      BoundingVolume volume = new BoundingVolume();
      volume.carveVolume(0, 0, 0, 15, 15, 15);
      return volume;
   }

   private boolean isCarved(int x, int y, int z) {
      if (x < 0 || x >= 16) {
         return true;
      } else if (y < 0 || y >= 16) {
         return true;
      } else {
         return z >= 0 && z < 16 ? this.volume[256 * y + 16 * z + x] : true;
      }
   }

   private void carve(int x, int y, int z) {
      if (x >= 0 && x < 16) {
         if (y >= 0 && y < 16) {
            if (z >= 0 && z < 16) {
               this.volume[256 * y + 16 * z + x] = true;
            }
         }
      }
   }

   private void fill(int x, int y, int z) {
      if (x >= 0 && x < 16) {
         if (y >= 0 && y < 16) {
            if (z >= 0 && z < 16) {
               this.volume[256 * y + 16 * z + x] = false;
            }
         }
      }
   }

   public void carveVolume(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      if (minX < 0) {
         minX = 0;
      }

      if (maxX > 15) {
         maxX = 15;
      }

      if (minY < 0) {
         minY = 0;
      }

      if (maxY > 15) {
         maxY = 15;
      }

      if (minZ < 0) {
         minZ = 0;
      }

      if (maxZ > 15) {
         maxZ = 15;
      }

      for (int y = minY; y <= maxY; y++) {
         for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
               this.carve(x, y, z);
            }
         }
      }
   }

   public void fillVolume(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      if (minX < 0) {
         minX = 0;
      }

      if (maxX > 15) {
         maxX = 15;
      }

      if (minY < 0) {
         minY = 0;
      }

      if (maxY > 15) {
         maxY = 15;
      }

      if (minZ < 0) {
         minZ = 0;
      }

      if (maxZ > 15) {
         maxZ = 15;
      }

      for (int y = minY; y <= maxY; y++) {
         for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
               this.fill(x, y, z);
            }
         }
      }
   }

   public void addVolume(BoundingVolume volume) {
      for (int y = 0; y < 16; y++) {
         for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
               if (this.isCarved(x, y, z) && !volume.isCarved(x, y, z)) {
                  this.fill(x, y, z);
               }
            }
         }
      }
   }

   public void subtractVolume(BoundingVolume volume) {
      for (int y = 0; y < 16; y++) {
         for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
               if (!this.isCarved(x, y, z) && volume.isCarved(x, y, z)) {
                  this.carve(x, y, z);
               }
            }
         }
      }
   }

   public void bake() {
      List<Integer> lineCoords = new ArrayList<>();
      float scale = 0.0625F;

      for (int y = 0; y < 16; y++) {
         for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
               for (BoundingVolume.Line line : this.getLines(x, y, z)) {
                  int[] coords = line.getCoords();
                  lineCoords.add(coords[0] + x);
                  lineCoords.add(coords[1] + y);
                  lineCoords.add(coords[2] + z);
                  lineCoords.add(coords[3] + x);
                  lineCoords.add(coords[4] + y);
                  lineCoords.add(coords[5] + z);
               }
            }
         }
      }

      System.out.println("Before line simplification: " + lineCoords.size() / 6 + " lines");
      List<Integer> mergedLineCoords = this.mergeLines(lineCoords);
      System.out.println("After line simplification: " + mergedLineCoords.size() / 6 + " lines");
      List<Float> floatLineCoords = new ArrayList<>();

      for (Integer i : mergedLineCoords) {
         floatLineCoords.add(i.intValue() * scale);
      }

      this.bakedLineCoords = floatLineCoords.toArray(new Float[0]);
   }

   private List<Integer> mergeLines(List<Integer> lineCoords) {
      List<Integer> mergedLineCoords = new ArrayList<>();

      for (int i = 0; i < lineCoords.size(); i += 6) {
         boolean lineHandled = false;

         for (int j = 0; j < mergedLineCoords.size(); j += 6) {
            int xMinA = mergedLineCoords.get(j + 0);
            int yMinA = mergedLineCoords.get(j + 1);
            int zMinA = mergedLineCoords.get(j + 2);
            int xMaxA = mergedLineCoords.get(j + 3);
            int yMaxA = mergedLineCoords.get(j + 4);
            int zMaxA = mergedLineCoords.get(j + 5);
            int xMinB = lineCoords.get(i + 0);
            int yMinB = lineCoords.get(i + 1);
            int zMinB = lineCoords.get(i + 2);
            int xMaxB = lineCoords.get(i + 3);
            int yMaxB = lineCoords.get(i + 4);
            int zMaxB = lineCoords.get(i + 5);
            if (xMinA == xMinB && yMinA == yMinB && zMinA == zMinB && xMaxA == xMaxB && yMaxA == yMaxB && zMaxA == zMaxB) {
               lineHandled = true;
               break;
            }

            int touchPoint = this.getLinesTouch(xMinA, yMinA, zMinA, xMaxA, yMaxA, zMaxA, xMinB, yMinB, zMinB, xMaxB, yMaxB, zMaxB);
            if (touchPoint == 1) {
               mergedLineCoords.set(j + 2, zMinB);
               mergedLineCoords.set(j + 5, zMaxA);
            } else if (touchPoint == 2) {
               mergedLineCoords.set(j + 2, zMinA);
               mergedLineCoords.set(j + 5, zMaxB);
            } else if (touchPoint == 3) {
               mergedLineCoords.set(j + 1, yMinB);
               mergedLineCoords.set(j + 4, yMaxA);
            } else if (touchPoint == 4) {
               mergedLineCoords.set(j + 1, yMinA);
               mergedLineCoords.set(j + 4, yMaxB);
            } else if (touchPoint == 5) {
               mergedLineCoords.set(j + 0, xMinB);
               mergedLineCoords.set(j + 3, xMaxA);
            } else if (touchPoint == 6) {
               mergedLineCoords.set(j + 0, xMinA);
               mergedLineCoords.set(j + 3, xMaxB);
            }

            if (touchPoint != -1) {
               lineHandled = true;
               break;
            }
         }

         if (!lineHandled) {
            mergedLineCoords.add(lineCoords.get(i + 0));
            mergedLineCoords.add(lineCoords.get(i + 1));
            mergedLineCoords.add(lineCoords.get(i + 2));
            mergedLineCoords.add(lineCoords.get(i + 3));
            mergedLineCoords.add(lineCoords.get(i + 4));
            mergedLineCoords.add(lineCoords.get(i + 5));
         }
      }

      return mergedLineCoords;
   }

   private int getLinesTouch(int xMinA, int yMinA, int zMinA, int xMaxA, int yMaxA, int zMaxA, int xMinB, int yMinB, int zMinB, int xMaxB, int yMaxB, int zMaxB) {
      if (xMinA == xMaxA && xMinB == xMaxB && xMinA == xMinB && xMaxA == xMaxB && yMinA == yMaxA && yMinB == yMaxB && yMinA == yMinB && yMaxA == yMaxB) {
         if (zMinA == zMaxB) {
            return 1;
         }

         if (zMaxA == zMinB) {
            return 2;
         }
      }

      if (xMinA == xMaxA && xMinB == xMaxB && xMinA == xMinB && xMaxA == xMaxB && zMinA == zMaxA && zMinB == zMaxB && zMinA == zMinB && zMaxA == zMaxB) {
         if (yMinA == yMaxB) {
            return 3;
         }

         if (yMaxA == yMinB) {
            return 4;
         }
      }

      if (yMinA == yMaxA && yMinB == yMaxB && yMinA == yMinB && yMaxA == yMaxB && zMinA == zMaxA && zMinB == zMaxB && zMinA == zMinB && zMaxA == zMaxB) {
         if (xMinA == xMaxB) {
            return 5;
         }

         if (xMaxA == zMinB) {
            return 6;
         }
      }

      return -1;
   }

   public Float[] getBakedLineCoords() {
      return this.bakedLineCoords;
   }

   private EnumSet<BoundingVolume.Line> getLines(int x, int y, int z) {
      EnumSet<BoundingVolume.Line> out = EnumSet.noneOf(BoundingVolume.Line.class);
      boolean addLine = !this.isCarved(x, y, z);

      for (BoundingVolume.Line line : BoundingVolume.Line.values()) {
         boolean valX = this.isCarved(x + line.dirs[0], y, z);
         boolean valY = this.isCarved(x, y + line.dirs[1], z);
         boolean valZ = this.isCarved(x, y, z + line.dirs[2]);
         if (line.dirs[0] == 0) {
            valX = addLine;
         }

         if (line.dirs[1] == 0) {
            valY = addLine;
         }

         if (line.dirs[2] == 0) {
            valZ = addLine;
         }

         if (valX == addLine && valY == addLine && valZ == addLine) {
            out.add(line);
         }
      }

      return out;
   }

   public BoundingVolume copy() {
      BoundingVolume volume = newEmptyVolume();
      volume.addVolume(this);
      return volume;
   }

   private static enum Line {
      MIN_X_MIN_Y(-1, -1, 0),
      MIN_X_MAX_Y(-1, 1, 0),
      MAX_X_MIN_Y(1, -1, 0),
      MAX_X_MAX_Y(1, 1, 0),
      MIN_X_MIN_Z(-1, 0, -1),
      MIN_X_MAX_Z(-1, 0, 1),
      MAX_X_MIN_Z(1, 0, -1),
      MAX_X_MAX_Z(1, 0, 1),
      MIN_Y_MIN_Z(0, -1, -1),
      MIN_Y_MAX_Z(0, -1, 1),
      MAX_Y_MIN_Z(0, 1, -1),
      MAX_Y_MAX_Z(0, 1, 1);

      public final int[] dirs;

      private Line(int dirX, int dirY, int dirZ) {
         this.dirs = new int[]{dirX, dirY, dirZ};
      }

      public int[] getCoords() {
         int[] out = new int[6];

         for (int i = 0; i < this.dirs.length; i++) {
            int dir = this.dirs[i];
            if (dir == -1) {
               out[i] = out[i + 3] = 0;
            } else if (dir == 0) {
               out[i] = 0;
               out[i + 3] = 1;
            } else if (dir == 1) {
               out[i] = out[i + 3] = 1;
            }
         }

         return out;
      }
   }
}
