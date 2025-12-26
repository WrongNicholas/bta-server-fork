package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketRespawn extends Packet {
   public byte respawnDimensionId;
   public byte respawnWorldTypeId;

   public PacketRespawn() {
   }

   public PacketRespawn(byte respawnDimensionId, byte respawnWorldTypeId) {
      this.respawnDimensionId = respawnDimensionId;
      this.respawnWorldTypeId = respawnWorldTypeId;
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleRespawn(this);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.respawnDimensionId = dis.readByte();
      this.respawnWorldTypeId = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.respawnDimensionId);
      dos.writeByte(this.respawnWorldTypeId);
   }

   @Override
   public int getEstimatedSize() {
      return 2;
   }
}
