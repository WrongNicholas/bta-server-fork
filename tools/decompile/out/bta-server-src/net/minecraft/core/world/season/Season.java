package net.minecraft.core.world.season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.world.weather.Weather;

public abstract class Season {
   private final String id;
   private final String languageKey;
   public List<Weather> allowedWeathers = new ArrayList<>();
   public Map<Weather, Float> weatherProbability = new HashMap<>();
   public boolean hasFallingLeaves = false;
   public boolean hasDeeperSnow = false;
   public boolean letWeatherCleanUpSnow = true;
   public boolean killFlowers = false;
   public boolean growFlowers = false;
   public float dayLength = 0.5F;
   public float cropGrowthFactor = 1.0F;
   public float saplingDropFactor = 1.0F;
   public float mobSpawnRateModifier = 1.0F;

   public Season(String id) {
      this.id = id;
      this.languageKey = "season." + id;
   }

   public Season(String id, boolean hasFallingLeaves, boolean hasDeeperSnow) {
      this(id);
      this.hasFallingLeaves = hasFallingLeaves;
      this.hasDeeperSnow = hasDeeperSnow;
   }

   public String getId() {
      return this.id;
   }

   public String getTranslatedName() {
      return I18n.getInstance().translateNameKey(this.languageKey);
   }

   public Season allowWeather(Weather weather, float probability) {
      this.allowedWeathers.add(weather);
      this.weatherProbability.put(weather, probability);
      return this;
   }

   public Season setLetWeatherCleanUpSnow(boolean value) {
      this.letWeatherCleanUpSnow = value;
      return this;
   }

   public Season setDayLength(float length) {
      this.dayLength = length;
      return this;
   }

   public Season setCropGrowthFactor(float cropGrowthFactor) {
      this.cropGrowthFactor = cropGrowthFactor;
      return this;
   }

   public Season setSaplingDropFactor(float saplingDropFactor) {
      this.saplingDropFactor = saplingDropFactor;
      return this;
   }

   public Season setKillFlowers() {
      this.killFlowers = true;
      return this;
   }

   public Season setGrowFlowers() {
      this.growFlowers = true;
      return this;
   }
}
