package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.util.helper.MathHelper;

public class PacketTeleportEntity extends Packet {
   public int id;
   public int x;
   public int y;
   public int z;
   public byte yaw;
   public byte pitch;

   public PacketTeleportEntity() {
   }

   public PacketTeleportEntity(Entity entity) {
      this.id = entity.id;
      this.x = MathHelper.floor(entity.x * 32.0);
      this.y = MathHelper.floor(entity.y * 32.0);
      this.z = MathHelper.floor(entity.z * 32.0);
      this.yaw = (byte)(entity.yRot * 256.0F / 360.0F);
      this.pitch = (byte)(entity.xRot * 256.0F / 360.0F);
   }

   public PacketTeleportEntity(int id, int x, int y, int z, byte yaw, byte pitch) {
      this.id = id;
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = yaw;
      this.pitch = pitch;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.id = dis.readInt();
      this.x = dis.readInt();
      this.y = dis.readInt();
      this.z = dis.readInt();
      this.yaw = (byte)dis.read();
      this.pitch = (byte)dis.read();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.id);
      dos.writeInt(this.x);
      dos.writeInt(this.y);
      dos.writeInt(this.z);
      dos.write(this.yaw);
      dos.write(this.pitch);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleEntityTeleport(this);
   }

   @Override
   public int getEstimatedSize() {
      return 34;
   }
}
