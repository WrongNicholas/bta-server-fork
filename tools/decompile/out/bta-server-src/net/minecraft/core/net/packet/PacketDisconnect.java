package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketDisconnect extends Packet {
   public String reason;
   private byte[] image = null;

   public PacketDisconnect() {
   }

   public PacketDisconnect(String s) {
      this(s, null);
   }

   public PacketDisconnect(String s, byte[] image) {
      this.reason = s;
      this.image = image;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.reason = readStringUTF16BE(dis, 255);
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      writeStringUTF16BE(this.reason, dos);
      if (this.image != null) {
         dos.write(this.image);
      }
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleKickDisconnect(this);
   }

   @Override
   public int getEstimatedSize() {
      return this.reason.length();
   }
}
