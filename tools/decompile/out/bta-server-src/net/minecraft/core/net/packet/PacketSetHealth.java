package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSetHealth extends Packet {
   public int healthMP;

   public PacketSetHealth() {
   }

   public PacketSetHealth(int i) {
      this.healthMP = i;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.healthMP = dis.readShort();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeShort(this.healthMP);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleUpdateHealth(this);
   }

   @Override
   public int getEstimatedSize() {
      return 2;
   }
}
