package net.minecraft.core.net.packet;

import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.world.ICarriable;
import org.jetbrains.annotations.Nullable;

public class PacketSetHeldObject extends Packet {
   public int entityID;
   @Nullable
   public CompoundTag objectTag;

   public PacketSetHeldObject() {
   }

   public PacketSetHeldObject(int entityID, @Nullable ICarriable object) {
      this.entityID = entityID;
      if (object != null) {
         CompoundTag tag = new CompoundTag();
         object.writeToNBT(tag);
         this.objectTag = tag;
      }
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityID = dis.readInt();
      if (dis.readByte() == 1) {
         this.objectTag = readCompressedCompoundTag(dis);
      } else {
         this.objectTag = null;
      }
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityID);
      if (this.objectTag != null) {
         dos.writeByte(1);
         writeCompressedCompoundTag(this.objectTag, dos);
      } else {
         dos.writeByte(0);
      }
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handlePlayerHeldObject(this);
   }

   @Override
   public int getEstimatedSize() {
      return 0;
   }
}
