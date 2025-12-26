package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSleep extends Packet {
   public int entityID;
   public int x;
   public int y;
   public int z;
   public int field_22046_e;

   public PacketSleep() {
   }

   public PacketSleep(Entity entity, int i, int x, int y, int z) {
      this.field_22046_e = i;
      this.x = x;
      this.y = y;
      this.z = z;
      this.entityID = entity.id;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityID = dis.readInt();
      this.field_22046_e = dis.readByte();
      this.x = dis.readInt();
      this.y = dis.readInt();
      this.z = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityID);
      dos.writeByte(this.field_22046_e);
      dos.writeInt(this.x);
      dos.writeInt(this.y);
      dos.writeInt(this.z);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleSleep(this);
   }

   @Override
   public int getEstimatedSize() {
      return 17;
   }
}
