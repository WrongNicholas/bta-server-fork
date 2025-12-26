package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketPlaySoundEffect extends Packet {
   public int soundID;
   public int data;
   public int x;
   public int y;
   public int z;

   public PacketPlaySoundEffect() {
   }

   public PacketPlaySoundEffect(int soundId, int x, int y, int z, int data) {
      this.soundID = soundId;
      this.x = x;
      this.y = y;
      this.z = z;
      this.data = data;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.soundID = dis.readInt();
      this.x = dis.readInt();
      this.y = dis.readInt();
      this.z = dis.readInt();
      this.data = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.soundID);
      dos.writeInt(this.x);
      dos.writeInt(this.y);
      dos.writeInt(this.z);
      dos.writeInt(this.data);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handlePlaySoundEffect(this);
   }

   @Override
   public int getEstimatedSize() {
      return 20;
   }
}
