package net.minecraft.core.net.packet;

import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.util.helper.MathHelper;

public class PacketAddItemEntity extends Packet {
   public int entityId;
   public int xPosition;
   public int yPosition;
   public int zPosition;
   public byte xd;
   public byte yd;
   public byte zd;
   public int itemID;
   public int count;
   public int itemDamage;
   public CompoundTag tag;

   public PacketAddItemEntity() {
   }

   public PacketAddItemEntity(EntityItem entityitem) {
      this.entityId = entityitem.id;
      this.itemID = entityitem.item.itemID;
      this.count = entityitem.item.stackSize;
      this.itemDamage = entityitem.item.getMetadata();
      this.tag = entityitem.item.getData();
      this.xPosition = MathHelper.floor(entityitem.x * 32.0);
      this.yPosition = MathHelper.floor(entityitem.y * 32.0);
      this.zPosition = MathHelper.floor(entityitem.z * 32.0);
      this.xd = (byte)(entityitem.xd * 128.0);
      this.yd = (byte)(entityitem.yd * 128.0);
      this.zd = (byte)(entityitem.zd * 128.0);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
      this.itemID = dis.readShort();
      this.count = dis.readByte();
      this.itemDamage = dis.readShort();
      this.tag = readCompressedCompoundTag(dis);
      this.xPosition = dis.readInt();
      this.yPosition = dis.readInt();
      this.zPosition = dis.readInt();
      this.xd = dis.readByte();
      this.yd = dis.readByte();
      this.zd = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
      dos.writeShort(this.itemID);
      dos.writeByte(this.count);
      dos.writeShort(this.itemDamage);
      writeCompressedCompoundTag(this.tag, dos);
      dos.writeInt(this.xPosition);
      dos.writeInt(this.yPosition);
      dos.writeInt(this.zPosition);
      dos.writeByte(this.xd);
      dos.writeByte(this.yd);
      dos.writeByte(this.zd);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handlePickupSpawn(this);
   }

   @Override
   public int getEstimatedSize() {
      return 24;
   }
}
