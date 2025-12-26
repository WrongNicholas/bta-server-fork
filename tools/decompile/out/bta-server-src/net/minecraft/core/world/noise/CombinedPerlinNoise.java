package net.minecraft.core.world.noise;

public class CombinedPerlinNoise {
   private final BasePerlinNoise<?> perlinNoiseA;
   private final BasePerlinNoise<?> perlinNoiseB;

   public CombinedPerlinNoise(BasePerlinNoise<?> perlinNoiseA, BasePerlinNoise<?> perlinNoiseB) {
      this.perlinNoiseA = perlinNoiseA;
      this.perlinNoiseB = perlinNoiseB;
   }

   public double get(double x, double y) {
      return this.perlinNoiseA.get(x + this.perlinNoiseB.get(x, y), y);
   }
}
