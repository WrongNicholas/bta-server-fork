package net.minecraft.core.world.chunk;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.data.ChunkNibbleArray;
import net.minecraft.core.world.data.ChunkUnsignedByteArray;
import org.jetbrains.annotations.NotNull;

public class ChunkSection {
   public static final int SECTION_SIZE_Y = 16;
   public Chunk chunk;
   public int yPosition;
   public short[] blocks;
   public ChunkUnsignedByteArray data;
   public ChunkNibbleArray skylightMap;
   public ChunkNibbleArray blocklightMap;
   public byte[] biome;
   public List<Entity> entities;

   public ChunkSection(Chunk chunk, int yPosition) {
      this.chunk = chunk;
      this.yPosition = yPosition;
      this.entities = new ArrayList<>();
      this.biome = new byte[512];
   }

   public void init() {
      this.data = new ChunkUnsignedByteArray(16, 16, 16);
      this.skylightMap = new ChunkNibbleArray(16, 16, 16);
      this.blocklightMap = new ChunkNibbleArray(16, 16, 16);
      Arrays.fill(this.biome, (byte)-1);
   }

   public static int makeBlockIndex(int x, int y, int z) {
      return y * 16 * 16 + z * 16 + x;
   }

   public static int makeBiomeIndex(int x, int y, int z) {
      y >>= 3;
      return y * 16 * 16 + z * 16 + x;
   }

   public int getBlock(int x, int y, int z) {
      if (x < 0 || x >= 16 || y < 0 || y >= 16 || z < 0 || z >= 16) {
         return 0;
      } else {
         return this.blocks == null ? 0 : this.blocks[makeBlockIndex(x, y, z)] & 16383;
      }
   }

   public void setBlock(int x, int y, int z, int id) {
      assert id == 0 || Blocks.getBlock(id) != null;

      if (x >= 0 && x < 16 && y >= 0 && y < 16 && z >= 0 && z < 16) {
         if (this.blocks != null || id != 0) {
            if (this.blocks == null) {
               this.blocks = new short[4096];
            }

            this.blocks[makeBlockIndex(x, y, z)] = (short)(id & 16383);
         }
      }
   }

   public int getData(int x, int y, int z) {
      if (x < 0 || x >= 16 || y < 0 || y >= 16 || z < 0 || z >= 16) {
         return 0;
      } else {
         return this.blocks != null && this.data != null ? this.data.get(x, y, z) : 0;
      }
   }

   public void setData(int x, int y, int z, int data) {
      if (x >= 0 && x < 16 && y >= 0 && y < 16 && z >= 0 && z < 16) {
         if (this.blocks != null) {
            if (this.data == null) {
               this.data = new ChunkUnsignedByteArray(16, 16, 16);
            }

            this.data.set(x, y, z, data);
         }
      }
   }

   public Biome getBiome(int x, int y, int z) {
      if (x >= 0 && x < 16 && y >= 0 && y < 16 && z >= 0 && z < 16) {
         byte biomeId = this.biome[makeBiomeIndex(x, y, z)];
         return biomeId < 0 ? null : Registries.BIOMES.getItemByNumericId(biomeId);
      } else {
         return null;
      }
   }

   public void setBiome(int x, int y, int z, Biome biome) {
      if (x >= 0 && x < 16 && y >= 0 && y < 16 && z >= 0 && z < 16) {
         int biomeId = Registries.BIOMES.getNumericIdOfItem(biome);
         this.biome[makeBiomeIndex(x, y, z)] = (byte)biomeId;
      }
   }

   public int getBrightness(LightLayer layer, int x, int y, int z) {
      if (x >= 0 && x < 16 && y >= 0 && y < 16 && z >= 0 && z < 16) {
         switch (layer) {
            case Sky:
               if (this.skylightMap == null) {
                  return 0;
               }

               return this.skylightMap.get(x, y, z);
            case Block:
               if (this.blocklightMap == null) {
                  return 0;
               }

               return this.blocklightMap.get(x, y, z);
            default:
               return 0;
         }
      } else {
         return 0;
      }
   }

   public void setBrightness(LightLayer layer, int x, int y, int z, int brightness) {
      if (x >= 0 && x < 16 && y >= 0 && y < 16 && z >= 0 && z < 16) {
         switch (layer) {
            case Sky:
               if (this.skylightMap == null) {
                  this.skylightMap = new ChunkNibbleArray(16, 16, 16);
               }

               this.skylightMap.set(x, y, z, brightness);
               break;
            case Block:
               if (this.blocklightMap == null) {
                  this.blocklightMap = new ChunkNibbleArray(16, 16, 16);
               }

               this.blocklightMap.set(x, y, z, brightness);
         }
      }
   }

