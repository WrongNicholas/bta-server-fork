package net.minecraft.core.world.generate.feature.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicFlower;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.BlockLogicLog;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkPosition;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class WorldFeatureTreePalm extends WorldFeature {
   protected World world;
   protected Random random;
   protected Block<?> log;
   protected Block<?> leaves;
   protected boolean big;
   protected boolean update;
   protected boolean checkForWater;
   protected ChunkPosition basePos;
   protected ChunkPosition endPos;
   protected float[] leafAngles;
   protected float[] leafAnglesUpper;
   protected int logHeight;
   protected Set<Integer> intSet = new HashSet<>();
   protected List<Integer> intList = new ArrayList<>();

   @MethodParametersAnnotation(names = {"log", "leaves", "big", "update"})
   public WorldFeatureTreePalm(Block<?> log, Block<?> leaves, boolean big, boolean update, boolean checkForWater) {
      this.log = log;
      this.leaves = leaves;
      this.update = update;
      this.big = big;
      this.checkForWater = checkForWater;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      this.world = world;
      this.random = random;
      this.basePos = new ChunkPosition(x, y, z);
      int blockBelow = world.getBlockId(x, y - 1, z);
      if (!this.canGrowOnThisBlock(blockBelow, x, y, z)) {
         return false;
      } else if (!this.generateTrunkIfPossible()) {
         return false;
      } else {
         WorldFeatureTree.onTreeGrown(world, x, y, z);
         this.calculateLeafAngles();
         this.generateLeaves();
         return true;
      }
   }

   public boolean generateTrunkIfPossible() {
      int height;
      if (this.big) {
         height = 11 + this.random.nextInt(7);
      } else {
         height = 7 + this.random.nextInt(3);
      }

      float angle = this.random.nextFloat() * 0.5F + 0.5F;
      float bend;
      if (this.big) {
         bend = this.random.nextFloat() * 5.0F + 2.0F;
      } else {
         bend = this.random.nextFloat() * 3.0F + 2.0F;
      }

      Direction direction = Direction.horizontalDirections[this.random.nextInt(4)];
      return this.generateTrunkIfPossible(height, angle, bend, direction);
   }

   public boolean generateTrunkIfPossible(int height, float angle, float bend, Direction direction) {
      this.intList.clear();
      this.intSet.clear();

      for (int i = 0; i < height; i++) {
         float yFactor = (float)i / height;
         int offset = MathHelper.floor(Math.pow(yFactor, angle + 1.0F) * bend);
         int offsetX = direction.getOffsetX() * offset;
         int offsetZ = direction.getOffsetZ() * offset;
         this.intList.add(encode(offsetX, i, offsetZ));
      }

      int lastPossibleBlockIndex = this.intList.size() - 1;

      for (int i = 0; i < this.intList.size(); i++) {
         int offset = this.intList.get(i);
         int x = this.basePos.x + decodeX(offset);
         int y = this.basePos.y + decodeY(offset);
         int z = this.basePos.z + decodeZ(offset);
         if (!this.isClear(x, y, z)) {
            lastPossibleBlockIndex = i - 1;
            break;
         }
      }

      for (int ix = lastPossibleBlockIndex; ix >= 0; ix--) {
         int offset = this.intList.get(ix);
         int x = this.basePos.x + decodeX(offset);
         int y = this.basePos.y + decodeY(offset);
         int z = this.basePos.z + decodeZ(offset);
         if (this.isClearRange(x, y, z, 2, 2, 2)) {
            break;
         }

         lastPossibleBlockIndex = ix - 1;
      }

      int minHeight = Math.min(6, height - 2);
      if (lastPossibleBlockIndex < minHeight) {
         return false;
      } else {
         int x = 0;
         int y = 0;
         int z = 0;
         this.logHeight = lastPossibleBlockIndex + 1;

         for (int ix = 0; ix < this.logHeight; ix++) {
            int offset = this.intList.get(ix);
            x = this.basePos.x + decodeX(offset);
            y = this.basePos.y + decodeY(offset);
            z = this.basePos.z + decodeZ(offset);
            this.generateBlock(x, y, z, this.log);
         }

         this.endPos = new ChunkPosition(x, y, z);
         return true;
      }
   }

   public boolean isClear(int x, int y, int z) {
      int id = this.world.getBlockId(x, y, z);
      if (id == 0) {
         return true;
      } else {
         Block<?> block = Blocks.getBlock(id);
         return Block.hasLogicClass(block, BlockLogicLog.class)
            || Block.hasLogicClass(block, BlockLogicLeavesBase.class)
            || Block.hasLogicClass(block, BlockLogicFlower.class);
      }
   }

   public boolean isClearRange(int x, int y, int z, int w, int h, int d) {
      for (int i = -w; i <= w; i++) {
         for (int j = -h; j <= h; j++) {
            for (int k = -d; k <= d; k++) {
               int x1 = x + i;
               int y1 = y + j;
               int z1 = z + k;
               if (!this.isClear(x1, y1, z1)) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public void calculateLeafAngles() {
      int leafCount = 6;
      float baseAngle = 360.0F / leafCount;
      float offset = this.random.nextFloat() * baseAngle;
      this.leafAngles = new float[leafCount];

      for (int i = 0; i < leafCount; i++) {
         float angle = offset + baseAngle * i;
         this.leafAngles[i] = angle;
      }

      this.leafAnglesUpper = new float[leafCount];

      for (int i = 0; i < leafCount; i++) {
         float angle1 = this.leafAngles[i];
         float angle2 = this.leafAngles[(i + 1) % leafCount];
         if (angle2 < angle1) {
            angle2 += 360.0F;
         }

         float newAngle = (angle1 + angle2) / 2.0F;
         this.leafAnglesUpper[i] = newAngle;
      }
   }

   public void generateLeaves() {
      float leafLength = this.logHeight >= 10 ? 5.0F : 4.0F;

      for (int i = 0; i < this.leafAngles.length; i++) {
         float angle = this.leafAngles[i];
         this.createHorizontalLine(angle, leafLength);

         for (int j = 0; j < this.intList.size(); j++) {
            int xyz = this.intList.get(j);
            int x = this.endPos.x + decodeX(xyz);
            int y = this.endPos.y;
            int z = this.endPos.z + decodeZ(xyz);
            if (j == 0) {
               y++;
            }

            if (j >= this.intList.size() - 1) {
               y--;
            }

            if (this.world.getBlockId(x, y, z) == 0) {
               this.generateBlock(x, y, z, this.leaves);
            }
         }
      }

      for (int i = 0; i < this.leafAnglesUpper.length; i++) {
         float angle = this.leafAnglesUpper[i];
         this.createHorizontalLine(angle, leafLength);

         for (int j = 0; j < this.intList.size(); j++) {
            int xyzx = this.intList.get(j);
            int xx = this.endPos.x + decodeX(xyzx);
            int yx = this.endPos.y;
            int zx = this.endPos.z + decodeZ(xyzx);
            if (j > 1 && j < this.intList.size() - 1) {
               yx += 2;
            } else {
               yx++;
            }

            if (this.world.getBlockId(xx, yx, zx) == 0) {
               this.generateBlock(xx, yx, zx, this.leaves);
            }
         }
      }
   }

   public void createHorizontalLine(float angle, float length) {
      this.intSet.clear();
      this.intList.clear();
      int iterations = (int)(length * 2.0F);
      double angrad = Math.toRadians(angle);
      float x1 = (float)(Math.sin(angrad) * length);
      float z1 = (float)(-Math.cos(angrad) * length);

      for (int i = 0; i < iterations; i++) {
         float f = (float)i / iterations;
         int x = Math.round(x1 * f);
         int z = Math.round(z1 * f);
         int xyz = encode(x, 0, z);
         if (!this.intSet.contains(xyz)) {
            this.intSet.add(xyz);
            this.intList.add(xyz);
         }
      }
   }

   private boolean waterNearby(int x, int y, int z) {
      int distance = 1;

      for (int xi = x - distance; xi <= x + distance; xi++) {
         for (int zi = z - distance; zi <= z + distance; zi++) {
            Block<?> blockAtRange = this.world.getBlock(xi, y - 1, zi);
            if (blockAtRange != null && blockAtRange.hasTag(BlockTags.IS_WATER)) {
               return true;
            }
         }
      }

      return false;
   }

   public void generateBlock(int x, int y, int z, Block<?> block) {
      if (this.update) {
         this.world.setBlockWithNotify(x, y, z, block.id());
      } else {
         this.world.setBlock(x, y, z, block.id());
      }
   }

   public boolean canGrowOnThisBlock(int id, int x, int y, int z) {
      if (Blocks.hasTag(id, BlockTags.GROWS_TREES)) {
         return true;
      } else {
         return id == Blocks.SAND.id() ? !this.checkForWater || this.waterNearby(x, y, z) : false;
      }
   }

   public static int encode(int x, int y, int z) {
      int x1 = x + 512;
      int y1 = y + 512;
      int z1 = z + 512;
      if (x1 < 0 || x1 >= 1024) {
         throw new IndexOutOfBoundsException("X: " + x);
      } else if (y1 < 0 || y1 >= 1024) {
         throw new IndexOutOfBoundsException("Y: " + y);
      } else if (z1 >= 0 && z1 < 1024) {
         return x1 << 20 | y1 << 10 | z1;
      } else {
         throw new IndexOutOfBoundsException("Z: " + z);
      }
   }

   public static int decodeX(int xyz) {
      return (xyz >> 20 & 1023) - 512;
   }

   public static int decodeY(int xyz) {
      return (xyz >> 10 & 1023) - 512;
   }

   public static int decodeZ(int xyz) {
      return (xyz >> 0 & 1023) - 512;
   }
}
