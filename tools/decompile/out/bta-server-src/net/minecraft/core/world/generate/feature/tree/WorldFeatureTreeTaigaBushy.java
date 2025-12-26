package net.minecraft.core.world.generate.feature.tree;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class WorldFeatureTreeTaigaBushy extends WorldFeature {
   protected int leavesID;
   protected int logID;

   @MethodParametersAnnotation(names = {"leavesID", "logID"})
   public WorldFeatureTreeTaigaBushy(int leavesID, int logID) {
      this.leavesID = leavesID;
      this.logID = logID;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      int heightMod = random.nextInt(2) + 10;
      int l = random.nextInt(4) + heightMod;
      int i1 = 1 + random.nextInt(2);
      int j1 = l - i1;
      int k1 = 2 + random.nextInt(2);
      boolean flag = true;
      if (y >= 1 && y + l + 1 <= world.getHeightBlocks()) {
         for (int l1 = y; l1 <= y + 1 + l && flag; l1++) {
            int j2;
            if (l1 - y < i1) {
               j2 = 0;
            } else {
               j2 = k1;
            }

            for (int l2 = x - j2; l2 <= x + j2 && flag; l2++) {
               for (int j3 = z - j2; j3 <= z + j2 && flag; j3++) {
                  if (l1 >= 0 && l1 < world.getHeightBlocks()) {
                     int k3 = world.getBlockId(l2, l1, j3);
                     if (k3 != 0 && k3 != this.leavesID) {
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
               int k2 = random.nextInt(2);
               int i3 = 1;
               boolean flag1 = false;

               for (int l3 = 0; l3 <= j1; l3++) {
                  int j4 = y + l - l3;

                  for (int l4 = x - k2; l4 <= x + k2; l4++) {
                     int j5 = l4 - x;

                     for (int k5 = z - k2; k5 <= z + k2; k5++) {
                        int l5 = k5 - z;
                        if ((Math.abs(j5) != k2 || Math.abs(l5) != k2 || k2 <= 0) && WorldFeatureTree.canLeavesReplace(world, l4, j4, k5)) {
                           world.setBlockWithNotify(l4, j4, k5, this.leavesID);
                        }
                     }
                  }

                  if (k2 >= i3) {
                     k2 = flag1 ? 1 : 0;
                     flag1 = true;
                     if (++i3 > k1) {
                        i3 = k1;
                     }
                  } else {
                     k2++;
                  }
               }

               int i4 = random.nextInt(3);

               for (int k4 = 0; k4 < l - i4; k4++) {
                  int i5 = world.getBlockId(x, y + k4, z);
                  if (i5 == 0 || i5 == this.leavesID) {
                     world.setBlockWithNotify(x, y + k4, z, this.logID);
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
