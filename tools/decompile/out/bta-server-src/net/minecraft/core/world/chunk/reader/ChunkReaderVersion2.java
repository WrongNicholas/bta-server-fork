package net.minecraft.core.world.chunk.reader;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.world.World;
import net.minecraft.core.world.data.ChunkNibbleArray;
import net.minecraft.core.world.data.ChunkUnsignedByteArray;

public class ChunkReaderVersion2 extends ChunkReader {
   private final ListTag sectionsTag;

   public ChunkReaderVersion2(World world, CompoundTag chunkTag) {
      super(world, chunkTag);
      this.sectionsTag = chunkTag.getList("Sections");
   }

   private CompoundTag findSection(int y) {
      for (Tag<?> tag : this.sectionsTag) {
         if (tag instanceof CompoundTag) {
            CompoundTag cTag = (CompoundTag)tag;
            int tagY = cTag.getInteger("yPos");
            if (tagY == y) {
               return cTag;
            }
         }
      }

      return null;
   }

   @Override
   public int getX() {
      return this.tag.getInteger("xPos");
   }

   @Override
   public int getZ() {
      return this.tag.getInteger("zPos");
   }

   @Override
   public short[] getBlocks(int ySection) {
      short[] blocks = null;
      CompoundTag sectionTag = this.findSection(ySection);
      if (sectionTag != null) {
         blocks = sectionTag.getShortArrayOrDefault("Blocks", null);
      }

      return blocks;
   }

   @Override
   public ChunkUnsignedByteArray getData(int ySection) {
      byte[] data = null;
      CompoundTag sectionTag = this.findSection(ySection);
      if (sectionTag != null) {
         data = sectionTag.getByteArrayOrDefault("Data", null);
      }

      return data == null ? null : new ChunkUnsignedByteArray(16, 16, 16, data);
   }

   @Override
   public ChunkNibbleArray getSkyLight(int ySection) {
      byte[] skylight = null;
      CompoundTag sectionTag = this.findSection(ySection);
      if (sectionTag != null) {
         skylight = sectionTag.getByteArrayOrDefault("SkyLight", null);
      }

      return skylight == null ? null : new ChunkNibbleArray(16, 16, 16, skylight);
   }

   @Override
   public ChunkNibbleArray getBlockLight(int ySection) {
      byte[] blocklight = null;
      CompoundTag sectionTag = this.findSection(ySection);
      if (sectionTag != null) {
         blocklight = sectionTag.getByteArrayOrDefault("BlockLight", null);
      }

      return blocklight == null ? null : new ChunkNibbleArray(16, 16, 16, blocklight);
   }

   @Override
   public short[] getHeightMap() {
      return this.tag.getShortArrayOrDefault("HeightMap", null);
   }

   @Override
   public int getAverageBlockHeight() {
      return this.tag.getInteger("AverageBlockHeight");
   }

   @Override
   public boolean getIsTerrainPopulated() {
      return this.tag.getBoolean("TerrainPopulated");
   }

   @Override
   public double[] getTemperatureMap() {
      return this.tag.getDoubleArray("TemperatureMap");
   }

   @Override
   public double[] getHumidityMap() {
      return this.tag.getDoubleArray("HumidityMap");
   }

   @Override
   public byte[] getBiomeMap(int ySection, Map<Integer, String> biomeRegistry) {
      byte[] biomeMap = null;
      CompoundTag sectionTag = this.findSection(ySection);
      if (sectionTag != null) {
         biomeMap = sectionTag.getByteArrayOrDefault("BiomeMap", null);
      }

      if (biomeMap == null) {
         biomeMap = new byte[512];
         Arrays.fill(biomeMap, (byte)-1);
      } else {
         for (int i = 0; i < biomeMap.length; i++) {
            byte oldBiomeId = biomeMap[i];
            if (oldBiomeId >= 0) {
               try {
                  biomeMap[i] = (byte)Registries.BIOMES.getNumericIdOfItem(Registries.BIOMES.getItem(biomeRegistry.get(Integer.valueOf(oldBiomeId))));
               } catch (NullPointerException var8) {
                  biomeMap[i] = -1;
               }
            }
         }
      }

      return biomeMap;
   }

   @Override
   public Map<Integer, String> getBiomeRegistry() {
      CompoundTag registriesTag = this.tag.getCompoundOrDefault("Registries", null);
      CompoundTag biomesTag = null;
      if (registriesTag != null) {
         biomesTag = registriesTag.getCompoundOrDefault("Biomes", null);
      }

      return registriesTag != null && biomesTag != null ? Registry.readIdMapFromTag(biomesTag) : null;
   }
}
