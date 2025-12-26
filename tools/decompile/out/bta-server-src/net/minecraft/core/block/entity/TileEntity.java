package net.minecraft.core.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.motion.CarriedBlock;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.ICarriable;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class TileEntity implements ICarriable {
   @Nullable
   public World worldObj;
   @Nullable
   public CarriedBlock carriedBlock;
   public int x;
   public int y;
   public int z;
   protected boolean tileEntityInvalid;

   public void tick() {
   }

   public void setChanged() {
      if (this.worldObj != null) {
         int id = this.getBlockId();
         if (id != 0 && Blocks.getBlock(id).isSignalSource()) {
            for (Side s : Side.sides) {
               this.worldObj.notifyBlocksOfNeighborChange(this.x + s.getOffsetX(), this.y + s.getOffsetY(), this.z + s.getOffsetZ(), this.getBlockId());
            }
         }

         this.worldObj.updateTileEntityChunkAndSendToPlayer(this.x, this.y, this.z, this);
      }
   }

   public Packet getDescriptionPacket() {
      return null;
   }

   public int getBlockId() {
      return this.carriedBlock != null ? this.carriedBlock.blockId : this.worldObj.getBlockId(this.x, this.y, this.z);
   }

   public Block<?> getBlock() {
      return this.carriedBlock != null ? this.carriedBlock.block() : this.worldObj.getBlock(this.x, this.y, this.z);
   }

   public int getBlockMeta() {
      return this.carriedBlock != null ? this.carriedBlock.metadata : this.worldObj.getBlockMetadata(this.x, this.y, this.z);
   }

   public double getDistanceFrom(double x, double y, double z) {
      double dx = this.x + 0.5 - x;
      double dy = this.y + 0.5 - y;
      double dz = this.z + 0.5 - z;
      return dx * dx + dy * dy + dz * dz;
   }

   public boolean isInvalid() {
      return this.tileEntityInvalid;
   }

   public void invalidate() {
      this.tileEntityInvalid = true;
   }

   public void validate() {
      this.tileEntityInvalid = false;
   }

   @Override
   public void readFromNBT(CompoundTag nbttagcompound) {
      this.x = nbttagcompound.getInteger("x");
      this.y = nbttagcompound.getInteger("y");
      this.z = nbttagcompound.getInteger("z");
   }

   @Override
   public void writeToNBT(CompoundTag nbttagcompound) {
      NamespaceID name = TileEntityDispatcher.getIDFromClass((Class<? extends TileEntity>)this.getClass());
      if (name == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         nbttagcompound.putString("id", name.toString());
         nbttagcompound.putInt("x", this.x);
         nbttagcompound.putInt("y", this.y);
         nbttagcompound.putInt("z", this.z);
      }
   }

   @Override
   public void heldTick(World world, Entity holder) {
   }

   public void dropContents(World world, int x, int y, int z) {
   }

   @Override
   public boolean tryPlace(World world, Entity holder, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced) {
      CarriedBlock carriedBlock = this.carriedBlock;
      this.x = blockX + side.getOffsetX();
      this.y = blockY + side.getOffsetY();
      this.z = blockZ + side.getOffsetZ();
      Block<?> currentBlock = world.getBlock(this.x, this.y, this.z);
      if (currentBlock != null && !currentBlock.hasTag(BlockTags.PLACE_OVERWRITES)) {
         return false;
      } else {
         world.setBlockAndMetadata(this.x, this.y, this.z, carriedBlock.blockId, carriedBlock.metadata);
         this.worldObj = world;
         this.validate();
         world.removeBlockTileEntity(this.x, this.y, this.z);
         world.setTileEntity(this.x, this.y, this.z, this);
         Block<?> b = world.getBlock(this.x, this.y, this.z);
         if (b != null && holder instanceof Mob) {
            b.onBlockPlacedByMob(world, this.x, this.y, this.z, side, (Mob)holder, xPlaced, yPlaced);
         }

         world.notifyBlockChange(this.x, this.y, this.z, carriedBlock.blockId);
         if (carriedBlock.blockId != 0 && Blocks.getBlock(carriedBlock.blockId).isSignalSource()) {
            for (Side s : Side.sides) {
               world.notifyBlocksOfNeighborChange(this.x + s.getOffsetX(), this.y + s.getOffsetY(), this.z + s.getOffsetZ(), this.getBlockId());
            }
         }

         return true;
      }
   }

   @Override
   public void drop(World world, Entity holder) {
      int holderX = MathHelper.floor(holder.x);
      int holderY = MathHelper.floor(holder.y);
      int holderZ = MathHelper.floor(holder.z);

      for (int _y = holderY - 1; _y <= holderY + 1; _y++) {
         for (int _x = holderX - 1; _x <= holderX + 1; _x++) {
            for (int _z = holderZ - 1; _z <= holderZ + 1; _z++) {
               if (this.tryPlace(world, holder, _x, _y - 1, _z, Side.TOP, 0.0, 0.0)) {
                  return;
               }
            }
         }
      }

      if (!world.isClientSide) {
         this.dropContents(world, holderX, holderY, holderZ);

         assert this.carriedBlock != null;

         this.carriedBlock.block().dropBlockWithCause(world, EnumDropCause.WORLD, holderX, holderY, holderZ, this.carriedBlock.metadata, this, null);
      }
   }

   @Override
   public boolean canBeCarried(World world, Entity potentialHolder) {
      return false;
   }

   @Override
   public ICarriable pickup(World world, Entity holder) {
      Block<?> currentBlock = world.getBlock(this.x, this.y, this.z);
      int currentMeta = world.getBlockMetadata(this.x, this.y, this.z);
      world.removeBlockTileEntity(this.x, this.y, this.z);
      world.setBlockRaw(this.x, this.y, this.z, 0);
      world.notifyBlockChange(this.x, this.y, this.z, 0);
      int id = this.getBlockId();
      if (currentBlock.isSignalSource()) {
         for (Side s : Side.sides) {
            this.worldObj.notifyBlocksOfNeighborChange(this.x + s.getOffsetX(), this.y + s.getOffsetY(), this.z + s.getOffsetZ(), this.getBlockId());
         }
      }

      this.worldObj = null;
      this.carriedBlock = this.getCarriedEntry(world, holder, currentBlock, currentMeta);
      return this.carriedBlock;
   }

   public CarriedBlock getCarriedEntry(World world, Entity holder, Block<?> currentBlock, int currentMeta) {
      return new CarriedBlock(holder, currentBlock, currentMeta, this);
   }
}
