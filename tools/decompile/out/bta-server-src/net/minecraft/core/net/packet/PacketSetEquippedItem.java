package net.minecraft.core.net.packet;

import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSetEquippedItem extends Packet {
   public int entityID;
   public int slot;
   public int itemID;
   public int itemMeta;
   public CompoundTag itemData;

   public PacketSetEquippedItem() {
   }

   public PacketSetEquippedItem(int entityID, int slot, ItemStack itemstack) {
      this.entityID = entityID;
      this.slot = slot;
      if (itemstack == null) {
         this.itemID = -1;
         this.itemMeta = 0;
         this.itemData = new CompoundTag();
      } else {
         this.itemID = itemstack.itemID;
         this.itemMeta = itemstack.getMetadata();
         this.itemData = itemstack.getData();
      }
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityID = dis.readInt();
      this.slot = dis.readShort();
      this.itemID = dis.readShort();
      this.itemMeta = dis.readShort();
      this.itemData = readCompressedCompoundTag(dis);
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityID);
      dos.writeShort(this.slot);
      dos.writeShort(this.itemID);
      dos.writeShort(this.itemMeta);
      writeCompressedCompoundTag(this.itemData, dos);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handlePlayerInventory(this);
   }

   @Override
   public int getEstimatedSize() {
      return 8;
   }
}
