package net.minecraft.core.world.chunk;

public class ChunkCoordinate {
   public final int x;
   public final int z;

   public ChunkCoordinate(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public static int toInt(int x, int z) {
      return (x >= 0 ? 0 : Integer.MIN_VALUE) | (x & 16383) << 16 | (z >= 0 ? 0 : 32768) | z & 16383;
   }

   @Override
   public int hashCode() {
      return toInt(this.x, this.z);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof ChunkCoordinate)) {
         return false;
      } else {
         ChunkCoordinate otherPair = (ChunkCoordinate)obj;
         return otherPair.x == this.x && otherPair.z == this.z;
      }
   }
}
