package net.minecraft.core.world.wind;

import java.util.Random;
import net.minecraft.core.world.World;
import net.minecraft.core.world.noise.ImprovedNoise;

public class WindProviderGeneric extends WindProvider {
   private final double perlinMax = Math.sqrt(0.75);
   private long randomSeed = -1L;
   private Random directionRandom = null;
   private Random intensityRandom = null;
   private ImprovedNoise directionNoise = null;
   private ImprovedNoise intensityNoise = null;
   private long lastSampleTime = -1L;
   private double lastDirectionSample = 0.0;
   private double lastIntensitySample = 0.0;

   private void initialize(World world) {
      if (this.randomSeed != world.getRandomSeed()
         || this.directionRandom == null
         || this.intensityRandom == null
         || this.directionNoise == null
         || this.intensityNoise == null) {
         this.randomSeed = world.getRandomSeed();
         this.directionRandom = new Random(this.randomSeed * 16237L);
         this.directionNoise = new ImprovedNoise(this.directionRandom);
         this.intensityRandom = new Random(this.randomSeed * 3183L);
         this.intensityNoise = new ImprovedNoise(this.intensityRandom);
      }
   }

   private void resample(World world) {
      if (this.lastSampleTime != world.getWorldTime()) {
         this.lastSampleTime = world.getWorldTime();
         this.lastDirectionSample = this.directionNoise.getValue((float)world.getWorldTime() / 2500.0F, 0.0);
         this.lastIntensitySample = this.intensityNoise.getValue((float)world.getWorldTime() / 150.0F, 0.0);
      }
   }

   @Override
   public float getWindDirection(World world, float x, float y, float z) {
      this.initialize(world);
      this.resample(world);
      double direction = this.lastDirectionSample;
      direction /= this.perlinMax;
      int avgChunkHeight = world.getChunkFromBlockCoords((int)x, (int)z).averageBlockHeight;
      return (float)direction;
   }

   @Override
   public float getWindIntensity(World world, float x, float y, float z) {
      this.initialize(world);
      this.resample(world);
      double intensity = this.lastIntensitySample;
      intensity += this.perlinMax;
      intensity /= 2.0 * this.perlinMax;
      float yFloat = y / world.getHeightBlocks();
      int avgChunkHeight = world.getChunkFromBlockCoords((int)x, (int)z).averageBlockHeight;
      if (yFloat < 0.0F) {
         yFloat = 0.0F;
      }

      if (yFloat > 1.0F) {
         yFloat = 1.0F;
      }

      if (yFloat < 0.4) {
         float percentage = yFloat / 0.4F;
         intensity *= 0.25F * percentage;
      } else if (yFloat >= 0.4F && yFloat < 0.6F) {
         float percentage = (yFloat - 0.4F) / 0.2F;
         intensity *= 0.25F + 0.5F * percentage;
      } else if (yFloat >= 0.6F) {
         float percentage = (yFloat - 0.6F) / 0.4F;
         intensity *= 0.75F + 0.25F * percentage;
      }

      if (y < avgChunkHeight) {
         intensity = 0.0;
      }

      return (float)intensity;
   }
}
