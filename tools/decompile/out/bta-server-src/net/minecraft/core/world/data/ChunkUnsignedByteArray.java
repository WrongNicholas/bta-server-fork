package net.minecraft.core.world.data;

public class ChunkUnsignedByteArray implements ChunkArray<Integer> {
   public final byte[] data;
   private final int xSize;
   private final int ySize;
   private final int zSize;

   public ChunkUnsignedByteArray(int xSize, int ySize, int zSize) {
      this.xSize = xSize;
      this.ySize = ySize;
      this.zSize = zSize;
      this.data = new byte[xSize * ySize * zSize];
   }

   public ChunkUnsignedByteArray(int xSize, int ySize, int zSize, byte[] data) {
      this.xSize = xSize;
      this.ySize = ySize;
      this.zSize = zSize;
      if (data.length == xSize * ySize * zSize) {
         this.data = data;
      } else {
         this.data = null;
      }
   }

   public Integer get(int x, int y, int z) {
      int index = y * this.zSize * this.xSize + z * this.xSize + x;
      return Byte.toUnsignedInt(this.data[index]);
   }

   public void set(int x, int y, int z, Integer value) {
      int index = y * this.zSize * this.xSize + z * this.xSize + x;
      this.data[index] = value.byteValue();
   }

   @Override
   public boolean isValid() {
      return this.data != null && this.data.length == this.xSize * this.ySize * this.zSize;
   }
}
