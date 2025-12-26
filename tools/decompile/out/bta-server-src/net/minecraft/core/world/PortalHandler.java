package net.minecraft.core.world;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicPortal;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.type.WorldType;
import org.jetbrains.annotations.Nullable;

public class PortalHandler {
   private final Random rand = new Random();

   public void teleportEntity(World world, Entity entity, @Nullable DyeColor portalColor, Dimension oldDim, Dimension newDim) {
      if (!this.attemptToTeleportToClosestPortal(world, entity, portalColor, oldDim, newDim)) {
         this.generatePortal(world, entity, portalColor, oldDim, newDim);
         this.attemptToTeleportToClosestPortal(world, entity, portalColor, oldDim, newDim);
      }
   }

   public boolean attemptToTeleportToClosestPortal(World world, Entity entity, @Nullable DyeColor portalColor, Dimension oldDim, Dimension newDim) {
      int searchRadius = 128;
      double lowestEntityDistanceSquaredXZ = -1.0;
      double lowestEntityDistanceSquaredY = -1.0;
      int closestPortalX = 0;
      int closestPortalY = 0;
      int closestPortalZ = 0;
      int entityBlockX = MathHelper.floor(entity.x);
      int entityBlockZ = MathHelper.floor(entity.z);
      WorldType oldWorldType = oldDim.getDimensionData(world).getWorldType();
      WorldType newWorldType = newDim.getDimensionData(world).getWorldType();
      double entityPosYScaled = entity.y - entity.heightOffset;
      int oldDimRangeY = oldWorldType.getMaxPortalY() - oldWorldType.getMinPortalY();
      entityPosYScaled -= oldWorldType.getMinPortalY();
      entityPosYScaled /= oldDimRangeY;
      int newDimRangeY = newWorldType.getMaxPortalY() - newWorldType.getMinPortalY();
      entityPosYScaled *= newDimRangeY;
      entityPosYScaled += newWorldType.getMinPortalY();
      entityPosYScaled = MathHelper.clamp(entityPosYScaled, (double)newWorldType.getMinPortalY(), (double)newWorldType.getMaxPortalY());
      Block<? extends BlockLogicPortal> targetPortal;
      if (newDim.homeDim == null) {
         targetPortal = oldDim.portalBlock;
      } else {
         targetPortal = newDim.portalBlock;
      }

      for (int dx = entityBlockX - searchRadius; dx <= entityBlockX + searchRadius; dx++) {
         double xEntityDistance = dx + 0.5 - entity.x;

         for (int dz = entityBlockZ - searchRadius; dz <= entityBlockZ + searchRadius; dz++) {
            double zEntityDistance = dz + 0.5 - entity.z;

            for (int dy = newWorldType.getMaxPortalY() - 1; dy >= newWorldType.getMinPortalY(); dy--) {
               if (world.getBlock(dx, dy, dz) == targetPortal
                  && (portalColor == null || targetPortal == null || targetPortal.getLogic().getColor(world, dx, dy, dz) == portalColor)) {
                  while (world.getBlock(dx, dy - 1, dz) == targetPortal) {
                     dy--;
                  }

                  double entityDistanceSquared = xEntityDistance * xEntityDistance + zEntityDistance * zEntityDistance;
                  if (lowestEntityDistanceSquaredXZ < 0.0 || entityDistanceSquared < lowestEntityDistanceSquaredXZ) {
                     lowestEntityDistanceSquaredXZ = entityDistanceSquared;
                     closestPortalX = dx;
                     closestPortalY = dy;
                     closestPortalZ = dz;
                  }
               }
            }
         }
      }

      if (lowestEntityDistanceSquaredXZ >= 0.0) {
         int cx = closestPortalX;
         int cz = closestPortalZ;

         for (int dx = closestPortalX - 1; dx <= cx + 1; dx++) {
            double xEntityDistance = dx + 0.5 - entity.x;

            for (int dz = cz - 1; dz <= cz + 1; dz++) {
               double zEntityDistance = dz + 0.5 - entity.z;

               for (int dyx = newWorldType.getMaxPortalY() - 1; dyx >= newWorldType.getMinPortalY(); dyx--) {
                  if (world.getBlock(dx, dyx, dz) == targetPortal
                     && (portalColor == null || targetPortal == null || targetPortal.getLogic().getColor(world, dx, dyx, dz) == portalColor)) {
                     while (world.getBlock(dx, dyx - 1, dz) == targetPortal) {
                        dyx--;
                     }

                     double yEntityDistance = dyx + 0.5 - entityPosYScaled;
                     double entityDistanceSquared = xEntityDistance * xEntityDistance + yEntityDistance * yEntityDistance + zEntityDistance * zEntityDistance;
                     if (lowestEntityDistanceSquaredY < 0.0 || entityDistanceSquared < lowestEntityDistanceSquaredY) {
                        lowestEntityDistanceSquaredY = entityDistanceSquared;
                        closestPortalX = dx;
                        closestPortalY = dyx;
                        closestPortalZ = dz;
                     }
                  }
               }
            }
         }
      }

      if (lowestEntityDistanceSquaredY >= 0.0) {
         double newEntityX = closestPortalX + 0.5;
         double newEntityY = closestPortalY + 0.5;
         double newEntityZ = closestPortalZ + 0.5;
         if (world.getBlock(closestPortalX - 1, closestPortalY, closestPortalZ) == targetPortal) {
            newEntityX -= 0.5;
         }

         if (world.getBlock(closestPortalX + 1, closestPortalY, closestPortalZ) == targetPortal) {
            newEntityX += 0.5;
         }

         if (world.getBlock(closestPortalX, closestPortalY, closestPortalZ - 1) == targetPortal) {
            newEntityZ -= 0.5;
         }

         if (world.getBlock(closestPortalX, closestPortalY, closestPortalZ + 1) == targetPortal) {
            newEntityZ += 0.5;
         }

         entity.moveTo(newEntityX, newEntityY - 0.5, newEntityZ, entity.yRot, 0.0F);
         entity.xd = entity.yd = entity.zd = 0.0;
         return true;
      } else {
         return false;
      }
   }

