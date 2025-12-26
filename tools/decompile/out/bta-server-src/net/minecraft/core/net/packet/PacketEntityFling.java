package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketEntityFling extends Packet {
   public int entityId;
   public double xd;
   public double yd;
   public double zd;
   public float pushTime;
   public int pushesTick;

   public PacketEntityFling() {
   }

   public PacketEntityFling(int entityId, double xd, double yd, double zd, float pushTime, int pushesTick) {
      this.entityId = entityId;
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.pushTime = pushTime;
      this.pushesTick = pushesTick;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
      this.xd = dis.readFloat();
      this.yd = dis.readFloat();
      this.zd = dis.readFloat();
      this.pushTime = dis.readFloat();
      this.pushesTick = Byte.toUnsignedInt(dis.readByte());
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
      dos.writeFloat((float)this.xd);
      dos.writeFloat((float)this.yd);
      dos.writeFloat((float)this.zd);
      dos.writeFloat(this.pushTime);
      dos.writeByte((byte)this.pushesTick);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleEntityFling(this);
   }

   @Override
   public int getEstimatedSize() {
      return 21;
   }
}
