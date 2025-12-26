package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketContainerOpen extends Packet {
   public static final int TYPE_GENERIC_CONTAINER = 0;
   public static final int TYPE_CRAFTING = 1;
   public static final int TYPE_FURNACE = 2;
   public static final int TYPE_DISPENSER = 3;
   public static final int TYPE_BLAST_FURNACE = 4;
   public static final int TYPE_TROMMEL = 5;
   public static final int TYPE_ACTIVATOR = 6;
   public static final int TYPE_PAINTING = 7;
   public int windowId;
   public int inventoryType;
   public String windowTitle;
   public int slotsCount;

   public PacketContainerOpen() {
   }

   public PacketContainerOpen(int windowId, int inventoryType, String windowTitle, int slotCount) {
      assert windowId <= 127 : "Window Id > 127 won't serialize properly";

      assert inventoryType <= 127 : "Inventory Type > 127 won't serialize properly";

      assert slotCount <= 127 : "Slot Count > 127 won't serialize properly";

      this.windowId = windowId;
      this.inventoryType = inventoryType;
      this.windowTitle = windowTitle;
      this.slotsCount = slotCount;
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleOpenWindow(this);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.windowId = dis.readByte();
      this.inventoryType = dis.readByte();
      this.windowTitle = dis.readUTF();
      this.slotsCount = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.windowId);
      dos.writeByte(this.inventoryType);
      dos.writeUTF(this.windowTitle);
      dos.writeByte(this.slotsCount);
   }

   @Override
   public int getEstimatedSize() {
      return 3 + this.windowTitle.length();
   }
}
