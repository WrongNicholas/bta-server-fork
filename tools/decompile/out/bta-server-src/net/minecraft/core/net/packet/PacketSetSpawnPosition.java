package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSetSpawnPosition extends Packet {
   public int x;
   public int y;
   public int z;

   public PacketSetSpawnPosition() {
   }

   public PacketSetSpawnPosition(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.x = dis.readInt();
      this.y = dis.readInt();
      this.z = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.x);
      dos.writeInt(this.y);
      dos.writeInt(this.z);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleSpawnPosition(this);
   }

   @Override
   public int getEstimatedSize() {
      return 12;
   }
}
