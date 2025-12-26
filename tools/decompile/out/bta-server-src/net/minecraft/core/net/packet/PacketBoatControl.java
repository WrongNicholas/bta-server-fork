package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketBoatControl extends Packet {
   public int entityId;
   public float targetXD;
   public float targetZD;
   public float targetYRot;

   public PacketBoatControl() {
   }

   public PacketBoatControl(Entity entity) {
      this.entityId = entity.id;
      this.targetXD = (float)entity.xd;
      this.targetZD = (float)entity.zd;
      this.targetYRot = entity.yRot;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
      this.targetXD = dis.readFloat();
      this.targetZD = dis.readFloat();
      this.targetYRot = dis.readFloat();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
      dos.writeFloat(this.targetXD);
      dos.writeFloat(this.targetZD);
      dos.writeFloat(this.targetYRot);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleBoatControl(this);
   }

   @Override
   public int getEstimatedSize() {
      return 12;
   }
}
