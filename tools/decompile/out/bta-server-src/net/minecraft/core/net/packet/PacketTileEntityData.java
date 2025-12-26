package net.minecraft.core.net.packet;

import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketTileEntityData extends Packet {
   public CompoundTag tag;

   public PacketTileEntityData() {
      this.isChunkDataPacket = true;
   }

   public PacketTileEntityData(TileEntity tileEntity) {
      this();
      this.tag = new CompoundTag();
      tileEntity.writeToNBT(this.tag);
   }

   public PacketTileEntityData(CompoundTag tag) {
      this.tag = tag;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.tag = readCompressedCompoundTag(dis);
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      writeCompressedCompoundTag(this.tag, dos);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleTileEntityData(this);
   }

   @Override
   public int getEstimatedSize() {
      return 0;
   }
}
