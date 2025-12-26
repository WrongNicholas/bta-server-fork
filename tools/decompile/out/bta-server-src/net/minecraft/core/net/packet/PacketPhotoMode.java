package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketPhotoMode extends Packet {
   public boolean disabled = false;

   public PacketPhotoMode() {
   }

   public PacketPhotoMode(boolean disabled) {
      this.disabled = disabled;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.disabled = dis.readBoolean();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeBoolean(this.disabled);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handlePhotoMode(this);
   }

   @Override
   public int getEstimatedSize() {
      return 1;
   }
}
