package net.minecraft.core.world.generate.chunk.perlin.overworld.hell;

import java.util.Random;
import net.minecraft.core.block.BlockLogicOreCoal;
import net.minecraft.core.block.BlockLogicOreDiamond;
import net.minecraft.core.block.BlockLogicOreGold;
import net.minecraft.core.block.BlockLogicOreIron;
import net.minecraft.core.block.BlockLogicOreLapis;
import net.minecraft.core.block.BlockLogicOreRedstone;
import net.minecraft.core.block.BlockLogicSand;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.WorldFeatureClay;
import net.minecraft.core.world.generate.feature.WorldFeatureDeadBush;
import net.minecraft.core.world.generate.feature.WorldFeatureDungeon;
import net.minecraft.core.world.generate.feature.WorldFeatureLabyrinth;
import net.minecraft.core.world.generate.feature.WorldFeatureLake;
import net.minecraft.core.world.generate.feature.WorldFeatureLiquid;
import net.minecraft.core.world.generate.feature.WorldFeatureOre;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancy;
import net.minecraft.core.world.noise.PerlinNoise;

public class ChunkDecoratorOverworldHell implements ChunkDecorator {
   private final World world;
   private final PerlinNoise treeDensityNoise;

   public ChunkDecoratorOverworldHell(World world) {
      this.world = world;
      this.treeDensityNoise = new PerlinNoise(world.getRandomSeed(), 8, 74);
   }

   @Override
   public void decorate(Chunk chunk) {
      int chunkX = chunk.xPosition;
      int chunkZ = chunk.zPosition;
      int minY = this.world.getWorldType().getMinY();
      int maxY = this.world.getWorldType().getMaxY();
      int rangeY = maxY + 1 - minY;
      BlockLogicSand.fallInstantly = true;
      int x = chunkX * 16;
      int z = chunkZ * 16;
      Random rand = new Random(this.world.getRandomSeed());
      long l1 = rand.nextLong() / 2L * 2L + 1L;
      long l2 = rand.nextLong() / 2L * 2L + 1L;
      rand.setSeed(chunkX * l1 + chunkZ * l2 ^ this.world.getRandomSeed());
      if (rand.nextInt(32) == 0) {
         int i1 = x + rand.nextInt(16) + 8;
         int l4 = minY + rand.nextInt(rangeY);
         int i8 = z + rand.nextInt(16) + 8;
         new WorldFeatureLake(Blocks.FLUID_LAVA_STILL.id()).place(this.world, rand, i1, l4, i8);
      }

      if (rand.nextInt(16) == 0) {
         int j1 = x + rand.nextInt(16) + 8;
         int i5 = minY + rand.nextInt(rangeY - 16) + 8;
         int j8 = z + rand.nextInt(16) + 8;
         if (i5 < minY + rangeY / 2 || rand.nextInt(10) == 0) {
            if (rand.nextInt(4) == 0) {
               new WorldFeatureLake(Blocks.OBSIDIAN.id()).place(this.world, rand, j1, i5, j8);
            } else {
               new WorldFeatureLake(Blocks.FLUID_LAVA_STILL.id()).place(this.world, rand, j1, i5, j8);
            }
         }
      }

      for (int l18 = 0; l18 < 50; l18++) {
         int l21 = x + rand.nextInt(16) + 8;
         int k23 = minY + rand.nextInt(rangeY / 2);
         int l24 = z + rand.nextInt(16) + 8;
         if (k23 < minY + rangeY / 2) {
            new WorldFeatureLiquid(Blocks.FLUID_WATER_FLOWING.id()).place(this.world, rand, l21, k23, l24);
         }
      }

      for (int k1 = 0; k1 < 8; k1++) {
         int j5 = x + rand.nextInt(16) + 8;
         int k8 = minY + rand.nextInt(rangeY);
         int j11 = z + rand.nextInt(16) + 8;
         if (rand.nextInt(2) == 0) {
            new WorldFeatureDungeon(Blocks.BRICK_CLAY.id(), Blocks.BRICK_CLAY.id(), null).place(this.world, rand, j5, k8, j11);
         } else {
            new WorldFeatureDungeon(Blocks.COBBLE_STONE.id(), Blocks.COBBLE_STONE_MOSSY.id(), null).place(this.world, rand, j5, k8, j11);
         }
      }

      for (int k1x = 0; k1x < 1; k1x++) {
         int j5 = x + rand.nextInt(16) + 8;
         int j11 = z + rand.nextInt(16) + 8;
         int k8 = this.world.getHeightValue(j5, j11) - (rand.nextInt(2) + 2);
         if (rand.nextInt(10) == 0) {
            k8 -= rand.nextInt(10) + 30;
         }

         if (rand.nextInt(512) == 0) {
            new WorldFeatureLabyrinth().place(this.world, rand, j5, k8, j11);
         }
      }

      for (int i2 = 0; i2 < 20; i2++) {
         int k5 = x + rand.nextInt(16);
         int l8 = minY + rand.nextInt(rangeY);
         int k11 = z + rand.nextInt(16);
         new WorldFeatureClay(32).place(this.world, rand, k5, l8, k11);
      }

      for (int j2 = 0; j2 < 20; j2++) {
         int l5 = x + rand.nextInt(16);
         int i9 = minY + rand.nextInt(rangeY);
         int l11 = z + rand.nextInt(16);
         new WorldFeatureOre(Blocks.DIRT.id(), 32).place(this.world, rand, l5, i9, l11);
      }

      for (int k2 = 0; k2 < 10; k2++) {
         int i6 = x + rand.nextInt(16);
         int j9 = minY + rand.nextInt(rangeY);
         int i12 = z + rand.nextInt(16);
         new WorldFeatureOre(Blocks.GRAVEL.id(), 32).place(this.world, rand, i6, j9, i12);
      }

      for (int i3 = 0; i3 < 20; i3++) {
         int j6 = x + rand.nextInt(16);
         int k9 = minY + rand.nextInt(rangeY);
         int j12 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreCoal.variantMap, 16).place(this.world, rand, j6, k9, j12);
      }