   public int getRawBrightness(int x, int y, int z, int skySubtract) {
      return x >= 0 && x < 16 && y >= 0 && y < 16 && z >= 0 && z < 16
         ? Math.max(this.getBrightness(LightLayer.Sky, x, y, z) - skySubtract, this.getBrightness(LightLayer.Block, x, y, z))
         : 0;
   }

   public void addEntity(Entity entity) {
      this.entities.add(entity);
   }

   public void removeEntity(Entity entity) {
      this.entities.remove(entity);
   }

   public void getEntitiesWithin(Entity toExclude, AABB aabb, List<Entity> entities) {
      for (Entity e : this.entities) {
         if (e != toExclude && e.bb.intersects(aabb)) {
            entities.add(e);
         }
      }
   }

   public <T extends Entity> void getEntitiesWithin(Class<T> ofClass, AABB aabb, List<@NotNull T> entities) {
      for (Entity e : this.entities) {
         if (ofClass.isAssignableFrom(e.getClass()) && e.bb.intersects(aabb)) {
            entities.add((T)e);
         }
      }
   }

   public void onLoad(World world) {
      world.addLoadedEntities(this.entities);
   }

   public void onUnload(World world) {
      world.unloadEntities(this.entities);
   }

   public int setChunkSectionData(byte[] data, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int startIndex) {
      int xSize = maxX - minX;
      int ySize = maxY - minY;
      int zSize = maxZ - minZ;
      if (data[startIndex++] == 1) {
         if (this.blocks == null) {
            this.blocks = new short[4096];
         }

         ByteBuffer bb = ByteBuffer.allocate(this.blocks.length * 2);
         bb.asShortBuffer().put(this.blocks);

         for (int cy = minY; cy < maxY; cy++) {
            for (int cz = minZ; cz < maxZ; cz++) {
               int cIndex = makeBlockIndex(minX, cy, cz);
               System.arraycopy(data, startIndex, bb.array(), cIndex * 2, xSize * 2);
               startIndex += xSize * 2;
            }
         }

         ShortBuffer.wrap(this.blocks).put(bb.asShortBuffer());
      }

      if (data[startIndex++] == 1) {
         if (this.data == null || this.data.data == null) {
            this.data = new ChunkUnsignedByteArray(16, 16, 16);
         }

         for (int cy = minY; cy < maxY; cy++) {
            for (int cz = minZ; cz < maxZ; cz++) {
               int cIndex = makeBlockIndex(minX, cy, cz);
               System.arraycopy(data, startIndex, this.data.data, cIndex, xSize);
               startIndex += xSize;
            }
         }
      }

      if (data[startIndex++] == 1) {
         if (this.skylightMap == null || this.skylightMap.data == null) {
            this.skylightMap = new ChunkNibbleArray(16, 16, 16);
         }

         for (int cy = minY; cy < maxY; cy++) {
            for (int cz = minZ; cz < maxZ; cz++) {
               int cIndex = makeBlockIndex(minX, cy, cz) / 2;
               System.arraycopy(data, startIndex, this.skylightMap.data, cIndex, xSize / 2);
               startIndex += xSize / 2;
            }
         }
      }

      if (data[startIndex++] == 1) {
         if (this.blocklightMap == null || this.blocklightMap.data == null) {
            this.blocklightMap = new ChunkNibbleArray(16, 16, 16);
         }

         for (int cy = minY; cy < maxY; cy++) {
            for (int cz = minZ; cz < maxZ; cz++) {
               int cIndex = makeBlockIndex(minX, cy, cz) / 2;
               System.arraycopy(data, startIndex, this.blocklightMap.data, cIndex, xSize / 2);
               startIndex += xSize / 2;
            }
         }
      }

      if (data[startIndex++] == 1) {
         if (this.biome == null) {
            this.biome = new byte[512];
         }

         for (int cy = minY >> 3; cy < maxY >> 3; cy++) {
            for (int cz = minZ; cz < maxZ; cz++) {
               int cIndex = makeBlockIndex(minX, cy, cz);
               System.arraycopy(data, startIndex, this.biome, cIndex, xSize);
               startIndex += xSize;
            }
         }
      }

      return startIndex;
   }

