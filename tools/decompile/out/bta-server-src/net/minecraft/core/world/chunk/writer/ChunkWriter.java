package net.minecraft.core.world.chunk.writer;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.world.World;
import net.minecraft.core.world.data.ChunkNibbleArray;
import net.minecraft.core.world.data.ChunkUnsignedByteArray;

public class ChunkWriter {
   private final World world;
   private final CompoundTag tag;
   private final ListTag sectionsTag;

   public ChunkWriter(World world, CompoundTag tag) {
      this.world = world;
      this.tag = tag;
      if (this.tag.containsKey("Sections")) {
         this.sectionsTag = this.tag.getList("Sections");
      } else {
         this.sectionsTag = new ListTag();
         this.tag.putList("Sections", this.sectionsTag);
      }
   }

   private CompoundTag findSection(int ySection) {
      for (Tag<?> tag : this.sectionsTag) {
         if (tag instanceof CompoundTag) {
            CompoundTag cTag = (CompoundTag)tag;
            int tagY = cTag.getInteger("yPos");
            if (tagY == ySection) {
               return cTag;
            }
         }
      }

      CompoundTag sectionTag = new CompoundTag();
      sectionTag.putInt("yPos", ySection);
      this.sectionsTag.addTag(sectionTag);
      return sectionTag;
   }

   public void putXPos(int xPos) {
      this.tag.putInt("xPos", xPos);
   }

   public void putZPos(int zPos) {
      this.tag.putInt("zPos", zPos);
   }

   public void putLastUpdate(long lastUpdate) {
      this.tag.putLong("LastUpdate", lastUpdate);
   }

   public void putBlocks(int ySection, short[] blocks) {
      this.findSection(ySection).putShortArray("Blocks", blocks);
   }

   public void putData(int ySection, ChunkUnsignedByteArray data) {
      this.findSection(ySection).putByteArray("Data", data.data);
   }

   public void putSkyLight(int ySection, ChunkNibbleArray skyLight) {
      this.findSection(ySection).putByteArray("SkyLight", skyLight.data);
   }

   public void putBlockLight(int ySection, ChunkNibbleArray blockLight) {
      this.findSection(ySection).putByteArray("BlockLight", blockLight.data);
   }

   public void putHeightMap(short[] heightMap) {
      this.tag.putShortArray("HeightMap", heightMap);
   }

   public void putAverageBlockHeight(int averageBlockHeight) {
      this.tag.putInt("AverageBlockHeight", averageBlockHeight);
   }

   public void putTerrainPopulated(boolean terrainPopulated) {
      this.tag.putBoolean("TerrainPopulated", terrainPopulated);
   }

   public void putTemperatureMap(double[] temperatureMap) {
      this.tag.putDoubleArray("TemperatureMap", temperatureMap);
   }

   public void putHumidityMap(double[] humidityMap) {
      this.tag.putDoubleArray("HumidityMap", humidityMap);
   }

   public void putBiomeMap(int ySection, byte[] biomeMap) {
      this.findSection(ySection).putByteArray("BiomeMap", biomeMap);
   }

   public void putBiomeRegistry() {
      CompoundTag registriesTag = this.tag.getCompoundOrDefault("Registries", new CompoundTag());
      CompoundTag biomesTag = new CompoundTag();
      Registry.writeIdMapToTag(Registries.BIOMES, biomesTag);
      registriesTag.putCompound("Biomes", biomesTag);
      this.tag.putCompound("Registries", registriesTag);
   }
}
