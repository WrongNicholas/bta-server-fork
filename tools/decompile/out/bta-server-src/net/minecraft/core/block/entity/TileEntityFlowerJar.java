package net.minecraft.core.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicFlowerStackable;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.world.World;

public class TileEntityFlowerJar extends TileEntity {
   public int flowerInPot;
   public int flowerData;

   @Override
   public void readFromNBT(CompoundTag tag) {
      super.readFromNBT(tag);
      this.flowerInPot = tag.getInteger("PlantedId");
      this.flowerData = Byte.toUnsignedInt(tag.getByteOrDefault("PlantedData", (byte)0));
   }

   @Override
   public void writeToNBT(CompoundTag tag) {
      super.writeToNBT(tag);
      if (this.flowerInPot > 0) {
         tag.putInt("PlantedId", this.flowerInPot);
         tag.putByte("PlantedData", (byte)this.flowerData);
      }
   }

   @Override
   public void dropContents(World world, int x, int y, int z) {
      super.dropContents(world, x, y, z);
      if (this.flowerInPot > 0 && Blocks.hasTag(this.flowerInPot, BlockTags.PLANTABLE_IN_JAR)) {
         if (!world.isClientSide) {
            if (Block.hasLogicClass(Blocks.getBlock(this.flowerInPot), BlockLogicFlowerStackable.class)) {
               world.dropItem(x, y, z, new ItemStack(this.flowerInPot, BlockLogicFlowerStackable.getStackCount(this.flowerData) + 1, 0));
            } else {
               world.dropItem(x, y, z, new ItemStack(this.flowerInPot, 1, this.flowerData));
            }
         }

         this.flowerInPot = 0;
      }
   }

   @Override
   public Packet getDescriptionPacket() {
      return new PacketTileEntityData(this);
   }
}
