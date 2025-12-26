package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketContainerSetData extends Packet {
   public int windowId;
   public int progressBar;
   public int progressBarValue;

   public PacketContainerSetData() {
   }

   public PacketContainerSetData(int windowId, int progressBar, int progressBarValue) {
      this.windowId = windowId;
      this.progressBar = progressBar;
      this.progressBarValue = progressBarValue;
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleUpdateProgressbar(this);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.windowId = dis.readByte();
      this.progressBar = dis.readShort();
      this.progressBarValue = dis.readShort();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.windowId);
      dos.writeShort(this.progressBar);
      dos.writeShort(this.progressBarValue);
   }

   @Override
   public int getEstimatedSize() {
      return 7;
   }
}
