package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.World;

public class WorldFeatureLake extends WorldFeature {
   private int liquidId;

   @MethodParametersAnnotation(names = "liquidId")
   public WorldFeatureLake(int liquidId) {
      this.liquidId = liquidId;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      x -= 8;
      z -= 8;

      while (y > 0 && world.isAirBlock(x, y, z)) {
         y--;
      }

      y -= 4;
      boolean[] shouldPlaceLiquid = new boolean[2048];
      int l = random.nextInt(4) + 4;

      for (int i = 0; i < l; i++) {
         double d = random.nextDouble() * 6.0 + 3.0;
         double d1 = random.nextDouble() * 4.0 + 2.0;
         double d2 = random.nextDouble() * 6.0 + 3.0;
         double d3 = random.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
         double d4 = random.nextDouble() * (8.0 - d1 - 4.0) + 2.0 + d1 / 2.0;
         double d5 = random.nextDouble() * (16.0 - d2 - 2.0) + 1.0 + d2 / 2.0;

         for (int dx = 1; dx < 15; dx++) {
            for (int dz = 1; dz < 15; dz++) {
               for (int dy = 1; dy < 7; dy++) {
                  double d6 = (dx - d3) / (d / 2.0);
                  double d7 = (dy - d4) / (d1 / 2.0);
                  double d8 = (dz - d5) / (d2 / 2.0);
                  double d9 = d6 * d6 + d7 * d7 + d8 * d8;
                  if (d9 < 1.0) {
                     shouldPlaceLiquid[(dx * 16 + dz) * 8 + dy] = true;
                  }
               }
            }
         }
      }

      for (int dx = 0; dx < 16; dx++) {
         for (int dz = 0; dz < 16; dz++) {
            for (int dyx = 0; dyx < 8; dyx++) {
               boolean flag = !shouldPlaceLiquid[(dx * 16 + dz) * 8 + dyx]
                  && (
                     dx < 15 && shouldPlaceLiquid[((dx + 1) * 16 + dz) * 8 + dyx]
                        || dx > 0 && shouldPlaceLiquid[((dx - 1) * 16 + dz) * 8 + dyx]
                        || dz < 15 && shouldPlaceLiquid[(dx * 16 + dz + 1) * 8 + dyx]
                        || dz > 0 && shouldPlaceLiquid[(dx * 16 + (dz - 1)) * 8 + dyx]
                        || dyx < 7 && shouldPlaceLiquid[(dx * 16 + dz) * 8 + dyx + 1]
                        || dyx > 0 && shouldPlaceLiquid[(dx * 16 + dz) * 8 + (dyx - 1)]
                  );
               if (flag) {
                  Material material = world.getBlockMaterial(x + dx, y + dyx, z + dz);
                  if (dyx >= 4 && material.isLiquid()) {
                     return false;
                  }

                  if (dyx < 4 && !material.isSolid() && world.getBlockId(x + dx, y + dyx, z + dz) != this.liquidId) {
                     return false;
                  }

                  if (world.getBlockId(x + dx, y + dyx, z + dz) == Blocks.ICE.id()) {
                     return false;
                  }
               }
            }
         }
      }

      for (int dx = 0; dx < 16; dx++) {
         for (int dz = 0; dz < 16; dz++) {
            for (int dyxx = 0; dyxx < 8; dyxx++) {
               if (shouldPlaceLiquid[(dx * 16 + dz) * 8 + dyxx] && world.getBlockId(x + dx, y + dyxx, z + dz) != Blocks.BEDROCK.id()) {
                  world.setBlockWithNotify(x + dx, y + dyxx, z + dz, dyxx < 4 ? this.liquidId : 0);
               }
            }
         }
      }

      for (int dx = 0; dx < 16; dx++) {
         for (int dz = 0; dz < 16; dz++) {
            for (int dyxxx = 4; dyxxx < 8; dyxxx++) {
               if (shouldPlaceLiquid[(dx * 16 + dz) * 8 + dyxxx]
                  && world.getBlockId(x + dx, y + dyxxx - 1, z + dz) != Blocks.BEDROCK.id()
                  && world.getBlockId(x + dx, y + dyxxx - 1, z + dz) == Blocks.DIRT.id()
                  && world.getSavedLightValue(LightLayer.Sky, x + dx, y + dyxxx, z + dz) > 0) {
                  world.setBlock(x + dx, y + dyxxx - 1, z + dz, Blocks.GRASS.id());
               }
            }
         }
      }

      if (Blocks.blocksList[this.liquidId].getMaterial() == Material.lava) {
         for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
               for (int dyxxxx = 0; dyxxxx < 8; dyxxxx++) {
                  boolean flag1 = !shouldPlaceLiquid[(dx * 16 + dz) * 8 + dyxxxx]
                     && (
                        dx < 15 && shouldPlaceLiquid[((dx + 1) * 16 + dz) * 8 + dyxxxx]
                           || dx > 0 && shouldPlaceLiquid[((dx - 1) * 16 + dz) * 8 + dyxxxx]
                           || dz < 15 && shouldPlaceLiquid[(dx * 16 + dz + 1) * 8 + dyxxxx]
                           || dz > 0 && shouldPlaceLiquid[(dx * 16 + (dz - 1)) * 8 + dyxxxx]
                           || dyxxxx < 7 && shouldPlaceLiquid[(dx * 16 + dz) * 8 + dyxxxx + 1]
                           || dyxxxx > 0 && shouldPlaceLiquid[(dx * 16 + dz) * 8 + (dyxxxx - 1)]
                     );
                  if (flag1
                     && (dyxxxx < 4 || random.nextInt(2) != 0)
                     && world.getBlockId(x + dx, y + dyxxxx, z + dz) != Blocks.BEDROCK.id()
                     && world.getBlockMaterial(x + dx, y + dyxxxx, z + dz).isSolid()) {
                     world.setBlock(x + dx, y + dyxxxx, z + dz, world.getWorldType().getFillerBlockId());
                  }
               }
            }
         }
      }

      return true;
   }
}
