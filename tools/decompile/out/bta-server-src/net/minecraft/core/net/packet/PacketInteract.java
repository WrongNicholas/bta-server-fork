package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketInteract extends Packet {
   public static final int ACTION_INTERACT = 0;
   public static final int ACTION_ATTACK = 1;
   public int sourceEntityID;
   public int targetEntityID;
   public int action;

   public PacketInteract() {
   }

   public PacketInteract(int source, int target, int action) {
      this.sourceEntityID = source;
      this.targetEntityID = target;
      this.action = action;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.sourceEntityID = dis.readInt();
      this.targetEntityID = dis.readInt();
      this.action = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.sourceEntityID);
      dos.writeInt(this.targetEntityID);
      dos.writeByte(this.action);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleUseEntity(this);
   }

   @Override
   public int getEstimatedSize() {
      return 9;
   }
}
