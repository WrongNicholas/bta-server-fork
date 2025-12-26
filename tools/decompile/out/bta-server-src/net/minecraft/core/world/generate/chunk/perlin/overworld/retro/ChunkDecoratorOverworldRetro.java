package net.minecraft.core.world.generate.chunk.perlin.overworld.retro;

import java.util.Random;
import net.minecraft.core.block.BlockLogicOreCoal;
import net.minecraft.core.block.BlockLogicOreDiamond;
import net.minecraft.core.block.BlockLogicOreGold;
import net.minecraft.core.block.BlockLogicOreIron;
import net.minecraft.core.block.BlockLogicOreRedstone;
import net.minecraft.core.block.BlockLogicSand;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.WorldFeatureCactus;
import net.minecraft.core.world.generate.feature.WorldFeatureClay;
import net.minecraft.core.world.generate.feature.WorldFeatureDungeon;
import net.minecraft.core.world.generate.feature.WorldFeatureFlowers;
import net.minecraft.core.world.generate.feature.WorldFeatureLiquid;
import net.minecraft.core.world.generate.feature.WorldFeatureOre;
import net.minecraft.core.world.generate.feature.WorldFeatureSugarCane;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancy;
import net.minecraft.core.world.noise.RetroPerlinNoise;

public class ChunkDecoratorOverworldRetro implements ChunkDecorator {
   private final World world;
   private final RetroPerlinNoise treeDensityNoise;
   private final boolean snowCovered;

   public ChunkDecoratorOverworldRetro(World world) {
      this.world = world;
      this.treeDensityNoise = new RetroPerlinNoise(world.getRandomSeed(), 8, 74);
      this.snowCovered = false;
   }

   @Override
   public void decorate(Chunk chunk) {
      this.world.scheduledUpdatesAreImmediate = true;
      int chunkX = chunk.xPosition;
      int chunkZ = chunk.zPosition;
      int minY = this.world.getWorldType().getMinY();
      int maxY = this.world.getWorldType().getMaxY();
      int rangeY = maxY + 1 - minY;
      BlockLogicSand.fallInstantly = true;
      int k = chunkX * 16;
      int l = chunkZ * 16;
      Random rand = new Random(this.world.getRandomSeed());
      long l1 = rand.nextLong() / 2L * 2L + 1L;
      long l2 = rand.nextLong() / 2L * 2L + 1L;
      rand.setSeed(chunkX * l1 + chunkZ * l2 ^ this.world.getRandomSeed());

      for (int i1 = 0; i1 < 8; i1++) {
         int i4 = k + rand.nextInt(16) + 8;
         int j6 = minY + rand.nextInt(rangeY);
         int i11 = l + rand.nextInt(16) + 8;
         new WorldFeatureDungeon(Blocks.COBBLE_STONE.id(), Blocks.COBBLE_STONE_MOSSY.id(), null).place(this.world, rand, i4, j6, i11);
      }

      for (int j1 = 0; j1 < 10; j1++) {
         int j4 = k + rand.nextInt(16);
         int k6 = minY + rand.nextInt(rangeY);
         int j11 = l + rand.nextInt(16);
         new WorldFeatureClay(32).place(this.world, rand, j4, k6, j11);
      }

      for (int k1 = 0; k1 < 20; k1++) {
         int k4 = k + rand.nextInt(16);
         int l6 = minY + rand.nextInt(rangeY);
         int k11 = l + rand.nextInt(16);
         new WorldFeatureOre(Blocks.DIRT.id(), 32).place(this.world, rand, k4, l6, k11);
      }

      for (int i2 = 0; i2 < 10; i2++) {
         int l4 = k + rand.nextInt(16);
         int i7 = minY + rand.nextInt(rangeY);
         int l11 = l + rand.nextInt(16);
         new WorldFeatureOre(Blocks.GRAVEL.id(), 32).place(this.world, rand, l4, i7, l11);
      }

      for (int j2 = 0; j2 < 20; j2++) {
         int i5 = k + rand.nextInt(16);
         int j7 = minY + rand.nextInt(rangeY);
         int i12 = l + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreCoal.variantMap, 16).place(this.world, rand, i5, j7, i12);
      }

