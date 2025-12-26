package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSetCarriedItem extends Packet {
   public int id;

   public PacketSetCarriedItem() {
   }

   public PacketSetCarriedItem(int i) {
      this.id = i;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.id = dis.readShort();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeShort(this.id);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleBlockItemSwitch(this);
   }

   @Override
   public int getEstimatedSize() {
      return 2;
   }
}
