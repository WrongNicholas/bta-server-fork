package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketLogin extends Packet {
   public int playerEntityIdAndProtocolVersion;
   public String username;
   public UUID uuid;
   public long worldSeed;
   public byte dimensionId;
   public byte worldTypeId;
   public int packetDelay;
   public String publicKey;

   public PacketLogin() {
   }

   public PacketLogin(
      String username, UUID uuid, int playerEntityIdOrProtocolVersion, long worldSeed, byte dimensionId, byte worldTypeId, int packetDelay, String publicKey
   ) {
      this.username = username;
      this.uuid = uuid;
      this.playerEntityIdAndProtocolVersion = playerEntityIdOrProtocolVersion;
      this.worldSeed = worldSeed;
      this.dimensionId = dimensionId;
      this.worldTypeId = worldTypeId;
      this.packetDelay = packetDelay;
      this.publicKey = publicKey;
   }

   public PacketLogin(String username, UUID uuid, int i, String publicKey) {
      this.username = username;
      this.uuid = uuid;
      this.playerEntityIdAndProtocolVersion = i;
      this.publicKey = publicKey;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.playerEntityIdAndProtocolVersion = dis.readInt();
      this.username = readStringUTF8(dis, 16);
      this.uuid = readUUID(dis);
      this.publicKey = readStringUTF8(dis, 392);
      this.worldSeed = dis.readLong();
      this.dimensionId = dis.readByte();
      this.worldTypeId = dis.readByte();
      this.packetDelay = dis.readByte();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.playerEntityIdAndProtocolVersion);
      writeStringUTF8(this.username, dos);
      writeUUID(this.uuid, dos);
      writeStringUTF8(this.publicKey, dos);
      dos.writeLong(this.worldSeed);
      dos.writeByte(this.dimensionId);
      dos.writeByte(this.worldTypeId);
      dos.writeByte(this.packetDelay);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleLogin(this);
   }

   @Override
   public int getEstimatedSize() {
      return 4 + this.username.length() + 8 + 1 + 1 + 1;
   }
}
