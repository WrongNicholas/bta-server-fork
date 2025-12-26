package net.minecraft.core.block.entity;

import java.util.List;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.block.BlockLogicMotionSensor;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;

public class TileEntitySensor extends TileEntity {
   private int getSightRange(World world, double x, double y, double z, Direction facing) {
      if (facing == Direction.NONE) {
         return 0;
      } else {
         int range = 4;
         int blockInFront = world.getBlockId(
            MathHelper.round(x + facing.getOffsetX()), MathHelper.round(y + facing.getOffsetY()), MathHelper.round(z + facing.getOffsetZ())
         );
         if (Blocks.hasTag(blockInFront, BlockTags.EXTENDS_MOTION_SENSOR_RANGE)) {
            range = 8;
         }

         for (int i = 1; i <= range; i++) {
            int x1 = MathHelper.round(x + facing.getOffsetX() * i);
            int y1 = MathHelper.round(y + facing.getOffsetY() * i);
            int z1 = MathHelper.round(z + facing.getOffsetZ() * i);
            int id = world.getBlockId(x1, y1, z1);
            if (Blocks.solid[id] && !Blocks.hasTag(id, BlockTags.EXTENDS_MOTION_SENSOR_RANGE)) {
               return i - 1;
            }
         }

         return range;
      }
   }

   private AABB getDetectionBox(double x, double y, double z, Direction facing, int range) {
      double x1 = x + facing.getOffsetX();
      double y1 = y + facing.getOffsetY();
      double z1 = z + facing.getOffsetZ();
      double x2 = x + facing.getOffsetX() * range;
      double y2 = y + facing.getOffsetY() * range;
      double z2 = z + facing.getOffsetZ() * range;
      double minX = Math.min(x1, x2);
      double minY = Math.min(y1, y2);
      double minZ = Math.min(z1, z2);
      double maxX = Math.max(x1, x2) + 1.0;
      double maxY = Math.max(y1, y2) + 1.0;
      double maxZ = Math.max(z1, z2) + 1.0;
      return AABB.getTemporaryBB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   @Override
   public void tick() {
      if (this.worldObj != null && !this.worldObj.isClientSide) {
         int id = this.worldObj.getBlockId(this.x, this.y, this.z);
         int meta = this.worldObj.getBlockMetadata(this.x, this.y, this.z);
         boolean shouldBeActive = false;
         Direction facing = BlockLogicMotionSensor.getFacingDirection(meta);
         int effectiveRange = this.getSightRange(this.worldObj, this.x, this.y, this.z, facing);
         if (effectiveRange > 0) {
            AABB detectionBox = this.getDetectionBox(this.x, this.y, this.z, facing, effectiveRange);
            List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, detectionBox);

            for (int i = 0; i < list.size(); i++) {
               Entity entity = list.get(i);
               if (entity.canInteract()) {
                  if (entity instanceof Player) {
                     Player player = (Player)entity;
                     player.triggerAchievement(Achievements.GET_SENSED);
                  }

                  shouldBeActive = true;
               }
            }
         }

         if (shouldBeActive && id == Blocks.MOTION_SENSOR_IDLE.id()) {
            this.worldObj
               .playSoundEffect(
                  null, SoundCategory.WORLD_SOUNDS, this.x + 0.5, this.y + 0.5, this.z + 0.5, "tile.sensor_block.sense", 1.0F, this.worldObj.rand.nextFloat()
               );
            this.updateSensorBlockState(true, this.worldObj);
         }

         if (!shouldBeActive && id == Blocks.MOTION_SENSOR_ACTIVE.id()) {
            this.updateSensorBlockState(false, this.worldObj);
         }
      }
   }

   @Override
   public void heldTick(World world, Entity holder) {
      if (world != null && this.carriedBlock != null && !world.isClientSide) {
         assert this.worldObj == null;

         int id = this.carriedBlock.blockId;
         int meta = this.carriedBlock.metadata;
         boolean shouldBeActive = false;
         Direction facing = BlockLogicMotionSensor.getFacingDirection(meta);
         int effectiveRange = this.getSightRange(world, holder.x, holder.y, holder.z, facing);
         if (effectiveRange > 0) {
            AABB detectionBox = this.getDetectionBox(holder.x, holder.y, holder.z, facing, effectiveRange);
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, detectionBox);

            for (int i = 0; i < list.size(); i++) {
               Entity entity = list.get(i);
               if (entity != holder) {
                  if (entity instanceof Player) {
                     Player player = (Player)entity;
                     player.triggerAchievement(Achievements.GET_SENSED);
                  }

                  shouldBeActive = true;
               }
            }
         }

         if (shouldBeActive && id == Blocks.MOTION_SENSOR_IDLE.id()) {
            world.playSoundEffect(
               null, SoundCategory.WORLD_SOUNDS, this.x + 0.5, this.y + 0.5, this.z + 0.5, "tile.sensor_block.sense", 1.0F, world.rand.nextFloat()
            );
            this.updateSensorBlockState(true, world);
         }

         if (!shouldBeActive && id == Blocks.MOTION_SENSOR_ACTIVE.id()) {
            this.updateSensorBlockState(false, world);
         }
      }
   }

   public void updateSensorBlockState(boolean active, World world) {
      if (this.carriedBlock != null) {
         if (active) {
            this.carriedBlock.blockId = Blocks.MOTION_SENSOR_ACTIVE.id();
         } else {
            this.carriedBlock.blockId = Blocks.MOTION_SENSOR_IDLE.id();
         }
      } else {
         int meta = world.getBlockMetadata(this.x, this.y, this.z);
         int blockId;
         if (active) {
            blockId = Blocks.MOTION_SENSOR_ACTIVE.id();
         } else {
            blockId = Blocks.MOTION_SENSOR_IDLE.id();
         }

         world.setBlock(this.x, this.y, this.z, blockId);
         world.setBlockMetadata(this.x, this.y, this.z, meta);
         world.notifyBlockChange(this.x, this.y, this.z, blockId);
      }
   }
}
