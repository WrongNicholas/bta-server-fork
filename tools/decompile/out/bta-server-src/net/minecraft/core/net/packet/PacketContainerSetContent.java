package net.minecraft.core.net.packet;

import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketContainerSetContent extends Packet {
   public int windowId;
   public ItemStack[] stackList;

   public PacketContainerSetContent() {
   }

   public PacketContainerSetContent(int i, List<ItemStack> list) {
      this.windowId = i;
      this.stackList = new ItemStack[list.size()];

      for (int j = 0; j < this.stackList.length; j++) {
         ItemStack itemstack = list.get(j);
         this.stackList[j] = itemstack != null ? itemstack.copy() : null;
      }
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.windowId = dis.readByte();
      short listSize = dis.readShort();
      this.stackList = new ItemStack[listSize];

      for (int i = 0; i < listSize; i++) {
         short itemId = dis.readShort();
         if (itemId >= 0) {
            byte size = dis.readByte();
            short meta = dis.readShort();
            CompoundTag tag = readCompressedCompoundTag(dis);
            ItemStack s = new ItemStack(itemId, size, meta, tag);
            this.stackList[i] = s;
         }
      }
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.windowId);
      dos.writeShort(this.stackList.length);

      for (int i = 0; i < this.stackList.length; i++) {
         if (this.stackList[i] == null) {
            dos.writeShort(-1);
         } else {
            dos.writeShort((short)this.stackList[i].itemID);
            dos.writeByte((byte)this.stackList[i].stackSize);
            dos.writeShort((short)this.stackList[i].getMetadata());
            writeCompressedCompoundTag(this.stackList[i].getData(), dos);
         }
      }
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleWindowItems(this);
   }

   @Override
   public int getEstimatedSize() {
      return 3 + this.stackList.length * 5;
   }
}
