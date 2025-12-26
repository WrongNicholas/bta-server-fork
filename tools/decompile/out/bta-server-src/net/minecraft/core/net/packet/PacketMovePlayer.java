package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketMovePlayer extends Packet {
   public double x;
   public double y;
   public double z;
   public float yaw;
   public float pitch;
   public boolean onGround;
   public boolean hasPosition;
   public boolean hasRotation;

   public PacketMovePlayer() {
   }

   public PacketMovePlayer(boolean flag) {
      this.onGround = flag;
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleFlying(this);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.onGround = dis.read() != 0;
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.write(this.onGround ? 1 : 0);
   }

   @Override
   public int getEstimatedSize() {
      return 1;
   }

   public static class Pos extends PacketMovePlayer {
      public Pos() {
         this.hasPosition = true;
      }

      public Pos(double x, double y, double z, boolean onGround) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.onGround = onGround;
         this.hasPosition = true;
      }

      @Override
      public void read(DataInputStream dataInputStream) throws IOException {
         this.x = dataInputStream.readDouble();
         this.y = dataInputStream.readDouble();
         this.z = dataInputStream.readDouble();
         super.read(dataInputStream);
      }

      @Override
      public void write(DataOutputStream dataOutputStream) throws IOException {
         dataOutputStream.writeDouble(this.x);
         dataOutputStream.writeDouble(this.y);
         dataOutputStream.writeDouble(this.z);
         super.write(dataOutputStream);
      }

      @Override
      public int getEstimatedSize() {
         return 33;
      }
   }

   public static class PosRot extends PacketMovePlayer {
      public PosRot() {
         this.hasRotation = true;
         this.hasPosition = true;
      }

      public PosRot(double x, double y, double z, float yaw, float pitch, boolean onGround) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.yaw = yaw;
         this.pitch = pitch;
         this.onGround = onGround;
         this.hasRotation = true;
         this.hasPosition = true;
      }

      @Override
      public void read(DataInputStream dataInputStream) throws IOException {
         this.x = dataInputStream.readDouble();
         this.y = dataInputStream.readDouble();
         this.z = dataInputStream.readDouble();
         this.yaw = dataInputStream.readFloat();
         this.pitch = dataInputStream.readFloat();
         super.read(dataInputStream);
      }

      @Override
      public void write(DataOutputStream dataOutputStream) throws IOException {
         dataOutputStream.writeDouble(this.x);
         dataOutputStream.writeDouble(this.y);
         dataOutputStream.writeDouble(this.z);
         dataOutputStream.writeFloat(this.yaw);
         dataOutputStream.writeFloat(this.pitch);
         super.write(dataOutputStream);
      }

      @Override
      public int getEstimatedSize() {
         return 41;
      }
   }

   public static class Rot extends PacketMovePlayer {
      public Rot() {
         this.hasRotation = true;
      }

      public Rot(float yaw, float pitch, boolean onGround) {
         this.yaw = yaw;
         this.pitch = pitch;
         this.onGround = onGround;
         this.hasRotation = true;
      }

      @Override
      public void read(DataInputStream dataInputStream) throws IOException {
         this.yaw = dataInputStream.readFloat();
         this.pitch = dataInputStream.readFloat();
         super.read(dataInputStream);
      }

      @Override
      public void write(DataOutputStream dataOutputStream) throws IOException {
         dataOutputStream.writeFloat(this.yaw);
         dataOutputStream.writeFloat(this.pitch);
         super.write(dataOutputStream);
      }

      @Override
      public int getEstimatedSize() {
         return 9;
      }
   }
}
