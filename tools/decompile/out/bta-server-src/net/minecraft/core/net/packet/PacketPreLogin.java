package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketPreLogin extends Packet {
   public String username;

   public PacketPreLogin() {
   }

   public PacketPreLogin(String s) {
      this.username = s;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.username = readStringUTF8(dis, 16);
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      writeStringUTF8(this.username, dos);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleHandshake(this);
   }

   @Override
   public int getEstimatedSize() {
      return 4 + this.username.length() + 4;
   }
}
