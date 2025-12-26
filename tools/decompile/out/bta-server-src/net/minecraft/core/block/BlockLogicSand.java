package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.EntityFallingBlock;
import net.minecraft.core.world.World;

public class BlockLogicSand extends BlockLogic {
   public static boolean fallInstantly = false;

   public BlockLogicSand(Block<?> block) {
      super(block, Material.sand);
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay());
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay());
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      this.tryToFall(world, x, y, z);
   }

   private void tryToFall(World world, int x, int y, int z) {
      if (canFallBelow(world, x, y - 1, z) && y >= 0) {
         byte byte0 = 32;
         if (!fallInstantly && world.areBlocksLoaded(x - byte0, y - byte0, z - byte0, x + byte0, y + byte0, z + byte0)) {
            EntityFallingBlock entityFallingBlock = new EntityFallingBlock(world, x + 0.5, y + 0.5, z + 0.5, this.block.id(), 0, null);
            world.entityJoinedWorld(entityFallingBlock);
         } else {
            world.setBlockWithNotify(x, y, z, 0);

            while (canFallBelow(world, x, y - 1, z) && y > 0) {
               y--;
            }

            if (y > 0) {
               world.setBlockWithNotify(x, y, z, this.block.id());
            }
         }
      }
   }

   @Override
   public int tickDelay() {
      return 3;
   }

   public static boolean canFallBelow(World world, int x, int y, int z) {
      Block<?> block = world.getBlock(x, y, z);
      return block == null || block.hasTag(BlockTags.PLACE_OVERWRITES);
   }
}
