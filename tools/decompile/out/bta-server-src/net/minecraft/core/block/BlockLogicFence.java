package net.minecraft.core.block;

import java.util.ArrayList;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicFence extends BlockLogic implements IPaintable {
   public BlockLogicFence(Block<?> block) {
      super(block, Material.wood);
   }

   @Override
   public boolean canPlaceOnSurface() {
      return true;
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return super.getCollisionBoundingBoxFromPool(world, x, y, z).expand(0.0, 0.5, 0.0);
   }

   @Override
   public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList<AABB> aabbList) {
      boolean connectXPos = this.canConnectTo(world, x + 1, y, z);
      boolean connectXNeg = this.canConnectTo(world, x - 1, y, z);
      boolean connectZPos = this.canConnectTo(world, x, y, z + 1);
      boolean connectZNeg = this.canConnectTo(world, x, y, z - 1);
      double thickness = 0.125;
      if (connectXPos) {
         this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.375, 0.0, 0.375, 1.0, 1.5, 0.625).move(x, y, z), aabbList);
      }

      if (connectXNeg) {
         this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.375, 0.625, 1.5, 0.625).move(x, y, z), aabbList);
      }

      if (connectZPos) {
         this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.375, 0.0, 0.375, 0.625, 1.5, 1.0).move(x, y, z), aabbList);
      }

      if (connectZNeg) {
         this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.375, 0.0, 0.0, 0.625, 1.5, 0.625).move(x, y, z), aabbList);
      }

      if (!connectXPos && !connectXNeg && !connectZPos && !connectZNeg) {
         this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.375, 0.0, 0.375, 0.625, 1.5, 0.625).move(x, y, z), aabbList);
      }
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      boolean connectXPos = this.canConnectTo(world, x + 1, y, z);
      boolean connectXNeg = this.canConnectTo(world, x - 1, y, z);
      boolean connectZPos = this.canConnectTo(world, x, y, z + 1);
      boolean connectZNeg = this.canConnectTo(world, x, y, z - 1);
      return AABB.getTemporaryBB(
         connectXNeg ? 0.0F : 0.375F, 0.0, connectZNeg ? 0.0F : 0.375F, 1.0F - (connectXPos ? 0.0F : 0.375F), 1.0, 1.0F - (connectZPos ? 0.0F : 0.375F)
      );
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   public boolean canConnectTo(WorldSource worldSource, int x, int y, int z) {
      int l = worldSource.getBlockId(x, y, z);
      return Blocks.hasTag(l, BlockTags.FENCES_CONNECT);
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      world.setBlock(x, y, z, Blocks.FENCE_PLANKS_OAK_PAINTED.id());
      Blocks.FENCE_PLANKS_OAK_PAINTED.getLogic().setColor(world, x, y, z, color);
   }
}
