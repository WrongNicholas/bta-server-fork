package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicFluidStill extends BlockLogicFluid {
   public final Block<?> blockFlowing;

   public BlockLogicFluidStill(Block<?> block, Material material, Block<?> blockFlowing) {
      super(block, material);
      block.setTicking(material == Material.lava);
      this.blockFlowing = blockFlowing;
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      super.onNeighborBlockChange(world, x, y, z, blockId);
      if (blockId != Side.TOP.getId()) {
         if (world.getBlockId(x, y, z) == this.block.id()) {
            this.setFlowing(world, x, y, z);
         }
      }
   }

   private void setFlowing(World world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      world.noNeighborUpdate = true;
      world.setBlockAndMetadata(x, y, z, this.blockFlowing.id(), meta);
      world.markBlocksDirty(x, y, z, x, y, z);
      world.scheduleBlockUpdate(x, y, z, this.blockFlowing.id(), this.tickDelay());
      world.noNeighborUpdate = false;
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (this.material == Material.lava) {
         int l = rand.nextInt(3);

         for (int i1 = 0; i1 < l; i1++) {
            x += rand.nextInt(3) - 1;
            y++;
            z += rand.nextInt(3) - 1;
            int blockId = world.getBlockId(x, y, z);
            if (blockId == 0) {
               if (this.isFlammable(world, x - 1, y, z)
                  || this.isFlammable(world, x + 1, y, z)
                  || this.isFlammable(world, x, y, z - 1)
                  || this.isFlammable(world, x, y, z + 1)
                  || this.isFlammable(world, x, y - 1, z)
                  || this.isFlammable(world, x, y + 1, z)) {
                  world.setBlockWithNotify(x, y, z, Blocks.FIRE.id());
                  return;
               }
            } else if (Blocks.blocksList[blockId].getMaterial().blocksMotion()) {
               return;
            }
         }
      }
   }

   private boolean isFlammable(World world, int x, int y, int z) {
      return BlockLogicFire.canBurn(world, x, y, z);
   }
}
