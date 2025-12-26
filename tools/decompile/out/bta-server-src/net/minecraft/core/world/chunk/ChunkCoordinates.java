package net.minecraft.core.world.chunk;

import java.util.Objects;

public class ChunkCoordinates implements Comparable<ChunkCoordinates> {
   public int x;
   public int y;
   public int z;

   public ChunkCoordinates() {
   }

   public ChunkCoordinates(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public ChunkCoordinates(ChunkCoordinates pos) {
      this.x = pos.x;
      this.y = pos.y;
      this.z = pos.z;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof ChunkCoordinates)) {
         return false;
      } else {
         ChunkCoordinates chunkcoordinates = (ChunkCoordinates)obj;
         return this.x == chunkcoordinates.x && this.y == chunkcoordinates.y && this.z == chunkcoordinates.z;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.x, this.y, this.z);
   }

   public int compareChunkCoordinate(ChunkCoordinates chunkcoordinates) {
      if (this.y == chunkcoordinates.y) {
         return this.z == chunkcoordinates.z ? this.x - chunkcoordinates.x : this.z - chunkcoordinates.z;
      } else {
         return this.y - chunkcoordinates.y;
      }
   }

   public double getSqDistanceTo(int x, int y, int z) {
      int dx = this.x - x;
      int dy = this.y - y;
      int dz = this.z - z;
      return Math.sqrt(dx * dx + dy * dy + dz * dz);
   }

   public int compareTo(ChunkCoordinates o) {
      return this.compareChunkCoordinate(o);
   }

   public boolean equals(int x, int y, int z) {
      return this.x == x && this.y == y && this.z == z;
   }

   @Override
   public String toString() {
      return "ChunkCoordinates{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
   }
}
