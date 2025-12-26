package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketContainerAck extends Packet {
   public int windowId;
   public short shortWindowId;
   public boolean accepted;

   public PacketContainerAck() {
   }

   public PacketContainerAck(int windowId, short shortWindowId, boolean accepted) {
      this.windowId = windowId;
      this.shortWindowId = shortWindowId;
      this.accepted = accepted;
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleTransaction(this);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.windowId = dis.readByte();
      this.shortWindowId = dis.readShort();
      this.accepted = dis.readByte() != 0;
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.windowId);
      dos.writeShort(this.shortWindowId);
      dos.writeByte(this.accepted ? 1 : 0);
   }

   @Override
   public int getEstimatedSize() {
      return 4;
   }
}
