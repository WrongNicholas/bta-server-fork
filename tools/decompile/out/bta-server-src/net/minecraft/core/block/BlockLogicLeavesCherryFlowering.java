package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.IBonemealable;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Seasons;
import org.jetbrains.annotations.Nullable;

public class BlockLogicLeavesCherryFlowering extends BlockLogicLeavesCherry implements IBonemealable {
   public static final int MASK_GROWTH_DATA = 240;
   public static final int MAX_GROWTH_STATE = 1;

   public BlockLogicLeavesCherryFlowering(Block<?> block) {
      super(block);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      int growthRate = getGrowthRate(meta);
      if (dropCause == EnumDropCause.PICK_BLOCK || dropCause == EnumDropCause.SILK_TOUCH) {
         return new ItemStack[]{new ItemStack(this)};
      } else {
         return growthRate == 0 ? null : new ItemStack[]{new ItemStack(Items.FOOD_CHERRY, world.rand.nextInt(2) + 1)};
      }
   }

   @Override
   public void onBlockLeftClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      this.onBlockRightClicked(world, x, y, z, player, null, 0.0, 0.0);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      return this.harvest(world, x, y, z, player);
   }

   public boolean harvest(World world, int x, int y, int z, @Nullable Player player) {
      int meta = world.getBlockMetadata(x, y, z);
      int growthRate = getGrowthRate(meta);
      if (growthRate > 0) {
         if (player != null) {
            world.playSoundAtEntity(player, player, "item.pickup", 1.0F, 1.0F);
         }

         if (!world.isClientSide) {
            this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, meta, null, null);
         }

         world.setBlockMetadataWithNotify(x, y, z, setGrowthRate(meta, 0));
         world.scheduleBlockUpdate(x, y, z, Blocks.LEAVES_CHERRY_FLOWERING.id(), this.tickDelay());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
      this.harvest(world, x, y, z, null);
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      super.updateTick(world, x, y, z, rand);
      int meta = world.getBlockMetadata(x, y, z);
      int growthRate = getGrowthRate(meta);
      if (world.getSeasonManager().getCurrentSeason() == Seasons.OVERWORLD_SPRING) {
         if (rand.nextInt(20) == 0 && growthRate == 0) {
            world.setBlockMetadataWithNotify(x, y, z, setGrowthRate(meta, 1));
            world.scheduleBlockUpdate(x, y, z, Blocks.LEAVES_CHERRY_FLOWERING.id(), this.tickDelay());
         }
      } else if (growthRate > 0) {
         world.setBlockMetadataWithNotify(x, y, z, meta & 15);
         world.scheduleBlockUpdate(x, y, z, Blocks.LEAVES_CHERRY_FLOWERING.id(), this.tickDelay());
      }
   }

   @Override
   public boolean onBonemealUsed(
      ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      int meta = world.getBlockMetadata(blockX, blockY, blockZ);
      if (getGrowthRate(meta) != 0) {
         return false;
      } else {
         if (!world.isClientSide) {
            if (world.getSeasonManager().getCurrentSeason() != Seasons.OVERWORLD_SPRING) {
               return true;
            }

            world.setBlockMetadataWithNotify(blockX, blockY, blockZ, setGrowthRate(meta, 1));
            if (player == null || player.getGamemode().consumeBlocks()) {
               itemstack.stackSize--;
            }
         }

         return true;
      }
   }

   public static int getGrowthRate(int meta) {
      return (meta & 240) >> 4;
   }

   public static int setGrowthRate(int meta, int growthRate) {
      return meta & -241 | growthRate << 4 & 240;
   }
}
