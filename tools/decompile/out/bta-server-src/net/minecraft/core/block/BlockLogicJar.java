package net.minecraft.core.block;

import java.util.function.Supplier;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityFlowerJar;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;

public class BlockLogicJar extends BlockLogic {
   private final Supplier<Item> itemSupplier;

   public BlockLogicJar(Block<?> block, @NotNull Supplier<Item> itemSupplier) {
      super(block, Material.glass);
      block.withEntity(TileEntityFlowerJar::new);
      this.setBlockBounds(0.3125, 0.0, 0.3125, 0.6875, 0.375, 0.6875);
      this.itemSupplier = itemSupplier;
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return !world.canPlaceOnSurfaceOfBlock(x, y - 1, z) ? false : super.canPlaceBlockAt(world, x, y, z);
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      return AABB.getTemporaryBB(0.3125, 0.0, 0.3125, 0.6875, 0.375, 0.6875);
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (player.getHeldItem() == null) {
         if (world.getBlockMetadata(x, y, z) == 0) {
            world.setBlockWithNotify(x, y, z, 0);
            world.playSoundAtEntity(player, player, "item.pickup", 1.0F, 1.0F);
            if (!world.isClientSide) {
               world.dropItem(x, y, z, new ItemStack(this.itemSupplier.get(), 1, 0));
            }
         } else {
            TileEntityFlowerJar tileEntity = (TileEntityFlowerJar)world.getTileEntity(x, y, z);
            int flowerInPot = tileEntity.flowerInPot;
            int flowerData = tileEntity.flowerData;
            if (flowerInPot > 0 && Blocks.hasTag(flowerInPot, BlockTags.PLANTABLE_IN_JAR)) {
               tileEntity.flowerInPot = 0;
               world.setBlockMetadataWithNotify(x, y, z, 0);
               world.playSoundAtEntity(player, player, "item.pickup", 1.0F, 1.0F);
               if (!world.isClientSide) {
                  if (Block.hasLogicClass(Blocks.getBlock(flowerInPot), BlockLogicFlowerStackable.class)) {
                     for (int i = 0; i <= BlockLogicFlowerStackable.getStackCount(flowerData); i++) {
                        world.dropItem(x, y, z, new ItemStack(flowerInPot, 1, 0));
                     }
                  } else {
                     world.dropItem(x, y, z, new ItemStack(flowerInPot, 1, flowerData));
                  }
               }
            }
         }
      } else if (player.getHeldItem().getItem() instanceof ItemBlock) {
         Block<?> blockInHand = ((ItemBlock)player.getHeldItem().getItem()).getBlock();
         if (Blocks.hasTag(blockInHand.id(), BlockTags.PLANTABLE_IN_JAR)) {
            TileEntityFlowerJar tileEntity = (TileEntityFlowerJar)world.getTileEntity(x, y, z);
            if (world.getBlockMetadata(x, y, z) == 0) {
               player.getHeldItem().consumeItem(player);
               world.setBlockMetadataWithNotify(x, y, z, 1);
               tileEntity.flowerInPot = blockInHand.id();
               tileEntity.flowerData = player.getHeldItem().getMetadata();
               world.playBlockSoundEffect(player, x + 0.5F, y + 0.5F, z + 0.5F, this.block, EnumBlockSoundEffectType.PLACE);
               return true;
            }

            if (blockInHand.getLogic() instanceof BlockLogicFlowerStackable && blockInHand == Blocks.getBlock(tileEntity.flowerInPot)) {
               int metadata = tileEntity.flowerData;
               int currentStackCount = BlockLogicFlowerStackable.getStackCount(metadata);
               if (currentStackCount >= 3) {
                  return false;
               }

               tileEntity.flowerData = BlockLogicFlowerStackable.setPermanent(BlockLogicFlowerStackable.setStackCount(metadata, currentStackCount + 1), true);
               world.playBlockSoundEffect(player, x + 0.5F, y + 0.5F, z + 0.5F, this.block, EnumBlockSoundEffectType.PLACE);
               player.getHeldItem().consumeItem(player);
               return true;
            }
         }
      }

      return true;
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return 1;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case PICK_BLOCK:
         case SILK_TOUCH:
         case WORLD:
            return new ItemStack[]{new ItemStack(this.itemSupplier.get())};
         default:
            return null;
      }
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }
}
