package net.minecraft.core.world.weather;

import java.util.List;
import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLightning;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;

public class WeatherStorm extends WeatherRain {
   public WeatherStorm(int id) {
      super(id);
   }

   @Override
   public float[] modifyFogColor(float r, float g, float b, float intensity) {
      float[] out = super.modifyFogColor(r, g, b, intensity);
      float f9 = 1.0F - intensity * 0.5F;
      out[0] *= f9;
      out[1] *= f9;
      out[2] *= f9;
      return out;
   }

   @Override
   public void doEnvironmentUpdate(World world, Random rand, int x, int z) {
      super.doEnvironmentUpdate(world, rand, x, z);
      if (rand.nextInt((int)(100000.0F * (1.0F / world.weatherManager.getWeatherPower()))) == 0) {
         int searchRadius = 32;
         int maxWeight = Integer.MIN_VALUE;
         int targetX = x;
         int targetY = world.getHeightValue(x, z);
         int targetZ = z;

         for (int _x = -32; _x <= 32; _x++) {
            for (int _z = -32; _z < 32; _z++) {
               int checkX = x + _x;
               int checkZ = z + _z;
               int checkY = world.getHeightValue(checkX, checkZ);
               int weight = this.evalStrikeWeight(world, rand, x, z, checkX, checkY, checkZ);
               if (weight > maxWeight) {
                  maxWeight = weight;
                  targetX = checkX;
                  targetY = checkY;
                  targetZ = checkZ;
               }
            }
         }

         if (world.canBlockBeRainedOn(targetX, targetY, targetZ)) {
            world.addWeatherEffect(new EntityLightning(world, targetX, targetY, targetZ));
         }
      }
   }

   public int evalStrikeWeight(World world, Random random, int orgX, int orgZ, int checkX, int checkY, int checkZ) {
      Material material = world.getBlockMaterial(checkX, checkY - 1, checkZ);
      int weight = random.nextInt(10);
      weight += checkY;
      weight += material.getConductivity();
      List<Mob> entityList = world.getEntitiesWithinAABB(Mob.class, AABB.getTemporaryBB(checkX, checkY, checkZ, checkX + 1, checkY + 1, checkZ + 1));
      if (!entityList.isEmpty()) {
         weight += 4;
      }

      int distX = checkX - orgX;
      int distZ = checkZ - orgZ;
      double dist = Math.sqrt(distX * distX + distZ + distZ);
      int decayFactor = MathHelper.floor(Math.sqrt(dist) / 4.0);
      return weight - decayFactor;
   }
}
