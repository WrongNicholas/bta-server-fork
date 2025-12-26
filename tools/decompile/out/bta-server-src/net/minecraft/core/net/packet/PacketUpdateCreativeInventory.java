package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketUpdateCreativeInventory extends Packet {
   public int windowId;
   public int page;
   public String searchText;

   public PacketUpdateCreativeInventory() {
   }

   public PacketUpdateCreativeInventory(int windowId, int page, String searchText) {
      this.windowId = windowId;
      this.page = page;
      this.searchText = searchText;
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleUpdateCreativeInventory(this);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.windowId = dis.readByte();
      this.page = dis.readInt();
      this.searchText = dis.readUTF();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.windowId);
      dos.writeInt(this.page);
      dos.writeUTF(this.searchText);
   }

   @Override
   public int getEstimatedSize() {
      return 8 + this.searchText.length() * 2;
   }
}
