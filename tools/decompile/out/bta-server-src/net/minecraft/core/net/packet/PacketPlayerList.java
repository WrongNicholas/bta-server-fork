package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketPlayerList extends Packet {
   public static final int MAX_SCORE_STRING_SIZE = 256;
   public String[] players;
   public String[] scores;
   public int count;

   public PacketPlayerList() {
   }

   public PacketPlayerList(int playerCount, String[] players, String[] scores) {
      this.count = playerCount;
      this.players = players;
      this.scores = scores;
   }

   @Override
   public void read(DataInputStream in) throws IOException {
      this.count = in.readInt();
      this.players = new String[this.count];
      this.scores = new String[this.count];

      for (int i = 0; i < this.count; i++) {
         this.players[i] = readStringUTF16BE(in, 256);
         this.scores[i] = readStringUTF16BE(in, 256);
      }
   }

   @Override
   public void write(DataOutputStream out) throws IOException {
      out.writeInt(this.count);

      for (int i = 0; i < this.count; i++) {
         writeStringUTF16BE(this.players[i], out);
         writeStringUTF16BE(this.scores[i], out);
      }
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handlePlayerList(this);
   }

   @Override
   public int getEstimatedSize() {
      int size = 4;

      for (int i = 0; i < this.scores.length; i++) {
         size += this.scores[i].length();
      }

      for (int i = 0; i < this.players.length; i++) {
         size += this.players[i].length();
      }

      return size;
   }
}