   public int getChunkSectionData(byte[] data, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int startIndex) {
      int xSize = maxX - minX;
      int ySize = maxY - minY;
      int zSize = maxZ - minZ;
      ByteBuffer bb = null;
      if (this.blocks != null) {
         bb = ByteBuffer.allocate(this.blocks.length * 2);
         bb.asShortBuffer().put(this.blocks);
      }

      if (xSize * ySize * zSize == 4096) {
         if (this.blocks != null) {
            data[startIndex++] = 1;
            System.arraycopy(bb.array(), 0, data, startIndex, this.blocks.length * 2);
            startIndex += this.blocks.length * 2;
         } else {
            data[startIndex++] = 0;
         }

         if (this.data != null && this.data.isValid() && this.data.data != null) {
            data[startIndex++] = 1;
            System.arraycopy(this.data.data, 0, data, startIndex, this.data.data.length);
            startIndex += this.data.data.length;
         } else {
            data[startIndex++] = 0;
         }

         if (this.skylightMap != null && this.skylightMap.isValid() && this.skylightMap.data != null) {
            data[startIndex++] = 1;
            System.arraycopy(this.skylightMap.data, 0, data, startIndex, this.skylightMap.data.length);
            startIndex += this.skylightMap.data.length;
         } else {
            data[startIndex++] = 0;
         }

         if (this.blocklightMap != null && this.blocklightMap.isValid() && this.blocklightMap.data != null) {
            data[startIndex++] = 1;
            System.arraycopy(this.blocklightMap.data, 0, data, startIndex, this.blocklightMap.data.length);
            startIndex += this.blocklightMap.data.length;
         } else {
            data[startIndex++] = 0;
         }

         if (this.biome != null) {
            data[startIndex++] = 1;
            System.arraycopy(this.biome, 0, data, startIndex, this.biome.length);
            startIndex += this.biome.length;
         } else {
            data[startIndex++] = 0;
         }
      } else {
         if (this.blocks == null) {
            data[startIndex++] = 0;
         } else {
            data[startIndex++] = 1;

            for (int cy = minY; cy < maxY; cy++) {
               for (int cz = minZ; cz < maxZ; cz++) {
                  int cIndex = makeBlockIndex(minX, cy, cz);
                  System.arraycopy(bb.array(), cIndex * 2, data, startIndex, xSize * 2);
                  startIndex += xSize * 2;
               }
            }
         }

         if (this.data != null && this.data.isValid() && this.data.data != null) {
            data[startIndex++] = 1;

            for (int cy = minY; cy < maxY; cy++) {
               for (int cz = minZ; cz < maxZ; cz++) {
                  int cIndex = makeBlockIndex(minX, cy, cz);
                  System.arraycopy(this.data.data, cIndex, data, startIndex, xSize);
                  startIndex += xSize;
               }
            }
         } else {
            data[startIndex++] = 0;
         }

         if (this.skylightMap != null && this.skylightMap.isValid() && this.skylightMap.data != null) {
            data[startIndex++] = 1;

            for (int cy = minY; cy < maxY; cy++) {
               for (int cz = minZ; cz < maxZ; cz++) {
                  int cIndex = makeBlockIndex(minX, cy, cz) / 2;
                  System.arraycopy(this.skylightMap.data, cIndex, data, startIndex, xSize / 2);
                  startIndex += xSize / 2;
               }
            }
         } else {
            data[startIndex++] = 0;
         }

         if (this.blocklightMap != null && this.blocklightMap.isValid() && this.blocklightMap.data != null) {
            data[startIndex++] = 1;

            for (int cy = minY; cy < maxY; cy++) {
               for (int cz = minZ; cz < maxZ; cz++) {
                  int cIndex = makeBlockIndex(minX, cy, cz) / 2;
                  System.arraycopy(this.blocklightMap.data, cIndex, data, startIndex, xSize / 2);
                  startIndex += xSize / 2;
               }
            }
         } else {
            data[startIndex++] = 0;
         }

         if (this.biome != null) {
            data[startIndex++] = 1;

            for (int cy = minY >> 3; cy < maxY >> 3; cy++) {
               for (int cz = minZ; cz < maxZ; cz++) {
                  int cIndex = makeBlockIndex(minX, cy, cz);
                  System.arraycopy(this.biome, cIndex, data, startIndex, xSize);
                  startIndex += xSize;
               }
            }
         } else {
            data[startIndex++] = 0;
         }
      }

      return startIndex;
   }
}
