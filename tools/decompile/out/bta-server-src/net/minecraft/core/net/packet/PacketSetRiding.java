package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSetRiding extends Packet {
   public int passengerId;
   public boolean isTileEntity;
   public int vehicleId;
   public int x;
   public int y;
   public int z;

   public PacketSetRiding() {
   }

   public PacketSetRiding(Entity passenger, Entity vehicle) {
      this.passengerId = passenger.id;
      this.vehicleId = vehicle == null ? -1 : vehicle.id;
      this.isTileEntity = false;
   }

   public PacketSetRiding(Entity passenger, int x, int y, int z) {
      this.passengerId = passenger.id;
      this.x = x;
      this.y = y;
      this.z = z;
      this.isTileEntity = true;
   }

   @Override
   public int getEstimatedSize() {
      return 8;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.passengerId = dis.readInt();
      this.isTileEntity = dis.readByte() != 0;
      if (this.isTileEntity) {
         this.x = dis.readInt();
         this.y = dis.readInt();
         this.z = dis.readInt();
      } else {
         this.vehicleId = dis.readInt();
      }
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.passengerId);
      dos.writeByte(this.isTileEntity ? 1 : 0);
      if (this.isTileEntity) {
         dos.writeInt(this.x);
         dos.writeInt(this.y);
         dos.writeInt(this.z);
      } else {
         dos.writeInt(this.vehicleId);
      }
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleAttachEntity(this);
   }
}
