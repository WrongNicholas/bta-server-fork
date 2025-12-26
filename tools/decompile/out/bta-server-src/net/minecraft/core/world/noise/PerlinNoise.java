package net.minecraft.core.world.noise;

import java.util.Random;

public class PerlinNoise extends BasePerlinNoise<ImprovedNoise> {
   public PerlinNoise(long seed, int levels) {
      super(seed, levels);
   }

   public PerlinNoise(long seed, int levels, int preLevels) {
      super(seed, levels, preLevels);
   }

   protected ImprovedNoise[] newNoiseLevels(Random random, int numLevels) {
      ImprovedNoise[] levels = new ImprovedNoise[numLevels];

      for (int i = 0; i < numLevels; i++) {
         levels[i] = new ImprovedNoise(random);
      }

      return levels;
   }
}
