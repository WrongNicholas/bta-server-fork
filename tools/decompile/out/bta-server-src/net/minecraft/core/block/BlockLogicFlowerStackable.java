package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicFlowerStackable extends BlockLogicFlower {
   public static final int MASK_STACK_COUNT = 96;
   public static final int OFFSET_STACK_COUNT = 5;
   public static final int MAX_STACK_COUNT = 3;

   public BlockLogicFlowerStackable(Block<?> block) {
      super(block);
   }

   @Override
   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      int metadata = stack.getMetadata();
      return setPermanent(metadata, true);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      int stackCount = getStackCount(meta) + 1;
      if (dropCause == EnumDropCause.PICK_BLOCK) {
         return new ItemStack[]{new ItemStack(this, 1, 0)};
      } else {
         return dropCause == EnumDropCause.SILK_TOUCH
            ? new ItemStack[]{new ItemStack(this, 1, meta & 96)}
            : new ItemStack[]{new ItemStack(this, stackCount, 0)};
      }
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      int metadata = world.getBlockMetadata(x, y, z);
      int currentStackCount = getStackCount(metadata);
      if (currentStackCount >= 3) {
         return false;
      } else {
         ItemStack heldItem = player.getHeldItem();
         if (heldItem != null && heldItem.stackSize >= 1 && heldItem.getItem().id == this.block.id()) {
            int newMetadata = setPermanent(setStackCount(metadata, currentStackCount + 1), true);
            world.setBlockMetadataWithNotify(x, y, z, newMetadata);
            world.playBlockSoundEffect(player, x + 0.5F, y + 0.5F, z + 0.5F, this.block, EnumBlockSoundEffectType.PLACE);
            heldItem.consumeItem(player);
            return true;
         } else {
            return false;
         }
      }
   }

   public static int getStackCount(int metadata) {
      return (metadata & 96) >> 5;
   }

   public static int setStackCount(int metadata, int stackCount) {
      return metadata & -97 | stackCount << 5 & 96;
   }
}
