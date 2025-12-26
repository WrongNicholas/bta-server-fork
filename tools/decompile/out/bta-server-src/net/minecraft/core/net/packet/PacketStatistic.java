package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.util.collection.NamespaceID;

public class PacketStatistic extends Packet {
   public String statID;
   public int valueChange;

   public PacketStatistic() {
   }

   public PacketStatistic(NamespaceID statID, int change) {
      this.statID = statID.toString();
      this.valueChange = change;
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleStatistic(this);
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.statID = readStringUTF8(dis, Integer.MAX_VALUE);
      this.valueChange = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      writeStringUTF8(this.statID, dos);
      dos.writeByte(this.valueChange);
   }

   @Override
   public int getEstimatedSize() {
      return 6;
   }
}
