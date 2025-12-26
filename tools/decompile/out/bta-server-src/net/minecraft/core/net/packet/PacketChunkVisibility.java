package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketChunkVisibility extends Packet {
   public int chunkX;
   public int chunkZ;
   public boolean playerAdded;

   public PacketChunkVisibility() {
      this.isChunkDataPacket = false;
   }

   public PacketChunkVisibility(int chunkX, int chunkZ, boolean playerAdded) {
      this.isChunkDataPacket = false;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
      this.playerAdded = playerAdded;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.chunkX = dis.readInt();
      this.chunkZ = dis.readInt();
      this.playerAdded = dis.read() != 0;
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.chunkX);
      dos.writeInt(this.chunkZ);
      dos.write(this.playerAdded ? 1 : 0);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handlePreChunk(this);
   }

   @Override
   public int getEstimatedSize() {
      return 9;
   }
}
