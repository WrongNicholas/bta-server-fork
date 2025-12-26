package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketGuidebook extends Packet {
   public boolean isGuidebookOpen;

   public PacketGuidebook() {
   }

   public PacketGuidebook(boolean i) {
      this.isGuidebookOpen = i;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.isGuidebookOpen = dis.readBoolean();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeBoolean(this.isGuidebookOpen);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleOpenGuidebook(this);
   }

   @Override
   public int getEstimatedSize() {
      return 1;
   }
}
