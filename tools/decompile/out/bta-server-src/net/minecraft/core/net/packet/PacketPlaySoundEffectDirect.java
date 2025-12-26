package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.sound.SoundCategory;

public class PacketPlaySoundEffectDirect extends Packet {
   public int soundId;
   public SoundCategory soundType;
   public double x;
   public double y;
   public double z;
   public float volume;
   public float pitch;

   public PacketPlaySoundEffectDirect() {
   }

   public PacketPlaySoundEffectDirect(int soundId, SoundCategory soundType, double x, double y, double z, float volume, float pitch) {
      this.soundId = soundId;
      this.soundType = soundType;
      this.x = x;
      this.y = y;
      this.z = z;
      this.volume = volume;
      this.pitch = pitch;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.soundId = dis.readShort() & '\uffff';
      this.soundType = SoundCategory.values()[dis.readByte()];
      this.x = dis.readFloat();
      this.y = dis.readFloat();
      this.z = dis.readFloat();
      this.volume = dis.readFloat();
      this.pitch = dis.readFloat();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeShort(this.soundId);
      dos.writeByte(this.soundType.ordinal());
      dos.writeFloat((float)this.x);
      dos.writeFloat((float)this.y);
      dos.writeFloat((float)this.z);
      dos.writeFloat(this.volume);
      dos.writeFloat(this.pitch);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handlePlaySoundDirectly(this);
   }

   @Override
   public int getEstimatedSize() {
      return 23;
   }
}
