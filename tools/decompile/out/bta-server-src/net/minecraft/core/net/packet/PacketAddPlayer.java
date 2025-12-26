package net.minecraft.core.net.packet;

import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.Nullable;

public class PacketAddPlayer extends Packet {
   public int entityId;
   public String name;
   public UUID uuid;
   public int xPosition;
   public int yPosition;
   public int zPosition;
   public byte rotation;
   public byte pitch;
   public int currentItem;
   @Nullable
   public CompoundTag heldObjectTag;
   public String nickname;
   public byte chatColor;
   public int gamemode;

   public PacketAddPlayer() {
   }

   public PacketAddPlayer(Player entityplayer) {
      this.entityId = entityplayer.id;
      this.name = entityplayer.username;
      this.uuid = entityplayer.uuid;
      this.xPosition = MathHelper.floor(entityplayer.x * 32.0);
      this.yPosition = MathHelper.floor(entityplayer.y * 32.0);
      this.zPosition = MathHelper.floor(entityplayer.z * 32.0);
      this.rotation = (byte)(entityplayer.yRot * 256.0F / 360.0F);
      this.pitch = (byte)(entityplayer.xRot * 256.0F / 360.0F);
      ItemStack itemstack = entityplayer.inventory.getCurrentItem();
      this.currentItem = itemstack != null ? itemstack.itemID : 0;
      this.nickname = entityplayer.getDisplayName();
      this.chatColor = entityplayer.chatColor;
      if (entityplayer.getHeldObject() == null) {
         this.heldObjectTag = null;
      } else {
         CompoundTag tag = new CompoundTag();
         entityplayer.getHeldObject().writeToNBT(tag);
         this.heldObjectTag = tag;
      }

      this.gamemode = entityplayer.gamemode.getId();
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
      this.name = readStringUTF8(dis, 16);
      this.uuid = readUUID(dis);
      this.xPosition = dis.readInt();
      this.yPosition = dis.readInt();
      this.zPosition = dis.readInt();
      this.rotation = dis.readByte();
      this.pitch = dis.readByte();
      this.currentItem = dis.readShort();
      this.nickname = readStringUTF16BE(dis, 256);
      this.chatColor = dis.readByte();
      this.gamemode = Byte.toUnsignedInt(dis.readByte());
      if (dis.readByte() == 1) {
         this.heldObjectTag = readCompressedCompoundTag(dis);
      } else {
         this.heldObjectTag = null;
      }
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
      writeStringUTF8(this.name, dos);
      writeUUID(this.uuid, dos);
      dos.writeInt(this.xPosition);
      dos.writeInt(this.yPosition);
      dos.writeInt(this.zPosition);
      dos.writeByte(this.rotation);
      dos.writeByte(this.pitch);
      dos.writeShort(this.currentItem);
      writeStringUTF16BE(this.nickname, dos);
      dos.writeByte(this.chatColor);
      dos.writeByte(this.gamemode & 0xFF);
      if (this.heldObjectTag != null) {
         dos.writeByte(1);
         writeCompressedCompoundTag(this.heldObjectTag, dos);
      } else {
         dos.writeByte(0);
      }
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleNamedEntitySpawn(this);
   }

   @Override
   public int getEstimatedSize() {
      return 29;
   }
}
