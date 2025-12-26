package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSignUpdate extends Packet {
   public int xPosition;
   public int yPosition;
   public int zPosition;
   public String[] signLines;
   public int picture;
   public int color;

   public PacketSignUpdate() {
      this.isChunkDataPacket = true;
   }

   public PacketSignUpdate(int x, int y, int z, String[] lines, int picture, int color) {
      this.isChunkDataPacket = true;
      this.xPosition = x;
      this.yPosition = y;
      this.zPosition = z;
      this.signLines = lines;
      this.picture = picture;
      this.color = color;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.xPosition = dis.readInt();
      this.yPosition = dis.readShort();
      this.zPosition = dis.readInt();
      this.signLines = new String[4];

      for (int i = 0; i < 4; i++) {
         this.signLines[i] = readStringUTF16BE(dis, 15);
      }

      this.picture = dis.readInt();
      this.color = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.xPosition);
      dos.writeShort(this.yPosition);
      dos.writeInt(this.zPosition);

      for (int i = 0; i < 4; i++) {
         writeStringUTF16BE(this.signLines[i], dos);
      }

      dos.writeInt(this.picture);
      dos.writeInt(this.color);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleUpdateSign(this);
   }

   @Override
   public int getEstimatedSize() {
      int i = 0;

      for (int j = 0; j < 4; j++) {
         i += this.signLines[j].length();
      }

      i += 4;
      return i + 4;
   }
}
