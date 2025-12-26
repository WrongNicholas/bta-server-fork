package net.minecraft.core.block;

import java.util.List;
import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;

public class BlockLogicPressurePlate<T extends Entity> extends BlockLogic implements IPaintable {
   public static final int MASK_POWERED = 1;
   public static final int MASK_SIDE = 14;
   private final Class<T> triggerMobClass;

   public BlockLogicPressurePlate(Block<?> block, Class<T> mobType, Material material) {
      super(block, material);
      this.triggerMobClass = mobType;
      block.setTicking(true);
      float pixel = 0.0625F;
      this.setBlockBounds(pixel, 0.0, pixel, 1.0F - pixel, pixel / 2.0F, 1.0F - pixel);
   }

   @Override
   public int tickDelay() {
      return 20;
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return world.canPlaceOnSurfaceOfBlock(x, y - 1, z)
         || world.isBlockNormalCube(x, y + 1, z)
         || world.isBlockNormalCube(x + 1, y, z)
         || world.isBlockNormalCube(x - 1, y, z)
         || world.isBlockNormalCube(x, y, z + 1)
         || world.isBlockNormalCube(x, y, z - 1);
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      boolean flag = !this.canPlaceBlockAt(world, x, y, z);
      if (flag) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (!world.isClientSide) {
         if (isPressed(world.getBlockMetadata(x, y, z))) {
            this.updateState(world, x, y, z);
         }
      }
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      if (!world.isClientSide) {
         if (!isPressed(world.getBlockMetadata(x, y, z))) {
            this.updateState(world, x, y, z);
         }
      }
   }

