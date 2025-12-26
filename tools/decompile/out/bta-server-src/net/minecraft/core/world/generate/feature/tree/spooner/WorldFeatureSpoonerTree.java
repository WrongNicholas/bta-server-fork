package net.minecraft.core.world.generate.feature.tree.spooner;

import java.util.List;
import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;

public abstract class WorldFeatureSpoonerTree extends WorldFeature {
   protected int[] pos = null;
   protected int height;
   protected final int trunkId;
   protected final int trunkData;
   protected final int leavesId;
   protected final int leavesData;
   protected Random random = null;

   protected static int distToMat(double[] coord, double[] vec, List<Integer> matidxlist, World world, boolean invert, double limit) {
      double[] curCoord = new double[]{coord[0] + 0.5, coord[1] + 0.5, coord[2] + 0.5};
      int iterations = 0;

      do {
         int x = (int)Math.floor(curCoord[0]);
         int y = (int)Math.floor(curCoord[1]);
         int z = (int)Math.floor(curCoord[2]);
         int blockId = world.getBlockId(x, y, z);
         if (matidxlist.contains(blockId) && !invert || !matidxlist.contains(blockId) && invert) {
            break;
         }

         for (int i = 0; i < 3; i++) {
            curCoord[i] += vec[i];
         }
      } while (limit == 0.0 || !(++iterations > limit));

      return iterations;
   }

   protected boolean assignValue(int x, int y, int z, int id, int meta, World world) {
      if (id == this.leavesId) {
         return WorldFeatureTree.canLeavesReplace(world, x, y, z) ? world.setBlockAndMetadataWithNotify(x, y, z, id, meta) : false;
      } else {
         return world.setBlockAndMetadata(x, y, z, id, meta);
      }
   }

   @MethodParametersAnnotation(names = {"height", "trunkId", "trunkData", "leavesId", "leavesData"})
   public WorldFeatureSpoonerTree(int height, int trunkId, int trunkData, int leavesId, int leavesData) {
      this.height = height;
      this.trunkId = trunkId;
      this.trunkData = trunkData;
      this.leavesId = leavesId;
      this.leavesData = leavesData;
   }

   protected void prepare(World world) {
   }

   protected void makeTrunk(World world) {
   }

   protected void makeFoliage(World world) {
   }

   private boolean canGenerateTree(World world) {
      int blockIdUnderneath = world.getBlockId(this.pos[0], this.pos[1] - 1, this.pos[2]);
      if (!Blocks.hasTag(blockIdUnderneath, BlockTags.GROWS_TREES)) {
         return false;
      } else {
         for (int yC = this.pos[1]; yC <= this.pos[1] + 5; yC++) {
            for (int xC = this.pos[0] - 1; xC <= this.pos[0] + 1; xC++) {
               for (int zC = this.pos[2] - 1; zC <= this.pos[2] + 1; zC++) {
                  int blockId = world.getBlockId(xC, yC, zC);
                  if (blockId != 0 && blockId != this.leavesId) {
                     return false;
                  }
               }
            }
         }

         int interruptedTrunkHeight = -1;

         for (int y = this.pos[1]; y < world.getHeightBlocks(); y++) {
            if (Blocks.solid[world.getBlockId(this.pos[0], y, this.pos[2])]) {
               interruptedTrunkHeight = y - this.pos[1];
               break;
            }
         }

         if (interruptedTrunkHeight == -1) {
            return true;
         } else if (interruptedTrunkHeight < 6) {
            return false;
         } else {
            this.height = interruptedTrunkHeight;
            return true;
         }
      }
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      this.random = random;
      this.pos = new int[]{x, y, z};
      if (y < 1 || y + this.height > world.getHeightBlocks()) {
         return false;
      } else if (!this.canGenerateTree(world)) {
         return false;
      } else {
         this.prepare(world);
         this.makeFoliage(world);
         this.makeTrunk(world);
         return true;
      }
   }
}
