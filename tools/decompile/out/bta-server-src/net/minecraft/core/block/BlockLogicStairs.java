package net.minecraft.core.block;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.MaterialColor;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;

public class BlockLogicStairs extends BlockLogic {
   public Block<?> modelBlock;

   public BlockLogicStairs(Block<?> block, Block<?> modelBlock) {
      super(block, Material.stone);
      this.modelBlock = modelBlock;
      block.withLightBlock(255);
   }

   @Override
   public void initializeBlock() {
      this.block.withHardness(this.modelBlock.blockHardness);
      this.block.withBlastResistance(this.modelBlock.blastResistance / 3.0F);
   }

   @NotNull
   @Override
   public Material getMaterial() {
      return this.modelBlock.getMaterial();
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
   public boolean canPlaceOnSurfaceOnCondition(World world, int x, int y, int z) {
      return (world.getBlockMetadata(x, y, z) & 8) > 0;
   }

   @Override
   public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList<AABB> aabbList) {
      int meta = world.getBlockMetadata(x, y, z);
      int hRotation = meta & 3;
      int vRotation = meta & 8;
      float stepYOffset = vRotation > 0 ? 0.5F : 0.0F;
      switch (hRotation) {
         case 0:
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0F + stepYOffset, 0.0, 0.5, 0.5F + stepYOffset, 1.0).move(x, y, z), aabbList);
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.5, 0.0, 0.0, 1.0, 1.0, 1.0).move(x, y, z), aabbList);
            break;
         case 1:
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.0, 0.5, 1.0, 1.0).move(x, y, z), aabbList);
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.5, 0.0F + stepYOffset, 0.0, 1.0, 0.5F + stepYOffset, 1.0).move(x, y, z), aabbList);
            break;
         case 2:
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0F + stepYOffset, 0.0, 1.0, 0.5F + stepYOffset, 0.5).move(x, y, z), aabbList);
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0).move(x, y, z), aabbList);
            break;
         default:
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5).move(x, y, z), aabbList);
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0F + stepYOffset, 0.5, 1.0, 0.5F + stepYOffset, 1.0).move(x, y, z), aabbList);
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      ItemStack[] result = dropCause != EnumDropCause.IMPROPER_TOOL ? new ItemStack[]{new ItemStack(this.block)} : null;
      if (result != null) {
         for (ItemStack stack : result) {
            stack.setMetadata(meta & 240);
            stack.itemID = this.id();
         }
      }

      return result;
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      this.modelBlock.animationTick(world, x, y, z, rand);
   }

   @Override
   public void onBlockLeftClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      this.modelBlock.onBlockLeftClicked(world, x, y, z, player, side, xHit, yHit);
   }

   @Override
   public float getBlockBrightness(WorldSource blockAccess, int x, int y, int z) {
      return this.modelBlock.getBlockBrightness(blockAccess, x, y, z);
   }

   @Override
   public float getBlastResistance(Entity entity) {
      return this.modelBlock.getBlastResistance(entity);
   }

   @Override
   public int tickDelay() {
      return this.modelBlock.tickDelay();
   }

   @Override
   public void handleEntityInside(World world, int x, int y, int z, Entity entity, Vec3 entityVelocity) {
      this.modelBlock.handleEntityInside(world, x, y, z, entity, entityVelocity);
   }

   @Override
   public boolean isCollidable() {
      return this.modelBlock.isCollidable();
   }

   @Override
   public boolean canCollideCheck(int meta, boolean shouldCollideWithFluids) {
      return this.modelBlock.canCollideCheck(meta, shouldCollideWithFluids);
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return this.modelBlock.canPlaceBlockAt(world, x, y, z);
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      this.onNeighborBlockChange(world, x, y, z, 0);
      this.modelBlock.onBlockPlacedByWorld(world, x, y, z);
   }

   @Override
   public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
      this.modelBlock.onEntityWalking(world, x, y, z, entity);
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      this.modelBlock.updateTick(world, x, y, z, rand);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      return this.modelBlock.onBlockRightClicked(world, x, y, z, player, side, xPlaced, yPlaced);
   }

   @Override
   public void onBlockDestroyedByExplosion(World world, int x, int y, int z) {
      this.modelBlock.onBlockDestroyedByExplosion(world, x, y, z);
   }

   @Override
   public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
      int meta = world.getBlockMetadata(x, y, z) & 240;
      Direction hRotation = mob.getHorizontalPlacementDirection(side).getOpposite();
      if (hRotation == Direction.NORTH) {
         meta |= 2;
      }

      if (hRotation == Direction.EAST) {
         meta |= 1;
      }

      if (hRotation == Direction.SOUTH) {
         meta |= 3;
      }

      if (hRotation == Direction.WEST) {
         meta |= 0;
      }

      Direction vRotation = mob.getVerticalPlacementDirection(side, yPlaced);
      if (vRotation == Direction.DOWN) {
         meta |= 0;
      }

      if (vRotation == Direction.UP) {
         meta |= 8;
      }

      world.setBlockMetadataWithNotify(x, y, z, meta);
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      int meta = world.getBlockMetadata(x, y, z) & 240;
      side = side.getOpposite();
      Side hSide = !side.isHorizontal() ? Side.NORTH : side;
      Direction hRotation = hSide.getOpposite().getDirection();
      if (hRotation == Direction.NORTH) {
         meta |= 2;
      }

      if (hRotation == Direction.EAST) {
         meta |= 1;
      }

      if (hRotation == Direction.SOUTH) {
         meta |= 3;
      }

      if (hRotation == Direction.WEST) {
         meta |= 0;
      }

      Side vSide = !side.isVertical() ? Side.BOTTOM : side;
      Direction vRotation = vSide.getDirection();
      if (vRotation == Direction.DOWN) {
         meta |= 0;
      }

      if (vRotation == Direction.UP) {
         meta |= 8;
      }

      world.setBlockMetadataWithNotify(x, y, z, meta);
   }

   @Override
   public float getAmbientOcclusionStrength(WorldSource blockAccess, int x, int y, int z) {
      return 0.0F;
   }

   @NotNull
   @Override
   public MaterialColor getMaterialColor() {
      return this.modelBlock.getMaterialColor();
   }
}
