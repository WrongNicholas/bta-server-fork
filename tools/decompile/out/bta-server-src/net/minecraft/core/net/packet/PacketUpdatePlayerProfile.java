package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketUpdatePlayerProfile extends Packet {
   public String username;
   public String nickname;
   public UUID uuid;
   public int score;
   public byte chatColor;
   public boolean isOnline;
   public boolean isOperator;

   public PacketUpdatePlayerProfile() {
   }

   public PacketUpdatePlayerProfile(String username, String nickname, UUID uuid, int score, byte chatColor, boolean isOnline, boolean isOperator) {
      this.username = username;
      this.nickname = nickname;
      this.uuid = uuid;
      this.score = score;
      this.chatColor = chatColor;
      this.isOnline = isOnline;
      this.isOperator = isOperator;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.username = readStringUTF8(dis, 16);
      this.nickname = readStringUTF16BE(dis, 256);
      this.uuid = readUUID(dis);
      this.score = dis.readInt();
      this.chatColor = dis.readByte();
      this.isOnline = dis.readBoolean();
      this.isOperator = dis.readBoolean();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      writeStringUTF8(this.username, dos);
      writeStringUTF16BE(this.nickname, dos);
      writeUUID(this.uuid, dos);
      dos.writeInt(this.score);
      dos.writeByte(this.chatColor);
      dos.writeBoolean(this.isOnline);
      dos.writeBoolean(this.isOperator);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleUpdatePlayerProfile(this);
   }

   @Override
   public int getEstimatedSize() {
      return 10 + this.username.length() + 4;
   }
}
