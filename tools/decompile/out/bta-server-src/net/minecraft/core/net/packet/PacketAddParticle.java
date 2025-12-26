package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketAddParticle extends Packet {
   public String particleKey;
   public double x;
   public double y;
   public double z;
   public double motionX;
   public double motionY;
   public double motionZ;
   public int data;
   public double maxDistance;
   public boolean isGroup = false;
   public byte amount;
   public float randOffX;
   public float randOffY;
   public float randOffZ;
   public float randMotionX;
   public float randMotionY;
   public float randMotionZ;

   public PacketAddParticle() {
   }

   public PacketAddParticle(String particleKey, double x, double y, double z, double motionX, double motionY, double motionZ, int data) {
      this(particleKey, x, y, z, motionX, motionY, motionZ, data, 16.0);
   }

   public PacketAddParticle(String particleKey, double x, double y, double z, double motionX, double motionY, double motionZ, int data, double maxDistance) {
      this(particleKey, x, y, z, motionX, motionY, motionZ, data, maxDistance, (byte)-1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public PacketAddParticle(
      String particleKey,
      double x,
      double y,
      double z,
      double motionX,
      double motionY,
      double motionZ,
      int data,
      double maxDistance,
      byte amount,
      float randOffX,
      float randOffY,
      float randOffZ,
      float randMotionX,
      float randMotionY,
      float randMotionZ
   ) {
      this.particleKey = particleKey;
      this.x = x;
      this.y = y;
      this.z = z;
      this.motionX = motionX;
      this.motionY = motionY;
      this.motionZ = motionZ;
      this.data = data;
      this.maxDistance = maxDistance;
      if (amount > 0) {
         this.isGroup = true;
         this.amount = amount;
         this.randOffX = randOffX;
         this.randOffY = randOffY;
         this.randOffZ = randOffZ;
         this.randMotionX = randMotionX;
         this.randMotionY = randMotionY;
         this.randMotionZ = randMotionZ;
      }
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.particleKey = dis.readUTF();
      this.x = dis.readDouble();
      this.y = dis.readDouble();
      this.z = dis.readDouble();
      this.motionX = dis.readDouble();
      this.motionY = dis.readDouble();
      this.motionZ = dis.readDouble();
      this.maxDistance = dis.readDouble();
      if (dis.readBoolean()) {
         this.isGroup = true;
         this.amount = dis.readByte();
         this.randOffX = dis.readFloat();
         this.randOffY = dis.readFloat();
         this.randOffZ = dis.readFloat();
         this.randMotionX = dis.readFloat();
         this.randMotionY = dis.readFloat();
         this.randMotionZ = dis.readFloat();
      } else {
         this.isGroup = false;
      }
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeUTF(this.particleKey);
      dos.writeDouble(this.x);
      dos.writeDouble(this.y);
      dos.writeDouble(this.z);
      dos.writeDouble(this.motionX);
      dos.writeDouble(this.motionY);
      dos.writeDouble(this.motionZ);
      dos.writeDouble(this.maxDistance);
      dos.writeBoolean(this.isGroup);
      if (this.isGroup) {
         dos.writeByte(this.amount);
         dos.writeFloat(this.randOffX);
         dos.writeFloat(this.randOffY);
         dos.writeFloat(this.randOffZ);
         dos.writeFloat(this.randMotionX);
         dos.writeFloat(this.randMotionY);
         dos.writeFloat(this.randMotionZ);
      }
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleSpawnParticle(this);
   }

   @Override
   public int getEstimatedSize() {
      return 40;
   }
}
