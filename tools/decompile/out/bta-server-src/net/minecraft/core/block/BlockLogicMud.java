package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.EntityFallingBlock;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;

public class BlockLogicMud extends BlockLogic {
   private final boolean isHardened;

   public BlockLogicMud(Block<?> block, Material material, boolean isHardened) {
      super(block, material);
      this.isHardened = isHardened;
   }

   @Override
   public int tickDelay() {
      return 5;
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay());
   }

   private boolean isWaterNearby(World world, int x, int y, int z, int range) {
      for (int x1 = x - range; x1 <= x + range; x1++) {
         for (int y1 = y - range; y1 <= y + range; y1++) {
            for (int z1 = z - range; z1 <= z + range; z1++) {
               if (Blocks.hasTag(world.getBlockId(x1, y1, z1), BlockTags.IS_WATER)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean isLavaNearby(World world, int x, int y, int z) {
      for (int x1 = x - 1; x1 <= x + 1; x1++) {
         for (int y1 = y - 1; y1 <= y + 1; y1++) {
            for (int z1 = z - 1; z1 <= z + 1; z1++) {
               if (Blocks.hasTag(world.getBlockId(x1, y1, z1), BlockTags.IS_LAVA)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean canBecomeWet(World world, int x, int y, int z) {
      if (this.isWaterNearby(world, x, y, z, 1)) {
         return true;
      } else {
         if (this.isWaterNearby(world, x, y, z, 3)) {
            for (int x1 = x - 1; x1 <= x + 1; x1++) {
               for (int y1 = y - 1; y1 <= y + 1; y1++) {
                  for (int z1 = z - 1; z1 <= z + 1; z1++) {
                     int bID = world.getBlockId(x1, y1, z1);
                     if (bID == Blocks.MUD.id() || bID == Blocks.FARMLAND_DIRT.id() && BlockLogicFarmland.isWet(world.getBlockMetadata(x1, y1, z1))) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay());
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(this, 1, meta)};
   }

   private void tryToFall(World world, int x, int y, int z) {
      if (world.getBlockId(x, y, z) != Blocks.MUD_BAKED.id()) {
         if (canFallBelow(world, x, y - 1, z) && y >= 0) {
            byte byte0 = 32;
            if (!world.areBlocksLoaded(x - byte0, y - byte0, z - byte0, x + byte0, y + byte0, z + byte0)) {
               world.setBlockWithNotify(x, y, z, 0);

               while (canFallBelow(world, x, y - 1, z) && y > 0) {
                  y--;
               }

               if (y > 0) {
                  world.setBlockWithNotify(x, y, z, this.block.id());
               }
            } else {
               EntityFallingBlock entityFallingBlock = new EntityFallingBlock(world, x + 0.5, y + 0.5, z + 0.5, this.block.id(), 0, null);
               world.entityJoinedWorld(entityFallingBlock);
            }
         }
      }
   }

   public static boolean canFallBelow(World world, int x, int y, int z) {
      int blockId = world.getBlockId(x, y, z);
      return blockId == 0 || blockId == Blocks.FIRE.id() || Blocks.hasTag(blockId, BlockTags.IS_WATER) || Blocks.hasTag(blockId, BlockTags.IS_LAVA);
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (this.isHardened && !this.isLavaNearby(world, x, y, z) && this.canBecomeWet(world, x, y, z)) {
         world.setBlock(x, y, z, Blocks.MUD.id());
         world.notifyBlockChange(x, y, z, Blocks.MUD.id());
      } else if (!this.isHardened && (this.isLavaNearby(world, x, y, z) || world.dimension == Dimension.NETHER)) {
         world.playSoundEffect(
            null, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.5, z + 0.5, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F
         );

         for (int l = 0; l < 8; l++) {
            world.spawnParticle("largesmoke", x + Math.random(), y + Math.random(), z + Math.random(), 0.0, 0.0, 0.0, 0);
         }

         world.setBlock(x, y, z, Blocks.MUD_BAKED.id());
         world.notifyBlockChange(x, y, z, Blocks.MUD_BAKED.id());
      } else {
         this.tryToFall(world, x, y, z);
      }
   }
}
