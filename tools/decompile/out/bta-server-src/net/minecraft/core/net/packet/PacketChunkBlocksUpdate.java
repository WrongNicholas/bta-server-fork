package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

public class PacketChunkBlocksUpdate extends Packet {
   public int xChunk;
   public int zChunk;
   public int[] coordinateArray;
   public short[] typeArray;
   public byte[] metadataArray;
   public int size;

   public PacketChunkBlocksUpdate() {
      this.isChunkDataPacket = true;
   }

   public PacketChunkBlocksUpdate(int xChunk, int zChunk, int[] blocksToUpdate, int numBlocksToUpdate, World world) {
      this.isChunkDataPacket = true;
      this.xChunk = xChunk;
      this.zChunk = zChunk;
      this.size = numBlocksToUpdate;
      this.coordinateArray = new int[numBlocksToUpdate];
      this.typeArray = new short[numBlocksToUpdate];
      this.metadataArray = new byte[numBlocksToUpdate];
      Chunk chunk = world.getChunkFromChunkCoords(xChunk, zChunk);

      for (int i = 0; i < numBlocksToUpdate; i++) {
         int blockX = blocksToUpdate[i] >> 0 & 15;
         int blockY = blocksToUpdate[i] >> 8 & 0xFF;
         int blockZ = blocksToUpdate[i] >> 4 & 15;
         this.coordinateArray[i] = blocksToUpdate[i];
         this.typeArray[i] = (short)chunk.getBlockID(blockX, blockY, blockZ);
         this.metadataArray[i] = (byte)chunk.getBlockMetadata(blockX, blockY, blockZ);
      }
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.xChunk = dis.readInt();
      this.zChunk = dis.readInt();
      this.size = dis.readShort() & '\uffff';
      this.coordinateArray = new int[this.size];
      this.typeArray = new short[this.size];
      this.metadataArray = new byte[this.size];

      for (int i = 0; i < this.size; i++) {
         this.coordinateArray[i] = dis.readInt();
      }

      for (int i = 0; i < this.size; i++) {
         this.typeArray[i] = dis.readShort();
      }

      dis.readFully(this.metadataArray);
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.xChunk);
      dos.writeInt(this.zChunk);
      dos.writeShort((short)this.size);

      for (int i = 0; i < this.size; i++) {
         dos.writeInt(this.coordinateArray[i]);
      }

      for (int i = 0; i < this.size; i++) {
         dos.writeShort(this.typeArray[i]);
      }

      dos.write(this.metadataArray);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleMultiBlockChange(this);
   }

   @Override
   public int getEstimatedSize() {
      return 10 + this.size * 4;
   }
}
