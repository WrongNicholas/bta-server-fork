package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.world.data.SynchedEntityData;
import org.jetbrains.annotations.Nullable;

public class PacketSetEntityData extends Packet {
   public int entityId;
   @Nullable
   private List<SynchedEntityData.DataItem<?>> packedItems;

   public PacketSetEntityData() {
   }

   public PacketSetEntityData(int i, SynchedEntityData datawatcher) {
      this.entityId = i;
      this.packedItems = datawatcher.packDirty();
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
      this.packedItems = SynchedEntityData.unpack(dis);
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
      SynchedEntityData.pack(this.packedItems, dos);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleEntityMetadata(this);
   }

   @Override
   public int getEstimatedSize() {
      return 5;
   }

   @Nullable
   public List<SynchedEntityData.DataItem<?>> getUnpackedData() {
      return this.packedItems;
   }
}
