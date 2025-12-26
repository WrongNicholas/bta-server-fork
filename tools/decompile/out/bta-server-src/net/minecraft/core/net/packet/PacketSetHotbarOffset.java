package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSetHotbarOffset extends Packet {
   public int hotbarOffset;

   public PacketSetHotbarOffset(int hotbarOffset) {
      this.hotbarOffset = hotbarOffset;
   }

   public PacketSetHotbarOffset() {
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.hotbarOffset = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.hotbarOffset);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleSetHotbarOffset(this);
   }

   @Override
   public int getEstimatedSize() {
      return 0;
   }
}
