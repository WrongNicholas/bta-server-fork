package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;

public class BlockLogicPumice extends BlockLogic {
   final boolean isWet;

   public BlockLogicPumice(Block<?> block, boolean isWet) {
      super(block, Material.stone);
      this.isWet = isWet;
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      super.onBlockPlacedByWorld(world, x, y, z);
      byte radius = 2;
      if (this.isWet && (this.inWater(world, x, y, z) || world.dimension.id == Dimension.PARADISE.id)) {
         world.setBlockWithNotify(x, y, z, Blocks.PUMICE_DRY.id());
         world.playSoundEffect(
            null, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.5, z + 0.5, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F
         );

         for (int l = 0; l < 8; l++) {
            world.spawnParticle("largesmoke", x + Math.random(), y + Math.random(), z + Math.random(), 0.0, 0.0, 0.0, 0);
         }
      }

      if (!this.isWet && this.inLava(world, x, y, z)) {
         for (int _x = x - radius; _x <= x + radius; _x++) {
            for (int _y = y - radius; _y <= y + radius; _y++) {
               for (int _z = z - radius; _z <= z + radius; _z++) {
                  if (Blocks.hasTag(world.getBlockId(_x, _y, _z), BlockTags.IS_LAVA)) {
                     world.setBlockWithNotify(_x, _y, _z, 0);
                  }
               }
            }
         }

         world.setBlockWithNotify(x, y, z, Blocks.PUMICE_WET.id());
      }
   }

   public boolean inLava(World world, int x, int y, int z) {
      if (Blocks.hasTag(world.getBlockId(x + 1, y, z), BlockTags.IS_LAVA)) {
         return true;
      } else if (Blocks.hasTag(world.getBlockId(x - 1, y, z), BlockTags.IS_LAVA)) {
         return true;
      } else if (Blocks.hasTag(world.getBlockId(x, y + 1, z), BlockTags.IS_LAVA)) {
         return true;
      } else if (Blocks.hasTag(world.getBlockId(x, y - 1, z), BlockTags.IS_LAVA)) {
         return true;
      } else {
         return Blocks.hasTag(world.getBlockId(x, y, z + 1), BlockTags.IS_LAVA) ? true : Blocks.hasTag(world.getBlockId(x, y, z - 1), BlockTags.IS_LAVA);
      }
   }

   public boolean inWater(World world, int x, int y, int z) {
      if (Blocks.hasTag(world.getBlockId(x + 1, y, z), BlockTags.IS_WATER)) {
         return true;
      } else if (Blocks.hasTag(world.getBlockId(x - 1, y, z), BlockTags.IS_WATER)) {
         return true;
      } else if (Blocks.hasTag(world.getBlockId(x, y + 1, z), BlockTags.IS_WATER)) {
         return true;
      } else if (Blocks.hasTag(world.getBlockId(x, y - 1, z), BlockTags.IS_WATER)) {
         return true;
      } else {
         return Blocks.hasTag(world.getBlockId(x, y, z + 1), BlockTags.IS_WATER) ? true : Blocks.hasTag(world.getBlockId(x, y, z - 1), BlockTags.IS_WATER);
      }
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      if (!this.isWet) {
         byte radius = 2;

         for (int _x = x - radius; _x <= x + radius; _x++) {
            for (int _y = y - radius; _y <= y + radius; _y++) {
               for (int _z = z - radius; _z <= z + radius; _z++) {
                  world.notifyBlocksOfNeighborChange(_x, _y, _z, world.getBlockId(_x, _y, _z));
               }
            }
         }
      }
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (this.isWet) {
         Side side = Side.sides[rand.nextInt(6)];
         if (side == Side.TOP || world.isBlockOpaqueCube(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ())) {
            return;
         }

         float off1 = rand.nextFloat() * 0.9F + 0.05F;
         float off2 = rand.nextFloat() * 0.9F + 0.05F;
         switch (side) {
            case BOTTOM:
               world.spawnParticle("dripLava", x + off1, y, z + off2, 0.0, 0.0, 0.0, 0);
               break;
            case NORTH:
               world.spawnParticle("dripLava", x + off1, y + off2, z, 0.0, 0.0, 0.0, 0);
               break;
            case SOUTH:
               world.spawnParticle("dripLava", x + off1, y + off2, z + 1, 0.0, 0.0, 0.0, 0);
               break;
            case WEST:
               world.spawnParticle("dripLava", x, y + off1, z + off2, 0.0, 0.0, 0.0, 0);
               break;
            case EAST:
               world.spawnParticle("dripLava", x + 1, y + off1, z + off2, 0.0, 0.0, 0.0, 0);
         }
      }
   }
}
