package net.minecraft.core.item;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicBrazier;
import net.minecraft.core.block.BlockLogicFluid;
import net.minecraft.core.block.BlockLogicTNT;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class ItemFireStriker extends Item {
   public ItemFireStriker(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.maxStackSize = 1;
      this.setMaxDamage(ToolMaterial.iron.getDurability() / 2);
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, Player entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      blockX += side.getOffsetX();
      blockY += side.getOffsetY();
      blockZ += side.getOffsetZ();
      int i1 = world.getBlockId(blockX, blockY, blockZ);
      if (i1 == 0) {
         if (world.setBlockWithNotify(blockX, blockY, blockZ, Blocks.FIRE.id())) {
            world.playSoundEffect(
               entityplayer, SoundCategory.WORLD_SOUNDS, blockX + 0.5, blockY + 0.5, blockZ + 0.5, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F
            );
            itemstack.damageItem(1, entityplayer);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
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
      if (!this.onUseItemOnBlock(itemStack, null, world, blockX, blockY, blockZ, direction.getSide(), 0.5, 0.5)) {
         blockX += direction.getOffsetX();
         blockY += direction.getOffsetY();
         blockZ += direction.getOffsetZ();
         Block<?> b = world.getBlock(blockX, blockY, blockZ);
         if (Block.hasLogicClass(b, BlockLogicBrazier.class) && !((BlockLogicBrazier)b.getLogic()).isBurning()) {
            if (Block.hasLogicClass(world.getBlock(blockX + 1, blockY, blockZ), BlockLogicFluid.class)
               || Block.hasLogicClass(world.getBlock(blockX - 1, blockY, blockZ), BlockLogicFluid.class)
               || Block.hasLogicClass(world.getBlock(blockX, blockY, blockZ + 1), BlockLogicFluid.class)
               || Block.hasLogicClass(world.getBlock(blockX, blockY, blockZ - 1), BlockLogicFluid.class)) {
               return;
            }

            world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, Blocks.BRAZIER_ACTIVE.id(), 0);
            itemStack.damageItem(1, null);
            world.playSoundEffect(
               null, SoundCategory.WORLD_SOUNDS, blockX + 0.5, blockY + 0.5, blockZ + 0.5, "fire.ignite", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F
            );
         } else if (Block.hasLogicClass(b, BlockLogicTNT.class)) {
            ((BlockLogicTNT)b.getLogic()).ignite(world, blockX, blockY, blockZ, true);
         }
      }
   }
}
