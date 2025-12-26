package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketEntityEvent extends Packet {
   public int entityId;
   public byte entityStatus;
   public float attackedAtYaw;

   public PacketEntityEvent() {
   }

   public PacketEntityEvent(int entityId, byte entityStatus) {
      this.entityId = entityId;
      this.entityStatus = entityStatus;
      this.attackedAtYaw = 0.0F;
   }

   public PacketEntityEvent(int entityId, byte entityStatus, float attackedAtYaw) {
      this.entityId = entityId;
      this.entityStatus = entityStatus;
      this.attackedAtYaw = attackedAtYaw;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
      this.entityStatus = dis.readByte();
      this.attackedAtYaw = dis.readFloat();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
      dos.writeByte(this.entityStatus);
      dos.writeFloat(this.attackedAtYaw);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleEntityStatus(this);
   }

   @Override
   public int getEstimatedSize() {
      return 9;
   }
}
