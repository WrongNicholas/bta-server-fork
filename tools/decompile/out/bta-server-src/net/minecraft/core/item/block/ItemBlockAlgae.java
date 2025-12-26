package net.minecraft.core.item.block;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemBlockAlgae<T extends BlockLogic> extends ItemBlock<T> {
   public ItemBlockAlgae(Block<T> block) {
      super(block);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      return false;
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player player) {
      double reachDistance = player.getGamemode().getBlockReachDistance();
      HitResult rayTraceResult = player.rayTrace(reachDistance, 1.0F, true, false);
      if (rayTraceResult == null) {
         return itemstack;
      } else {
         if (rayTraceResult.hitType == HitResult.HitType.TILE) {
            int blockX = rayTraceResult.x;
            int blockY = rayTraceResult.y;
            int blockZ = rayTraceResult.z;
            if (world.getBlockId(blockX, blockY, blockZ) != Blocks.FLUID_WATER_STILL.id()) {
               return itemstack;
            }

            if (world.getBlockId(blockX, ++blockY, blockZ) != 0) {
               return itemstack;
            }

            if (itemstack.consumeItem(player) && world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, this.block.id(), 0)) {
               this.block.onBlockPlacedByMob(world, blockX, blockY, blockZ, Side.NONE, player, 0.5, 0.5);
               world.playBlockSoundEffect(player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, this.block, EnumBlockSoundEffectType.PLACE);
               player.swingItem();
            }
         }

         return itemstack;
      }
   }

   @Override
   public void onUseByActivator(
      ItemStack itemStack,
      TileEntityActivator activatorBlock,
      World world,
      Random random,
      int blockX,
      int blockY,
      int blockZ,
      double offX,
      double offY,
      double offZ,
      Direction direction
   ) {
      int x = blockX + direction.getOffsetX();
      int y = blockY + direction.getOffsetY();
      int z = blockZ + direction.getOffsetZ();
      Block<?> b = world.getBlock(x, y, z);
      Block<?> bAbove = world.getBlock(x, y + 1, z);
      if (b == Blocks.FLUID_WATER_STILL && (bAbove == null || bAbove.hasTag(BlockTags.PLACE_OVERWRITES))) {
         world.setBlockWithNotify(x, y + 1, z, this.block.id());
         this.block.onBlockPlacedOnSide(world, x, y + 1, z, Side.NONE, 0.5, 0.5);
      }
   }
}
