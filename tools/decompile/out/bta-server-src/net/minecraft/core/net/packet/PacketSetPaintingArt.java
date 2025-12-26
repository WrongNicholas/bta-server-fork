package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.enums.ArtType;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSetPaintingArt extends Packet {
   public int motive;

   public PacketSetPaintingArt() {
   }

   public PacketSetPaintingArt(ArtType art) {
      this.motive = ArtType.values.indexOf(art);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.motive = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.motive);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleSetPaintingMotive(this);
   }

   @Override
   public int getEstimatedSize() {
      return 0;
   }
}
