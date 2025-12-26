package net.minecraft.core.world.generate.feature.tree;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class WorldFeatureTree extends WorldFeature {
   protected int leavesID;
   protected int logID;
   protected int heightMod;

   @MethodParametersAnnotation(names = {"leavesID", "logID", "heightMod"})
   public WorldFeatureTree(int leavesID, int logID, int heightMod) {
      this.leavesID = leavesID;
      this.logID = logID;
      this.heightMod = heightMod;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      int treeHeight = random.nextInt(3) + this.heightMod;
      boolean canSpawn = true;
      if (y >= 1 && y + treeHeight + 1 <= world.getHeightBlocks()) {
         for (int iy = y; iy <= y + 1 + treeHeight; iy++) {
            byte treeRadius = 1;
            if (iy == y) {
               treeRadius = 0;
            }

            if (iy >= y + 1 + treeHeight - 2) {
               treeRadius = 2;
            }

            for (int ix = x - treeRadius; ix <= x + treeRadius && canSpawn; ix++) {
               for (int iz = z - treeRadius; iz <= z + treeRadius && canSpawn; iz++) {
                  if (iy >= 0 && iy < world.getHeightBlocks()) {
                     int blockId = world.getBlockId(ix, iy, iz);
                     if (blockId != 0 && blockId != this.leavesID) {
                        canSpawn = false;
                     }
                  } else {
                     canSpawn = false;
                  }
               }
            }
         }

         if (!canSpawn) {
            return false;
         } else {
            int idBelow = world.getBlockId(x, y - 1, z);
            if (Blocks.hasTag(idBelow, BlockTags.GROWS_TREES) && y < world.getHeightBlocks() - treeHeight - 1) {
               onTreeGrown(world, x, y, z);

               for (int iy = y - 3 + treeHeight; iy <= y + treeHeight; iy++) {
                  int j2 = iy - (y + treeHeight);
                  int i3 = 1 - j2 / 2;

                  for (int ix = x - i3; ix <= x + i3; ix++) {
                     int l3 = ix - x;

                     for (int izx = z - i3; izx <= z + i3; izx++) {
                        int j4 = izx - z;
                        if ((Math.abs(l3) != i3 || Math.abs(j4) != i3 || random.nextInt(2) != 0 && j2 != 0) && canLeavesReplace(world, ix, iy, izx)) {
                           this.placeLeaves(world, ix, iy, izx, random);
                        }
                     }
                  }
               }

               for (int l1 = 0; l1 < treeHeight; l1++) {
                  int id = world.getBlockId(x, y + l1, z);
                  if (id == 0 || this.isLeaf(id)) {
                     world.setBlockWithNotify(x, y + l1, z, this.logID);
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public void placeLeaves(World world, int x, int y, int z, Random rand) {
      world.setBlockWithNotify(x, y, z, this.leavesID);
   }

   public boolean isLeaf(int id) {
      return id == this.leavesID;
   }

   public static void onTreeGrown(World world, int x, int y, int z) {
      Block<?> dirt = getDirtForGrass(world.getBlockId(x, y - 1, z));
      if (dirt != null) {
         world.setBlockWithNotify(x, y - 1, z, dirt.id());
      }
   }

   public static Block<?> getDirtForGrass(int id) {
      if (id == Blocks.GRASS.id() || id == Blocks.GRASS_RETRO.id()) {
         return Blocks.DIRT;
      } else {
         return id == Blocks.GRASS_SCORCHED.id() ? Blocks.DIRT_SCORCHED : null;
      }
   }

   public static boolean canLeavesReplace(World world, int x, int y, int z) {
      Block<?> b = world.getBlock(x, y, z);
      return b == null || b.hasTag(BlockTags.PLACE_OVERWRITES);
   }
}
