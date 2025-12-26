package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketSyncIDs extends Packet {
   public static final int MAX_ID_STRING_SIZE = 128;
   public static final int DESTINATION_SOUND_IDS = 0;
   public static final int DESTINATION_ENTITY_IDS = 1;
   private int packetSize;
   public int destinationId = -1;
   public Map<Integer, String> mapping;

   public PacketSyncIDs() {
   }

   public PacketSyncIDs(int destinationId, Map<Integer, String> mapping) {
      this.destinationId = destinationId;
      this.mapping = mapping;
   }

   @Override
   public void read(DataInputStream in) throws IOException {
      int size = 0;
      size += this.readIds(in);
      this.packetSize = size;
   }

   @Override
   public void write(DataOutputStream out) throws IOException {
      int size = 0;
      size += this.writeIds(out);
      this.packetSize = size;
   }

   public int readIds(DataInputStream in) throws IOException {
      this.mapping = new HashMap<>();
      int bytesRead = 0;
      this.destinationId = in.readByte() & 255;
      int count = in.readInt();
      bytesRead += 4;

      for (int i = 0; i < count; i++) {
         int id = in.readShort() & '\uffff';
         String name = readStringUTF8(in, 128);
         this.mapping.put(id, name);
      }

      return bytesRead;
   }

   public int writeIds(DataOutputStream out) throws IOException {
      int bytesWritten = 0;
      out.writeByte(this.destinationId);
      out.writeInt(this.mapping.size());
      bytesWritten += 4;

      for (Entry<Integer, String> entry : this.mapping.entrySet()) {
         out.writeShort(entry.getKey());
         writeStringUTF8(entry.getValue(), out);
      }

      return bytesWritten;
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleIds(this);
   }

   @Override
   public int getEstimatedSize() {
      return this.packetSize + 1;
   }
}
