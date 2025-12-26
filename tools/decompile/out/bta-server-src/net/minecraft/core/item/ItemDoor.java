package net.minecraft.core.item;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class ItemDoor extends Item {
   protected final Block<?> doorBlockBottom;
   protected final Block<?> doorBlockTop;

   public ItemDoor(String name, String namespaceId, int id, Block<?> doorBlockBottom, Block<?> doorBlockTop) {
      super(name, namespaceId, id);
      this.maxStackSize = 64;
      this.doorBlockBottom = doorBlockBottom;
      this.doorBlockTop = doorBlockTop;
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (!world.canPlaceInsideBlock(blockX, blockY, blockZ)) {
         blockX += side.getOffsetX();
         blockY += side.getOffsetY();
         blockZ += side.getOffsetZ();
      }

      if (!this.doorBlockBottom.canPlaceBlockAt(world, blockX, blockY, blockZ)) {
         return false;
      } else {
         Direction dir = player.getHorizontalPlacementDirection(side).rotate(3);
         int meta = dir.getHorizontalIndex();
         int xOffset = -dir.getOffsetX();
         int zOffset = -dir.getOffsetZ();
         int isSolidBlockLeft = (world.isBlockNormalCube(blockX - xOffset, blockY, blockZ - zOffset) ? 1 : 0)
            + (world.isBlockNormalCube(blockX - xOffset, blockY + 1, blockZ - zOffset) ? 1 : 0);
         int isSolidBlockRight = (world.isBlockNormalCube(blockX + xOffset, blockY, blockZ + zOffset) ? 1 : 0)
            + (world.isBlockNormalCube(blockX + xOffset, blockY + 1, blockZ + zOffset) ? 1 : 0);
         boolean isDoorLeft = world.getBlockId(blockX - xOffset, blockY, blockZ - zOffset) == this.doorBlockBottom.id()
            || world.getBlockId(blockX - xOffset, blockY + 1, blockZ - zOffset) == this.doorBlockTop.id();
         boolean isDoorRight = world.getBlockId(blockX + xOffset, blockY, blockZ + zOffset) == this.doorBlockBottom.id()
            || world.getBlockId(blockX + xOffset, blockY + 1, blockZ + zOffset) == this.doorBlockTop.id();
         boolean isMirrored = false;
         if (isDoorLeft && !isDoorRight) {
            isMirrored = true;
         } else if (isSolidBlockRight > isSolidBlockLeft) {
            isMirrored = true;
         }

         if (isMirrored) {
            meta = meta - 1 & 3;
            meta += 4;
            meta |= 8;
         }

         world.noNeighborUpdate = true;
         world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, this.doorBlockBottom.id(), meta);
         world.setBlockAndMetadataWithNotify(blockX, blockY + 1, blockZ, this.doorBlockTop.id(), meta);
         world.noNeighborUpdate = false;
         world.notifyBlocksOfNeighborChange(blockX, blockY, blockZ, this.doorBlockBottom.id());
         world.notifyBlocksOfNeighborChange(blockX, blockY + 1, blockZ, this.doorBlockTop.id());
         world.playBlockSoundEffect(player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, this.doorBlockBottom, EnumBlockSoundEffectType.PLACE);
         this.doorBlockBottom.onBlockPlacedByMob(world, blockX, blockY, blockZ, side, player, xPlaced, yPlaced);
         this.doorBlockTop.onBlockPlacedByMob(world, blockX, blockY + 1, blockZ, side, player, xPlaced, yPlaced);
         itemstack.consumeItem(player);
         return true;
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
      if (!world.canPlaceInsideBlock(blockX, blockY, blockZ)) {
         blockX += direction.getOffsetX();
         blockY += direction.getOffsetY();
         blockZ += direction.getOffsetZ();
      }

      if (this.doorBlockBottom.canPlaceBlockAt(world, blockX, blockY, blockZ)) {
         if (!direction.isHorizontal()) {
            direction = Direction.NORTH;
         }

         Direction dir = direction.rotate(3);
         int meta = dir.getHorizontalIndex();
         int xOffset = -dir.getOffsetX();
         int zOffset = -dir.getOffsetZ();
         int isSolidBlockLeft = (world.isBlockNormalCube(blockX - xOffset, blockY, blockZ - zOffset) ? 1 : 0)
            + (world.isBlockNormalCube(blockX - xOffset, blockY + 1, blockZ - zOffset) ? 1 : 0);
         int isSolidBlockRight = (world.isBlockNormalCube(blockX + xOffset, blockY, blockZ + zOffset) ? 1 : 0)
            + (world.isBlockNormalCube(blockX + xOffset, blockY + 1, blockZ + zOffset) ? 1 : 0);
         boolean isDoorLeft = world.getBlockId(blockX - xOffset, blockY, blockZ - zOffset) == this.doorBlockBottom.id()
            || world.getBlockId(blockX - xOffset, blockY + 1, blockZ - zOffset) == this.doorBlockTop.id();
         boolean isDoorRight = world.getBlockId(blockX + xOffset, blockY, blockZ + zOffset) == this.doorBlockBottom.id()
            || world.getBlockId(blockX + xOffset, blockY + 1, blockZ + zOffset) == this.doorBlockTop.id();
         boolean isMirrored = false;
         if (isDoorLeft && !isDoorRight) {
            isMirrored = true;
         } else if (isSolidBlockRight > isSolidBlockLeft) {
            isMirrored = true;
         }

         if (isMirrored) {
            meta = meta - 1 & 3;
            meta += 4;
            meta |= 8;
         }

         world.noNeighborUpdate = true;
         world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, this.doorBlockBottom.id(), meta);
         world.setBlockAndMetadataWithNotify(blockX, blockY + 1, blockZ, this.doorBlockTop.id(), meta);
         world.noNeighborUpdate = false;
         world.notifyBlocksOfNeighborChange(blockX, blockY, blockZ, this.doorBlockBottom.id());
         world.notifyBlocksOfNeighborChange(blockX, blockY + 1, blockZ, this.doorBlockTop.id());
         world.playBlockSoundEffect(null, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, this.doorBlockBottom, EnumBlockSoundEffectType.PLACE);
         this.doorBlockBottom.onBlockPlacedOnSide(world, blockX, blockY, blockZ, direction.getSide(), 0.5, 0.5);
         this.doorBlockTop.onBlockPlacedOnSide(world, blockX, blockY + 1, blockZ, direction.getSide(), 0.5, 0.5);
         itemStack.consumeItem(null);
      }
   }
}
