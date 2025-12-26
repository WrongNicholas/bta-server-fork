package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.util.helper.Side;

public class PacketPlayerAction extends Packet {
   public static final int ACTION_DIG_START = 0;
   public static final int ACTION_DIG_CONTINUED = 1;
   public static final int ACTION_DIG_COMPLETE = 2;
   public static final int ACTION_DROP_ITEM_SINGLE = 4;
   public static final int ACTION_DROP_ITEM_STACK = 5;
   public static final int ACTION_PICK_BLOCK = 6;
   public int xPosition;
   public int yPosition;
   public int zPosition;
   public Side side;
   public double xHit;
   public double yHit;
   public int action;

   public PacketPlayerAction() {
   }

   public PacketPlayerAction(int action, int x, int y, int z, Side side, double xHit, double yHit) {
      this.action = action;
      this.xPosition = x;
      this.yPosition = y;
      this.zPosition = z;
      this.side = side;
      this.xHit = xHit;
      this.yHit = yHit;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.action = dis.read();
      this.xPosition = dis.readInt();
      this.yPosition = dis.read();
      this.zPosition = dis.readInt();
      this.side = Side.getSideById(dis.read());
      this.xHit = dis.readDouble();
      this.yHit = dis.readDouble();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.write(this.action);
      dos.writeInt(this.xPosition);
      dos.write(this.yPosition);
      dos.writeInt(this.zPosition);
      dos.write(this.side.getId());
      dos.writeDouble(this.xHit);
      dos.writeDouble(this.yHit);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleBlockDig(this);
   }

   @Override
   public int getEstimatedSize() {
      return 19;
   }
}
