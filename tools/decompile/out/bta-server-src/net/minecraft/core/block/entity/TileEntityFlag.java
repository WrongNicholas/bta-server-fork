package net.minecraft.core.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.UUIDHelper;
import org.jetbrains.annotations.Nullable;

public class TileEntityFlag extends TileEntity implements Container {
   public static final int CANVAS_WIDTH = 24;
   public static final int CANVAS_HEIGHT = 16;
   public boolean isDirty = true;
   public int colorHash = -1;
   public int activeDyes = 0;
   public final byte[] flagColors = new byte[384];
   public boolean flipped = true;
   public ItemStack[] items = new ItemStack[3];
   @Nullable
   public UUID owner;
   public int sway = new Random().nextInt(100);

   @Override
   public void tick() {
      this.sway++;
   }

   public byte getColor(int x, int y) {
      int xSample = x;
      int ySample = y;
      if (x < 0) {
         xSample = 0;
      }

      if (x >= 24) {
         xSample = 23;
      }

      if (y < 0) {
         ySample = 0;
      }

      if (y >= 16) {
         ySample = 15;
      }

      int colorIndex = this.flagColors[xSample + 24 * ySample] - 1;
      if (colorIndex < 0 || colorIndex >= this.items.length) {
         return 15;
      } else {
         return this.items[colorIndex] != null && this.items[colorIndex].itemID == Items.DYE.id ? (byte)(this.items[colorIndex].getMetadata() & 15) : 15;
      }
   }

   public void setFlipped(boolean flag) {
      this.flipped = flag;
   }

   public boolean getFlipped() {
      return this.flipped;
   }

   @Override
   public void writeToNBT(CompoundTag tag) {
      super.writeToNBT(tag);
      this.writeFlagNBT(tag);
   }

   public void writeFlagNBT(CompoundTag tag) {
      byte[] packedColors = this.packFlagColors(this.flagColors);
      tag.putByteArray("Colors", packedColors);
      UUIDHelper.writeToTag(tag, this.owner, "OwnerUUID");
      tag.putBoolean("Flip", this.flipped);
      ListTag list = new ListTag();

      for (int i = 0; i < this.items.length; i++) {
         if (this.items[i] != null) {
            CompoundTag compound = new CompoundTag();
            compound.putByte("Slot", (byte)i);
            this.items[i].writeToNBT(compound);
            list.addTag(compound);
         }
      }

      tag.putList("Items", list);
   }

   @Override
   public void readFromNBT(CompoundTag tag) {
      super.readFromNBT(tag);
      this.readFlagNBT(tag);
   }

   public void readFlagNBT(CompoundTag tag) {
      byte[] packedColors = tag.getByteArrayOrDefault("Colors", null);
      if (packedColors == null) {
         packedColors = new byte[96];
      }

      byte[] unpackedColors = this.unpackFlagColors(packedColors);
      System.arraycopy(unpackedColors, 0, this.flagColors, 0, 384);
      this.isDirty = true;
      this.colorHash = -1;

      for (int i = 0; i < this.flagColors.length; i++) {
         this.colorHash = this.colorHash ^ this.flagColors[i] << 8 * (i & 3);
      }

      UUID ownerUUID = UUIDHelper.readFromTag(tag, "OwnerUUID");
      if (ownerUUID == null) {
         String s = tag.getString("Owner");
         if (!s.isEmpty()) {
            UUIDHelper.runConversionAction(s, uuid -> this.owner = uuid, null);
         }
      } else {
         this.owner = ownerUUID;
      }

      this.flipped = tag.getBooleanOrDefault("Flip", false);
      ListTag list = tag.getList("Items");
      this.items = new ItemStack[this.getContainerSize()];

      for (int i = 0; i < list.tagCount(); i++) {
         CompoundTag compound = (CompoundTag)list.tagAt(i);
         int slot = compound.getByte("Slot");
         if (slot < this.items.length && slot > -1) {
            this.items[slot] = ItemStack.readItemStackFromNbt(compound);
         }
      }

      this.itemsChanges();
   }

   public void copyFlagNBT(CompoundTag tag) {
      byte[] packedColors = this.packFlagColors(this.flagColors);
      UUID copyOwner = UUIDHelper.readFromTag(tag, "OwnerUUID");
      if (copyOwner != null) {
         UUIDHelper.writeToTag(tag, this.owner, "OwnerUUID");
      }

      tag.putByteArray("Colors", packedColors);
      tag.putBoolean("Flip", this.flipped);
   }

   private byte[] packFlagColors(byte[] unpacked) {
      byte[] packed = new byte[96];

      for (int i = 0; i < 96; i++) {
         packed[i] = 0;
         packed[i] = (byte)(packed[i] | (unpacked[i * 4 + 0] & 3) << 0);
         packed[i] = (byte)(packed[i] | (unpacked[i * 4 + 1] & 3) << 2);
         packed[i] = (byte)(packed[i] | (unpacked[i * 4 + 2] & 3) << 4);
         packed[i] = (byte)(packed[i] | (unpacked[i * 4 + 3] & 3) << 6);
      }

      return packed;
   }

   private byte[] unpackFlagColors(byte[] packed) {
      byte[] unpacked = new byte[384];

      for (int i = 0; i < 96; i++) {
         unpacked[i * 4 + 0] = (byte)((packed[i] & 3) >> 0);
         unpacked[i * 4 + 1] = (byte)((packed[i] & 12) >> 2);
         unpacked[i * 4 + 2] = (byte)((packed[i] & 48) >> 4);
         unpacked[i * 4 + 3] = (byte)((packed[i] & 192) >> 6);
      }

      return unpacked;
   }

   @Override
   public int getContainerSize() {
      return 3;
   }

   @Nullable
   @Override
   public ItemStack getItem(int index) {
      index -= 36;
      return index >= 0 && index < 3 ? this.items[index] : null;
   }

   @Nullable
   @Override
   public ItemStack removeItem(int index, int takeAmount) {
      index -= 36;
      if (this.items[index] != null) {
         if (this.items[index].stackSize <= takeAmount) {
            ItemStack stack = this.items[index];
            this.items[index] = null;
            this.itemsChanges();
            this.setChanged();
            return stack;
         } else {
            ItemStack splitStack = this.items[index].splitStack(takeAmount);
            if (this.items[index].stackSize <= 0) {
               this.items[index] = null;
            }

            this.itemsChanges();
            this.setChanged();
            return splitStack;
         }
      } else {
         return null;
      }
   }

   @Override
   public Packet getDescriptionPacket() {
      return new PacketTileEntityData(this);
   }

   @Override
   public void setItem(int index, @Nullable ItemStack stack) {
      index -= 36;
      this.items[index] = stack;
      this.itemsChanges();
      this.setChanged();
   }

   private void itemsChanges() {
      this.activeDyes = 0;

      for (int i = 0; i < this.items.length; i++) {
         ItemStack s = this.items[i];
         if (s != null && s.getItem() == Items.DYE) {
            this.activeDyes = this.activeDyes | (s.getMetadata() + 1 & 31) << 5 * i;
         }
      }

      this.isDirty = true;
   }

   @Override
   public String getNameTranslationKey() {
      return "container.flag.name";
   }

   @Override
   public int getMaxStackSize() {
      return 1;
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return this.worldObj != null && this.worldObj.getTileEntity(this.x, this.y, this.z) == this
         ? entityplayer.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0
         : false;
   }

   @Override
   public void sortContainer() {
   }
}