   private void updateState(World world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      boolean isPressed = isPressed(meta);
      Side side = sideFromMeta(world.getBlockMetadata(x, y, z));
      boolean isSteppedOn = false;
      float pixel = 0.125F;
      List<T> list;
      switch (sideFromMeta(meta)) {
         case BOTTOM:
            list = world.getEntitiesWithinAABB(
               this.triggerMobClass, AABB.getTemporaryBB(pixel, 0.0, pixel, 1.0F - pixel, isPressed ? pixel / 2.0F : pixel, 1.0F - pixel).move(x, y, z)
            );
            break;
         case TOP:
            list = world.getEntitiesWithinAABB(
               this.triggerMobClass,
               AABB.getTemporaryBB(pixel, 1.0F - (isPressed ? pixel / 2.0F : pixel), pixel, 1.0F - pixel, 1.0, 1.0F - pixel).move(x, y, z)
            );
            break;
         case NORTH:
            list = world.getEntitiesWithinAABB(
               this.triggerMobClass, AABB.getTemporaryBB(pixel, pixel, 0.0, 1.0F - pixel, 1.0F - pixel, isPressed ? pixel / 2.0F : pixel).move(x, y, z)
            );
            break;
         case SOUTH:
            list = world.getEntitiesWithinAABB(
               this.triggerMobClass,
               AABB.getTemporaryBB(pixel, pixel, 1.0F - (isPressed ? pixel / 2.0F : pixel), 1.0F - pixel, 1.0F - pixel, 1.0).move(x, y, z)
            );
            break;
         case WEST:
            list = world.getEntitiesWithinAABB(
               this.triggerMobClass, AABB.getTemporaryBB(0.0, pixel, pixel, isPressed ? pixel / 2.0F : pixel, 1.0F - pixel, 1.0F - pixel).move(x, y, z)
            );
            break;
         case EAST:
         default:
            list = world.getEntitiesWithinAABB(
               this.triggerMobClass,
               AABB.getTemporaryBB(1.0F - (isPressed ? pixel / 2.0F : pixel), pixel, pixel, 1.0, 1.0F - pixel, 1.0F - pixel).move(x, y, z)
            );
      }

      if (list != null) {
         for (int i = 0; i < list.size(); i++) {
            if (list.get(i).canInteract()) {
               isSteppedOn = true;
               break;
            }
         }
      }

      if (isSteppedOn && !isPressed) {
         world.setBlockMetadataWithNotify(x, y, z, meta | 1);
         world.notifyBlocksOfNeighborChange(x, y, z, this.id());
         world.notifyBlocksOfNeighborChange(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), this.id());
         world.markBlocksDirty(x, y, z, x, y, z);
         world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.1, z + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (!isSteppedOn && isPressed) {
         world.setBlockMetadataWithNotify(x, y, z, meta & -2);
         world.notifyBlocksOfNeighborChange(x, y, z, this.id());
         world.notifyBlocksOfNeighborChange(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), this.id());
         world.markBlocksDirty(x, y, z, x, y, z);
         world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.1, z + 0.5, "random.click", 0.3F, 0.5F);
      }

      if (isSteppedOn) {
         world.scheduleBlockUpdate(x, y, z, this.id(), this.tickDelay());
      }
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      if (isPressed(data)) {
         Side side = sideFromMeta(world.getBlockMetadata(x, y, z));
         world.notifyBlocksOfNeighborChange(x, y, z, this.id());
         world.notifyBlocksOfNeighborChange(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), this.id());
      }

      super.onBlockRemoved(world, x, y, z, data);
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      boolean isPressed = isPressed(meta);
      float pixel = 0.0625F;
      switch (sideFromMeta(meta)) {
         case BOTTOM:
            return AABB.getTemporaryBB(pixel, 0.0, pixel, 1.0F - pixel, isPressed ? pixel / 2.0F : pixel, 1.0F - pixel);
         case TOP:
            return AABB.getTemporaryBB(pixel, 1.0F - (isPressed ? pixel / 2.0F : pixel), pixel, 1.0F - pixel, 1.0, 1.0F - pixel);
         case NORTH:
            return AABB.getTemporaryBB(pixel, pixel, 0.0, 1.0F - pixel, 1.0F - pixel, isPressed ? pixel / 2.0F : pixel);
         case SOUTH:
            return AABB.getTemporaryBB(pixel, pixel, 1.0F - (isPressed ? pixel / 2.0F : pixel), 1.0F - pixel, 1.0F - pixel, 1.0);
         case WEST:
            return AABB.getTemporaryBB(0.0, pixel, pixel, isPressed ? pixel / 2.0F : pixel, 1.0F - pixel, 1.0F - pixel);
         case EAST:
         default:
            return AABB.getTemporaryBB(1.0F - (isPressed ? pixel / 2.0F : pixel), pixel, pixel, 1.0, 1.0F - pixel, 1.0F - pixel);
      }
   }

   @Override
   public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
      return isPressed(worldSource.getBlockMetadata(x, y, z));
   }

   @Override
   public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
      int meta = world.getBlockMetadata(x, y, z);
      return isPressed(world.getBlockMetadata(x, y, z)) ? side == sideFromMeta(meta).getOpposite() : false;
   }

   public static boolean isPressed(int meta) {
      return (meta & 1) != 0;
   }

   public static int setSide(int meta, @NotNull Side side) {
      return meta & -15 | side.getId() << 1;
   }

   @NotNull
   public static Side sideFromMeta(int meta) {
      return Side.getSideById((meta & 14) >> 1);
   }

   @Override
   public boolean isSignalSource() {
      return true;
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return 1;
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      side = side.getOpposite();
      if (world.isBlockNormalCube(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ())) {
         world.setBlockMetadata(x, y, z, setSide(world.getBlockMetadata(x, y, z), side));
      } else if (world.canPlaceOnSurfaceOfBlock(x, y - 1, z)) {
         world.setBlockMetadata(x, y, z, setSide(world.getBlockMetadata(x, y, z), Side.BOTTOM));
      }
   }

   @Override
   public boolean canBePainted() {
      return this.id() == Blocks.PRESSURE_PLATE_PLANKS_OAK.id();
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockAndMetadataRaw(x, y, z, Blocks.PRESSURE_PLATE_PLANKS_OAK_PAINTED.id(), meta);
      world.setBlockMetadata(x, y, z, meta);
      ((BlockLogicPressurePlatePainted) Blocks.PRESSURE_PLATE_PLANKS_OAK_PAINTED.getLogic()).setColor(world, x, y, z, color);
      if (isPressed(meta)) {
         world.scheduleBlockUpdate(x, y, z, Blocks.PRESSURE_PLATE_PLANKS_OAK_PAINTED.id(), this.tickDelay());
      }
   }
}
