package net.minecraft.core.item;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.ProjectilePebble;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;

public class ItemPebble extends Item implements IDispensable {
   public ItemPebble(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.maxStackSize = 64;
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      itemstack.consumeItem(entityplayer);
      world.playSoundAtEntity(entityplayer, entityplayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      if (!world.isClientSide) {
         world.entityJoinedWorld(new ProjectilePebble(world, entityplayer));
      }

      return itemstack;
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      int id = world.getBlockId(blockX, blockY, blockZ);
      int meta = world.getBlockMetadata(blockX, blockY, blockZ);
      if (id != Blocks.OVERLAY_PEBBLES.id() && Blocks.blocksList[id] != null && Blocks.blocksList[id].hasTag(BlockTags.PLACE_OVERWRITES)) {
         id = 0;
         meta = 0;
      }

      if (itemstack.stackSize <= 0) {
         return false;
      } else if (blockY == world.getHeightBlocks() - 1 && Blocks.OVERLAY_PEBBLES.getMaterial().isSolid()) {
         return false;
      } else {
         if (id == Blocks.OVERLAY_PEBBLES.id() && side == Side.TOP) {
            int newMeta = meta + 1;
            if (!world.isBlockOpaqueCube(blockX, blockY - 1, blockZ)) {
               return false;
            }

            if (newMeta < 3) {
               world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, Blocks.OVERLAY_PEBBLES.id(), newMeta);
               world.playBlockSoundEffect(player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, Blocks.OVERLAY_PEBBLES, EnumBlockSoundEffectType.PLACE);
               itemstack.consumeItem(player);
               return true;
            }
         }

         if (id != 0) {
            blockX += side.getOffsetX();
            blockY += side.getOffsetY();
            blockZ += side.getOffsetZ();
            id = world.getBlockId(blockX, blockY, blockZ);
            meta = world.getBlockMetadata(blockX, blockY, blockZ);
         }

         if (id == Blocks.OVERLAY_PEBBLES.id()) {
            int newMetax = meta + 1;
            AABB bbBox = AABB.getTemporaryBB(blockX, blockY, blockZ, blockX + 1.0F, blockY + 2 * (newMetax + 1) / 16.0F, blockZ + 1.0F);
            if (!world.checkIfAABBIsClear(bbBox) || !world.isBlockOpaqueCube(blockX, blockY - 1, blockZ)) {
               return false;
            }

            if (newMetax < 3) {
               world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, Blocks.OVERLAY_PEBBLES.id(), newMetax);
               world.playBlockSoundEffect(player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, Blocks.OVERLAY_PEBBLES, EnumBlockSoundEffectType.PLACE);
               itemstack.consumeItem(player);
               return true;
            }
         }

         if (world.canBlockBePlacedAt(Blocks.OVERLAY_PEBBLES.id(), blockX, blockY, blockZ, false, side)
            && world.isBlockOpaqueCube(blockX, blockY - 1, blockZ)
            && world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, Blocks.OVERLAY_PEBBLES.id(), 0)) {
            Blocks.OVERLAY_PEBBLES.onBlockPlacedByMob(world, blockX, blockY, blockZ, side, player, xPlaced, yPlaced);
            world.playBlockSoundEffect(player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, Blocks.OVERLAY_PEBBLES, EnumBlockSoundEffectType.PLACE);
            itemstack.consumeItem(player);
            return true;
         } else {
            return false;
         }
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
      ProjectilePebble projectilePebble = new ProjectilePebble(world, blockX + offX, blockY + offY, blockZ + offZ);
      projectilePebble.setHeading(
         direction.getOffsetX() * 0.6, direction.getOffsetY() == 0 ? 0.1 : direction.getOffsetY() * 0.6, direction.getOffsetZ() * 0.6F, 1.1F, 6.0F
      );
      world.entityJoinedWorld(projectilePebble);
      itemStack.stackSize--;
   }

   @Override
   public void onDispensed(ItemStack itemStack, World world, double x, double y, double z, int xOffset, int yOffset, int zOffset, Random random) {
      ProjectilePebble projectilePebble = new ProjectilePebble(world, x, y, z);
      projectilePebble.setHeading(xOffset, yOffset + 0.1, zOffset, 1.1F, 6.0F);
      world.entityJoinedWorld(projectilePebble);
   }
}
