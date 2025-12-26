package net.minecraft.core.world.generate.chunk.perlin.nether;

import java.util.Random;
import net.minecraft.core.block.BlockLogicSand;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;
import net.minecraft.core.world.generate.feature.WorldFeatureFire;
import net.minecraft.core.world.generate.feature.WorldFeatureGlowstoneA;
import net.minecraft.core.world.generate.feature.WorldFeatureGlowstoneB;
import net.minecraft.core.world.generate.feature.WorldFeatureLake;
import net.minecraft.core.world.generate.feature.WorldFeatureNetherLava;
import net.minecraft.core.world.generate.feature.WorldFeatureOre;
import net.minecraft.core.world.generate.feature.WorldFeaturePumice;

public class ChunkDecoratorNether implements ChunkDecorator {
   private final World world;

   public ChunkDecoratorNether(World world) {
      this.world = world;
   }

   @Override
   public void decorate(Chunk chunk) {
      int chunkX = chunk.xPosition;
      int chunkZ = chunk.zPosition;
      int minY = this.world.getWorldType().getMinY();
      int maxY = this.world.getWorldType().getMaxY();
      int rangeY = maxY + 1 - minY;
      Random rand = new Random(chunkX * 341873128712L + chunkZ * 132897987541L);
      BlockLogicSand.fallInstantly = true;
      int x = chunkX * 16;
      int z = chunkZ * 16;

      for (int i = 0; i < 8; i++) {
         int xf = x + rand.nextInt(16) + 8;
         int yf = minY + rand.nextInt(rangeY - 8) + 4;
         int zf = z + rand.nextInt(16) + 8;
         new WorldFeatureNetherLava(Blocks.FLUID_LAVA_FLOWING.id()).place(this.world, rand, xf, yf, zf);
      }

      for (int i = 0; i < 10; i++) {
         int xf = x + rand.nextInt(16);
         int yf = minY + rand.nextInt(rangeY - 8) + 4;
         int zf = z + rand.nextInt(16);
         new WorldFeatureOre(Blocks.ORE_NETHERCOAL_NETHERRACK.id(), 12).place(this.world, rand, xf, yf, zf);
      }

      int max = rand.nextInt(rand.nextInt(10) + 1);

      for (int i = 0; i < max; i++) {
         int xf = x + rand.nextInt(16) + 8;
         int yf = minY + rand.nextInt(rangeY - 8) + 4;
         int zf = z + rand.nextInt(16) + 8;
         new WorldFeatureFire().place(this.world, rand, xf, yf, zf);
      }

      max = rand.nextInt(rand.nextInt(10) + 1);

      for (int i = 0; i < max; i++) {
         int xf = x + rand.nextInt(16) + 8;
         int yf = minY + rand.nextInt(rangeY - 8) + 4;
         int zf = z + rand.nextInt(16) + 8;
         new WorldFeatureGlowstoneA().place(this.world, rand, xf, yf, zf);
      }

      for (int i = 0; i < 10; i++) {
         int xf = x + rand.nextInt(16) + 8;
         int yf = minY + rand.nextInt(rangeY - 8) + 4;
         int zf = z + rand.nextInt(16) + 8;
         new WorldFeatureGlowstoneB().place(this.world, rand, xf, yf, zf);
      }

      if (rand.nextInt(8) == 0) {
         int xf = x + rand.nextInt(16) + 8;
         int yf = minY + rand.nextInt(rangeY - 16) + 8;
         int zf = z + rand.nextInt(16) + 8;
         new WorldFeatureLake(Blocks.FLUID_LAVA_STILL.id()).place(this.world, rand, xf, yf, zf);
      }

      for (int i = 0; i < 80; i++) {
         int xf = x + rand.nextInt(16);
         int yf = minY + rand.nextInt(rangeY);
         int zf = z + rand.nextInt(16);
         new WorldFeaturePumice(32).place(this.world, rand, xf, yf, zf);
      }

      BlockLogicSand.fallInstantly = false;
   }
}
