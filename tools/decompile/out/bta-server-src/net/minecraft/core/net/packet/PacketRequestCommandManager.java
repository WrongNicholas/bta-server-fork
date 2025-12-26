package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketRequestCommandManager extends Packet {
   public String username;
   public String text;
   public int cursor;

   public PacketRequestCommandManager(String username, String text, int cursor) {
      this.username = username;
      this.text = text;
      this.cursor = cursor;
   }

   public PacketRequestCommandManager() {
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.username = dis.readUTF();
      this.text = dis.readUTF();
      this.cursor = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeUTF(this.username);
      dos.writeUTF(this.text);
      dos.writeInt(this.cursor);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleRequestCommandManagerPacket(this);
   }

   @Override
   public int getEstimatedSize() {
      return 10;
   }
}
