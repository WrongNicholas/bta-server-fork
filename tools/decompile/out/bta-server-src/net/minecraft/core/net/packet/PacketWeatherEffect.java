package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLightning;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.util.helper.MathHelper;

public class PacketWeatherEffect extends Packet {
   public static final int EFFECT_LIGHTNING = 1;
   public int id;
   public int x;
   public int y;
   public int z;
   public int effectId;

   public PacketWeatherEffect() {
   }

   public PacketWeatherEffect(Entity entity) {
      this.id = entity.id;
      this.x = MathHelper.floor(entity.x * 32.0);
      this.y = MathHelper.floor(entity.y * 32.0);
      this.z = MathHelper.floor(entity.z * 32.0);
      if (entity instanceof EntityLightning) {
         this.effectId = 1;
      }
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.id = dis.readInt();
      this.effectId = dis.readByte();
      this.x = dis.readInt();
      this.y = dis.readInt();
      this.z = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.id);
      dos.writeByte(this.effectId);
      dos.writeInt(this.x);
      dos.writeInt(this.y);
      dos.writeInt(this.z);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleWeather(this);
   }

   @Override
   public int getEstimatedSize() {
      return 17;
   }
}
