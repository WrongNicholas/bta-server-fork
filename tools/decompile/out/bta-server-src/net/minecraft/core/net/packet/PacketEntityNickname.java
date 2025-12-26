package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketEntityNickname extends Packet {
   public int entityId;
   public String nickname;
   public byte chatColor;

   public PacketEntityNickname() {
   }

   public PacketEntityNickname(int entityId, String nickname, byte chatColor) {
      this.entityId = entityId;
      this.nickname = nickname;
      this.chatColor = chatColor;
   }

   @Override
   public int getEstimatedSize() {
      return 4 + this.nickname.length() + 1;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.nickname = readStringUTF16BE(dis, 256);
      this.chatColor = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      writeStringUTF16BE(this.nickname, dos);
      dos.writeByte(this.chatColor);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleEntityNickname(this);
   }
}
