package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.world.World;

public class PacketBlockUpdate extends Packet {
   public int xPosition;
   public int yPosition;
   public int zPosition;
   public int blockId;
   public int metadata;

   public PacketBlockUpdate() {
      this.isChunkDataPacket = true;
   }

   public PacketBlockUpdate(int x, int y, int z, World world) {
      this.isChunkDataPacket = true;
      this.xPosition = x;
      this.yPosition = y;
      this.zPosition = z;
      this.blockId = world.getBlockId(x, y, z);
      this.metadata = world.getBlockMetadata(x, y, z);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.xPosition = dis.readInt();
      this.yPosition = dis.readShort();
      this.zPosition = dis.readInt();
      this.blockId = dis.readShort();
      this.metadata = dis.read();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.xPosition);
      dos.writeShort(this.yPosition);
      dos.writeInt(this.zPosition);
      dos.writeShort(this.blockId);
      dos.write(this.metadata);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleBlockUpdate(this);
   }

   @Override
   public int getEstimatedSize() {
      return 11;
   }
}
