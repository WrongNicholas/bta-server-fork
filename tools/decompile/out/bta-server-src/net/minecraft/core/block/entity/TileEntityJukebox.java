package net.minecraft.core.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.save.conversion.ChunkConverter;

public class TileEntityJukebox extends TileEntity {
   public int record;

   @Override
   public void readFromNBT(CompoundTag nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.record = nbttagcompound.getInteger("Record");
      byte expanded = nbttagcompound.getByte("Expanded");
      if (expanded == 0 && this.record >= 256) {
         this.record = this.record + (Blocks.blocksList.length - 256);
      }

      if (this.record < Blocks.blocksList.length) {
         short[] id = new short[]{(short)this.record};
         byte[] meta = new byte[]{0};
         ChunkConverter.converters[0].convertBlocksAndMetadata(id, meta);
         this.record = id[0];
      }
   }

   @Override
   public void writeToNBT(CompoundTag nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      if (this.record > 0) {
         nbttagcompound.putInt("Record", this.record);
         nbttagcompound.putByte("Expanded", (byte)1);
      }
   }

   @Override
   public void dropContents(World world, int x, int y, int z) {
      if (this.record != 0) {
         float f = 0.7F;
         double d = world.rand.nextFloat() * f + (1.0F - f) * 0.5;
         double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.2 + 0.6;
         double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5;
         EntityItem item = new EntityItem(world, x + d, y + d1, z + d2, new ItemStack(this.record, 1, 0));
         item.pickupDelay = 10;
         world.entityJoinedWorld(item);
         world.playBlockEvent(1005, x, y, z, 0);
         world.playRecord(null, null, x, y, z);
         this.record = 0;
         this.setChanged();
         world.setBlockMetadataWithNotify(x, y, z, 0);
      }
   }
}
