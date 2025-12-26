package net.minecraft.core.world.chunk;

import com.mojang.logging.LogUtils;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.World;
import org.slf4j.Logger;

public class LightUpdate {
   private static final Logger LOGGER = LogUtils.getLogger();
   public final LightLayer layer;
   public int minX;
   public int minY;
   public int minZ;
   public int maxX;
   public int maxY;
   public int maxZ;

   public LightUpdate(LightLayer layer, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      this.layer = layer;
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
   }

   public void performLightUpdate(World world) {
      int sizeX = this.maxX - this.minX + 1;
      int sizeY = this.maxY - this.minY + 1;
      int sizeZ = this.maxZ - this.minZ + 1;
      int area = sizeX * sizeY * sizeZ;
      if (area > 256 * world.getHeightBlocks()) {
         LOGGER.warn("Light too large, skipping!");
      } else {
         for (int x = this.minX; x <= this.maxX; x++) {
            for (int z = this.minZ; z <= this.maxZ; z++) {
               if (world.isBlockLoaded(x, 0, z)) {
                  Chunk chunk = world.getChunkFromBlockCoords(x, z);
                  if (!chunk.isChunkEmpty()) {
                     if (this.minY < 0) {
                        this.minY = 0;
                     }

                     if (this.maxY >= world.getHeightBlocks()) {
                        this.maxY = world.getHeightBlocks() - 1;
                     }

                     for (int y = this.minY; y <= this.maxY; y++) {
                        int savedLightValue = world.getSavedLightValue(this.layer, x, y, z);
                        int blockId = world.getBlockId(x, y, z);
                        int blockLightOpacity = Blocks.lightBlock[blockId];
                        if (blockLightOpacity == 0) {
                           blockLightOpacity = 1;
                        }

                        int blockLightValue = 0;
                        if (this.layer == LightLayer.Sky) {
                           if (world.canExistingBlockSeeTheSky(x, y, z)) {
                              blockLightValue = 15;
                           }
                        } else if (this.layer == LightLayer.Block) {
                           blockLightValue = Blocks.lightEmission[blockId];
                        }

                        int newLightValue;
                        if (blockLightOpacity >= 15 && blockLightValue == 0) {
                           newLightValue = 0;
                        } else {
                           int lightNegX = world.getSavedLightValue(this.layer, x - 1, y, z);
                           int lightPosX = world.getSavedLightValue(this.layer, x + 1, y, z);
                           int lightNegY = world.getSavedLightValue(this.layer, x, y - 1, z);
                           int lightPosY = world.getSavedLightValue(this.layer, x, y + 1, z);
                           int lightNegZ = world.getSavedLightValue(this.layer, x, y, z - 1);
                           int lightPosZ = world.getSavedLightValue(this.layer, x, y, z + 1);
                           newLightValue = lightNegX;
                           if (lightPosX > lightNegX) {
                              newLightValue = lightPosX;
                           }

                           if (lightNegY > newLightValue) {
                              newLightValue = lightNegY;
                           }

                           if (lightPosY > newLightValue) {
                              newLightValue = lightPosY;
                           }

                           if (lightNegZ > newLightValue) {
                              newLightValue = lightNegZ;
                           }

                           if (lightPosZ > newLightValue) {
                              newLightValue = lightPosZ;
                           }

                           newLightValue -= blockLightOpacity;
                           if (newLightValue < 0) {
                              newLightValue = 0;
                           }

                           if (blockLightValue > newLightValue) {
                              newLightValue = blockLightValue;
                           }
                        }

                        if (savedLightValue != newLightValue) {
                           world.setLightValue(this.layer, x, y, z, newLightValue);
                           int propagatedLightValue = newLightValue - 1;
                           if (propagatedLightValue < 0) {
                              propagatedLightValue = 0;
                           }

                           world.neighborLightPropagationChanged(this.layer, x - 1, y, z, propagatedLightValue);
                           world.neighborLightPropagationChanged(this.layer, x, y - 1, z, propagatedLightValue);
                           world.neighborLightPropagationChanged(this.layer, x, y, z - 1, propagatedLightValue);
                           if (x + 1 >= this.maxX) {
                              world.neighborLightPropagationChanged(this.layer, x + 1, y, z, propagatedLightValue);
                           }

                           if (y + 1 >= this.maxY) {
                              world.neighborLightPropagationChanged(this.layer, x, y + 1, z, propagatedLightValue);
                           }

                           if (z + 1 >= this.maxZ) {
                              world.neighborLightPropagationChanged(this.layer, x, y, z + 1, propagatedLightValue);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public boolean expandToContain(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      if (minX >= this.minX && minY >= this.minY && minZ >= this.minZ && maxX <= this.maxX && maxY <= this.maxY && maxZ <= this.maxZ) {
         return true;
      } else {
         int range = 1;
         if (minX >= this.minX - range
            && minY >= this.minY - range
            && minZ >= this.minZ - range
            && maxX <= this.maxX + range
            && maxY <= this.maxY + range
            && maxZ <= this.maxZ + range) {
            if (minX > this.minX) {
               minX = this.minX;
            }

            if (minY > this.minY) {
               minY = this.minY;
            }

            if (minZ > this.minZ) {
               minZ = this.minZ;
            }

            if (maxX < this.maxX) {
               maxX = this.maxX;
            }

            if (maxY < this.maxY) {
               maxY = this.maxY;
            }

            if (maxZ < this.maxZ) {
               maxZ = this.maxZ;
            }

            int oldSizeX = this.maxX - this.minX;
            int oldSizeY = this.maxY - this.minY;
            int oldSizeZ = this.maxZ - this.minZ;
            int newSizeX = maxX - minX;
            int newSizeY = maxY - minY;
            int newSizeZ = maxZ - minZ;
            int oldArea = oldSizeX * oldSizeY * oldSizeZ;
            int newArea = newSizeX * newSizeY * newSizeZ;
            if (newArea - oldArea <= 2) {
               this.minX = minX;
               this.minY = minY;
               this.minZ = minZ;
               this.maxX = maxX;
               this.maxY = maxY;
               this.maxZ = maxZ;
               return true;
            }
         }

         return false;
      }
   }
}
