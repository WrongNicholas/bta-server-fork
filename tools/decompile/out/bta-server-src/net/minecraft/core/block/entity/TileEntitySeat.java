package net.minecraft.core.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.IVehicle;
import net.minecraft.core.world.World;

public class TileEntitySeat extends TileEntity implements IVehicle {
   private Entity passenger = null;
   public Block<?> seat;

   public TileEntitySeat() {
      this(null);
   }

   public TileEntitySeat(Block<?> seat) {
      this.seat = seat;
   }

   @Override
   public void tick() {
      if (this.passenger != null) {
         if (this.carriedBlock != null) {
            this.carriedBlock.world.updateEntity(this.passenger);
         } else {
            this.worldObj.updateEntity(this.passenger);
         }
      }

      if (this.worldObj != null && this.worldObj.getBlock(this.x, this.y, this.z) != this.seat) {
         this.ejectRider();
      }
   }

   @Override
   public boolean isRemoved() {
      return false;
   }

   @Override
   public Entity ejectRider() {
      Entity entity = this.passenger;
      if (entity == null) {
         return null;
      } else {
         this.passenger = null;
         entity.vehicle = null;
         double x;
         double y;
         double z;
         if (this.carriedBlock != null) {
            x = this.carriedBlock.holder.x;
            y = this.carriedBlock.holder.y;
            z = this.carriedBlock.holder.z;
         } else {
            x = this.x;
            y = this.y;
            z = this.z;
         }

         if (this.isSafe(x, y + 1.0, z)) {
            entity.moveTo(x + 0.5, y + 1.0, z + 0.5, entity.yRot, entity.xRot);
         } else if (this.isSafe(x - 1.0, y, z)) {
            entity.moveTo(x - 0.5, y, z + 0.5, entity.yRot, entity.xRot);
         } else if (this.isSafe(x + 1.0, y, z)) {
            entity.moveTo(x + 1.5, y, z + 0.5, entity.yRot, entity.xRot);
         } else if (this.isSafe(x, y, z - 1.0)) {
            entity.moveTo(x + 0.5, y, z - 0.5, entity.yRot, entity.xRot);
         } else if (this.isSafe(x, y, z + 1.0)) {
            entity.moveTo(x + 0.5, y, z + 1.5, entity.yRot, entity.xRot);
         } else {
            entity.moveTo(x + 0.5, y + 1.0, z + 0.5, entity.yRot, entity.xRot);
         }

         return entity;
      }
   }

   private boolean isSafe(double x, double y, double z) {
      int _x = MathHelper.round(x);
      int _y = MathHelper.round(y);
      int _z = MathHelper.round(z);
      return this.carriedBlock != null
         ? !this.carriedBlock.world.isBlockNormalCube(_x, _y, _z) && !this.carriedBlock.world.isBlockNormalCube(_x, _y + 1, _z)
         : !this.worldObj.isBlockNormalCube(_x, _y, _z) && !this.worldObj.isBlockNormalCube(_x, _y + 1, _z);
   }

   @Override
   public void positionRider() {
      if (this.carriedBlock != null) {
         this.passenger.setPos(this.carriedBlock.holder.x, this.carriedBlock.holder.y + this.passenger.getRidingHeight(), this.carriedBlock.holder.z);
      } else {
         this.passenger.setPos(this.x + 0.5, this.y + 0.5 + this.passenger.getRidingHeight(), this.z + 0.5);
      }
   }

   @Override
   public void dropContents(World world, int x, int y, int z) {
      this.ejectRider();
   }

   @Override
   public void setPassenger(Entity entity) {
      this.passenger = entity;
   }

   @Override
   public Entity getPassenger() {
      return this.passenger;
   }

   @Override
   public void moveExitingEntity(Entity entity) {
      if (this.carriedBlock != null) {
         entity.moveTo(this.carriedBlock.holder.x, this.carriedBlock.holder.y + 2.0, this.carriedBlock.holder.z, entity.yRot, entity.xRot);
      } else {
         entity.moveTo(this.x, this.y + 2, this.z, entity.yRot, entity.xRot);
      }
   }

   @Override
   public float getYRotDelta() {
      return 0.0F;
   }

   @Override
   public float getXRotDelta() {
      return 0.0F;
   }

   @Override
   public void heldTick(World world, Entity holder) {
      this.tick();
   }

   @Override
   public void writeToNBT(CompoundTag compoundTag) {
      super.writeToNBT(compoundTag);
      if (this.seat != null && this.seat != Blocks.SEAT) {
         compoundTag.putInt("Block", this.seat.id());
      }
   }

   @Override
   public void readFromNBT(CompoundTag compoundTag) {
      super.readFromNBT(compoundTag);
      this.seat = Blocks.getBlock(compoundTag.getIntegerOrDefault("Block", Blocks.SEAT.id()));
   }
}
