package net.minecraft.core.net.packet;

import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.data.SynchedEntityData;
import org.jetbrains.annotations.Nullable;

public class PacketAddEntity extends Packet {
   public static final int MASK_OWNER = 1;
   public static final int MASK_META = 2;
   public static final int MASK_VELOCITY = 4;
   public static final int MASK_TAG = 8;
   public int entityId;
   public int xPosition;
   public int yPosition;
   public int zPosition;
   public float pitch;
   public float yaw;
   public boolean hasVelocity = false;
   public short xVelocity = 0;
   public short yVelocity = 0;
   public short zVelocity = 0;
   public short type = -1;
   public int ownerId = -1;
   public int metaData = -1;
   @Nullable
   public CompoundTag tag;
   private SynchedEntityData synchedData;
   @Nullable
   private List<SynchedEntityData.DataItem<?>> unpackedData;

   public PacketAddEntity() {
   }

   public PacketAddEntity(Entity entity) {
      this(entity, -1, -1, null, null, null, null);
   }

   public PacketAddEntity(Entity entity, int metaData) {
      this(entity, metaData, -1, null, null, null, null);
   }

   public PacketAddEntity(Entity entity, int metaData, int ownerId) {
      this(entity, metaData, ownerId, null, null, null, null);
   }

   public PacketAddEntity(Entity entity, int metaData, int ownerId, Double xVelocity, Double yVelocity, Double zVelocity) {
      this(entity, metaData, ownerId, xVelocity, yVelocity, zVelocity, null);
   }

   public PacketAddEntity(Entity entity, int metaData, int ownerId, Double xVelocity, Double yVelocity, Double zVelocity, @Nullable CompoundTag tag) {
      this.entityId = entity.id;
      this.xPosition = MathHelper.floor(entity.x * 32.0);
      this.yPosition = MathHelper.floor(entity.y * 32.0);
      this.zPosition = MathHelper.floor(entity.z * 32.0);
      this.pitch = entity.xRot;
      this.yaw = entity.yRot;
      this.metaData = metaData;
      this.ownerId = ownerId;
      if (xVelocity == null && yVelocity == null && zVelocity == null) {
         this.hasVelocity = false;
      } else {
         this.xVelocity = (short)((xVelocity == null ? 0.0 : xVelocity) * 8000.0);
         this.yVelocity = (short)((yVelocity == null ? 0.0 : yVelocity) * 8000.0);
         this.zVelocity = (short)((zVelocity == null ? 0.0 : zVelocity) * 8000.0);
         this.hasVelocity = true;
      }

      this.tag = tag;
      this.synchedData = entity.getEntityData();
   }

   public PacketAddEntity setType(int type) {
      this.type = (short)type;
      return this;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
      this.type = dis.readShort();
      this.xPosition = dis.readInt();
      this.yPosition = dis.readInt();
      this.zPosition = dis.readInt();
      this.pitch = dis.readFloat();
      this.yaw = dis.readFloat();
      this.unpackedData = SynchedEntityData.unpack(dis);
      byte optionals = dis.readByte();
      if (this.hasVelocity(optionals)) {
         this.xVelocity = dis.readShort();
         this.yVelocity = dis.readShort();
         this.zVelocity = dis.readShort();
         this.hasVelocity = true;
      }

      if (this.hasOwner(optionals)) {
         this.ownerId = dis.readInt();
      }

      if (this.hasMeta(optionals)) {
         this.metaData = dis.readInt();
      }

      if (this.hasTag(optionals)) {
         this.tag = readCompressedCompoundTag(dis);
      }
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
      dos.writeShort(this.type);
      dos.writeInt(this.xPosition);
      dos.writeInt(this.yPosition);
      dos.writeInt(this.zPosition);
      dos.writeFloat(this.pitch);
      dos.writeFloat(this.yaw);
      this.synchedData.packAll(dos);
      byte optionals = this.makeOptionalsByte(this.hasVelocity, this.ownerId >= 0, this.metaData >= 0, this.tag != null);
      dos.writeByte(optionals);
      if (this.hasVelocity(optionals)) {
         dos.writeShort(this.xVelocity);
         dos.writeShort(this.yVelocity);
         dos.writeShort(this.zVelocity);
      }

      if (this.hasOwner(optionals)) {
         dos.writeInt(this.ownerId);
      }

      if (this.hasMeta(optionals)) {
         dos.writeInt(this.metaData);
      }

      if (this.hasTag(optionals)) {
         writeCompressedCompoundTag(this.tag, dos);
      }
   }

   public boolean hasOwner(byte val) {
      return (val & 1) != 0;
   }

   public boolean hasMeta(byte val) {
      return (val & 2) != 0;
   }

   public boolean hasVelocity(byte val) {
      return (val & 4) != 0;
   }

   public boolean hasTag(byte val) {
      return (val & 8) != 0;
   }

   public byte makeOptionalsByte(boolean hasVelocity, boolean hasOwner, boolean hasMeta, boolean hasCompound) {
      byte val = 0;
      if (hasOwner) {
         val = (byte)(val | 1);
      }

      if (hasMeta) {
         val = (byte)(val | 2);
      }

      if (hasVelocity) {
         val = (byte)(val | 4);
      }

      if (hasCompound) {
         val = (byte)(val | 8);
      }

      return val;
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleVehicleSpawn(this);
   }

   @Override
   public int getEstimatedSize() {
      return 21 + this.ownerId <= 0 ? 0 : 6;
   }

   @Nullable
   public List<SynchedEntityData.DataItem<?>> getUnpackedData() {
      return this.unpackedData;
   }
}
