package net.minecraft.core.world.chunk;

public class ChunkPosition {
   public final int x;
   public final int y;
   public final int z;

   public ChunkPosition(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof ChunkPosition)) {
         return false;
      } else {
         ChunkPosition chunkposition = (ChunkPosition)obj;
         return chunkposition.x == this.x && chunkposition.y == this.y && chunkposition.z == this.z;
      }
   }

   @Override
   public int hashCode() {
      int result = this.x;
      result = 31 * result + this.y;
      return 31 * result + this.z;
   }
}
