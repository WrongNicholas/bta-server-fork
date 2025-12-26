package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketPingHandshake extends Packet {
   public static long startTime = 0L;
   public int payload;
   public int identifier;
   public String pingHostString;
   public int protocolVersion;
   public String hostname;
   public int port;

   public PacketPingHandshake() {
   }

   public PacketPingHandshake(int payload, int identifier, String pingHostString, int protocolVersion, String hostname, int port) {
      this.payload = payload;
      this.identifier = identifier;
      this.pingHostString = pingHostString;
      this.protocolVersion = protocolVersion;
      this.hostname = hostname;
      this.port = port;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.payload = dis.readUnsignedByte();
      this.identifier = dis.readUnsignedByte();
      this.pingHostString = readStringUTF16BE(dis, 255);
      dis.readUnsignedShort();
      this.protocolVersion = dis.readUnsignedByte();
      this.hostname = readStringUTF16BE(dis, 255);
      this.port = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeByte(this.payload);
      dos.writeByte(this.identifier);
      writeStringUTF16BE(this.pingHostString, dos);
      dos.writeShort(3 + StandardCharsets.UTF_16BE.encode(this.pingHostString).array().length + 4);
      dos.writeByte(this.protocolVersion);
      writeStringUTF16BE(this.hostname, dos);
      dos.writeInt(this.port);
      startTime = System.currentTimeMillis();
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handlePingHandshake(this);
   }

   @Override
   public int getEstimatedSize() {
      return 0;
   }
}
