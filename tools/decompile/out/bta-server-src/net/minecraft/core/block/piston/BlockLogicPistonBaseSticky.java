package net.minecraft.core.block.piston;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;

public class BlockLogicPistonBaseSticky extends BlockLogicPistonBase {
   public BlockLogicPistonBaseSticky(Block<?> block, int maxPushedBlocks) {
      super(block, maxPushedBlocks);
   }

   @Override
   public void retractEvent(World world, int x, int y, int z, int data, Direction direction) {
      TileEntity tileEntity = world.getTileEntity(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ());
      if (tileEntity instanceof TileEntityMovingPistonBlock) {
         TileEntityMovingPistonBlock moving = (TileEntityMovingPistonBlock)tileEntity;
         if (!moving.isExtending()) {
            ((TileEntityMovingPistonBlock)tileEntity).finalTick();
         } else {
            world.setBlock(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ(), 0);
         }
      }

      world.setBlockAndMetadata(x, y, z, Blocks.PISTON_MOVING.id(), direction.getId());
      world.replaceBlockTileEntity(x, y, z, BlockLogicPistonMoving.createTileEntity(this.block.id(), direction.getId(), null, direction, false, true));
      int x1 = x + direction.getOffsetX() * 2;
      int y1 = y + direction.getOffsetY() * 2;
      int z1 = z + direction.getOffsetZ() * 2;
      int retractId = world.getBlockId(x1, y1, z1);
      int retactMeta = world.getBlockMetadata(x1, y1, z1);
      TileEntity retractEntity = world.getTileEntity(x1, y1, z1);
      boolean pulledMoving = false;
      if (retractId == Blocks.PISTON_MOVING.id()) {
         TileEntity tileEntity1 = world.getTileEntity(x1, y1, z1);
         if (tileEntity1 instanceof TileEntityMovingPistonBlock) {
            TileEntityMovingPistonBlock movingPistonBlock = (TileEntityMovingPistonBlock)tileEntity1;
            if (movingPistonBlock.getDirection() == direction && movingPistonBlock.isExtending()) {
               movingPistonBlock.finalTick();
               retractId = movingPistonBlock.getMovedId();
               retactMeta = movingPistonBlock.getMovedData();
               retractEntity = movingPistonBlock.getMovedEntity();
               pulledMoving = true;
            }
         }
      }

      Block<?> retractedBlock = Blocks.getBlock(retractId);
      if (!pulledMoving
         && retractedBlock != null
         && this.isPushable(retractId, world, x1, y1, z1, false)
         && (retractedBlock.getPistonPushReaction(world, x1, y1, z1) == 0 || retractedBlock.getLogic() instanceof BlockLogicPistonBase)) {
         if (!(retractEntity instanceof TileEntityMovingPistonBlock)) {
            world.removeBlockTileEntity(x1, y1, z1);
         }

         world.setBlock(x1, y1, z1, 0);
         x += direction.getOffsetX();
         y += direction.getOffsetY();
         z += direction.getOffsetZ();
         world.setBlockAndMetadata(x, y, z, Blocks.PISTON_MOVING.id(), retactMeta);
         world.replaceBlockTileEntity(x, y, z, BlockLogicPistonMoving.createTileEntity(retractId, retactMeta, retractEntity, direction, false, false));
         world.notifyBlockChange(x1, y1, z1, 0);
      } else if (!pulledMoving) {
         world.setBlockWithNotify(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ(), 0);
      }

      world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.5, z + 0.5, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
   }

   @Override
   public void createPistonHeadAt(World world, int x, int y, int z, int data, Direction direction) {
      world.setBlockAndMetadata(x, y, z, Blocks.PISTON_MOVING.id(), BlockLogicPistonHead.setPistonType(1, direction.getId()));
      world.replaceBlockTileEntity(
         x,
         y,
         z,
         BlockLogicPistonMoving.createTileEntity(
            Blocks.PISTON_HEAD.id(), BlockLogicPistonHead.setPistonType(1, direction.getId()), null, direction, true, false
         )
      );
   }
}
