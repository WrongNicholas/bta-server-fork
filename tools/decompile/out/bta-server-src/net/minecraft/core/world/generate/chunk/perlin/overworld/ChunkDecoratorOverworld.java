package net.minecraft.core.world.generate.chunk.perlin.overworld;

import java.util.Random;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.BlockLogicMoss;
import net.minecraft.core.block.BlockLogicOreCoal;
import net.minecraft.core.block.BlockLogicOreDiamond;
import net.minecraft.core.block.BlockLogicOreGold;
import net.minecraft.core.block.BlockLogicOreIron;
import net.minecraft.core.block.BlockLogicOreLapis;
import net.minecraft.core.block.BlockLogicOreRedstone;
import net.minecraft.core.block.BlockLogicSand;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.BiomeOutback;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.WorldFeatureCactus;
import net.minecraft.core.world.generate.feature.WorldFeatureClay;
import net.minecraft.core.world.generate.feature.WorldFeatureDeadBush;
import net.minecraft.core.world.generate.feature.WorldFeatureDungeon;
import net.minecraft.core.world.generate.feature.WorldFeatureFlowers;
import net.minecraft.core.world.generate.feature.WorldFeatureLabyrinth;
import net.minecraft.core.world.generate.feature.WorldFeatureLake;
import net.minecraft.core.world.generate.feature.WorldFeatureLiquid;
import net.minecraft.core.world.generate.feature.WorldFeatureMeadow;
import net.minecraft.core.world.generate.feature.WorldFeatureOre;
import net.minecraft.core.world.generate.feature.WorldFeaturePermaice;
import net.minecraft.core.world.generate.feature.WorldFeaturePumpkin;
import net.minecraft.core.world.generate.feature.WorldFeatureRichScorchedDirt;
import net.minecraft.core.world.generate.feature.WorldFeatureSpinifexPatch;
import net.minecraft.core.world.generate.feature.WorldFeatureSponge;
import net.minecraft.core.world.generate.feature.WorldFeatureSugarCane;
import net.minecraft.core.world.generate.feature.WorldFeatureSugarCaneTall;
import net.minecraft.core.world.generate.feature.WorldFeatureTallGrass;
import net.minecraft.core.world.noise.PerlinNoise;
import net.minecraft.core.world.type.WorldTypes;

public class ChunkDecoratorOverworld implements ChunkDecorator {
   private final World world;
   private final PerlinNoise treeDensityNoise;
   private final int treeDensityOverride;

   protected ChunkDecoratorOverworld(World world, int treeDensityOverride) {
      this.world = world;
      this.treeDensityOverride = treeDensityOverride;
      this.treeDensityNoise = new PerlinNoise(world.getRandomSeed(), 8, 74);
   }

   public ChunkDecoratorOverworld(World world) {
      this(world, -1);
   }