   public boolean generatePortal(World world, Entity entity, @Nullable DyeColor portalColor, Dimension oldDim, Dimension newDim) {
      WorldType oldWorldType = oldDim.getDimensionData(world).getWorldType();
      WorldType newWorldType = newDim.getDimensionData(world).getWorldType();
      double entityPosYScaled = entity.y - entity.heightOffset;
      int oldDimRangeY = oldWorldType.getMaxPortalY() - oldWorldType.getMinPortalY();
      entityPosYScaled -= oldWorldType.getMinPortalY();
      entityPosYScaled /= oldDimRangeY;
      int newDimRangeY = newWorldType.getMaxPortalY() - newWorldType.getMinPortalY();
      entityPosYScaled *= newDimRangeY;
      entityPosYScaled += newWorldType.getMinPortalY();
      entityPosYScaled = MathHelper.clamp(entityPosYScaled, (double)newWorldType.getMinPortalY(), (double)newWorldType.getMaxPortalY());
      byte portalSearchRadius = 16;
      double lowestEntityDistance = -1.0;
      int blockX = MathHelper.floor(entity.x);
      int blockY = MathHelper.floor(entityPosYScaled);
      int blockZ = MathHelper.floor(entity.z);
      int closestPortalX = blockX;
      int closestPortalY = blockY;
      int closestPortalZ = blockZ;
      int k1 = 0;
      int l1 = this.rand.nextInt(4);
      Block<?> targetPortalFrame;
      Block<? extends BlockLogicPortal> targetPortal;
      if (newDim.homeDim == null) {
         targetPortalFrame = oldDim.portalBlock.getLogic().portalFrame;
         targetPortal = oldDim.portalBlock;
      } else {
         targetPortalFrame = newDim.portalBlock.getLogic().portalFrame;
         targetPortal = newDim.portalBlock;
      }

      for (int dx = blockX - portalSearchRadius; dx <= blockX + portalSearchRadius; dx++) {
         double xEntityDistance = dx + 0.5 - entity.x;

         for (int dz = blockZ - portalSearchRadius; dz <= blockZ + portalSearchRadius; dz++) {
            double zEntityDistance = dz + 0.5 - entity.z;

            label296:
            for (int dy = newWorldType.getMaxPortalY() - 1; dy >= newWorldType.getMinPortalY(); dy--) {
               if (world.isAirBlock(dx, dy, dz)) {
                  while (dy > 0 && world.isAirBlock(dx, dy - 1, dz)) {
                     dy--;
                  }

                  for (int k5 = l1; k5 < l1 + 4; k5++) {
                     int frameXMult = k5 % 2;
                     int frameZMult = 1 - frameXMult;
                     if (k5 % 4 >= 2) {
                        frameXMult = -frameXMult;
                        frameZMult = -frameZMult;
                     }

                     for (int j9 = 0; j9 < 3; j9++) {
                        for (int xzOffset = 0; xzOffset < 4; xzOffset++) {
                           for (int yOffset = -1; yOffset < 4; yOffset++) {
                              int lookupX = dx + (xzOffset - 1) * frameXMult + j9 * frameZMult;
                              int lookupY = dy + yOffset;
                              int lookupZ = dz + (xzOffset - 1) * frameZMult - j9 * frameXMult;
                              if (yOffset < 0 && !world.getBlockMaterial(lookupX, lookupY, lookupZ).isSolid()
                                 || yOffset >= 0 && !world.isAirBlock(lookupX, lookupY, lookupZ)) {
                                 continue label296;
                              }
                           }
                        }
                     }

                     double yEntityDistance = dy + 0.5 - entityPosYScaled;
                     double entityDistanceSquared = xEntityDistance * xEntityDistance + yEntityDistance * yEntityDistance + zEntityDistance * zEntityDistance;
                     if (lowestEntityDistance < 0.0 || entityDistanceSquared < lowestEntityDistance) {
                        lowestEntityDistance = entityDistanceSquared;
                        closestPortalX = dx;
                        closestPortalY = dy;
                        closestPortalZ = dz;
                        k1 = k5 % 4;
                     }
                  }
               }
            }
         }
      }

      if (lowestEntityDistance < 0.0) {
         for (int dx = blockX - portalSearchRadius; dx <= blockX + portalSearchRadius; dx++) {
            double entityDistanceX = dx + 0.5 - entity.x;

            for (int dz = blockZ - portalSearchRadius; dz <= blockZ + portalSearchRadius; dz++) {
               double entityDistanceZ = dz + 0.5 - entity.z;

               label233:
               for (int dyx = newWorldType.getMaxPortalY() - 1; dyx >= newWorldType.getMinPortalY(); dyx--) {
                  if (world.isAirBlock(dx, dyx, dz)) {
                     while (world.isAirBlock(dx, dyx - 1, dz) && dyx > 0) {
                        dyx--;
                     }

                     for (int l5 = l1; l5 < l1 + 2; l5++) {
                        int i7 = l5 % 2;
                        int j8 = 1 - i7;

                        for (int k9 = 0; k9 < 4; k9++) {
                           for (int yOffsetx = -1; yOffsetx < 4; yOffsetx++) {
                              int lookupX = dx + (k9 - 1) * i7;
                              int lookupY = dyx + yOffsetx;
                              int lookupZ = dz + (k9 - 1) * j8;
                              if (yOffsetx < 0 && !world.getBlockMaterial(lookupX, lookupY, lookupZ).isSolid()
                                 || yOffsetx >= 0 && !world.isAirBlock(lookupX, lookupY, lookupZ)) {
                                 continue label233;
                              }
                           }
                        }

                        double entityDistanceY = dyx + 0.5 - entityPosYScaled;
                        double entityDistanceSquared = entityDistanceX * entityDistanceX
                           + entityDistanceY * entityDistanceY
                           + entityDistanceZ * entityDistanceZ;
                        if (lowestEntityDistance < 0.0 || entityDistanceSquared < lowestEntityDistance) {
                           lowestEntityDistance = entityDistanceSquared;
                           closestPortalX = dx;
                           closestPortalY = dyx;
                           closestPortalZ = dz;
                           k1 = l5 % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int portalX = closestPortalX;
      int portalY = closestPortalY;
      int portalZ = closestPortalZ;
      int frameXMultx = k1 % 2;
      int frameZMultx = 1 - frameXMultx;
      if (k1 % 4 >= 2) {
         frameXMultx = -frameXMultx;
         frameZMultx = -frameZMultx;
      }

      if (lowestEntityDistance < 0.0) {
         portalY = closestPortalY;

         for (int i5 = -1; i5 <= 1; i5++) {
            for (int i6 = 1; i6 < 3; i6++) {
               for (int j7 = -1; j7 < 3; j7++) {
                  int x = portalX + (i6 - 1) * frameXMultx + i5 * frameZMultx;
                  int y = portalY + j7;
                  int z = portalZ + (i6 - 1) * frameZMultx - i5 * frameXMultx;
                  boolean flag = j7 < 0;
                  world.setBlockWithNotify(x, y, z, flag ? targetPortalFrame.id() : 0);
               }
            }
         }
      }

      for (int j5 = 0; j5 < 4; j5++) {
         world.noNeighborUpdate = true;

         for (int xzOffset = 0; xzOffset < 4; xzOffset++) {
            for (int yOffsetxx = -1; yOffsetxx < 4; yOffsetxx++) {
               int lookupX = portalX + (xzOffset - 1) * frameXMultx;
               int lookupY = portalY + yOffsetxx;
               int lookupZ = portalZ + (xzOffset - 1) * frameZMultx;
               boolean isPortalBorder = xzOffset == 0 || xzOffset == 3 || yOffsetxx == -1 || yOffsetxx == 3;
               world.setBlockWithNotify(lookupX, lookupY, lookupZ, isPortalBorder ? targetPortalFrame.id() : 0);
            }
         }

         targetPortal.getLogic().tryToCreatePortal(world, portalX, portalY, portalZ, portalColor);
         world.noNeighborUpdate = false;

         for (int k6 = 0; k6 < 4; k6++) {
            for (int l7 = -1; l7 < 4; l7++) {
               int i9 = portalX + (k6 - 1) * frameXMultx;
               int j10 = portalY + l7;
               int k11 = portalZ + (k6 - 1) * frameZMultx;
               world.notifyBlocksOfNeighborChange(i9, j10, k11, world.getBlockId(i9, j10, k11));
            }
         }
      }

      return true;
   }
}
