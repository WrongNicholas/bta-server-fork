package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketContainerClick extends Packet {
   public int window_Id;
   public InventoryAction action;
   public int[] args;
   public short actionId;
   public ItemStack itemStack;

   public PacketContainerClick() {
   }

   public PacketContainerClick(int windowId, InventoryAction action, int[] args, ItemStack itemstack, short actionId) {
      this.window_Id = windowId;
      this.action = action;
      this.args = args;
      this.itemStack = itemstack;
      this.actionId = actionId;
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleWindowClick(this);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.window_Id = dis.readByte();
      this.action = InventoryAction.get(dis.readByte());
      int size = dis.readByte();
      this.args = new int[size];

      for (int i = 0; i < size; i++) {
         this.args[i] = dis.read();
      }

      this.actionId = dis.readShort();
      short word0 = dis.readShort();
      if (word0 >= 0) {
         byte byte0 = dis.readByte();
         short word1 = dis.readShort();
         this.itemStack = new ItemStack(word0, byte0, word1);
      } else {
         this.itemStack = null;
      }
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.window_Id);
      dos.writeByte(this.action.getId());
      if (this.args != null) {
         if (this.args.length > 255) {
            System.err.println("Too many args!");
            Thread.dumpStack();
            dos.write(0);
            return;
         }

         dos.write(this.args.length);

         for (int i = 0; i < this.args.length; i++) {
            dos.write(this.args[i]);
         }
      } else {
         dos.write(0);
      }

      dos.writeShort(this.actionId);
      if (this.itemStack == null) {
         dos.writeShort(-1);
      } else {
         dos.writeShort(this.itemStack.itemID);
         dos.writeByte(this.itemStack.stackSize);
         dos.writeShort(this.itemStack.getMetadata());
      }
   }

   @Override
   public int getEstimatedSize() {
      return 7 + (this.args != null ? this.args.length : 0);
   }
}
