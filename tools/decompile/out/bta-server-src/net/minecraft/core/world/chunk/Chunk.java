package net.minecraft.core.world.chunk;

import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Chunk {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int CHUNK_SIZE_X = 16;
   public static final int CHUNK_SIZE_Z = 16;
   public static final int CHUNK_SECTIONS = 16;
   public static boolean isLit;
   public boolean isLoaded;
   public World world;
   public short[] heightMap;
   public int lowestY;
   public final int xPosition;
   public final int zPosition;
   public Map<ChunkPosition, TileEntity> tileEntityMap;
   public double[] temperature;
   public double[] humidity;
   public double[] variety;
   public int averageBlockHeight;
   public boolean isTerrainPopulated;
   public boolean isModified;
   public boolean neverSave;
   public boolean hasEntities;
   public long lastSaveTime;
   protected final ChunkSection[] sections = new ChunkSection[16];

   public Chunk(World world, int x, int z) {
      this.tileEntityMap = new HashMap<>();
      this.isTerrainPopulated = false;
      this.isModified = false;
      this.hasEntities = false;
      this.lastSaveTime = 0L;
      this.world = world;
      this.xPosition = x;
      this.zPosition = z;
      this.heightMap = new short[256];
      this.temperature = new double[256];
      this.humidity = new double[256];
      this.variety = new double[256];

      for (int i = 0; i < this.sections.length; i++) {
         this.sections[i] = new ChunkSection(this, i);
      }
   }

   public static int makeBlockIndex(int x, int y, int z) {
      return y * 16 * 16 + z * 16 + x;
   }

   public ChunkSection getSection(int index) {
      return index >= 0 && index < 16 ? this.sections[index] : null;
   }

   public void init() {
      Arrays.fill(this.temperature, Double.NEGATIVE_INFINITY);
      Arrays.fill(this.humidity, Double.NEGATIVE_INFINITY);
      Arrays.fill(this.variety, Double.NEGATIVE_INFINITY);
   }

   public boolean isAtLocation(int x, int z) {
      return x == this.xPosition && z == this.zPosition;
   }

   public int getHeightValue(int x, int z) {
      return this.heightMap[z * 16 + x];
   }

   private void setHeightValue(int x, int z, int y) {
      this.heightMap[z * 16 + x] = (short)y;
   }

   public void recalcHeightmapOnly() {
      int acc = 0;
      int lowestY = 255;

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            int y = 255;

            while (y > 0 && Blocks.lightBlock[this.getBlockID(x, y - 1, z)] == 0) {
               y--;
            }

            this.setHeightValue(x, z, y);
            acc += y;
            if (y < lowestY) {
               lowestY = y;
            }
         }
      }

      this.lowestY = lowestY;
      this.isModified = true;
      this.averageBlockHeight = acc / 256;
   }

   public void recalcHeightmap() {
      this.recalcHeightmapOnly();
      if (!this.world.worldType.hasCeiling()) {
         for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
               int skyLightValue = 15;
               int lightY = 255;

               do {
                  skyLightValue -= Blocks.lightBlock[this.getBlockID(x, lightY, z) & 16383];
                  if (skyLightValue > 0) {
                     this.setBrightness(LightLayer.Sky, x, lightY, z, skyLightValue);
                  }
               } while (--lightY > 0 && skyLightValue > 0);
            }
         }
      }

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            this.lightGaps(x, z);
         }
      }

      this.isModified = true;
   }

   private void lightGaps(int x, int z) {
      int y = this.getHeightValue(x, z);
      int worldX = this.xPosition * 16 + x;
      int worldZ = this.zPosition * 16 + z;
      this.lightGap(worldX - 1, worldZ, y);
      this.lightGap(worldX + 1, worldZ, y);
      this.lightGap(worldX, worldZ - 1, y);
      this.lightGap(worldX, worldZ + 1, y);
   }

   private void lightGap(int x, int z, int heightValue) {
      int blockHeight = this.world.getHeightValue(x, z);
      if (blockHeight > heightValue) {
         this.world.scheduleLightingUpdate(LightLayer.Sky, x, heightValue, z, x, blockHeight, z);
         this.isModified = true;
      } else if (blockHeight < heightValue) {
         this.world.scheduleLightingUpdate(LightLayer.Sky, x, blockHeight, z, x, heightValue, z);
         this.isModified = true;
      }
   }

   private void recalcHeight(int x, int y, int z) {
      int heightValue = this.getHeightValue(x, z);
      int iy = heightValue;
      if (y > heightValue) {
         iy = y;
      }

      while (iy > 0 && Blocks.lightBlock[this.getBlockID(x, iy - 1, z) & 16383] == 0) {
         iy--;
      }

      if (iy != heightValue) {
         this.world.markBlocksDirtyVertical(x, z, iy, heightValue);
         this.setHeightValue(x, z, iy);
         if (iy < this.lowestY) {
            this.lowestY = iy;
         } else {
            int lowestY = 255;

            for (int ix = 0; ix < 16; ix++) {
               for (int iz = 0; iz < 16; iz++) {
                  int iHeightValue = this.getHeightValue(x, z);
                  if (iHeightValue < lowestY) {
                     lowestY = iHeightValue;
                  }
               }
            }

            this.lowestY = lowestY;
         }

         int worldX = this.xPosition * 16 + x;
         int worldZ = this.zPosition * 16 + z;
         if (iy < heightValue) {
            for (int dy = iy; dy < heightValue; dy++) {
               this.setBrightness(LightLayer.Sky, x, dy, z, 15);
            }
         } else {
            this.world.scheduleLightingUpdate(LightLayer.Sky, worldX, heightValue, worldZ, worldX, iy, worldZ);

            for (int dy = heightValue; dy < iy; dy++) {
               this.setBrightness(LightLayer.Sky, x, dy, z, 0);
            }
         }

         int lightValue = 15;

         int lightY;
         for (lightY = iy; iy > 0 && lightValue > 0; this.setBrightness(LightLayer.Sky, x, iy, z, lightValue)) {
            int lightBlock = Blocks.lightBlock[this.getBlockID(x, --iy, z)];
            if (lightBlock == 0) {
               lightBlock = 1;
            }

            lightValue -= lightBlock;
            if (lightValue < 0) {
               lightValue = 0;
            }
         }

         while (iy > 0 && Blocks.lightBlock[this.getBlockID(x, iy - 1, z)] == 0) {
            iy--;
         }

         if (iy != lightY) {
            this.world.scheduleLightingUpdate(LightLayer.Sky, worldX - 1, iy, worldZ - 1, worldX + 1, lightY, worldZ + 1);
         }

         this.isModified = true;
      }
   }

   public int getBlockID(int x, int y, int z) {
      return x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16 ? this.getSection(y / 16).getBlock(x, y % 16, z) : 0;
   }

   public boolean setBlockIDWithMetadataRaw(int x, int y, int z, int id, int data) {
      if (x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16) {
         ChunkSection section = this.getSection(y / 16);
         int heightValue = this.getHeightValue(x, z);
         int currentId = section.getBlock(x, y % 16, z);
         int currentData = section.getData(x, y % 16, z);
         if (currentId == id && currentData == data) {
            return false;
         } else {
            section.setBlock(x, y % 16, z, id);
            section.setData(x, y % 16, z, data);
            int worldX = this.xPosition * 16 + x;
            int worldZ = this.zPosition * 16 + z;
            if (!this.world.worldType.hasCeiling()) {
               if (Blocks.lightBlock[id & 16383] != 0) {
                  if (y >= heightValue) {
                     this.recalcHeight(x, y + 1, z);
                  }
               } else if (y == heightValue - 1) {
                  this.recalcHeight(x, y, z);
               }

               this.world.scheduleLightingUpdate(LightLayer.Sky, worldX, y, worldZ, worldX, y, worldZ);
            }

            this.world.scheduleLightingUpdate(LightLayer.Block, worldX, y, worldZ, worldX, y, worldZ);
            this.lightGaps(x, z);
            this.isModified = true;
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean setBlockIDWithMetadata(int x, int y, int z, int id, int data) {
      if (x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16) {
         ChunkSection section = this.getSection(y / 16);
         int heightValue = this.getHeightValue(x, z);
         int currentId = section.getBlock(x, y % 16, z);
         int currentData = section.getData(x, y % 16, z);
         if (currentId == id && currentData == data) {
            return false;
         } else {
            section.setBlock(x, y % 16, z, id);
            section.setData(x, y % 16, z, data);
            int worldX = this.xPosition * 16 + x;
            int worldZ = this.zPosition * 16 + z;
            if (currentId != 0 && !this.world.isClientSide) {
               Blocks.blocksList[currentId].onBlockRemoved(this.world, worldX, y, worldZ, currentData);
            }

            if (!this.world.worldType.hasCeiling()) {
               if (Blocks.lightBlock[id & 16383] != 0) {
                  if (y >= heightValue) {
                     this.recalcHeight(x, y + 1, z);
                  }
               } else if (y == heightValue - 1) {
                  this.recalcHeight(x, y, z);
               }

               this.world.scheduleLightingUpdate(LightLayer.Sky, worldX, y, worldZ, worldX, y, worldZ);
            }

            this.world.scheduleLightingUpdate(LightLayer.Block, worldX, y, worldZ, worldX, y, worldZ);
            this.lightGaps(x, z);
            if (Blocks.getBlock(id) != null) {
               Blocks.blocksList[id].onBlockPlacedByWorld(this.world, worldX, y, worldZ);
            }

            this.isModified = true;
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean setBlockID(int x, int y, int z, int id) {
      return this.setBlockIDWithMetadata(x, y, z, id, 0);
   }

   public int getBlockMetadata(int x, int y, int z) {
      return x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16 ? this.getSection(y / 16).getData(x, y % 16, z) : 0;
   }

   public void setBlockMetadata(int x, int y, int z, int value) {
      if (x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16) {
         this.getSection(y / 16).setData(x, y % 16, z, value);
         this.isModified = true;
      }
   }

   @Nullable
   public Biome getBlockBiome(int x, int y, int z) {
      return x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16 ? this.getSection(y / 16).getBiome(x, y % 16, z) : null;
   }

   public boolean setBlockBiome(int x, int y, int z, Biome biome) {
      if (x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16) {
         this.getSection(y / 16).setBiome(x, y % 16, z, biome);
         this.isModified = true;
         return true;
      } else {
         return false;
      }
   }

   public double getBlockTemperature(int x, int z) {
      return this.temperature[x * 16 + z];
   }

   public boolean setBlockTemperature(int x, int z, double temperature) {
      double presentTemperature = this.temperature[x * 16 + z];
      if (presentTemperature == temperature) {
         return false;
      } else {
         this.temperature[x * 16 + z] = temperature;
         this.isModified = true;
         return true;
      }
   }

   public double getBlockHumidity(int x, int z) {
      return this.humidity[x * 16 + z];
   }

   public boolean setBlockHumidity(int x, int z, double humidity) {
      double presentHumidity = this.humidity[x * 16 + z];
      if (presentHumidity == humidity) {
         return false;
      } else {
         this.humidity[x * 16 + z] = humidity;
         this.isModified = true;
         return true;
      }
   }

   public double getBlockVariety(int x, int z) {
      return this.variety[x * 16 + z];
   }

   public boolean setBlockVariety(int x, int z, double variety) {
      double presentVariety = this.variety[x * 16 + z];
      if (presentVariety == variety) {
         return false;
      } else {
         this.variety[x * 16 + z] = variety;
         this.isModified = true;
         return true;
      }
   }

   public int getBrightness(LightLayer layer, int x, int y, int z) {
      return x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16 ? this.getSection(y / 16).getBrightness(layer, x, y % 16, z) : 0;
   }

   public void setBrightness(LightLayer layer, int x, int y, int z, int value) {
      if (x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16) {
         this.getSection(y / 16).setBrightness(layer, x, y % 16, z, value);
         this.isModified = true;
      }
   }

   public int getRawBrightness(int x, int y, int z, int skySubtract) {
      if (x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16) {
         int lightValue = this.getSection(y / 16).getRawBrightness(x, y % 16, z, skySubtract);
         if (lightValue > 0) {
            isLit = true;
         }

         return lightValue;
      } else {
         return 0;
      }
   }

   public void addEntity(Entity entity) {
      this.hasEntities = true;
      this.isModified = true;
      int x = MathHelper.floor(entity.x / 16.0);
      int z = MathHelper.floor(entity.z / 16.0);
      if (x != this.xPosition || z != this.zPosition) {
         LOGGER.warn("Wrong location! {}", entity);
         Thread.dumpStack();
      }

      int section = MathHelper.clamp(MathHelper.floor(entity.y / 16.0), 0, 15);
      entity.addedToChunk = true;
      entity.chunkCoordX = this.xPosition;
      entity.chunkCoordY = section;
      entity.chunkCoordZ = this.zPosition;
      this.getSection(section).addEntity(entity);
   }

   public void removeEntity(Entity entity) {
      this.removeEntityAtIndex(entity, entity.chunkCoordY);
   }

   public void removeEntityAtIndex(Entity entity, int index) {
      index = MathHelper.clamp(index, 0, 15);
      this.getSection(index).removeEntity(entity);
   }

   public boolean canBlockSeeTheSky(int x, int y, int z) {
      return y >= this.getHeightValue(x, z);
   }

   public TileEntity getTileEntity(int x, int y, int z) {
      ChunkPosition pos = new ChunkPosition(x, y, z);
      TileEntity tileEntity = this.tileEntityMap.get(pos);
      if (tileEntity == null) {
         Block<?> block = Blocks.getBlock(this.getBlockID(x, y, z));
         if (block == null || !block.isEntityTile) {
            return null;
         }

         block.onBlockPlacedByWorld(this.world, this.xPosition * 16 + x, y, this.zPosition * 16 + z);
         tileEntity = this.tileEntityMap.get(pos);
      }

      if (tileEntity != null && tileEntity.isInvalid()) {
         this.tileEntityMap.remove(pos);
         return null;
      } else {
         return tileEntity;
      }
   }

   public void addTileEntity(TileEntity tileEntity) {
      int x = tileEntity.x - this.xPosition * 16;
      int y = tileEntity.y;
      int z = tileEntity.z - this.zPosition * 16;
      this.setTileEntity(x, y, z, tileEntity);
      if (this.isLoaded) {
         this.world.loadedTileEntityList.add(tileEntity);
      }
   }

   public boolean setTileEntity(int x, int y, int z, TileEntity tileEntity) {
      ChunkPosition chunkposition = new ChunkPosition(x, y, z);
      tileEntity.worldObj = this.world;
      tileEntity.x = this.xPosition * 16 + x;
      tileEntity.y = y;
      tileEntity.z = this.zPosition * 16 + z;
      Block<?> b = Blocks.blocksList[this.getBlockID(x, y, z)];
      if (b != null && b.isEntityTile) {
         tileEntity.validate();
         TileEntity previous = this.tileEntityMap.put(chunkposition, tileEntity);
         if (previous != null) {
            previous.invalidate();
         }

         return true;
      } else {
         LOGGER.warn("Attempted to place a tile entity {} at {} {} {} where there was no tile entity block!", tileEntity.getClass(), x, y, z);
         Thread.dumpStack();
         return false;
      }
   }

   public void removeTileEntity(int x, int y, int z) {
      ChunkPosition pos = new ChunkPosition(x, y, z);
      if (this.isLoaded) {
         TileEntity tileEntity = this.tileEntityMap.remove(pos);
         if (tileEntity != null) {
            tileEntity.invalidate();
         }
      }
   }

   public void removeTileEntity(TileEntity tileEntity) {
      if (this.isLoaded && tileEntity != null && this.tileEntityMap.containsValue(tileEntity)) {
         this.tileEntityMap.remove(new ChunkPosition(tileEntity.x & 15, tileEntity.y, tileEntity.z & 15));
         tileEntity.invalidate();
      }
   }

   public void onLoad() {
      this.isLoaded = true;
      this.world.addAllBlockEntities(this.tileEntityMap.values());

      for (ChunkSection section : this.sections) {
         section.onLoad(this.world);
      }
   }

   public void onUnload() {
      this.isLoaded = false;

      for (TileEntity tileentity : this.tileEntityMap.values()) {
         tileentity.invalidate();
      }

      for (ChunkSection section : this.sections) {
         section.onUnload(this.world);
      }
   }

   public void setChunkModified() {
      this.isModified = true;
   }

   public void getEntitiesWithin(Entity toExclude, AABB aabb, List<Entity> entities) {
      int minSection = Math.max(0, MathHelper.floor((aabb.minY - 2.0) / 16.0));
      int maxSection = Math.min(MathHelper.floor((aabb.maxY + 2.0) / 16.0), 15);

      for (int section = minSection; section <= maxSection; section++) {
         this.getSection(section).getEntitiesWithin(toExclude, aabb, entities);
      }
   }

   public <T extends Entity> void getEntitiesWithin(Class<T> ofClass, AABB aabb, List<@NotNull T> entities) {
      int minSection = Math.max(0, MathHelper.floor((aabb.minY - 2.0) / 16.0));
      int maxSection = Math.min(MathHelper.floor((aabb.maxY + 2.0) / 16.0), 15);

      for (int section = minSection; section <= maxSection; section++) {
         this.getSection(section).getEntitiesWithin(ofClass, aabb, entities);
      }
   }

   public boolean needsSaving(boolean saveImmediately) {
      if (this.neverSave) {
         return false;
      } else {
         if (saveImmediately) {
            if (this.hasEntities && this.world.getWorldTime() != this.lastSaveTime) {
               return true;
            }
         } else if (this.hasEntities && this.world.getWorldTime() >= this.lastSaveTime + 600L) {
            return true;
         }

         return this.isModified;
      }
   }

   public int setChunkData(byte[] data, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int startIndex) {
      int minSectionY = minY / 16;
      int maxSectionY = (int)Math.ceil(maxY / 16.0);

      for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
         if (sectionY >= 0 && sectionY < this.sections.length) {
            int minYSection = minY - sectionY * 16;
            int maxYSection = maxY - sectionY * 16;
            if (minYSection < 0) {
               minYSection = 0;
            }

            if (maxYSection > 16) {
               maxYSection = 16;
            }

            startIndex = this.sections[sectionY].setChunkSectionData(data, minX, minYSection, minZ, maxX, maxYSection, maxZ, startIndex);
         }
      }

      this.recalcHeightmapOnly();

      for (int x = minX; x < maxX; x++) {
         for (int z = minZ; z < maxZ; z++) {
            this.temperature[x * 16 + z] = (data[startIndex++] & 255) / 255.0;
         }
      }

      for (int x = minX; x < maxX; x++) {
         for (int z = minZ; z < maxZ; z++) {
            this.humidity[x * 16 + z] = (data[startIndex++] & 255) / 255.0;
         }
      }

      for (int x = minX; x < maxX; x++) {
         for (int z = minZ; z < maxZ; z++) {
            this.variety[x * 16 + z] = (data[startIndex++] & 255) / 255.0;
         }
      }

      return startIndex;
   }

   public int getChunkData(byte[] data, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int startIndex) {
      int minSectionY = minY / 16;
      int maxSectionY = (int)Math.ceil(maxY / 16.0);

      for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
         if (sectionY >= 0 && sectionY < this.sections.length) {
            int minYSection = minY - sectionY * 16;
            int maxYSection = maxY - sectionY * 16;
            if (minYSection < 0) {
               minYSection = 0;
            }

            if (maxYSection > 16) {
               maxYSection = 16;
            }

            startIndex = this.sections[sectionY].getChunkSectionData(data, minX, minYSection, minZ, maxX, maxYSection, maxZ, startIndex);
         }
      }

      for (int x = minX; x < maxX; x++) {
         for (int z = minZ; z < maxZ; z++) {
            data[startIndex++] = (byte)(this.temperature[x * 16 + z] * 255.0);
         }
      }

      for (int x = minX; x < maxX; x++) {
         for (int z = minZ; z < maxZ; z++) {
            data[startIndex++] = (byte)(this.humidity[x * 16 + z] * 255.0);
         }
      }

      for (int x = minX; x < maxX; x++) {
         for (int z = minZ; z < maxZ; z++) {
            data[startIndex++] = (byte)(this.variety[x * 16 + z] * 255.0);
         }
      }

      return startIndex;
   }

   public Random getChunkRandom(long xor) {
      return new Random(
         this.world.getRandomSeed()
               + this.xPosition * this.xPosition * 4987142L
               + this.xPosition * 5947611L
               + this.zPosition * this.zPosition * 4392871L
               + this.zPosition * 389711L
            ^ xor
      );
   }

   public boolean isChunkEmpty() {
      return false;
   }

   public void fixMissingBlocks() {
      for (ChunkSection section : this.sections) {
         if (section != null && section.blocks != null) {
            MissingBlockFixer.fixMissingBlocks(section.blocks);
         }
      }
   }
}
