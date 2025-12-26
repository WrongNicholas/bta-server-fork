package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketPlayerGamemode extends Packet {
   public int gamemodeId;
   public int entityId;

   public PacketPlayerGamemode() {
   }

   public PacketPlayerGamemode(int entityId, int gamemodeId) {
      this.entityId = entityId;
      this.gamemodeId = gamemodeId;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
      this.gamemodeId = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
      dos.writeByte(this.gamemodeId);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleEntityPlayerGamemode(this);
   }

   @Override
   public int getEstimatedSize() {
      return 5;
   }
}
