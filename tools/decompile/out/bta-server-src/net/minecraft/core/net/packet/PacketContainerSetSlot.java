package net.minecraft.core.net.packet;

import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketContainerSetSlot extends Packet {
   public int windowId;
   public int itemSlot;
   public ItemStack myItemStack;

   public PacketContainerSetSlot() {
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleSetSlot(this);
   }

   public PacketContainerSetSlot(int i, int j, ItemStack itemstack) {
      this.windowId = i;
      this.itemSlot = j;
      this.myItemStack = itemstack != null ? itemstack.copy() : itemstack;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.windowId = dis.readByte();
      this.itemSlot = dis.readShort();
      short word0 = dis.readShort();
      if (word0 >= 0) {
         byte byte0 = dis.readByte();
         short word1 = dis.readShort();
         CompoundTag tag = readCompressedCompoundTag(dis);
         this.myItemStack = new ItemStack(word0, byte0, word1, tag);
      } else {
         this.myItemStack = null;
      }
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.windowId);
      dos.writeShort(this.itemSlot);
      if (this.myItemStack == null) {
         dos.writeShort(-1);
      } else {
         dos.writeShort(this.myItemStack.itemID);
         dos.writeByte(this.myItemStack.stackSize);
         dos.writeShort(this.myItemStack.getMetadata());
         writeCompressedCompoundTag(this.myItemStack.getData(), dos);
      }
   }

   @Override
   public int getEstimatedSize() {
      return 8;
   }
}
