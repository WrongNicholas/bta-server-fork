package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketRemoveEntity extends Packet {
   public int entityId;

   public PacketRemoveEntity() {
   }

   public PacketRemoveEntity(int i) {
      this.entityId = i;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleDestroyEntity(this);
   }

   @Override
   public int getEstimatedSize() {
      return 4;
   }
}
