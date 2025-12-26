package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.data.SynchedEntityData;
import org.jetbrains.annotations.Nullable;

public class PacketAddMob extends Packet {
   public int id;
   public short type;
   public int x;
   public int y;
   public int z;
   public byte yaw;
   public byte pitch;
   private SynchedEntityData metaData;
   @Nullable
   private List<SynchedEntityData.DataItem<?>> unpackedData;
   public String nickname;
   public byte chatColor;

   public PacketAddMob() {
   }

   public PacketAddMob(Mob mob) {
      this.id = mob.id;
      this.type = (short)EntityDispatcher.numericIdForClass((Class<? extends Entity>)mob.getClass());
      this.x = MathHelper.floor(mob.x * 32.0);
      this.y = MathHelper.floor(mob.y * 32.0);
      this.z = MathHelper.floor(mob.z * 32.0);
      this.yaw = (byte)(mob.yRot * 256.0F / 360.0F);
      this.pitch = (byte)(mob.xRot * 256.0F / 360.0F);
      this.metaData = mob.getEntityData();
      this.nickname = mob.nickname;
      this.chatColor = mob.chatColor;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.id = dis.readInt();
      this.type = dis.readShort();
      this.x = dis.readInt();
      this.y = dis.readInt();
      this.z = dis.readInt();
      this.yaw = dis.readByte();
      this.pitch = dis.readByte();
      this.unpackedData = SynchedEntityData.unpack(dis);
      this.nickname = readStringUTF16BE(dis, 256);
      this.chatColor = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.id);
      dos.writeShort(this.type);
      dos.writeInt(this.x);
      dos.writeInt(this.y);
      dos.writeInt(this.z);
      dos.writeByte(this.yaw);
      dos.writeByte(this.pitch);
      this.metaData.packAll(dos);
      writeStringUTF16BE(this.nickname, dos);
      dos.writeByte(this.chatColor);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleMobSpawn(this);
   }

   @Override
   public int getEstimatedSize() {
      return 21;
   }

   @Nullable
   public List<SynchedEntityData.DataItem<?>> getUnpackedData() {
      return this.unpackedData;
   }
}
