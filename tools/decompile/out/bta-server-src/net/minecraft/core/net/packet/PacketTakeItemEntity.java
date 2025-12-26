package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketTakeItemEntity extends Packet {
   public int collectedEntityId;
   public int collectorEntityId;

   public PacketTakeItemEntity() {
   }

   public PacketTakeItemEntity(int i, int j) {
      this.collectedEntityId = i;
      this.collectorEntityId = j;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.collectedEntityId = dis.readInt();
      this.collectorEntityId = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.collectedEntityId);
      dos.writeInt(this.collectorEntityId);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleCollect(this);
   }

   @Override
   public int getEstimatedSize() {
      return 8;
   }
}
