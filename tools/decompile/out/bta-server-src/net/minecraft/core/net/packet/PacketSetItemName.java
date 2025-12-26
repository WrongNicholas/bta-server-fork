package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSetItemName extends Packet {
   public int slot;
   public String name;

   public PacketSetItemName() {
   }

   public PacketSetItemName(int slot, String name) {
      this.slot = slot;
      this.name = name;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.slot = dis.readInt();
      this.name = dis.readUTF();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.slot);
      dos.writeUTF(this.name);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleItemName(this);
   }

   @Override
   public int getEstimatedSize() {
      return 2;
   }
}
