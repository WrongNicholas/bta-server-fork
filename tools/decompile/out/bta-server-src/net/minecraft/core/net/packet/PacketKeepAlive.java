package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketKeepAlive extends Packet {
   @Override
   public void handlePacket(PacketHandler packetHandler) {
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
   }

   @Override
   public int getEstimatedSize() {
      return 0;
   }
}
