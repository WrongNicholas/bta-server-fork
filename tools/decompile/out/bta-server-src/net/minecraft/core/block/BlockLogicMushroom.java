package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicMushroom extends BlockLogicFlower {
   public BlockLogicMushroom(Block<?> block) {
      super(block);
      float f = 0.2F;
      this.setBlockBounds(0.5F - f, 0.0, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
      block.setTicking(true);
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (rand.nextInt(25) == 0) {
         int x1 = x + rand.nextInt(3) - 1;
         int randY = y + rand.nextInt(2) - rand.nextInt(2);
         int z1 = z + rand.nextInt(3) - 1;
         if (world.isAirBlock(x1, randY, z1) && this.canBlockStay(world, x1, randY, z1)) {
            int x2 = x + rand.nextInt(3) - 1;
            int z2 = z + rand.nextInt(3) - 1;
            if (world.isAirBlock(x2, randY, z2) && this.canBlockStay(world, x2, randY, z2)) {
               world.setBlockWithNotify(x2, randY, z2, this.block.id());
            }
         }
      }
   }

   @Override
   protected boolean mayPlaceOn(int blockId) {
      return Blocks.solid[blockId];
   }

   @Override
   public boolean canBlockStay(World world, int x, int y, int z) {
      return y >= 0 && y < world.getHeightBlocks() ? world.getFullBlockLightValue(x, y, z) < 13 && this.mayPlaceOn(world.getBlockId(x, y - 1, z)) : false;
   }

   @Override
   public boolean onBonemealUsed(
      ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      Random rand = world.rand;
      if (!world.isClientSide && this.canBeBonemealed) {
         if (player == null || player.getGamemode().consumeBlocks()) {
            itemstack.stackSize--;
         }

         label38:
         for (int j1 = 0; j1 < 32; j1++) {
            int _x = blockX;
            int _y = blockY;
            int _z = blockZ;

            for (int j2 = 0; j2 < j1 / 16; j2++) {
               _x += rand.nextInt(3) - 1;
               _y += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
               _z += rand.nextInt(3) - 1;
               if (world.getFullBlockLightValue(_x, _y, _z) >= 13 || !this.mayPlaceOn(world.getBlockId(_x, _y - 1, _z))) {
                  continue label38;
               }
            }

            if (world.getBlockId(_x, _y, _z) == 0 && rand.nextFloat() > 0.85) {
               world.setBlockWithNotify(_x, _y, _z, this.block.id());
            }
         }

         return true;
      } else {
         return this.canBeBonemealed;
      }
   }
}