      for (int k2 = 0; k2 < 20; k2++) {
         int j5 = k + rand.nextInt(16);
         int k7 = minY + rand.nextInt(rangeY / 2);
         int j12 = l + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreIron.variantMap, 8).place(this.world, rand, j5, k7, j12);
      }

      for (int i3 = 0; i3 < 2; i3++) {
         int k5 = k + rand.nextInt(16);
         int l7 = minY + rand.nextInt(rangeY / 4);
         int k12 = l + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreGold.variantMap, 8).place(this.world, rand, k5, l7, k12);
      }

      for (int j3 = 0; j3 < 8; j3++) {
         int l5 = k + rand.nextInt(16);
         int i8 = minY + rand.nextInt(rangeY / 8);
         int l12 = l + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreRedstone.variantMap, 7).place(this.world, rand, l5, i8, l12);
      }

      for (int k3 = 0; k3 < 1; k3++) {
         int i6 = k + rand.nextInt(16);
         int j8 = minY + rand.nextInt(rangeY / 8);
         int i13 = l + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreDiamond.variantMap, 7).place(this.world, rand, i6, j8, i13);
      }

      double d = 0.5;
      int l3 = (int)((this.treeDensityNoise.get(k * d, l * d) / 8.0 + rand.nextDouble() * 4.0 + 4.0) / 3.0);
      if (l3 < 0) {
         l3 = 0;
      }

      if (rand.nextInt(10) == 0) {
         l3++;
      }

      WorldFeature tree = new WorldFeatureTree(Blocks.LEAVES_OAK_RETRO.id(), Blocks.LOG_OAK.id(), 4);
      if (rand.nextInt(10) == 0) {
         tree = new WorldFeatureTreeFancy(Blocks.LEAVES_OAK_RETRO.id(), Blocks.LOG_OAK.id());
      }

      for (int k8 = 0; k8 < l3; k8++) {
         int j13 = k + rand.nextInt(16) + 8;
         int l15 = l + rand.nextInt(16) + 8;
         tree.init(1.0, 1.0, 1.0);
         tree.place(this.world, rand, j13, this.world.getHeightValue(j13, l15), l15);
      }

      for (int l8 = 0; l8 < 2; l8++) {
         int k13 = k + rand.nextInt(16) + 8;
         int i16 = minY + rand.nextInt(rangeY);
         int j18 = l + rand.nextInt(16) + 8;
         new WorldFeatureFlowers(Blocks.FLOWER_YELLOW.id(), 64, false).place(this.world, rand, k13, i16, j18);
      }

      if (rand.nextInt(2) == 0) {
         int i9 = k + rand.nextInt(16) + 8;
         int l13 = minY + rand.nextInt(rangeY);
         int j16 = l + rand.nextInt(16) + 8;
         new WorldFeatureFlowers(Blocks.FLOWER_RED.id(), 64, false).place(this.world, rand, i9, l13, j16);
      }

      if (rand.nextInt(4) == 0) {
         int j9 = k + rand.nextInt(16) + 8;
         int i14 = minY + rand.nextInt(rangeY);
         int k16 = l + rand.nextInt(16) + 8;
         new WorldFeatureFlowers(Blocks.MUSHROOM_BROWN.id(), 64, false).place(this.world, rand, j9, i14, k16);
      }

      if (rand.nextInt(8) == 0) {
         int k9 = k + rand.nextInt(16) + 8;
         int j14 = minY + rand.nextInt(rangeY);
         int l16 = l + rand.nextInt(16) + 8;
         new WorldFeatureFlowers(Blocks.MUSHROOM_RED.id(), 64, false).place(this.world, rand, k9, j14, l16);
      }

      for (int l9 = 0; l9 < 10; l9++) {
         int k14 = k + rand.nextInt(16) + 8;
         int i17 = minY + rand.nextInt(rangeY);
         int k18 = l + rand.nextInt(16) + 8;
         new WorldFeatureSugarCane().place(this.world, rand, k14, i17, k18);
      }

      for (int i10 = 0; i10 < 1; i10++) {
         int l14 = k + rand.nextInt(16) + 8;
         int j17 = minY + rand.nextInt(rangeY);
         int l18 = l + rand.nextInt(16) + 8;
         new WorldFeatureCactus().place(this.world, rand, l14, j17, l18);
      }

      for (int j10 = 0; j10 < 50; j10++) {
         int i15 = k + rand.nextInt(16) + 8;
         int k17 = minY + rand.nextInt(rand.nextInt(rangeY - 8) + 8);
         int i19 = l + rand.nextInt(16) + 8;
         new WorldFeatureLiquid(Blocks.FLUID_WATER_FLOWING.id()).place(this.world, rand, i15, k17, i19);
      }

      for (int k10 = 0; k10 < 20; k10++) {
         int j15 = k + rand.nextInt(16) + 8;
         int l17 = minY + rand.nextInt(rand.nextInt(rand.nextInt(rangeY / 2 - rangeY / 8) + 8) + 8);
         int j19 = l + rand.nextInt(16) + 8;
         new WorldFeatureLiquid(Blocks.FLUID_LAVA_FLOWING.id()).place(this.world, rand, j15, l17, j19);
      }

      for (int l10 = k + 8; l10 < k + 8 + 16; l10++) {
         for (int k15 = l + 8; k15 < l + 8 + 16; k15++) {
            int i18 = this.world.findTopSolidBlock(l10, k15);
            if (this.snowCovered
               && i18 > minY
               && i18 <= maxY
               && this.world.getBlockId(l10, i18, k15) == 0
               && this.world.getBlockMaterial(l10, i18 - 1, k15).blocksMotion()
               && this.world.getBlockMaterial(l10, i18 - 1, k15) != Material.ice) {
               this.world.setBlockWithNotify(l10, i18, k15, Blocks.LAYER_SNOW.id());
            }

            if (this.snowCovered && this.world.getBlockMaterial(l10, this.world.getWorldType().getOceanY() - 1, k15) == Material.water) {
               this.world.setBlockWithNotify(l10, this.world.getWorldType().getOceanY() - 1, k15, Blocks.ICE.id());
            }
         }
      }

      BlockLogicSand.fallInstantly = false;
      this.world.scheduledUpdatesAreImmediate = false;
   }
}
