package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketFlagOpen extends Packet {
   public int windowId;
   public int x;
   public int y;
   public int z;

   public PacketFlagOpen() {
   }

   public PacketFlagOpen(int windowId, int x, int y, int z) {
      this.windowId = windowId;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.windowId = dis.readByte();
      this.x = dis.readInt();
      this.y = dis.readShort();
      this.z = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.windowId);
      dos.writeInt(this.x);
      dos.writeShort(this.y);
      dos.writeInt(this.z);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleOpenFlagWindow(this);
   }

   @Override
   public int getEstimatedSize() {
      return 11;
   }
}
