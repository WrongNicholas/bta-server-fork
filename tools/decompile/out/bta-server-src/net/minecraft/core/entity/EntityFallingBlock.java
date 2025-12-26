package net.minecraft.core.entity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicSand;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.block.motion.CarriedBlock;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.IVehicle;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityFallingBlock extends Entity {
   public CarriedBlock carriedBlock;
   public int fallTime;
   public boolean hasRemovedBlock = false;

   public EntityFallingBlock(World world) {
      super(world);
      this.carriedBlock = new CarriedBlock(this, Blocks.SAND, 0, null);
      this.fallTime = 0;
      this.setSize(1.0F, 1.0F);
      this.heightOffset = this.bbHeight / 2.0F;
   }

   public EntityFallingBlock(World world, double x, double y, double z, int blockId, int blockMeta, @Nullable TileEntity tileEntity) {
      super(world);
      this.fallTime = 0;
      this.carriedBlock = new CarriedBlock(this, blockId, blockMeta, tileEntity);
      if (tileEntity != null) {
         tileEntity.worldObj = null;
         tileEntity.carriedBlock = this.carriedBlock;
      }

      this.blocksBuilding = true;
      this.setSize(1.0F, 1.0F);
      this.heightOffset = this.bbHeight / 2.0F;
      this.setPos(x, y, z);
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      this.xo = x;
      this.yo = y;
      this.zo = z;
   }

   @Override
   protected boolean makeStepSound() {
      return false;
   }

   @Override
   protected void defineSynchedData() {
   }

   @Override
   public boolean isPickable() {
      return !this.removed;
   }

   @Override
   public void tick() {
      if (this.carriedBlock.blockId == 0) {
         this.remove();
      } else {
         this.pushesThisTick = 0;
         this.pushTime *= 0.98F;
         if (this.pushTime < 0.05F || this.pushTime < 0.25 && this.onGround) {
            this.pushTime = 0.0F;
         }

         if (this.isOnFire() && this.carriedBlock.blockId == Blocks.TNT.id()) {
            this.remove();
            EntityPrimedTNT entityPrimedTNT = new EntityPrimedTNT(this.world, this.x, this.y, this.z);
            entityPrimedTNT.xd = this.xd;
            entityPrimedTNT.yd = this.yd;
            entityPrimedTNT.zd = this.zd;
            entityPrimedTNT.pushTime = this.pushTime;
            this.world.entityJoinedWorld(entityPrimedTNT);
         } else {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            if (this.pushTime < 0.01 && this.yd <= 0.0) {
               this.fallTime++;
            }

            this.yd -= 0.04;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.98;
            this.yd *= 0.98;
            this.zd *= 0.98;
            int x = MathHelper.round(this.x - 0.5);
            int y = MathHelper.round(this.y);
            int z = MathHelper.round(this.z - 0.5);
            if (this.world.getBlockId(x, y, z) == this.carriedBlock.blockId && !this.hasRemovedBlock) {
               this.world.setBlockWithNotify(x, y, z, 0);
               this.hasRemovedBlock = true;
            }

            if (this.onGround) {
               Block<?> selfBlock = this.carriedBlock.block();
               Block<?> blockBelow = this.world.getBlock(x, y - 1, z);
               double friction = selfBlock.friction;
               friction *= blockBelow == null ? 0.98 : blockBelow.friction * 0.91F;
               this.xd *= friction;
               this.zd *= friction;
               this.yd *= -0.5;
               this.pushTime *= (float)friction;
            }

            double v = Math.hypot(this.xd, this.zd);
            if (v < 0.001 || this.isInWall()) {
               if (this.onGround || this.isInWall()) {
                  Entity rider = this.getPassenger();
                  this.ejectRider();
                  this.remove();
                  if ((
                        !this.world.canBlockBePlacedAt(this.carriedBlock.blockId, x, y, z, true, Side.TOP)
                           || BlockLogicSand.canFallBelow(this.world, x, y - 1, z)
                           || !this.world.setBlock(x, y, z, this.carriedBlock.blockId)
                     )
                     && !this.world.isClientSide) {
                     if (this.hasRemovedBlock) {
                        this.drop();
                     }
                  } else if (!this.world.isClientSide) {
                     this.world.setBlockMetadata(x, y, z, this.carriedBlock.metadata);
                     if (this.carriedBlock.entity != null) {
                        TileEntity oldEnt = this.world.getTileEntity(x, y, z);
                        if (oldEnt != null) {
                           oldEnt.invalidate();
                        }

                        this.carriedBlock.entity.validate();
                        this.carriedBlock.entity.x = x;
                        this.carriedBlock.entity.y = y;
                        this.carriedBlock.entity.z = z;
                        this.carriedBlock.entity.worldObj = this.world;
                        this.carriedBlock.entity.carriedBlock = null;
                        this.world.replaceBlockTileEntity(x, y, z, this.carriedBlock.entity);
                     }

                     this.world.notifyBlockChange(x, y, z, this.carriedBlock.blockId);
                  }

                  if (rider != null) {
                     TileEntity te = this.world.getTileEntity(x, y, z);
                     if (te instanceof IVehicle) {
                        rider.startRiding((IVehicle)te);
                     }
                  }
               } else if (this.fallTime > 600 && !this.world.isClientSide) {
                  if (this.hasRemovedBlock) {
                     this.drop();
                  }

                  this.ejectRider();
                  this.remove();
               }

               this.carriedBlock.heldTick(this.world, this);
            }
         }
      }
   }

   public void drop() {
      Block<?> block = this.carriedBlock.block();
      if (block != null) {
         ItemStack[] drops = block.getBreakResult(this.world, EnumDropCause.SILK_TOUCH, this.carriedBlock.metadata, this.carriedBlock.entity);
         if (drops != null && drops.length > 0) {
            for (int i = 0; i < drops.length; i++) {
               this.dropItem(drops[i], 0.0F);
            }
         }
      }

      if (this.carriedBlock.entity != null) {
         int x = MathHelper.round(this.x - 0.5);
         int y = MathHelper.round(this.y);
         int z = MathHelper.round(this.z - 0.5);
         this.carriedBlock.entity.dropContents(this.world, x, y, z);
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      tag.putShort("Tile", (short)this.carriedBlock.blockId);
      tag.putShort("TileData", (short)this.carriedBlock.metadata);
      if (this.carriedBlock.entity != null) {
         CompoundTag entityTag = new CompoundTag();
         this.carriedBlock.entity.writeToNBT(entityTag);
         tag.putCompound("TileEntity", entityTag);
      }
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      this.carriedBlock = new CarriedBlock(
         this,
         tag.getShort("Tile") & 16383,
         tag.getShort("TileData") & 255,
         tag.containsKey("TileEntity") ? TileEntityDispatcher.createAndLoadEntity(tag.getCompound("TileEntity")) : null
      );
   }

   @Override
   public void fling(double xd, double yd, double zd, float pushTime) {
      super.fling(xd, yd, zd, pushTime);
      this.fallTime = 0;
   }

   @Override
   public float getShadowHeightOffs() {
      return 0.0F;
   }

   @Override
   public boolean showBoundingBoxOnHover() {
      return true;
   }

   @Override
   public double getRideHeight() {
      return 0.0;
   }
}
