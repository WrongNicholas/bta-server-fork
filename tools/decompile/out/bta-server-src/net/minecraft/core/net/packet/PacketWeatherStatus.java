package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketWeatherStatus extends Packet {
   public int dimId;
   public int id;
   public int newId;
   public long duration;
   public float intensity;
   public float power;

   public PacketWeatherStatus() {
   }

   public PacketWeatherStatus(int dimId, int id, int newId, long duration, float intensity, float power) {
      this.dimId = dimId;
      this.id = id;
      this.newId = newId;
      this.duration = duration;
      this.intensity = intensity;
      this.power = power;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.dimId = dis.readInt();
      this.id = dis.readInt();
      this.newId = dis.readInt();
      this.duration = dis.readLong();
      this.intensity = dis.readFloat();
      this.power = dis.readFloat();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.dimId);
      dos.writeInt(this.id);
      dos.writeInt(this.newId);
      dos.writeLong(this.duration);
      dos.writeFloat(this.intensity);
      dos.writeFloat(this.power);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleWeatherStatus(this);
   }

   @Override
   public int getEstimatedSize() {
      return 28;
   }
}
