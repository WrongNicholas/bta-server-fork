package net.minecraft.core.world.chunk;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.reader.ChunkReader;
import net.minecraft.core.world.chunk.reader.ChunkReaderLegacy;
import net.minecraft.core.world.chunk.reader.ChunkReaderVersion1;
import net.minecraft.core.world.chunk.reader.ChunkReaderVersion2;
import net.minecraft.core.world.chunk.writer.ChunkWriter;
import net.minecraft.core.world.data.ChunkNibbleArray;
import net.minecraft.core.world.data.ChunkUnsignedByteArray;
import net.minecraft.core.world.save.LevelData;
import org.slf4j.Logger;

public class ChunkLoaderLegacy implements IChunkLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private File worldDir;
   private boolean createIfNecessary;

   public ChunkLoaderLegacy(File worldDir, boolean createIfNecessary) {
      this.worldDir = worldDir;
      this.createIfNecessary = createIfNecessary;
   }

   private File chunkFileForXZ(int x, int z) {
      String fileName = "c." + Integer.toString(x, 36) + "." + Integer.toString(z, 36) + ".dat";
      String xDirName = Integer.toString(x & 63, 36);
      String zDirName = Integer.toString(z & 63, 36);
      File dir = new File(this.worldDir, xDirName);
      if (!dir.exists()) {
         if (!this.createIfNecessary) {
            return null;
         }

         dir.mkdir();
      }

      dir = new File(dir, zDirName);
      if (!dir.exists()) {
         if (!this.createIfNecessary) {
            return null;
         }

         dir.mkdir();
      }

      dir = new File(dir, fileName);
      return !dir.exists() && !this.createIfNecessary ? null : dir;
   }

   @Override
   public Chunk loadChunk(World world, int x, int z) throws IOException {
      File file = this.chunkFileForXZ(x, z);
      if (file != null && file.exists()) {
         try {
            InputStream fileStream = Files.newInputStream(file.toPath());
            CompoundTag tag = NbtIo.readCompressed(fileStream);
            if (!tag.containsKey("Level")) {
               LOGGER.warn("Chunk file at {},{} is missing level data, skipping", x, z);
               return null;
            }

            if (!tag.getCompound("Level").containsKey("Blocks")) {
               LOGGER.warn("Chunk file at {},{} is missing block data, skipping", x, z);
               return null;
            }

            Chunk chunk = loadChunkIntoWorldFromCompound(world, tag.getCompound("Level"));
            if (!chunk.isAtLocation(x, z)) {
               LOGGER.warn(
                  "Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", x, z, x, z, chunk.xPosition, chunk.zPosition
               );
               tag.putInt("xPos", x);
               tag.putInt("zPos", z);
               chunk = loadChunkIntoWorldFromCompound(world, tag.getCompound("Level"));
            }

            chunk.fixMissingBlocks();
            return chunk;
         } catch (Exception var8) {
            LOGGER.error("Exception while attempting to load chunk at X:{}, Z:{}", x, z, var8);
         }
      }

      return null;
   }

   @Override
   public void saveChunk(World world, Chunk chunk) throws IOException {
      world.checkSessionLock();
      File chunkFile = this.chunkFileForXZ(chunk.xPosition, chunk.zPosition);
      if (chunkFile.exists()) {
         LevelData levelData = world.getLevelData();
         levelData.setSizeOnDisk(levelData.getSizeOnDisk() - chunkFile.length());
      }

      try {
         File tmpChunkFile = new File(this.worldDir, "tmp_chunk.dat");
         OutputStream fileStream = Files.newOutputStream(tmpChunkFile.toPath());
         CompoundTag levelTag = new CompoundTag();
         CompoundTag chunkDataTag = new CompoundTag();
         levelTag.put("Level", chunkDataTag);
         storeChunkInCompound(chunk, world, chunkDataTag);
         NbtIo.writeCompressed(levelTag, fileStream);
         fileStream.close();
         if (chunkFile.exists()) {
            chunkFile.delete();
         }

         tmpChunkFile.renameTo(chunkFile);
         LevelData levelData = world.getLevelData();
         levelData.setSizeOnDisk(levelData.getSizeOnDisk() + chunkFile.length());
      } catch (Exception var9) {
         LOGGER.error("Exception while attempting to save chunk at X:{}, Z:{}", chunk.xPosition, chunk.zPosition, var9);
      }
   }

   @Override
   public boolean isSaving() {
      return false;
   }

   public static void storeChunkInCompound(Chunk chunk, World world, CompoundTag tag) {
      world.checkSessionLock();
      ChunkWriter writer = new ChunkWriter(world, tag);
      tag.putInt("Version", 2);
      writer.putXPos(chunk.xPosition);
      writer.putZPos(chunk.zPosition);
      writer.putLastUpdate(world.getWorldTime());
      writer.putHeightMap(chunk.heightMap);
      writer.putAverageBlockHeight(chunk.averageBlockHeight);
      writer.putTerrainPopulated(chunk.isTerrainPopulated);
      writer.putTemperatureMap(chunk.temperature);
      writer.putHumidityMap(chunk.humidity);

      for (int i = 0; i < 16; i++) {
         storeChunkSectionInCompound(chunk.getSection(i), writer);
      }

      chunk.hasEntities = false;
      ListTag entityListTag = new ListTag();

      for (int i = 0; i < 16; i++) {
         ChunkSection section = chunk.getSection(i);
         List<Entity> entities = section.entities;

         for (int j = 0; j < entities.size(); j++) {
            Entity entity = entities.get(j);
            chunk.hasEntities = true;
            CompoundTag entityTag = new CompoundTag();
            if (entity.save(entityTag)) {
               entityListTag.addTag(entityTag);
            }
         }
      }

      tag.put("Entities", entityListTag);
      ListTag tileEntityListTag = new ListTag();

      for (TileEntity tileEntity : chunk.tileEntityMap.values()) {
         CompoundTag tileEntityTag = new CompoundTag();
         tileEntity.writeToNBT(tileEntityTag);
         tileEntityListTag.addTag(tileEntityTag);
      }

      tag.put("TileEntities", tileEntityListTag);
      writer.putBiomeRegistry();
   }

   private static void storeChunkSectionInCompound(ChunkSection section, ChunkWriter writer) {
      if (section.blocks != null) {
         writer.putBlocks(section.yPosition, section.blocks);
      }

      if (section.data != null) {
         writer.putData(section.yPosition, section.data);
      }

      if (section.skylightMap != null) {
         writer.putSkyLight(section.yPosition, section.skylightMap);
      }

      if (section.blocklightMap != null) {
         writer.putBlockLight(section.yPosition, section.blocklightMap);
      }

      writer.putBiomeMap(section.yPosition, section.biome);
   }

   public static Chunk loadChunkIntoWorldFromCompound(World world, CompoundTag tag) {
      int version = tag.getIntegerOrDefault("Version", -1);
      ChunkReader reader = getChunkReaderByVersion(world, tag, version);
      int x = reader.getX();
      int z = reader.getZ();
      Chunk chunk = new Chunk(world, x, z);
      chunk.heightMap = reader.getHeightMap();
      chunk.averageBlockHeight = reader.getAverageBlockHeight();
      chunk.isTerrainPopulated = reader.getIsTerrainPopulated();
      chunk.temperature = reader.getTemperatureMap();
      chunk.humidity = reader.getHumidityMap();
      Map<Integer, String> biomeRegistry = reader.getBiomeRegistry();

      for (int i = 0; i < 16; i++) {
         loadChunkSectionFromCompound(chunk.getSection(i), reader, biomeRegistry);
      }

      if (chunk.heightMap == null) {
         chunk.heightMap = new short[256];
         chunk.recalcHeightmap();
      }

      if (chunk.temperature == null || chunk.temperature.length == 0) {
         chunk.temperature = new double[256];
         Arrays.fill(chunk.temperature, Double.NEGATIVE_INFINITY);
      }

      if (chunk.humidity == null || chunk.humidity.length == 0) {
         chunk.humidity = new double[256];
         Arrays.fill(chunk.humidity, Double.NEGATIVE_INFINITY);
      }

      ListTag entityListTag = tag.getList("Entities");
      if (entityListTag != null) {
         for (Tag<?> entityTagBase : entityListTag) {
            if (entityTagBase instanceof CompoundTag) {
               CompoundTag entityTag = (CompoundTag)entityTagBase;
               Entity entity = EntityDispatcher.createEntityFromNBT(entityTag, world);
               chunk.hasEntities = true;
               if (entity != null) {
                  chunk.addEntity(entity);
               }
            }
         }
      }

      ListTag tileEntityListTag = tag.getList("TileEntities");
      if (tileEntityListTag != null) {
         for (Tag<?> tileEntityTagBase : tileEntityListTag) {
            if (tileEntityTagBase instanceof CompoundTag) {
               CompoundTag tileEntityTag = (CompoundTag)tileEntityTagBase;
               TileEntity tileEntity = TileEntityDispatcher.createAndLoadEntity(tileEntityTag);
               if (tileEntity != null) {
                  chunk.addTileEntity(tileEntity);
               }
            }
         }
      }

      return chunk;
   }

   public static void loadChunkSectionFromCompound(ChunkSection section, ChunkReader reader, Map<Integer, String> biomeRegistry) {
      section.blocks = reader.getBlocks(section.yPosition);
      section.data = reader.getData(section.yPosition);
      section.skylightMap = reader.getSkyLight(section.yPosition);
      section.blocklightMap = reader.getBlockLight(section.yPosition);
      section.biome = reader.getBiomeMap(section.yPosition, biomeRegistry);
      if (section.data != null && !section.data.isValid()) {
         section.data = new ChunkUnsignedByteArray(16, 16, 16);
      }

      if (section.skylightMap != null && !section.skylightMap.isValid()) {
         section.skylightMap = new ChunkNibbleArray(16, 16, 16);
      }

      if (section.blocklightMap != null && !section.blocklightMap.isValid()) {
         section.blocklightMap = new ChunkNibbleArray(16, 16, 16);
      }

      if (section.biome == null || section.biome.length != 512) {
         section.biome = new byte[512];
         Arrays.fill(section.biome, (byte)-1);
      }
   }

   private static ChunkReader getChunkReaderByVersion(World world, CompoundTag tag, int version) {
      switch (version) {
         case 1:
            return new ChunkReaderVersion1(world, tag);
         case 2:
            return new ChunkReaderVersion2(world, tag);
         default:
            return new ChunkReaderLegacy(world, tag);
      }
   }
}
