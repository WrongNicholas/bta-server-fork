package net.minecraft.core.world.chunk.reader;

import com.mojang.nbt.tags.CompoundTag;
import java.util.Map;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkSection;
import net.minecraft.core.world.data.ChunkNibbleArray;
import net.minecraft.core.world.data.ChunkUnsignedByteArray;

public class ChunkReaderVersion1 extends ChunkReader {
   private final short[] blocks;
   private final byte[] data;
   private final byte[] skylight;
   private final byte[] blocklight;
   private final byte[] biome;

   private static int makeBlockIndex(int x, int y, int z) {
      return x * 16 * 256 + z * 256 + y;
   }

   public static int makeBiomeIndex(int x, int y, int z) {
      return (y >> 3) * 16 * 16 + x * 16 + z;
   }

   private static int makeNibbleIndex(int x, int y, int z) {
      return x * 16 * 256 + z * 256 + y;
   }

   public ChunkReaderVersion1(World world, CompoundTag tag) {
      super(world, tag);
      this.blocks = tag.getShortArrayOrDefault("Blocks", null);
      this.data = tag.getByteArrayOrDefault("Data", null);
      this.skylight = tag.getByteArrayOrDefault("SkyLight", null);
      this.blocklight = tag.getByteArrayOrDefault("BlockLight", null);
      this.biome = tag.getByteArrayOrDefault("BiomeMap", null);
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
      short[] newBlocks = new short[4096];
      if (this.blocks != null && makeBlockIndex(15, ySection * 16 + 15, 15) < this.blocks.length) {
         for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
               for (int y = 0; y < 16; y++) {
                  int yOffset = ySection * 16;
                  short id = this.blocks[makeBlockIndex(x, yOffset + y, z)];
                  newBlocks[ChunkSection.makeBlockIndex(x, y, z)] = id;
               }
            }
         }
      }

      return newBlocks;
   }

   @Override
   public ChunkUnsignedByteArray getData(int ySection) {
      byte[] newData = new byte[4096];
      ChunkUnsignedByteArray array = new ChunkUnsignedByteArray(16, 16, 16, newData);
      if (this.data != null && makeBlockIndex(15, ySection * 16 + 15, 15) < this.data.length) {
         for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
               for (int y = 0; y < 16; y++) {
                  int yOffset = ySection * 16;
                  byte data = this.data[makeBlockIndex(x, yOffset + y, z)];
                  array.set(x, y, z, Byte.toUnsignedInt(data));
               }
            }
         }
      }

      return array;
   }

   @Override
   public ChunkNibbleArray getSkyLight(int ySection) {
      byte[] newSkyLight = new byte[2048];
      ChunkNibbleArray array = new ChunkNibbleArray(16, 16, 16, newSkyLight);
      if (this.skylight != null && makeNibbleIndex(15, ySection * 16 + 15, 15) < this.skylight.length * 2) {
         for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
               for (int y = 0; y < 16; y++) {
                  int yOffset = ySection * 16;
                  int index = makeNibbleIndex(x, yOffset + y, z);
                  int nibbleIndex = index >> 1;
                  int nibblePart = index & 1;
                  int nibble;
                  if (nibblePart == 0) {
                     nibble = Byte.toUnsignedInt(this.skylight[nibbleIndex]) & 15;
                  } else {
                     nibble = Byte.toUnsignedInt(this.skylight[nibbleIndex]) >> 4 & 15;
                  }

                  array.set(x, y, z, nibble);
               }
            }
         }
      }

      return array;
   }

   @Override
   public ChunkNibbleArray getBlockLight(int ySection) {
      byte[] newBlockLight = new byte[2048];
      ChunkNibbleArray array = new ChunkNibbleArray(16, 16, 16, newBlockLight);
      if (this.blocklight != null && makeNibbleIndex(15, ySection * 16 + 15, 15) < this.blocklight.length * 2) {
         for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
               for (int y = 0; y < 16; y++) {
                  int yOffset = ySection * 16;
                  int index = makeNibbleIndex(x, yOffset + y, z);
                  int nibbleIndex = index >> 1;
                  int nibblePart = index & 1;
                  int nibble;
                  if (nibblePart == 0) {
                     nibble = Byte.toUnsignedInt(this.blocklight[nibbleIndex]) & 15;
                  } else {
                     nibble = Byte.toUnsignedInt(this.blocklight[nibbleIndex]) >> 4 & 15;
                  }

                  array.set(x, y, z, nibble);
               }
            }
         }
      }

      return array;
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
      byte[] newBiomes = new byte[512];
      if (this.biome != null && makeBiomeIndex(15, ySection * 16 + 15, 15) < this.biome.length) {
         for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
               for (int y = 0; y < 16; y++) {
                  int yOffset = ySection * 16;
                  byte oldBiomeId = this.biome[makeBiomeIndex(x, yOffset + y, z)];
                  if (oldBiomeId < 0) {
                     newBiomes[ChunkSection.makeBiomeIndex(x, y, z)] = oldBiomeId;
                  } else {
                     try {
                        byte newBiomeId = (byte)Registries.BIOMES.getNumericIdOfItem(Registries.BIOMES.getItem(biomeRegistry.get(Integer.valueOf(oldBiomeId))));
                        newBiomes[ChunkSection.makeBiomeIndex(x, y, z)] = newBiomeId;
                     } catch (NullPointerException var10) {
                        newBiomes[ChunkSection.makeBiomeIndex(x, y, z)] = -1;
                     }
                  }
               }
            }
         }
      }

      return newBiomes;
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
