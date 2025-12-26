package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketAESSendKey extends Packet {
   public String KEY;

   public PacketAESSendKey() {
   }

   public PacketAESSendKey(String KEY) {
      this.KEY = KEY;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.KEY = readStringUTF8(dis, 392);
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      writeStringUTF8(this.KEY, dos);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleSendKey(this);
   }

   @Override
   public int getEstimatedSize() {
      return 128;
   }
}