      for (int j3 = 0; j3 < 20; j3++) {
         int k6 = x + rand.nextInt(16);
         int l9 = minY + rand.nextInt(rangeY / 2);
         int k12 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreIron.variantMap, 8).place(this.world, rand, k6, l9, k12);
      }

      for (int k3 = 0; k3 < 2; k3++) {
         int l6 = x + rand.nextInt(16);
         int i10 = minY + rand.nextInt(rangeY / 4);
         int l12 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreGold.variantMap, 8).place(this.world, rand, l6, i10, l12);
      }

      for (int l3 = 0; l3 < 8; l3++) {
         int i7 = x + rand.nextInt(16);
         int j10 = minY + rand.nextInt(rangeY / 8);
         int i13 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreRedstone.variantMap, 7).place(this.world, rand, i7, j10, i13);
      }

      for (int i4 = 0; i4 < 1; i4++) {
         int j7 = x + rand.nextInt(16);
         int k10 = minY + rand.nextInt(rangeY / 8);
         int j13 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreDiamond.variantMap, 7).place(this.world, rand, j7, k10, j13);
      }

      for (int j4 = 0; j4 < 1; j4++) {
         int k7 = x + rand.nextInt(16);
         int l10 = minY + rand.nextInt(rangeY / 8) + rand.nextInt(rangeY / 8);
         int k13 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreLapis.variantMap, 6).place(this.world, rand, k7, l10, k13);
      }

      double d = 0.5;
      int treeDensity = (int)((this.treeDensityNoise.get(x * d, z * d) / 8.0 + rand.nextDouble() * 4.0 + 4.0) / 3.0);
      if (rand.nextInt(10) == 0) {
         treeDensity++;
      }

      treeDensity /= 2;

      for (int i11 = 0; i11 < treeDensity; i11++) {
         boolean hasLeaves = rand.nextInt(1234) == 0;
         int l13 = x + rand.nextInt(16) + 8;
         int j14 = z + rand.nextInt(16) + 8;
         WorldFeature wf;
         if (rand.nextInt(10) == 0) {
            wf = new WorldFeatureTreeFancy(hasLeaves ? Blocks.LEAVES_OAK.id() : 0, Blocks.LOG_OAK.id());
         } else {
            wf = new WorldFeatureTree(hasLeaves ? Blocks.LEAVES_OAK.id() : 0, Blocks.LOG_OAK.id(), 4);
         }

         wf.init(1.0, 1.0, 1.0);
         wf.place(this.world, rand, l13, this.world.getHeightValue(l13, j14), j14);
      }

      byte byte1 = 10;

      for (int i15 = 0; i15 < byte1; i15++) {
         int i17 = x + rand.nextInt(16) + 8;
         int i20 = minY + rand.nextInt(rangeY);
         int l22 = z + rand.nextInt(16) + 8;
         new WorldFeatureDeadBush(Blocks.DEADBUSH.id()).place(this.world, rand, i17, i20, l22);
      }

      for (int l18x = 0; l18x < 5; l18x++) {
         int l21 = x + rand.nextInt(16) + 8;
         int k23 = minY + rand.nextInt(rand.nextInt(rangeY - 8) + 8);
         int l24 = z + rand.nextInt(16) + 8;
         new WorldFeatureLiquid(Blocks.FLUID_LAVA_FLOWING.id()).place(this.world, rand, l21, k23, l24);
      }

      for (int i19 = 0; i19 < 20; i19++) {
         int i22 = x + rand.nextInt(16) + 8;
         int l23 = minY + rand.nextInt(rand.nextInt(rand.nextInt(rangeY - rangeY / 8) + 8) + 8);
         int i25 = z + rand.nextInt(16) + 8;
         new WorldFeatureLiquid(Blocks.FLUID_LAVA_FLOWING.id()).place(this.world, rand, i22, l23, i25);
      }

      if (this.world.getBlockId(x, minY + this.world.getWorldType().getOceanY() - 1, z) == this.world.getWorldType().getOceanBlockId()) {
         this.world.setBlockWithNotify(x, minY + this.world.getWorldType().getOceanY() - 1, z, this.world.getWorldType().getOceanBlockId());
      }

      BlockLogicSand.fallInstantly = false;
   }
}
