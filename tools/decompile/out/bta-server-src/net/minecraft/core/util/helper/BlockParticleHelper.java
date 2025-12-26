package net.minecraft.core.util.helper;

public class BlockParticleHelper {
   public static int encodeBlockData(int blockId, int meta, Side side) {
      return blockId & 16383 | (meta & 0xFF) << 14 | side.getId() << 22;
   }

   public static int decodeBlockID(int data) {
      return data & 16383;
   }

   public static int decodeBlockMeta(int data) {
      return data >> 14 & 0xFF;
   }

   public static Side decodeBlockSide(int data) {
      return Side.getSideById(data >> 22 & 15);
   }
}
