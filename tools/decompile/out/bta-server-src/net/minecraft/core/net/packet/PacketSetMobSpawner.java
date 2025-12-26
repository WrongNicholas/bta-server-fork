package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.net.handler.PacketHandler;
import org.jetbrains.annotations.Nullable;

public class PacketSetMobSpawner extends Packet {
   public int xPosition;
   public int yPosition;
   public int zPosition;
   @Nullable
   public Class<? extends Entity> entityClass;

   public PacketSetMobSpawner() {
      this.isChunkDataPacket = true;
   }

   public PacketSetMobSpawner(int x, int y, int z, @Nullable Class<? extends Entity> entityClass) {
      this.isChunkDataPacket = true;
      this.xPosition = x;
      this.yPosition = y;
      this.zPosition = z;
      this.entityClass = entityClass;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.xPosition = dis.readInt();
      this.yPosition = dis.readShort();
      this.zPosition = dis.readInt();
      int type = dis.readShort() & '\uffff';
      if (type == 65535) {
         this.entityClass = null;
      } else {
         this.entityClass = EntityDispatcher.classForNumericId(type);
      }
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.xPosition);
      dos.writeShort(this.yPosition);
      dos.writeInt(this.zPosition);
      dos.writeShort(this.entityClass == null ? '\uffff' : EntityDispatcher.numericIdForClass(this.entityClass));
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleSetMobSpawner(this);
   }

   @Override
   public int getEstimatedSize() {
      return 14;
   }
}
