package net.minecraft.core.world.save;

import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.ByteTag;
import com.mojang.nbt.tags.CompoundTag;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import net.minecraft.core.data.legacy.LegacyWorldTypes;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypes;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.Weathers;
import org.jetbrains.annotations.Nullable;

public class DimensionData {
   private String worldType;
   private int weatherCurrentId = -1;
   private int weatherNextId = -1;
   private long weatherDuration;
   private float weatherIntensity;
   private float weatherPower;

   public DimensionData(CompoundTag tag) {
      this.readFromNBTTag(tag);
   }

   public DimensionData(WorldType worldType) {
      this.worldType = Registries.WORLD_TYPES.getKey(worldType);
   }

   public DimensionData(DimensionData dimensionData) {
      this.worldType = dimensionData.worldType;
      this.weatherCurrentId = dimensionData.weatherCurrentId;
      this.weatherNextId = dimensionData.weatherNextId;
      this.weatherDuration = dimensionData.weatherDuration;
      this.weatherIntensity = dimensionData.weatherIntensity;
      this.weatherPower = dimensionData.weatherPower;
   }

   public DimensionData(File worldDir, Dimension dimension) throws IOException {
      CompoundTag nbtRootData = null;
      File dimensionsDir = new File(worldDir, "dimensions");
      File dimensionDir = new File(dimensionsDir, "" + dimension.id);
      if (worldDir.exists() && dimensionsDir.exists() && dimensionDir.exists()) {
         File dimensionDat = new File(dimensionDir, "dimension.dat");
         if (dimensionDat.exists()) {
            CompoundTag nbtRoot = NbtIo.readCompressed(Files.newInputStream(dimensionDat.toPath()));
            nbtRootData = nbtRoot.getCompound("Data");
         } else {
            dimensionDat = new File(dimensionDir, "dimension.dat_old");
            if (dimensionDat.exists()) {
               CompoundTag oldNbtRoot = NbtIo.readCompressed(Files.newInputStream(dimensionDat.toPath()));
               nbtRootData = oldNbtRoot.getCompound("Data");
            }
         }

         if (nbtRootData != null) {
            this.readFromNBTTag(nbtRootData);
         } else {
            throw new IOException();
         }
      } else {
         throw new IOException();
      }
   }

   public CompoundTag toNBTTag() {
      CompoundTag tag = new CompoundTag();
      this.writeToNBTTag(tag);
      return tag;
   }

   private void writeToNBTTag(CompoundTag dimTag) {
      dimTag.putString("WorldType", this.worldType);
      CompoundTag weatherTag = new CompoundTag();
      weatherTag.putInt("CurrentID", this.weatherCurrentId);
      weatherTag.putInt("NextID", this.weatherNextId);
      weatherTag.putLong("Duration", this.weatherDuration);
      weatherTag.putFloat("Intensity", this.weatherIntensity);
      weatherTag.putFloat("Power", this.weatherPower);
      dimTag.putCompound("Weather", weatherTag);
   }

   private void readFromNBTTag(CompoundTag dimTag) {
      if (dimTag.getTag("WorldType") instanceof ByteTag) {
         WorldType legacyWorldType = LegacyWorldTypes.getWorldTypeById(dimTag.getByte("WorldType"));
         if (legacyWorldType == null) {
            legacyWorldType = WorldTypes.OVERWORLD_EXTENDED;
         }

         this.worldType = Registries.WORLD_TYPES.getKey(legacyWorldType);
      } else {
         this.worldType = dimTag.getString("WorldType");
      }

      CompoundTag weatherTag = dimTag.getCompound("Weather");
      this.weatherCurrentId = weatherTag.getInteger("CurrentID");
      this.weatherNextId = weatherTag.getInteger("NextID");
      this.weatherDuration = weatherTag.getLong("Duration");
      this.weatherIntensity = weatherTag.getFloat("Intensity");
      this.weatherPower = weatherTag.getFloat("Power");
   }

   public WorldType getWorldType() {
      return Registries.WORLD_TYPES.getItem(this.worldType);
   }

   public void setWorldType(WorldType type) {
      this.worldType = Registries.WORLD_TYPES.getKey(type);
   }

   public Weather getCurrentWeather() {
      return this.weatherCurrentId < 0 ? null : Weathers.getWeather(this.weatherCurrentId);
   }

   public void setCurrentWeather(@Nullable Weather weather) {
      if (weather == null) {
         this.weatherCurrentId = -1;
      } else {
         this.weatherCurrentId = weather.weatherId;
      }
   }

   @Nullable
   public Weather getNextWeather() {
      return this.weatherNextId == -1 ? null : Weathers.getWeather(this.weatherNextId);
   }

   public void setNextWeather(@Nullable Weather weather) {
      if (weather == null) {
         this.weatherNextId = -1;
      } else {
         this.weatherNextId = weather.weatherId;
      }
   }

   public long getWeatherDuration() {
      return this.weatherDuration;
   }

   public void setWeatherDuration(long duration) {
      this.weatherDuration = duration;
   }

   public float getWeatherIntensity() {
      return this.weatherIntensity;
   }

   public void setWeatherIntensity(float intensity) {
      this.weatherIntensity = intensity;
   }

   public float getWeatherPower() {
      return this.weatherPower;
   }

   public void setWeatherPower(float power) {
      this.weatherPower = power;
   }
}
