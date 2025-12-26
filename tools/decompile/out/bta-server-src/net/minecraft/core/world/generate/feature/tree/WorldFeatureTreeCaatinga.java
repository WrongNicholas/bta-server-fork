package net.minecraft.core.world.generate.feature.tree;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class WorldFeatureTreeCaatinga extends WorldFeature {
   protected int leavesID;
   protected int logID;
   protected int heightMod = 3;
   static final byte[] dimensionLookup = new byte[]{2, 0, 0, 1, 2, 1};

   @MethodParametersAnnotation(names = {"leavesID", "logID"})
   public WorldFeatureTreeCaatinga(int leavesID, int logID) {
      this.leavesID = leavesID;
      this.logID = logID;
   }

   void placeBranch(int[] startPos, int[] endPos, int blockId, World world) {
      int[] dimensions = new int[]{0, 0, 0};
      int dim0 = 0;

      for (int i = 0; i < 3; i++) {
         dimensions[i] = endPos[i] - startPos[i];
         if (Math.abs(dimensions[i]) > Math.abs(dimensions[dim0])) {
            dim0 = i;
         }
      }

      if (dimensions[dim0] != 0) {
         byte dim1 = dimensionLookup[dim0];
         byte dim2 = dimensionLookup[dim0 + 3];
         byte delta;
         if (dimensions[dim0] > 0) {
            delta = 1;
         } else {
            delta = -1;
         }

         double dim1DeltaScale = (double)dimensions[dim1] / dimensions[dim0];
         double dim2DeltaScale = (double)dimensions[dim2] / dimensions[dim0];

         for (int ix = 0; ix != dimensions[dim0] + delta; ix += delta) {
            int[] pos = new int[]{0, 0, 0};
            pos[dim0] = MathHelper.floor((double)startPos[dim0] + ix + 0.5);
            pos[dim1] = MathHelper.floor(startPos[dim1] + ix * dim1DeltaScale + 0.5);
            pos[dim2] = MathHelper.floor(startPos[dim2] + ix * dim2DeltaScale + 0.5);
            world.setBlockWithNotify(pos[0], pos[1], pos[2], blockId);
         }
      }
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      int l = random.nextInt(3) + this.heightMod;
      int noCanopies = 2;
      boolean flag = true;
      if (y >= 1 && y + l + 1 <= world.getHeightBlocks()) {
         for (int i1 = y; i1 <= y + 1 + l; i1++) {
            byte byte0 = 1;
            if (i1 == y) {
               byte0 = 0;
            }

            if (i1 >= y + 1 + l - 2) {
               byte0 = 2;
            }

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
            if (Blocks.hasTag(idBelow, BlockTags.GROWS_TREES) && y < world.getHeightBlocks() - l - 1) {
               WorldFeatureTree.onTreeGrown(world, x, y, z);

               for (int i = 0; i < noCanopies; i++) {
                  int modX = random.nextInt(8) - 4;
                  int modZ = random.nextInt(8) - 4;
                  int modY = random.nextInt(4) + 1;
                  int x1 = x + modX;
                  int z1 = z + modZ;

                  for (int k1 = y - 2 + l; k1 <= y + l; k1++) {
                     int j2 = k1 - (y + l);
                     int i3 = 1 - j2 / 2;

                     for (int k3 = x - i3; k3 <= x + i3; k3++) {
                        int l3 = k3 - x;

                        for (int i4 = z - i3; i4 <= z + i3; i4++) {
                           int j4 = i4 - z;
                           if ((Math.abs(l3) != i3 || Math.abs(j4) != i3 || random.nextInt(2) != 0 && j2 != 0)
                              && WorldFeatureTree.canLeavesReplace(world, k3, k1, i4)) {
                              this.placeLeaves(world, k3 + modX, k1 + modY, i4 + modZ, random);
                           }
                        }
                     }
                  }

                  this.placeBranch(new int[]{x1, y + modY + l - 2, z1}, new int[]{x, y - 2 + l, z}, this.logID, world);
               }

               for (int l1 = 0; l1 < l; l1++) {
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
      Block<?> dirt = WorldFeatureTree.getDirtForGrass(world.getBlockId(x, y - 1, z));
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
}
