package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketMoveEntity extends Packet {
   public int id;
   public byte x;
   public byte y;
   public byte z;
   public byte yaw;
   public byte pitch;
   public boolean rotating = false;

   public PacketMoveEntity() {
   }

   public PacketMoveEntity(int i) {
      this.id = i;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.id = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.id);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleEntity(this);
   }

   @Override
   public int getEstimatedSize() {
      return 4;
   }

   public static class Pos extends PacketMoveEntity {
      public Pos() {
      }

      public Pos(int i, byte byte0, byte byte1, byte byte2) {
         super(i);
         this.x = byte0;
         this.y = byte1;
         this.z = byte2;
      }

      @Override
      public void read(DataInputStream dis) throws IOException {
         super.read(dis);
         this.x = dis.readByte();
         this.y = dis.readByte();
         this.z = dis.readByte();
      }

      @Override
      public void write(DataOutputStream dos) throws IOException {
         super.write(dos);
         dos.writeByte(this.x);
         dos.writeByte(this.y);
         dos.writeByte(this.z);
      }

      @Override
      public int getEstimatedSize() {
         return 7;
      }
   }

   public static class PosRot extends PacketMoveEntity {
      public PosRot() {
         this.rotating = true;
      }

      public PosRot(int i, byte byte0, byte byte1, byte byte2, byte byte3, byte byte4) {
         super(i);
         this.x = byte0;
         this.y = byte1;
         this.z = byte2;
         this.yaw = byte3;
         this.pitch = byte4;
         this.rotating = true;
      }

      @Override
      public void read(DataInputStream dis) throws IOException {
         super.read(dis);
         this.x = dis.readByte();
         this.y = dis.readByte();
         this.z = dis.readByte();
         this.yaw = dis.readByte();
         this.pitch = dis.readByte();
      }

      @Override
      public void write(DataOutputStream dos) throws IOException {
         super.write(dos);
         dos.writeByte(this.x);
         dos.writeByte(this.y);
         dos.writeByte(this.z);
         dos.writeByte(this.yaw);
         dos.writeByte(this.pitch);
      }

      @Override
      public int getEstimatedSize() {
         return 9;
      }
   }

   public static class Rot extends PacketMoveEntity {
      public Rot() {
         this.rotating = true;
      }

      public Rot(int i, byte byte0, byte byte1) {
         super(i);
         this.yaw = byte0;
         this.pitch = byte1;
         this.rotating = true;
      }

      @Override
      public void read(DataInputStream dis) throws IOException {
         super.read(dis);
         this.yaw = dis.readByte();
         this.pitch = dis.readByte();
      }

      @Override
      public void write(DataOutputStream dos) throws IOException {
         super.write(dos);
         dos.writeByte(this.yaw);
         dos.writeByte(this.pitch);
      }

      @Override
      public int getEstimatedSize() {
         return 6;
      }
   }
}
