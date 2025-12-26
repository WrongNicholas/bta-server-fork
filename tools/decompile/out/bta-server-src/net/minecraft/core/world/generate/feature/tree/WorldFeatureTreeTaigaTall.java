package net.minecraft.core.world.generate.feature.tree;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class WorldFeatureTreeTaigaTall extends WorldFeature {
   protected int leavesID;
   protected int logID;

   @MethodParametersAnnotation(names = {"leavesID", "logID"})
   public WorldFeatureTreeTaigaTall(int leavesID, int logID) {
      this.leavesID = leavesID;
      this.logID = logID;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      int l = random.nextInt(5) + 7;
      int i1 = l - random.nextInt(2) - 3;
      int j1 = l - i1;
      int k1 = 1 + random.nextInt(j1 + 1);
      boolean flag = true;
      if (y >= 1 && y + l + 1 <= world.getHeightBlocks()) {
         for (int l1 = y; l1 <= y + 1 + l && flag; l1++) {
            int j2 = 1;
            if (l1 - y < i1) {
               j2 = 0;
            } else {
               j2 = k1;
            }

            for (int l2 = x - j2; l2 <= x + j2 && flag; l2++) {
               for (int k3 = z - j2; k3 <= z + j2 && flag; k3++) {
                  if (l1 >= 0 && l1 < world.getHeightBlocks()) {
                     int j4 = world.getBlockId(l2, l1, k3);
                     if (j4 != 0 && j4 != this.leavesID) {
                        flag = false;
                     }
                  } else {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         } else {
            int idBelow = world.getBlockId(x, y - 1, z);
            if (Blocks.hasTag(idBelow, BlockTags.GROWS_TREES) && y < world.getHeightBlocks() - l - 1) {
               WorldFeatureTree.onTreeGrown(world, x, y, z);
               int k2 = 0;

               for (int i3 = y + l; i3 >= y + i1; i3--) {
                  for (int l3 = x - k2; l3 <= x + k2; l3++) {
                     int k4 = l3 - x;

                     for (int l4 = z - k2; l4 <= z + k2; l4++) {
                        int i5 = l4 - z;
                        if ((Math.abs(k4) != k2 || Math.abs(i5) != k2 || k2 <= 0) && WorldFeatureTree.canLeavesReplace(world, l3, i3, l4)) {
                           world.setBlockWithNotify(l3, i3, l4, this.leavesID);
                        }
                     }
                  }

                  if (k2 >= 1 && i3 == y + i1 + 1) {
                     k2--;
                  } else if (k2 < k1) {
                     k2++;
                  }
               }

               for (int j3 = 0; j3 < l - 1; j3++) {
                  int i4 = world.getBlockId(x, y + j3, z);
                  if (i4 == 0 || i4 == this.leavesID) {
                     world.setBlockWithNotify(x, y + j3, z, this.logID);
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
}
