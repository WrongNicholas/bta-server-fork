package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;

public class BlockLogicSponge extends BlockLogic {
   final boolean isWet;

   public BlockLogicSponge(Block<?> block, boolean isWet) {
      super(block, Material.sponge);
      this.isWet = isWet;
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      super.onBlockPlacedByWorld(world, x, y, z);
      byte byte0 = 2;
      if ((world.dimension == Dimension.NETHER || this.inHot(world, x, y, z)) && this.isWet) {
         world.setBlockWithNotify(x, y, z, Blocks.SPONGE_DRY.id());
         world.playSoundEffect(
            null, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.5, z + 0.5, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F
         );

         for (int l = 0; l < 8; l++) {
            world.spawnParticle("largesmoke", x + Math.random(), y + Math.random(), z + Math.random(), 0.0, 0.0, 0.0, 0);
         }
      }

      if (!this.isWet && this.inWater(world, x, y, z)) {
         for (int l = x - byte0; l <= x + byte0; l++) {
            for (int i1 = y - byte0; i1 <= y + byte0; i1++) {
               for (int j1 = z - byte0; j1 <= z + byte0; j1++) {
                  if (Blocks.hasTag(world.getBlockId(l, i1, j1), BlockTags.IS_WATER)) {
                     world.setBlockWithNotify(l, i1, j1, 0);
                  }
               }
            }
         }

         world.setBlockWithNotify(x, y, z, Blocks.SPONGE_WET.id());
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

   public boolean inHot(World world, int x, int y, int z) {
      Block<?> b;
      if (!Blocks.hasTag(b = world.getBlock(x + 1, y, z), BlockTags.IS_LAVA) && b != Blocks.COBBLE_NETHERRACK_IGNEOUS) {
         if (!Blocks.hasTag(b = world.getBlock(x - 1, y, z), BlockTags.IS_LAVA) && b != Blocks.COBBLE_NETHERRACK_IGNEOUS) {
            if (!Blocks.hasTag(b = world.getBlock(x, y + 1, z), BlockTags.IS_LAVA) && b != Blocks.COBBLE_NETHERRACK_IGNEOUS) {
               if (!Blocks.hasTag(b = world.getBlock(x, y - 1, z), BlockTags.IS_LAVA) && b != Blocks.COBBLE_NETHERRACK_IGNEOUS) {
                  Block var10;
                  return !Blocks.hasTag(b = world.getBlock(x, y, z + 1), BlockTags.IS_LAVA) && b != Blocks.COBBLE_NETHERRACK_IGNEOUS
                     ? Blocks.hasTag(var10 = world.getBlock(x, y, z - 1), BlockTags.IS_LAVA) || var10 == Blocks.COBBLE_NETHERRACK_IGNEOUS
                     : true;
               } else {
                  return true;
               }
            } else {
               return true;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      if (!this.isWet) {
         byte byte0 = 2;

         for (int l = x - byte0; l <= x + byte0; l++) {
            for (int i1 = y - byte0; i1 <= y + byte0; i1++) {
               for (int j1 = z - byte0; j1 <= z + byte0; j1++) {
                  world.notifyBlocksOfNeighborChange(l, i1, j1, world.getBlockId(l, i1, j1));
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
         float lOff = 0.1F;
         switch (side) {
            case BOTTOM:
               world.spawnParticle("dripWater", x + off1, y - lOff, z + off2, 0.0, 0.0, 0.0, 0);
               break;
            case NORTH:
               world.spawnParticle("dripWater", x + off1, y + off2, z - lOff, 0.0, 0.0, 0.0, 0);
               break;
            case SOUTH:
               world.spawnParticle("dripWater", x + off1, y + off2, z + 1 + lOff, 0.0, 0.0, 0.0, 0);
               break;
            case WEST:
               world.spawnParticle("dripWater", x - lOff, y + off1, z + off2, 0.0, 0.0, 0.0, 0);
               break;
            case EAST:
               world.spawnParticle("dripWater", x + 1 + lOff, y + off1, z + off2, 0.0, 0.0, 0.0, 0);
         }
      }
   }
}
