package net.minecraft.core.world.saveddata.maps;

import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MapWaypoint {
   public byte mapCoordX;
   public byte mapCoordZ;
   public int xPos;
   public int yPos;
   public int zPos;
   public byte[] colors = new byte[9];

   public MapWaypoint(byte mapCoordX, byte mapCoordZ, int x, int y, int z, byte[] colors) {
      this.mapCoordX = mapCoordX;
      this.mapCoordZ = mapCoordZ;
      this.xPos = x;
      this.yPos = y;
      this.zPos = z;

      for (int i = 0; i < this.colors.length; i++) {
         if (i < colors.length) {
            this.colors[i] = colors[i];
         } else {
            this.colors[i] = 0;
         }
      }
   }

   public MapWaypoint(CompoundTag nbt) {
      this.readFromNBT(nbt);
   }

   public MapWaypoint(DataInputStream dis) throws IOException {
      this.readInputStream(dis);
   }

   public CompoundTag writeToNBT(CompoundTag tag) {
      tag.putByte("MapCoordX", this.mapCoordX);
      tag.putByte("MapCoordZ", this.mapCoordZ);
      tag.putInt("xPos", this.xPos);
      tag.putInt("yPos", this.yPos);
      tag.putInt("zPos", this.zPos);
      tag.putByteArray("Colors", this.colors);
      return tag;
   }

   public void readFromNBT(CompoundTag tag) {
      this.mapCoordX = tag.getByte("MapCoordX");
      this.mapCoordZ = tag.getByte("MapCoordZ");
      this.xPos = tag.getInteger("xPos");
      this.yPos = tag.getInteger("yPos");
      this.zPos = tag.getInteger("zPos");
      this.colors = tag.getByteArray("Colors");
   }

   public void readInputStream(DataInputStream dis) throws IOException {
      this.mapCoordX = dis.readByte();
      this.mapCoordZ = dis.readByte();
      this.xPos = dis.readInt();
      this.yPos = dis.readInt();
      this.zPos = dis.readInt();
      this.colors = new byte[9];

      for (int i = 0; i < this.colors.length; i++) {
         this.colors[i] = dis.readByte();
      }
   }

   public void writeToOutputStream(DataOutputStream dos) throws IOException {
      dos.writeByte(this.mapCoordX);
      dos.writeByte(this.mapCoordZ);
      dos.writeInt(this.xPos);
      dos.writeInt(this.yPos);
      dos.writeInt(this.zPos);

      for (int i = 0; i < this.colors.length; i++) {
         dos.writeByte(this.colors[i]);
      }
   }
}
