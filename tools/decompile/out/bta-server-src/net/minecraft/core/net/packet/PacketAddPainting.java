package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.entity.EntityPainting;
import net.minecraft.core.enums.ArtType;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketAddPainting extends Packet {
   public int entityId;
   public int xPosition;
   public int yPosition;
   public int zPosition;
   public int direction;
   public String key;
   public int itemID;
   public int meta;

   public PacketAddPainting() {
   }

   public PacketAddPainting(EntityPainting entitypainting) {
      this.entityId = entitypainting.id;
      this.xPosition = entitypainting.blockX;
      this.yPosition = entitypainting.blockY;
      this.zPosition = entitypainting.blockZ;
      this.direction = entitypainting.direction;
      this.key = entitypainting.art.key;
      this.itemID = -1;
      this.meta = -1;
      ItemStack stack = entitypainting.getBorderStack();
      if (stack != null) {
         this.itemID = stack.itemID;
         this.meta = stack.getMetadata();
      }
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.entityId = dis.readInt();
      this.key = readStringUTF8(dis, ArtType.getMaxArtKeyLength());
      this.xPosition = dis.readInt();
      this.yPosition = dis.readInt();
      this.zPosition = dis.readInt();
      this.direction = dis.readInt();
      this.itemID = dis.readInt();
      this.meta = dis.readInt();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(this.entityId);
      writeStringUTF8(this.key, dos);
      dos.writeInt(this.xPosition);
      dos.writeInt(this.yPosition);
      dos.writeInt(this.zPosition);
      dos.writeInt(this.direction);
      dos.writeInt(this.itemID);
      dos.writeInt(this.meta);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleEntityPainting(this);
   }

   @Override
   public int getEstimatedSize() {
      return 24;
   }
}
