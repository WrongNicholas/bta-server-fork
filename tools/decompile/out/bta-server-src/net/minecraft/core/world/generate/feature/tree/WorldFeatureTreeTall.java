package net.minecraft.core.world.generate.feature.tree;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class WorldFeatureTreeTall extends WorldFeature {
   protected int leavesID;
   protected int logID;

   @MethodParametersAnnotation(names = {"leavesID", "logID"})
   public WorldFeatureTreeTall(int leavesID, int logID) {
      this.leavesID = leavesID;
      this.logID = logID;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      int trunkHeight = random.nextInt(5) + 10;
      boolean flag = true;
      if (y >= 1 && y + trunkHeight + 1 <= world.getHeightBlocks()) {
         for (int y1 = y; y1 <= y + 1 + trunkHeight; y1++) {
            byte byte0 = 1;
            if (y1 == y) {
               byte0 = 0;
            }

            if (y1 >= y + 1 + trunkHeight - 2) {
               byte0 = 2;
            }

            for (int i2 = x - byte0; i2 <= x + byte0 && flag; i2++) {
               for (int l2 = z - byte0; l2 <= z + byte0 && flag; l2++) {
                  if (y1 >= 0 && y1 < world.getHeightBlocks()) {
                     int j3 = world.getBlockId(i2, y1, l2);
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
            if (Blocks.hasTag(idBelow, BlockTags.GROWS_TREES) && y < world.getHeightBlocks() - trunkHeight - 1) {
               WorldFeatureTree.onTreeGrown(world, x, y, z);

               for (int k1 = y - 3 + trunkHeight; k1 <= y + trunkHeight; k1++) {
                  int j2 = k1 - (y + trunkHeight);
                  int i3 = 1 - j2 / 2;

                  for (int k3 = x - i3; k3 <= x + i3; k3++) {
                     int l3 = k3 - x;

                     for (int i4 = z - i3; i4 <= z + i3; i4++) {
                        int j4 = i4 - z;
                        if ((Math.abs(l3) != i3 || Math.abs(j4) != i3 || random.nextInt(2) != 0 && j2 != 0)
                           && WorldFeatureTree.canLeavesReplace(world, k3, k1, i4)) {
                           world.setBlockWithNotify(k3, k1, i4, this.leavesID);
                        }
                     }
                  }
               }

               for (int l1 = 0; l1 < trunkHeight; l1++) {
                  int k2 = world.getBlockId(x, y + l1, z);
                  if (k2 == 0 || k2 == this.leavesID) {
                     world.setBlockWithNotify(x, y + l1, z, this.logID);
                  }
               }

               if (trunkHeight > 4) {
                  int branches = random.nextInt(2) + 1;

                  for (int i = 0; i < branches; i++) {
                     int yOffset = random.nextInt(trunkHeight / (i + 1));
                     if (yOffset >= trunkHeight - 5) {
                        yOffset = trunkHeight - 5;
                     } else if (yOffset <= 3) {
                        yOffset = 3;
                     }

                     boolean onXAxis = random.nextInt(2) == 0;
                     int xOffset = 0;
                     int zOffset = 0;
                     if (onXAxis) {
                        if (random.nextInt(2) == 1) {
                           xOffset = 1;
                        } else {
                           xOffset = -1;
                        }
                     } else if (random.nextInt(2) == 1) {
                        zOffset = 1;
                     } else {
                        zOffset = -1;
                     }

                     world.setBlockAndMetadataWithNotify(x + xOffset, y + yOffset, z + zOffset, this.logID, onXAxis ? 2 : 1);
                     if (random.nextInt(10) == 0 && world.getBlockId(x + xOffset, y + yOffset + 1, z + zOffset) == 0) {
                        world.setBlockWithNotify(x + xOffset, y + yOffset + 1, z + zOffset, Blocks.MUSHROOM_BROWN.id());
                     }
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
