package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.util.helper.Direction;

public class PacketUseOrPlaceItemStack extends Packet {
   public static final byte TYPE_USE_OR_PLACE_ON_TILE = 0;
   public static final byte TYPE_USE_ON_NOTHING = 1;
   public static final byte TYPE_PLACE_ONLY = 2;
   @Deprecated
   public static final byte TYPE_USE_ITEM_ON = 0;
   @Deprecated
   public static final byte TYPE_USE_ITEM = 1;
   @Deprecated
   public static final byte TYPE_USE_STACK_ON = 2;
   public int xPosition;
   public int yPosition;
   public int zPosition;
   public Direction direction;
   public double xPlaced;
   public double yPlaced;
   public ItemStack itemStack;
   public byte type;

   public PacketUseOrPlaceItemStack() {
   }

   public PacketUseOrPlaceItemStack(int x, int y, int z, Direction direction, ItemStack itemstack, double xPlaced, double yPlaced, byte type) {
      this.xPosition = x;
      this.yPosition = y;
      this.zPosition = z;
      this.direction = direction;
      this.itemStack = itemstack;
      this.xPlaced = xPlaced;
      this.yPlaced = yPlaced;
      this.type = type;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.xPosition = dis.readInt();
      this.yPosition = dis.read();
      this.zPosition = dis.readInt();
      this.direction = Direction.getDirectionById(dis.read());
      this.xPlaced = dis.readDouble();
      this.yPlaced = dis.readDouble();
      this.type = dis.readByte();
      short word0 = dis.readShort();
      if (word0 >= 0) {
         byte byte0 = dis.readByte();
         short word1 = dis.readShort();
         this.itemStack = new ItemStack(word0, byte0, word1);
      } else {
         this.itemStack = null;
      }
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.xPosition);
      dos.write(this.yPosition);
      dos.writeInt(this.zPosition);
      dos.write(this.direction.getId());
      dos.writeDouble(this.xPlaced);
      dos.writeDouble(this.yPlaced);
      dos.writeByte(this.type);
      if (this.itemStack == null) {
         dos.writeShort(-1);
      } else {
         dos.writeShort(this.itemStack.itemID);
         dos.writeByte(this.itemStack.stackSize);
         dos.writeShort(this.itemStack.getMetadata());
      }
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handlePlace(this);
   }

   @Override
   public int getEstimatedSize() {
      return 20;
   }
}
