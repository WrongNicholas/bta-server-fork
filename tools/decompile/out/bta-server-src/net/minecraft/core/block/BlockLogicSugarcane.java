package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.IBonemealable;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.Nullable;

public class BlockLogicSugarcane extends BlockLogic implements IBonemealable {
   public BlockLogicSugarcane(Block<?> block) {
      super(block, Material.plant);
      float f = 0.375F;
      this.setBlockBounds(0.5F - f, 0.0, 0.5F - f, 0.5F + f, 1.0, 0.5F + f);
      block.setTicking(true);
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (world.isAirBlock(x, y + 1, z)) {
         int l = 1;

         while (world.getBlockId(x, y - l, z) == this.id()) {
            l++;
         }

         if (l < 3) {
            int i1 = world.getBlockMetadata(x, y, z);
            if (i1 == 15) {
               world.setBlockWithNotify(x, y + 1, z, this.id());
               world.setBlockMetadataWithNotify(x, y, z, 0);
            } else {
               world.setBlockMetadataWithNotify(x, y, z, i1 + 1);
            }
         }
      }
   }

   public void growReedOnTop(World world, int x, int y, int z) {
      int l = 1;

      while (world.getBlockId(x, y + l, z) == this.id()) {
         l++;
      }

      if (world.isAirBlock(x, y + l, z)) {
         world.setBlockWithNotify(x, y + l, z, this.id());
      }
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      int l = world.getBlockId(x, y - 1, z);
      if (l == this.id()) {
         return true;
      } else if (Blocks.blocksList[l] == null) {
         return false;
      } else if (!Blocks.blocksList[l].hasTag(BlockTags.GROWS_SUGAR_CANE)) {
         return false;
      } else if (Blocks.hasTag(world.getBlockId(x - 1, y - 1, z), BlockTags.IS_WATER)) {
         return true;
      } else if (Blocks.hasTag(world.getBlockId(x + 1, y - 1, z), BlockTags.IS_WATER)) {
         return true;
      } else {
         return Blocks.hasTag(world.getBlockId(x, y - 1, z - 1), BlockTags.IS_WATER)
            ? true
            : Blocks.hasTag(world.getBlockId(x, y - 1, z + 1), BlockTags.IS_WATER);
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      this.checkBlockCoordValid(world, x, y, z);
   }

   protected final void checkBlockCoordValid(World world, int x, int y, int z) {
      if (!this.canBlockStay(world, x, y, z)) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(Items.SUGARCANE)};
   }

   @Override
   public boolean canBlockStay(World world, int x, int y, int z) {
      return this.canPlaceBlockAt(world, x, y, z);
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
   public boolean onBonemealUsed(
      ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (!world.isClientSide) {
         this.growReedOnTop(world, blockX, blockY, blockZ);
         if (player == null || player.getGamemode().consumeBlocks()) {
            itemstack.stackSize--;
         }
      }

      return true;
   }
}
