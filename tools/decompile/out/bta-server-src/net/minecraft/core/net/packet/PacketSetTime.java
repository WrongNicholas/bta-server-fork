package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSetTime extends Packet {
   public long time;

   public PacketSetTime() {
   }

   public PacketSetTime(long l) {
      this.time = l;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.time = dis.readLong();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeLong(this.time);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleUpdateTime(this);
   }

   @Override
   public int getEstimatedSize() {
      return 8;
   }
}
