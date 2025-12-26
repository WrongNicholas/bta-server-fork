package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSetEntityMotion extends Packet {
   public int entityId;
   public int motionX;
   public int motionY;
   public int motionZ;

   public PacketSetEntityMotion() {
   }

   public PacketSetEntityMotion(Entity entity) {
      this(entity.id, entity.xd, entity.yd, entity.zd);
   }

   public PacketSetEntityMotion(int i, double d, double d1, double d2) {
      this.entityId = i;
      double d3 = 3.9;
      if (d < -d3) {
         d = -d3;
      }

      if (d1 < -d3) {
         d1 = -d3;
      }

      if (d2 < -d3) {
         d2 = -d3;
      }

      if (d > d3) {
         d = d3;
      }

      if (d1 > d3) {
         d1 = d3;
      }

      if (d2 > d3) {
         d2 = d3;
      }

      this.motionX = (int)(d * 8000.0);
      this.motionY = (int)(d1 * 8000.0);
      this.motionZ = (int)(d2 * 8000.0);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
      this.motionX = dis.readShort();
      this.motionY = dis.readShort();
      this.motionZ = dis.readShort();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
      dos.writeShort(this.motionX);
      dos.writeShort(this.motionY);
      dos.writeShort(this.motionZ);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleEntityVelocity(this);
   }

   @Override
   public int getEstimatedSize() {
      return 10;
   }
}
