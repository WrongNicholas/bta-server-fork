package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketContainerClose extends Packet {
   public int windowId;

   public PacketContainerClose() {
   }

   public PacketContainerClose(int i) {
      this.windowId = i;
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleCloseWindow(this);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.windowId = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.windowId);
   }

   @Override
   public int getEstimatedSize() {
      return 1;
   }
}
