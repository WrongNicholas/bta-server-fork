package net.minecraft.core.block.piston;

import com.mojang.nbt.tags.CompoundTag;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class TileEntityMovingPistonBlock extends TileEntity {
   public static final int TICKS_TO_EXTEND = 2;
   private static final List<Entity> entityBuffer = new ArrayList<>();
   private int movedId;
   private int movedData;
   @Nullable
   private TileEntity movedEntity;
   private Direction direction;
   private boolean extending;
   private boolean isSourcePiston;
   private float progress;
   private float progressO;

   public TileEntityMovingPistonBlock() {
   }

   public TileEntityMovingPistonBlock(
      int movedId, int movedData, @Nullable TileEntity movedEntity, Direction direction, boolean extending, boolean isSourcePiston
   ) {
      this.movedId = movedId;
      this.movedData = movedData;
      this.movedEntity = movedEntity;
      this.direction = direction;
      this.extending = extending;
      this.isSourcePiston = isSourcePiston;
      this.progress = this.progressO = 0.0F;
   }

   public int getMovedId() {
      return this.movedId;
   }

   public int getMovedData() {
      return this.movedData;
   }

   @Nullable
   public TileEntity getMovedEntity() {
      return this.movedEntity;
   }

   public boolean isExtending() {
      return this.extending;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public boolean isSourcePiston() {
      return this.isSourcePiston;
   }

   public float getProgress(float partialTick) {
      if (partialTick > 1.0F) {
         partialTick = 1.0F;
      }

      return this.progressO + (this.progress - this.progressO) * partialTick;
   }

   public float getXOff(float partialTick) {
      return this.extending
         ? (this.getProgress(partialTick) - 1.0F) * this.direction.getOffsetX()
         : (1.0F - this.getProgress(partialTick)) * this.direction.getOffsetX();
   }

   public float getYOff(float partialTick) {
      return this.extending
         ? (this.getProgress(partialTick) - 1.0F) * this.direction.getOffsetY()
         : (1.0F - this.getProgress(partialTick)) * this.direction.getOffsetY();
   }

   public float getZOff(float partialTick) {
      return this.extending
         ? (this.getProgress(partialTick) - 1.0F) * this.direction.getOffsetZ()
         : (1.0F - this.getProgress(partialTick)) * this.direction.getOffsetZ();
   }

   private void moveCollidedEntities(float stretch, float force) {
      if (!this.extending) {
         stretch--;
      } else {
         stretch = 1.0F - stretch;
      }

      AABB aabb = Blocks.PISTON_MOVING.getLogic().getCollisionShapeFromTileEntity(this.worldObj, this.x, this.y, this.z, this.movedId, stretch, this.direction);
      if (aabb != null) {
         List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, aabb);
         if (!list.isEmpty()) {
            entityBuffer.addAll(list);

            for (Entity entity : entityBuffer) {
               if (entity != null && !entity.noPhysics) {
                  entity.move(force * this.direction.getOffsetX(), force * this.direction.getOffsetY(), force * this.direction.getOffsetZ());
               }
            }

            entityBuffer.clear();
         }
      }
   }

   public void finalTick() {
      this.progressO = this.progress = 1.0F;
      this.worldObj.removeBlockTileEntity(this.x, this.y, this.z);
      this.invalidate();
      if (this.worldObj.getBlockId(this.x, this.y, this.z) == Blocks.PISTON_MOVING.id()) {
         this.worldObj.setBlockAndMetadataRaw(this.x, this.y, this.z, this.movedId, this.movedData);
         if (this.movedEntity != null) {
            TileEntity oldEnt = this.worldObj.getTileEntity(this.x, this.y, this.z);
            if (oldEnt != null) {
               oldEnt.invalidate();
            }

            this.movedEntity.validate();
            this.movedEntity.x = this.x;
            this.movedEntity.y = this.y;
            this.movedEntity.z = this.z;
            this.worldObj.replaceBlockTileEntity(this.x, this.y, this.z, this.movedEntity);
         }

         if (this.movedId != 0) {
            Block.disableNormalEntityLogic = true;
            Blocks.blocksList[this.movedId].onBlockPlacedByWorld(this.worldObj, this.x, this.y, this.z);
            Block.disableNormalEntityLogic = false;
         }

         assert this.movedEntity == null || this.worldObj.getTileEntity(this.x, this.y, this.z) == this.movedEntity : "Piston failed to actually move entity!";

         this.worldObj.notifyBlockChange(this.x, this.y, this.z, this.movedId);
      }
   }

   @Override
   public void tick() {
      this.progressO = this.progress;
      if (this.progressO >= 1.0F) {
         this.moveCollidedEntities(1.0F, 0.25F);
         this.finalTick();
      } else {
         this.progress += 0.5F;
         if (this.progress >= 0.999F) {
            this.progress = 1.0F;
         }

         if (this.extending) {
            this.moveCollidedEntities(this.progress, this.progress - this.progressO + 0.0625F);
         }
      }
   }

   @Override
   public void readFromNBT(CompoundTag tag) {
      super.readFromNBT(tag);
      this.movedId = tag.getInteger("blockId");
      this.movedData = tag.getInteger("blockData");
      if (tag.containsKey("tileEntity")) {
         CompoundTag entityTag = tag.getCompound("tileEntity");
         this.movedEntity = TileEntityDispatcher.createAndLoadEntity(entityTag);
      }

      this.direction = Direction.getDirectionById(tag.getInteger("facing"));
      this.progressO = this.progress = tag.getFloat("progress");
      this.extending = tag.getBoolean("extending");
   }

   @Override
   public void writeToNBT(CompoundTag tag) {
      super.writeToNBT(tag);
      tag.putInt("blockId", this.movedId);
      tag.putInt("blockData", this.movedData);
      if (this.movedEntity != null) {
         CompoundTag entityTag = new CompoundTag();
         this.movedEntity.writeToNBT(entityTag);
         tag.put("tileEntity", entityTag);
      }

      tag.putInt("facing", this.direction.getId());
      tag.putFloat("progress", this.progressO);
      tag.putBoolean("extending", this.extending);
   }

   @Override
   public boolean canBeCarried(World world, Entity potentialHolder) {
      return false;
   }
}
