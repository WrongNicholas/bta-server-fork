package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.world.World;

public class PacketBlockRegionUpdate extends Packet {
   public int xPosition;
   public int yPosition;
   public int zPosition;
   public int xSize;
   public int ySize;
   public int zSize;
   public byte[] chunk;
   private int chunkSize;

   public PacketBlockRegionUpdate() {
      this.isChunkDataPacket = true;
   }

   public PacketBlockRegionUpdate(int xPosition, int yPosition, int zPosition, int xSize, int ySize, int zSize, World world) {
      this.isChunkDataPacket = true;
      this.xPosition = xPosition;
      this.yPosition = yPosition;
      this.zPosition = zPosition;
      this.xSize = xSize;
      this.ySize = ySize;
      this.zSize = zSize;
      byte[] inflatedBuffer = world.getChunkData(xPosition, yPosition, zPosition, xSize, ySize, zSize);
      Deflater deflater = new Deflater(-1);

      try {
         deflater.setInput(inflatedBuffer);
         deflater.finish();
         this.chunk = new byte[xSize * ySize * zSize * 8];
         this.chunkSize = deflater.deflate(this.chunk);
      } finally {
         deflater.end();
      }
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.xPosition = dis.readInt();
      this.yPosition = dis.readShort();
      this.zPosition = dis.readInt();
      this.xSize = dis.read() + 1;
      this.ySize = dis.read() + 1;
      this.zSize = dis.read() + 1;
      this.chunkSize = dis.readInt();
      byte[] deflatedBuffer = new byte[this.chunkSize];
      dis.readFully(deflatedBuffer);
      Inflater inflater = new Inflater();

      try {
         inflater.setInput(deflatedBuffer);
         this.chunk = new byte[this.xSize * this.ySize * this.zSize * 8];
         inflater.inflate(this.chunk);
      } catch (DataFormatException var8) {
         throw new IOException("Bad compressed data format");
      } finally {
         inflater.end();
      }
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.xPosition);
      dos.writeShort(this.yPosition);
      dos.writeInt(this.zPosition);
      dos.write(this.xSize - 1);
      dos.write(this.ySize - 1);
      dos.write(this.zSize - 1);
      dos.writeInt(this.chunkSize);
      dos.write(this.chunk, 0, this.chunkSize);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleMapChunk(this);
   }

   @Override
   public int getEstimatedSize() {
      return 17 + this.chunkSize;
   }
}
