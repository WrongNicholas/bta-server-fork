package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.IBonemealable;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Seasons;
import org.jetbrains.annotations.Nullable;

public class BlockLogicLeavesCacao extends BlockLogicLeavesBase implements IBonemealable {
   public static final int MASK_GROWTH_DATA = 240;
   public static final int MAX_GROWTH_STATE = 4;
   static final int BEAN_GROWTH_RATE = 50;

   public BlockLogicLeavesCacao(Block<?> block) {
      super(block, Material.leaves, Blocks.SAPLING_CACAO);
      block.setTicking(true);
   }

   public boolean canBeansGrow(World world, int x, int y, int z) {
      for (int x1 = x - 1; x1 < x + 1; x1++) {
         for (int z1 = z - 1; z1 < z + 1; z1++) {
            if (world.getBlock(x1, y, z1) == null) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      if (dropCause != EnumDropCause.PICK_BLOCK && dropCause != EnumDropCause.SILK_TOUCH) {
         int growthRate = getGrowthRate(meta);
         return growthRate > 1
            ? new ItemStack[]{new ItemStack(Items.DYE, MathHelper.ceil(growthRate / 2.0), DyeColor.BROWN.itemMeta)}
            : super.getBreakResult(world, dropCause, meta, tileEntity);
      } else {
         return new ItemStack[]{new ItemStack(this)};
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
         world.scheduleBlockUpdate(x, y, z, Blocks.LEAVES_CACAO.id(), this.tickDelay());
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
      if (rand.nextInt(50) == 0) {
         if (world.getSeasonManager().getCurrentSeason() == Seasons.OVERWORLD_SUMMER) {
            if (this.canBeansGrow(world, x, y, z) && growthRate < 4) {
               world.setBlockMetadataWithNotify(x, y, z, setGrowthRate(meta, ++growthRate));
               world.scheduleBlockUpdate(x, y, z, Blocks.LEAVES_CACAO.id(), this.tickDelay());
            }
         } else if (growthRate > 0) {
            world.setBlockMetadataWithNotify(x, y, z, setGrowthRate(meta, 0));
            world.scheduleBlockUpdate(x, y, z, Blocks.LEAVES_CACAO.id(), this.tickDelay());
         }
      }
   }

   @Override
   public boolean onBonemealUsed(
      ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      int meta = world.getBlockMetadata(blockX, blockY, blockZ);
      if (getGrowthRate(meta) >= 4) {
         return false;
      } else {
         if (!world.isClientSide) {
            if (world.getSeasonManager().getCurrentSeason() != Seasons.OVERWORLD_SUMMER) {
               return true;
            }

            world.setBlockMetadataWithNotify(blockX, blockY, blockZ, setGrowthRate(meta, 4));
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
