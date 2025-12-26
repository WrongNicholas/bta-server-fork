package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.IBonemealable;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class BlockLogicSaplingBase extends BlockLogicFlower implements IBonemealable {
   public boolean canGrowOnSand = false;

   public BlockLogicSaplingBase(Block<?> block) {
      super(block);
      float f = 0.4F;
      this.setBlockBounds(0.5F - f, 0.0, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
   }

   @Override
   public boolean mayPlaceOn(int blockId) {
      return blockId == Blocks.SAND.id() || super.mayPlaceOn(blockId);
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (!world.isClientSide) {
         if (!this.canGrowOnSand && world.getBlockId(x, y - 1, z) == Blocks.SAND.id()) {
            world.setBlockWithNotify(x, y, z, Blocks.DEADBUSH.id());
         }

         super.updateTick(world, x, y, z, rand);
         int growthRate = 30;
         if (world.getSeasonManager().getCurrentSeason() != null) {
            growthRate = MathHelper.floor_float(growthRate / world.getSeasonManager().getCurrentSeason().cropGrowthFactor);
         }

         if (world.getBlockLightValue(x, y + 1, z) >= 9 && rand.nextInt(growthRate) == 0) {
            int l = world.getBlockMetadata(x, y, z);
            if ((l & 8) == 0) {
               world.setBlockMetadataWithNotify(x, y, z, l | 8);
            } else {
               this.growTree(world, x, y, z, rand);
            }
         }
      }
   }

   @Override
   public boolean onBonemealUsed(
      ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (!world.isClientSide) {
         this.growTree(world, blockX, blockY, blockZ, world.rand);
         if (player == null || player.getGamemode().consumeBlocks()) {
            itemstack.stackSize--;
         }
      }

      return true;
   }

   public abstract void growTree(World var1, int var2, int var3, int var4, Random var5);
}
