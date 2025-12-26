package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.world.chunk.ChunkPosition;

public class PacketExplosion extends Packet {
   public double explosionX;
   public double explosionY;
   public double explosionZ;
   public float explosionSize;
   public Set<ChunkPosition> destroyedBlockPositions;
   public boolean isCannonball;

   public PacketExplosion() {
   }

   public PacketExplosion(double d, double d1, double d2, float f, Set<ChunkPosition> set, boolean isCannonball) {
      this.explosionX = d;
      this.explosionY = d1;
      this.explosionZ = d2;
      this.explosionSize = f;
      this.destroyedBlockPositions = new HashSet<>(set);
      this.isCannonball = isCannonball;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.explosionX = dis.readDouble();
      this.explosionY = dis.readDouble();
      this.explosionZ = dis.readDouble();
      this.explosionSize = dis.readFloat();
      int i = dis.readInt();
      this.destroyedBlockPositions = new HashSet<>();
      int j = (int)this.explosionX;
      int k = (int)this.explosionY;
      int l = (int)this.explosionZ;

      for (int i1 = 0; i1 < i; i1++) {
         int j1 = dis.readByte() + j;
         int k1 = dis.readByte() + k;
         int l1 = dis.readByte() + l;
         this.destroyedBlockPositions.add(new ChunkPosition(j1, k1, l1));
      }

      this.isCannonball = dis.readBoolean();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeDouble(this.explosionX);
      dos.writeDouble(this.explosionY);
      dos.writeDouble(this.explosionZ);
      dos.writeFloat(this.explosionSize);
      dos.writeInt(this.destroyedBlockPositions.size());
      int i = (int)this.explosionX;
      int j = (int)this.explosionY;
      int k = (int)this.explosionZ;

      for (ChunkPosition chunkPosition : this.destroyedBlockPositions) {
         dos.writeByte(chunkPosition.x - i);
         dos.writeByte(chunkPosition.y - j);
         dos.writeByte(chunkPosition.z - k);
      }

      dos.writeBoolean(this.isCannonball);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleExplosion(this);
   }

   @Override
   public int getEstimatedSize() {
      return 32 + this.destroyedBlockPositions.size() * 3 + 1;
   }
}
