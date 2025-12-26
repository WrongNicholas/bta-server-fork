package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketAnimate extends Packet {
   public int entityId;
   public int animate;

   public PacketAnimate() {
   }

   public PacketAnimate(Entity entity, int i) {
      this.entityId = entity.id;
      this.animate = i;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
      this.animate = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
      dos.writeByte(this.animate);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleAnimation(this);
   }

   @Override
   public int getEstimatedSize() {
      return 5;
   }
}
