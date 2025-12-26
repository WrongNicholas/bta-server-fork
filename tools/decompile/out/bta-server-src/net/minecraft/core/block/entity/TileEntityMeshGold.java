package net.minecraft.core.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class TileEntityMeshGold extends TileEntity {
   @Nullable
   public ItemStack filterItem;
   public int ticksRan = 0;

   @Override
   public void tick() {
      if (this.ticksRan == 0) {
         this.ticksRan = this.worldObj.rand.nextInt(360);
      }

      this.ticksRan++;
   }

   public boolean setFilterItem(Player player, @Nullable ItemStack stack) {
      boolean success = false;
      boolean wasNull = this.filterItem == null;
      if (this.filterItem != null && player.getGamemode().consumeBlocks()) {
         player.inventory.insertItem(this.filterItem, true);
         if (this.filterItem.stackSize > 0) {
            player.dropPlayerItem(this.filterItem);
         }

         success = true;
      }

      if (stack != null) {
         stack.consumeItem(player);
         this.filterItem = new ItemStack(stack.getItem(), 1, stack.getMetadata(), stack.getData());
         this.setChanged();
         return true;
      } else {
         this.filterItem = null;
         this.setChanged();
         return success | !wasNull;
      }
   }

   @Override
   public void readFromNBT(CompoundTag compoundTag) {
      super.readFromNBT(compoundTag);
      if (compoundTag.getBoolean("hasItem")) {
         this.filterItem = ItemStack.readItemStackFromNbt(compoundTag.getCompound("item"));
      } else {
         this.filterItem = null;
      }
   }

   @Override
   public void writeToNBT(CompoundTag compoundTag) {
      super.writeToNBT(compoundTag);
      if (this.filterItem != null) {
         compoundTag.putCompound("item", this.filterItem.writeToNBT(new CompoundTag()));
         compoundTag.putBoolean("hasItem", true);
      }
   }

   @Override
   public void dropContents(World world, int x, int y, int z) {
      super.dropContents(world, x, y, z);
      if (this.filterItem != null) {
         EntityItem item = world.dropItem(x, y, z, this.filterItem);
         item.xd *= 0.5;
         item.yd *= 0.5;
         item.zd *= 0.5;
         item.pickupDelay = 0;
      }
   }

   @Override
   public Packet getDescriptionPacket() {
      return new PacketTileEntityData(this);
   }
}
