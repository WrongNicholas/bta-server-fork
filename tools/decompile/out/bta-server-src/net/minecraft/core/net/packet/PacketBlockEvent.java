package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketBlockEvent extends Packet {
   public int xLocation;
   public int yLocation;
   public int zLocation;
   public int index;
   public int data;

   public PacketBlockEvent() {
   }

   public PacketBlockEvent(int x, int y, int z, int index, int data) {
      this.xLocation = x;
      this.yLocation = y;
      this.zLocation = z;
      this.index = index;
      this.data = data;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.xLocation = dis.readInt();
      this.yLocation = dis.readShort();
      this.zLocation = dis.readInt();
      this.index = dis.read();
      this.data = dis.read();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.xLocation);
      dos.writeShort(this.yLocation);
      dos.writeInt(this.zLocation);
      dos.write(this.index);
      dos.write(this.data);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleBlockEvent(this);
   }

   @Override
   public int getEstimatedSize() {
      return 12;
   }
}
