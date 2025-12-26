package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketBedMessage extends Packet {
   public static final String[] BED_MESSAGE_KEYS = new String[]{"bed.notValid", null, null};
   public int bedState;

   public PacketBedMessage() {
   }

   public PacketBedMessage(int state) {
      this.bedState = state;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.bedState = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.bedState);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleBed(this);
   }

   @Override
   public int getEstimatedSize() {
      return 1;
   }
}