   @Override
   public void decorate(Chunk chunk) {
      this.world.scheduledUpdatesAreImmediate = true;
      int chunkX = chunk.xPosition;
      int chunkZ = chunk.zPosition;
      int minY = this.world.getWorldType().getMinY();
      int maxY = this.world.getWorldType().getMaxY();
      int rangeY = maxY + 1 - minY;
      float oreHeightModifier = rangeY / 128.0F;
      BlockLogicSand.fallInstantly = true;
      int x = chunkX * 16;
      int z = chunkZ * 16;
      int y = this.world.getHeightValue(x + 16, z + 16);
      Biome biome = this.world.getBlockBiome(x + 16, y, z + 16);
      Random rand = new Random(this.world.getRandomSeed());
      long l1 = rand.nextLong() / 2L * 2L + 1L;
      long l2 = rand.nextLong() / 2L * 2L + 1L;
      rand.setSeed(chunkX * l1 + chunkZ * l2 ^ this.world.getRandomSeed());
      Random swampRand = new Random(chunkX * l1 + chunkZ * l2 ^ this.world.getRandomSeed());
      if (biome == Biomes.OVERWORLD_SWAMPLAND) {
         for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
               int topBlock = this.world.getHeightValue(x + dx, z + dz);
               int id = this.world.getBlockId(x + dx, topBlock - 1, z + dz);
               if (id == Blocks.GRASS.id()) {
                  boolean shouldPlaceWater = swampRand.nextFloat() < 0.5F;
                  if (shouldPlaceWater) {
                     int posXId = this.world.getBlockId(x + dx + 1, topBlock - 1, z + dz);
                     int negXId = this.world.getBlockId(x + dx - 1, topBlock - 1, z + dz);
                     int posZId = this.world.getBlockId(x + dx, topBlock - 1, z + dz + 1);
                     int negZId = this.world.getBlockId(x + dx, topBlock - 1, z + dz - 1);
                     int negYId = this.world.getBlockId(x + dx, topBlock - 2, z + dz);
                     if (posXId != 0
                        && (Blocks.blocksList[posXId].getMaterial().isSolid() || Blocks.blocksList[posXId].getMaterial() == Material.water)
                        && negXId != 0
                        && (Blocks.blocksList[negXId].getMaterial().isSolid() || Blocks.blocksList[negXId].getMaterial() == Material.water)
                        && posZId != 0
                        && (Blocks.blocksList[posZId].getMaterial().isSolid() || Blocks.blocksList[posZId].getMaterial() == Material.water)
                        && negZId != 0
                        && (Blocks.blocksList[negZId].getMaterial().isSolid() || Blocks.blocksList[negZId].getMaterial() == Material.water)
                        && negYId != 0
                        && Blocks.blocksList[negYId].getMaterial().isSolid()) {
                        this.world.setBlock(x + dx, topBlock - 1, z + dz, Blocks.FLUID_WATER_STILL.id());
                        this.world.setBlock(x + dx, topBlock, z + dz, 0);
                     }
                  }
               }
            }
         }
      }

      int lakeChance = 4;
      if (biome == Biomes.OVERWORLD_SWAMPLAND) {
         lakeChance = 2;
      }

      if (biome == Biomes.OVERWORLD_DESERT) {
         lakeChance = 0;
      }

      if (lakeChance != 0 && rand.nextInt(lakeChance) == 0) {
         int fluid = Blocks.FLUID_WATER_STILL.id();
         if (biome.hasSurfaceSnow()) {
            fluid = Blocks.ICE.id();
         }

         int i1 = x + rand.nextInt(16) + 8;
         int l4 = minY + rand.nextInt(rangeY);
         int i8 = z + rand.nextInt(16) + 8;
         new WorldFeatureLake(fluid).place(this.world, rand, i1, l4, i8);
      }

      if (rand.nextInt(8) == 0) {
         int xf = x + rand.nextInt(16) + 8;
         int yf = minY + rand.nextInt(rand.nextInt(rangeY - rangeY / 16) + rangeY / 16);
         int zf = z + rand.nextInt(16) + 8;
         if (yf < minY + rangeY / 2 || rand.nextInt(10) == 0) {
            new WorldFeatureLake(Blocks.FLUID_LAVA_STILL.id()).place(this.world, rand, xf, yf, zf);
         }
      }

      for (int k1 = 0; k1 < 8.0F * oreHeightModifier; k1++) {
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
         if (rand.nextInt(5) == 0) {
            k8 -= rand.nextInt(10) + 30;
         }

         if (rand.nextInt(700) == 0) {
            Random lRand = chunk.getChunkRandom(75644760L);
            new WorldFeatureLabyrinth().place(this.world, lRand, j5, k8, j11);
         }
      }

      for (int i2 = 0; i2 < 20.0F * oreHeightModifier; i2++) {
         int k5 = x + rand.nextInt(16);
         int l8 = minY + rand.nextInt(rangeY);
         int k11 = z + rand.nextInt(16);
         new WorldFeatureClay(32).place(this.world, rand, k5, l8, k11);
      }

      if (biome instanceof BiomeOutback || biome == Biomes.OVERWORLD_OUTBACK) {
         int l5 = x + rand.nextInt(16);
         int l11 = z + rand.nextInt(16);
         int i9 = this.world.getHeightValue(l5, l11);
         new WorldFeatureRichScorchedDirt(10).place(this.world, rand, l5, i9, l11);
      }

      if (biome == Biomes.OVERWORLD_GLACIER || biome == Biomes.OVERWORLD_TUNDRA) {
         for (int i4 = 0; i4 < 5.0F * oreHeightModifier; i4++) {
            int j7 = x + rand.nextInt(16);
            int k10 = minY + rand.nextInt(rangeY / 2);
            int j13 = z + rand.nextInt(16);
            new WorldFeaturePermaice(32 + rand.nextInt(32), Blocks.PERMAFROST).place(this.world, rand, j7, k10, j13);
         }
      }

      for (int j2 = 0; j2 < 20.0F * oreHeightModifier; j2++) {
         int l5 = x + rand.nextInt(16);
         int i9 = minY + rand.nextInt(rangeY);
         int l11 = z + rand.nextInt(16);
         new WorldFeatureOre(Blocks.DIRT.id(), 32).place(this.world, rand, l5, i9, l11);
      }

      for (int k2 = 0; k2 < 10.0F * oreHeightModifier; k2++) {
         int i6 = x + rand.nextInt(16);
         int j9 = minY + rand.nextInt(rangeY);
         int i12 = z + rand.nextInt(16);
         new WorldFeatureOre(Blocks.GRAVEL.id(), 32).place(this.world, rand, i6, j9, i12);
      }

      for (int i3 = 0; i3 < 20.0F * oreHeightModifier; i3++) {
         int j6 = x + rand.nextInt(16);
         int k9 = minY + rand.nextInt(rangeY);
         int j12 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreCoal.variantMap, 16).place(this.world, rand, j6, k9, j12);
      }

      for (int j3 = 0; j3 < 20.0F * oreHeightModifier; j3++) {
         int k6 = x + rand.nextInt(16);
         int l9 = minY + rand.nextInt(rangeY / 2);
         int k12 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreIron.variantMap, 8).place(this.world, rand, k6, l9, k12);
      }

      for (int k3 = 0; k3 < 2.0F * oreHeightModifier; k3++) {
         int l6 = x + rand.nextInt(16);
         int i10 = minY + rand.nextInt(rangeY / 4);
         int l12 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreGold.variantMap, 8).place(this.world, rand, l6, i10, l12);
      }

      for (int l3 = 0; l3 < 8.0F * oreHeightModifier; l3++) {
         int i7 = x + rand.nextInt(16);
         int j10 = minY + rand.nextInt(rangeY / 8);
         int i13 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreRedstone.variantMap, 7).place(this.world, rand, i7, j10, i13);
      }

      for (int i4 = 0; i4 < oreHeightModifier; i4++) {
         int j7 = x + rand.nextInt(16);
         int k10 = minY + rand.nextInt(rangeY / 8);
         int j13 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreDiamond.variantMap, 7).place(this.world, rand, j7, k10, j13);
      }

      for (int i4 = 0; i4 < oreHeightModifier; i4++) {
         int j7 = x + rand.nextInt(16);
         int k10 = minY + rand.nextInt(rangeY / 2);
         int j13 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicMoss.variantMap, 32).place(this.world, rand, j7, k10, j13);
      }

      for (int j4 = 0; j4 < oreHeightModifier; j4++) {
         int k7 = x + rand.nextInt(16);
         int l10 = minY + rand.nextInt(rangeY / 8) + rand.nextInt(rangeY / 8);
         int k13 = z + rand.nextInt(16);
         new WorldFeatureOre(BlockLogicOreLapis.variantMap, 6).place(this.world, rand, k7, l10, k13);
      }

      double d = 0.5;
      int k4 = (int)((this.treeDensityNoise.get(x * d, z * d) / 8.0 + rand.nextDouble() * 4.0 + 4.0) / 3.0);
      int treeDensity = 0;
      if (rand.nextInt(10) == 0) {
         treeDensity++;
      }

      if (biome == Biomes.OVERWORLD_FOREST) {
         treeDensity += k4 + 5;
      }

      if (biome == Biomes.OVERWORLD_SHRUBLAND) {
         treeDensity += k4 - 2;
      }

      if (biome == Biomes.OVERWORLD_BIRCH_FOREST) {
         treeDensity += k4 + 4;
      }

      if (biome == Biomes.OVERWORLD_RAINFOREST) {
         treeDensity += k4 + 10;
      }

      if (biome == Biomes.OVERWORLD_SEASONAL_FOREST) {
         treeDensity += k4 + 2;
      }

      if (biome == Biomes.OVERWORLD_TAIGA) {
         treeDensity += k4 + 5;
      }

      if (biome == Biomes.OVERWORLD_BOREAL_FOREST) {
         treeDensity += k4 + 3;
      }

      if (biome == Biomes.OVERWORLD_DESERT) {
         treeDensity = 0;
      }

      if (biome == Biomes.OVERWORLD_TUNDRA) {
         treeDensity -= 20;
      }

      if (biome == Biomes.OVERWORLD_DESERT) {
         treeDensity += k4;
      }

      if (biome == Biomes.OVERWORLD_CAATINGA) {
         treeDensity += k4 + 4;
      }

      if (biome == Biomes.OVERWORLD_SWAMPLAND) {
         treeDensity += k4 + 4;
      }

      if (biome == Biomes.OVERWORLD_OUTBACK_GRASSY) {
         treeDensity += k4;
      }

      if (this.treeDensityOverride != -1) {
         treeDensity = this.treeDensityOverride;
      }

      try {
         BlockLogicLeavesBase.enableDecay = false;

         for (int i11 = 0; i11 < treeDensity; i11++) {
            int l13 = x + rand.nextInt(16) + 8;
            int j14 = z + rand.nextInt(16) + 8;
            WorldFeature feature = biome.getRandomWorldGenForTrees(rand);
            feature.init(1.0, 1.0, 1.0);
            feature.place(this.world, rand, l13, this.world.getHeightValue(l13, j14), j14);
         }
      } finally {
         BlockLogicLeavesBase.enableDecay = true;
      }

      byte var106 = 0;
      if (biome == Biomes.OVERWORLD_RAINFOREST) {
         var106 = 1;
      }

      for (int i11 = 0; i11 < var106; i11++) {
         int i18 = x + rand.nextInt(16) + 8;
         int i23 = z + rand.nextInt(16) + 8;
         int i21 = this.world.getHeightValue(i18, i23);
         new WorldFeatureSugarCaneTall().place(this.world, rand, i18, i21, i23);
      }

      byte byteOrchid = 0;
      if (biome == Biomes.OVERWORLD_BIRCH_FOREST) {
         byteOrchid = 3;
      }

      if (biome == Biomes.OVERWORLD_SEASONAL_FOREST) {
         byteOrchid = 1;
      }

      for (int l14 = 0; l14 < byteOrchid; l14++) {
         int[] flowerIds = new int[]{Blocks.FLOWER_YELLOW.id(), Blocks.FLOWER_PINK.id(), Blocks.FLOWER_RED.id()};
         int l19 = x + rand.nextInt(16) + 8;
         int k22 = rand.nextInt(this.world.getHeightBlocks());
         int j24 = z + rand.nextInt(16) + 8;
         new WorldFeatureMeadow(flowerIds).place(this.world, rand, l19, k22, j24);
      }

      byte byteMeadow = 0;
      if (biome == Biomes.OVERWORLD_MEADOW) {
         byteMeadow = 3;
      }

      if (biome == Biomes.OVERWORLD_BOREAL_FOREST) {
         byteMeadow = 2;
      }

      if (biome == Biomes.OVERWORLD_SHRUBLAND) {
         byteMeadow = 1;
      }

      if (biome == Biomes.OVERWORLD_TAIGA) {
         byteMeadow = 1;
      }

      for (int l14 = 0; l14 < byteMeadow; l14++) {
         int blockId = Blocks.FLOWER_PURPLE.id();
         if (rand.nextInt(12) == 0) {
            blockId = Blocks.FLOWER_RED.id();
         }

         if (rand.nextInt(6) == 0) {
            blockId = Blocks.FLOWER_YELLOW.id();
         }

         int l19 = x + rand.nextInt(16) + 8;
         int k22 = rand.nextInt(this.world.getHeightBlocks());
         int j24 = z + rand.nextInt(16) + 8;
         new WorldFeatureFlowers(blockId, 128, true).place(this.world, rand, l19, k22, j24);
      }

      byte byteBluebells = 0;
      if (biome == Biomes.OVERWORLD_FOREST) {
         byteBluebells = 2;
      }

      if (biome == Biomes.OVERWORLD_SWAMPLAND) {
         byteBluebells = 3;
      }

      if (biome == Biomes.OVERWORLD_RAINFOREST) {
         byteBluebells = 4;
      }

      if (biome == Biomes.OVERWORLD_CAATINGA || biome == Biomes.OVERWORLD_CAATINGA_PLAINS) {
         byteBluebells = 3;
      }

      for (int i14 = 0; i14 < byteBluebells; i14++) {
         int k14 = x + rand.nextInt(16) + 8;
         int l16 = minY + rand.nextInt(rangeY);
         int k19 = z + rand.nextInt(16) + 8;
         new WorldFeatureFlowers(Blocks.FLOWER_LIGHT_BLUE.id(), 64, true).place(this.world, rand, k14, l16, k19);
      }

      byte byteMarigold = 0;
      if (biome == Biomes.OVERWORLD_GRASSLANDS) {
         byteMarigold = 1;
      }

      if (biome == Biomes.OVERWORLD_PLAINS) {
         byteMarigold = 5;
      }

      if (biome == Biomes.OVERWORLD_CAATINGA || biome == Biomes.OVERWORLD_CAATINGA_PLAINS) {
         byteMarigold = 5;
      }

      if (biome == Biomes.OVERWORLD_OUTBACK_GRASSY || biome == Biomes.OVERWORLD_OUTBACK) {
         byteMarigold = 3;
      }

      for (int i14 = 0; i14 < byteMarigold; i14++) {
         int k14 = x + rand.nextInt(16) + 8;
         int l16 = minY + rand.nextInt(rangeY);
         int k19 = z + rand.nextInt(16) + 8;
         new WorldFeatureFlowers(Blocks.FLOWER_ORANGE.id(), 64, true).place(this.world, rand, k14, l16, k19);
      }

      byte byte0 = 0;
      if (biome == Biomes.OVERWORLD_FOREST) {
         byte0 = 2;
      }

      if (biome == Biomes.OVERWORLD_SWAMPLAND) {
         byte0 = 2;
      }

      if (biome == Biomes.OVERWORLD_TAIGA) {
         byte0 = 2;
      }

      if (biome == Biomes.OVERWORLD_PLAINS) {
         byte0 = 3;
      }

      if (biome == Biomes.OVERWORLD_CAATINGA || biome == Biomes.OVERWORLD_CAATINGA_PLAINS) {
         byte0 = 5;
      }

      if (biome == Biomes.OVERWORLD_OUTBACK_GRASSY || biome == Biomes.OVERWORLD_OUTBACK) {
         byte0 = 2;
      }

      for (int i14 = 0; i14 < byte0; i14++) {
         int k14 = x + rand.nextInt(16) + 8;
         int l16 = minY + rand.nextInt(rangeY);
         int k19 = z + rand.nextInt(16) + 8;
         new WorldFeatureFlowers(Blocks.FLOWER_YELLOW.id(), 64, true).place(this.world, rand, k14, l16, k19);
      }

      byte byte1 = 0;
      if (biome == Biomes.OVERWORLD_FOREST) {
         byte1 = 2;
      }

      if (biome == Biomes.OVERWORLD_MEADOW) {
         byte1 = 2;
      }

      if (biome == Biomes.OVERWORLD_RAINFOREST) {
         byte1 = 10;
      }

      if (biome == Biomes.OVERWORLD_SEASONAL_FOREST) {
         byte1 = 2;
      }

      if (biome == Biomes.OVERWORLD_TAIGA) {
         byte1 = 1;
      }

      if (biome == Biomes.OVERWORLD_BOREAL_FOREST) {
         byte1 = 5;
      }

      if (biome == Biomes.OVERWORLD_GRASSLANDS) {
         byte1 = 8;
      }

      if (biome == Biomes.OVERWORLD_CAATINGA) {
         byte1 = 2;
      }

      if (biome == Biomes.OVERWORLD_SWAMPLAND) {
         byte1 = 4;
      }

      if (biome == Biomes.OVERWORLD_BIRCH_FOREST) {
         byte1 = 2;
      }

      for (int l14 = 0; l14 < byte1; l14++) {
         int type = Blocks.TALLGRASS.id();
         if ((
               biome == Biomes.OVERWORLD_RAINFOREST
                  || biome == Biomes.OVERWORLD_SWAMPLAND
                  || biome == Biomes.OVERWORLD_BOREAL_FOREST
                  || biome == Biomes.OVERWORLD_TAIGA
            )
            && rand.nextInt(3) != 0) {
            type = Blocks.TALLGRASS_FERN.id();
         }

         int l19 = x + rand.nextInt(16) + 8;
         int k22 = minY + rand.nextInt(rangeY);
         int j24 = z + rand.nextInt(16) + 8;
         new WorldFeatureTallGrass(type).place(this.world, rand, l19, k22, j24);
      }

      byte1 = 0;
      if (biome == Biomes.OVERWORLD_OUTBACK || biome == Biomes.OVERWORLD_OUTBACK_GRASSY) {
         byte1 = 2;
      }

      for (int i15 = 0; i15 < byte1; i15++) {
         int i17 = x + rand.nextInt(16) + 8;
         int i20 = minY + rand.nextInt(rangeY);
         int l22 = z + rand.nextInt(16) + 8;
         new WorldFeatureSpinifexPatch().place(this.world, rand, i17, i20, l22);
      }

      byte1 = 0;
      if (biome == Biomes.OVERWORLD_DESERT) {
         byte1 = 2;
      }

      if (biome == Biomes.OVERWORLD_CAATINGA_PLAINS || biome == Biomes.OVERWORLD_CAATINGA) {
         byte1 = 1;
      }

      for (int i15 = 0; i15 < byte1; i15++) {
         int i17 = x + rand.nextInt(16) + 8;
         int i20 = minY + rand.nextInt(rangeY);
         int l22 = z + rand.nextInt(16) + 8;
         new WorldFeatureDeadBush(Blocks.DEADBUSH.id()).place(this.world, rand, i17, i20, l22);
      }

      if (rand.nextInt(2) == 0) {
         int j15 = x + rand.nextInt(16) + 8;
         int j17 = minY + rand.nextInt(rangeY);
         int j20 = z + rand.nextInt(16) + 8;
         new WorldFeatureFlowers(Blocks.FLOWER_RED.id(), 64, true).place(this.world, rand, j15, j17, j20);
      }

      if (rand.nextInt(4) == 0) {
         int k15 = x + rand.nextInt(16) + 8;
         int k17 = minY + rand.nextInt(rangeY);
         int k20 = z + rand.nextInt(16) + 8;
         new WorldFeatureFlowers(Blocks.MUSHROOM_BROWN.id(), 64, false).place(this.world, rand, k15, k17, k20);
      }

      if (rand.nextInt(8) == 0) {
         int l15 = x + rand.nextInt(16) + 8;
         int l17 = minY + rand.nextInt(rangeY);
         int l20 = z + rand.nextInt(16) + 8;
         new WorldFeatureFlowers(Blocks.MUSHROOM_RED.id(), 64, false).place(this.world, rand, l15, l17, l20);
      }

      if (rand.nextInt(5) == 0) {
         int i18 = x + rand.nextInt(16) + 8;
         int i23 = z + rand.nextInt(16) + 8;
         int i21 = this.world.getHeightValue(i18, i23);
         new WorldFeatureSugarCane().place(this.world, rand, i18, i21, i23);
      }

      if (rand.nextInt(128) == 0) {
         int j16 = x + rand.nextInt(16) + 8;
         int j21 = z + rand.nextInt(16) + 8;
         int i22 = this.world.getHeightValue(j16, j21);
         new WorldFeaturePumpkin().place(this.world, rand, j16, i22, j21);
      }

      if (rand.nextInt(64) == 0) {
         int j16 = x + rand.nextInt(16) + 8;
         int j21 = z + rand.nextInt(16) + 8;
         int i22 = this.world.getHeightValue(j16, j21);
         new WorldFeatureSponge().place(this.world, rand, j16, i22, j21);
      }

      int k16 = 0;
      if (biome == Biomes.OVERWORLD_DESERT) {
         k16 += 10;
      }

      if (biome == Biomes.OVERWORLD_CAATINGA || biome == Biomes.OVERWORLD_CAATINGA_PLAINS) {
         k16 += 10;
      }

      for (int k18 = 0; k18 < k16; k18++) {
         int k21 = x + rand.nextInt(16) + 8;
         int j23 = minY + rand.nextInt(rangeY);
         int k24 = z + rand.nextInt(16) + 8;
         new WorldFeatureCactus().place(this.world, rand, k21, j23, k24);
      }

      for (int l18 = 0; l18 < 50; l18++) {
         int l21 = x + rand.nextInt(16) + 8;
         int k23 = minY + rand.nextInt(rand.nextInt(rangeY - rangeY / 16) + rangeY / 16);
         int l24 = z + rand.nextInt(16) + 8;
         new WorldFeatureLiquid(Blocks.FLUID_WATER_FLOWING.id()).place(this.world, rand, l21, k23, l24);
      }

      for (int i19 = 0; i19 < 20; i19++) {
         int i22 = x + rand.nextInt(16) + 8;
         int l23 = minY + rand.nextInt(rand.nextInt(rand.nextInt(rangeY - rangeY / 8) + rangeY / 16) + rangeY / 16);
         int i25 = z + rand.nextInt(16) + 8;
         new WorldFeatureLiquid(Blocks.FLUID_LAVA_FLOWING.id()).place(this.world, rand, i22, l23, i25);
      }

      int oceanY = this.world.getWorldType().getOceanY();

      for (int dx = x + 8; dx < x + 8 + 16; dx++) {
         for (int dzx = z + 8; dzx < z + 8 + 16; dzx++) {
            int dy = this.world.getHeightValue(dx, dzx);
            Biome localBiome = this.world.getBlockBiome(dx, dy, dzx);
            if ((localBiome.hasSurfaceSnow() || this.world.worldType == WorldTypes.OVERWORLD_WINTER)
               && dy > 0
               && dy < this.world.getHeightBlocks()
               && this.world.isAirBlock(dx, dy, dzx)
               && this.world.getBlockMaterial(dx, dy - 1, dzx).blocksMotion()) {
               this.world.setBlockWithNotify(dx, dy, dzx, Blocks.LAYER_SNOW.id());
            }

            if ((localBiome.hasSurfaceSnow() || this.world.worldType == WorldTypes.OVERWORLD_WINTER)
               && (
                  this.world.getBlockId(dx, oceanY - 1, dzx) == Blocks.FLUID_WATER_STILL.id()
                     || this.world.getBlockId(dx, oceanY - 1, dzx) == Blocks.FLUID_WATER_FLOWING.id()
               )) {
               this.world.setBlockWithNotify(dx, oceanY - 1, dzx, Blocks.ICE.id());
            }
         }
      }

      BlockLogicSand.fallInstantly = false;
      this.world.scheduledUpdatesAreImmediate = false;
   }
}
