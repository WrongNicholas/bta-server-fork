package net.minecraft.core.world.generate.feature.tree;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class WorldFeatureTreeShrub extends WorldFeature {
   protected int leavesID;
   protected int logID;

   @MethodParametersAnnotation(names = {"leavesID", "logID"})
   public WorldFeatureTreeShrub(int leavesID, int logID) {
      this.leavesID = leavesID;
      this.logID = logID;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      int maxHeight = 1;
      boolean flag = true;
      if (y >= 1 && y + maxHeight + 1 <= world.getHeightBlocks()) {
         for (int i1 = y; i1 <= y + 1 + maxHeight; i1++) {
            byte byte0 = 2;

            for (int i2 = x - byte0; i2 <= x + byte0 && flag; i2++) {
               for (int l2 = z - byte0; l2 <= z + byte0 && flag; l2++) {
                  if (i1 >= 0 && i1 < world.getHeightBlocks()) {
                     int j3 = world.getBlockId(i2, i1, l2);
                     if (j3 != 0 && j3 != this.leavesID) {
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
            if (Blocks.hasTag(idBelow, BlockTags.GROWS_TREES) && y < world.getHeightBlocks() - maxHeight - 1) {
               WorldFeatureTree.onTreeGrown(world, x, y, z);

               for (int k1 = y - 2 + maxHeight; k1 <= y + maxHeight; k1++) {
                  int j2 = k1 - (y + maxHeight + 1);
                  int i3 = 1 - j2 / 2;

                  for (int k3 = x - i3; k3 <= x + i3; k3++) {
                     int l3 = k3;

                     for (int i4 = z - i3; i4 <= z + i3; i4++) {
                        int j4 = i4 - z;
                        if ((Math.abs(l3) != i3 || Math.abs(j4) != i3 || random.nextInt(2) != 0)
                           && WorldFeatureTree.canLeavesReplace(world, k3, k1, i4)
                           && (k3 != x - i3 && k3 != x + i3 || i4 != z - i3 && i4 != z + i3)) {
                           world.setBlockWithNotify(k3, k1, i4, this.leavesID);
                        }
                     }
                  }
               }

               int k2 = world.getBlockId(x, y, z);
               if (k2 == 0 || k2 == this.leavesID) {
                  world.setBlock(x, y, z, this.logID);
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
