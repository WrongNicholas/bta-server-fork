package net.minecraft.core.world.data;

public class ChunkNibbleArray implements ChunkArray<Integer> {
   public final byte[] data;
   private final int xSize;
   private final int ySize;
   private final int zSize;

   public ChunkNibbleArray(int xSize, int ySize, int zSize) {
      this.xSize = xSize;
      this.ySize = ySize;
      this.zSize = zSize;
      this.data = new byte[xSize * ySize * zSize >> 1];
   }

   public ChunkNibbleArray(int xSize, int ySize, int zSize, byte[] data) {
      this.xSize = xSize;
      this.ySize = ySize;
      this.zSize = zSize;
      if (data.length == xSize * ySize * zSize >> 1) {
         this.data = data;
      } else {
         this.data = null;
      }
   }

   public Integer get(int x, int y, int z) {
      int index = y * this.zSize * this.xSize + z * this.xSize + x;
      int nibbleIndex = index >> 1;
      int nibblePart = index & 1;
      return nibblePart == 0 ? this.data[nibbleIndex] & 15 : this.data[nibbleIndex] >> 4 & 15;
   }

   public void set(int x, int y, int z, Integer value) {
      int index = y * this.zSize * this.xSize + z * this.xSize + x;
      int nibbleIndex = index >> 1;
      int nibblePart = index & 1;
      if (nibblePart == 0) {
         this.data[nibbleIndex] = (byte)(this.data[nibbleIndex] & 240 | value & 15);
      } else {
         this.data[nibbleIndex] = (byte)(this.data[nibbleIndex] & 15 | (value & 15) << 4);
      }
   }

   @Override
   public boolean isValid() {
      return this.data != null && this.data.length == this.xSize * this.ySize * this.zSize >> 1;
   }
}
