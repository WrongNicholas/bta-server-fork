package net.minecraft.core.world;

import java.util.Random;
import net.minecraft.core.world.noise.ImprovedNoise;

public class AuroraProvider {
   private final ImprovedNoise noise;
   private final World world;

   public AuroraProvider(World world, long seed) {
      this.world = world;
      this.noise = new ImprovedNoise(new Random(seed));
   }

   public double getAuroraPresence(double x, double z, long worldTime, float celestialAngle) {
      double auroraLevel = 0.0;
      double auroraRadius = 0.03F;
      if (!(celestialAngle < 0.25F) && !(celestialAngle > 0.75F)) {
         celestialAngle = (celestialAngle - 0.25F) * 2.0F;
         celestialAngle = celestialAngle * 2.0F - 1.0F;
         celestialAngle = Math.abs(celestialAngle);
         celestialAngle = -celestialAngle + 1.0F;
         double mult = celestialAngle;
         double sample = this.noise.getValue(x / 1000.0, worldTime / 5000.0, z / 1000.0);
         sample += this.noise.getValue(x / 30.0, worldTime / 50.0, z / 30.0) * 0.05;
         if (!(sample < auroraLevel - auroraRadius) && !(sample > auroraLevel + auroraRadius)) {
            sample -= auroraLevel;
            sample /= auroraRadius;
            sample = Math.abs(sample);
            sample = -sample;
            return ++sample * mult;
         } else {
            return 0.0;
         }
      } else {
         return 0.0;
      }
   }

   public double getAuroraHeightOffset(double x, double z, double worldTime) {
      return this.noise.getValue(x / 100.0, worldTime / 5000.0, z / 100.0) * 25.0;
   }

   public float getAuroraRedOffset(double x, double z, double worldTime) {
      double sample = this.noise.getValue(x / 500.0, worldTime / 5000.0 + 10000.0, z / 500.0);
      double sample2 = this.noise.getValue(x / 200.0, worldTime / 5000.0, z / 200.0);
      if (sample < 0.0) {
         sample = 0.0;
      }

      if (sample > 0.25) {
         sample = 0.25;
      }

      if (sample2 < 0.0) {
         sample2 = 0.0;
      }

      if (sample2 > 0.1F) {
         sample2 = 0.1F;
      }

      return (float)(sample * 3.0 + sample2 * 2.0);
   }

   public float getAuroraGreenOffset(double x, double z, double worldTime) {
      double sample = this.noise.getValue(x / 500.0, worldTime / 5000.0 + 10000.0, z / 500.0);
      double sample2 = this.noise.getValue(x / 500.0, worldTime / 5000.0, z / 500.0);
      if (sample < 0.0) {
         sample = 0.0;
      }

      if (sample > 0.125) {
         sample = 0.125;
      }

      if (sample2 < 0.0) {
         sample2 = 0.0;
      }

      if (sample2 > 0.25) {
         sample2 = 0.25;
      }

      return (float)(sample * 4.0 - sample2 * 2.0);
   }

   public float getAuroraBlueOffset(double x, double z, double worldTime) {
      double sample = this.noise.getValue(x / 500.0, worldTime / 5000.0, z / 500.0);
      if (sample < 0.0) {
         sample = 0.0;
      }

      if (sample > 0.25) {
         sample = 0.25;
      }

      return (float)sample;
   }
}
