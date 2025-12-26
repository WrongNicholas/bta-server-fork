package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketUpdatePlayerState extends Packet {
   public static final int STATE_SNEAK = 1;
   public static final int STATE_UN_SNEAK = 2;
   public static final int STATE_LEAVE_BED = 3;
   public int state;

   public PacketUpdatePlayerState() {
   }

   public PacketUpdatePlayerState(int state) {
      this.state = state;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.state = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.state);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handlePlayerState(this);
   }

   @Override
   public int getEstimatedSize() {
      return 1;
   }
}
