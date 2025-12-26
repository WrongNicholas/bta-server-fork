package net.minecraft.core.world.noise;

import java.util.Random;

public class RetroPerlinNoise extends BasePerlinNoise<RetroImprovedNoise> {
   public RetroPerlinNoise(long seed, int levels) {
      super(seed, levels);
   }

   public RetroPerlinNoise(long seed, int levels, int preLevels) {
      super(seed, levels, preLevels);
   }

   protected RetroImprovedNoise[] newNoiseLevels(Random random, int numLevels) {
      RetroImprovedNoise[] levels = new RetroImprovedNoise[numLevels];

      for (int i = 0; i < numLevels; i++) {
         levels[i] = new RetroImprovedNoise(random);
      }

      return levels;
   }
}
