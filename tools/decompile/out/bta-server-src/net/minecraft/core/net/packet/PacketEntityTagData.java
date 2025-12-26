package net.minecraft.core.net.packet;

import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketEntityTagData extends Packet {
   public int entityId;
   public CompoundTag tag;

   public PacketEntityTagData() {
   }

   public PacketEntityTagData(Entity entity) {
      this.entityId = entity.id;
      this.tag = new CompoundTag();
      entity.addAdditionalSaveData(this.tag);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
      this.tag = Packet.readCompressedCompoundTag(dis);
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
      Packet.writeCompressedCompoundTag(this.tag, dos);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleEntityTagData(this);
   }

   @Override
   public int getEstimatedSize() {
      return 0;
   }
}
