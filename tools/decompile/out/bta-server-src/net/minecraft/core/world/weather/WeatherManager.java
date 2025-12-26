package net.minecraft.core.world.weather;

import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Season;

public class WeatherManager {
   private final World world;
   private Weather lastWeather = null;
   private Weather currentWeather = null;
   private Weather nextWeather = null;
   private long weatherDuration = 24000L;
   private float weatherIntensity = 1.0F;
   private float weatherPower = 1.0F;

   public WeatherManager(World world) {
      this.world = world;
   }

   public WeatherManager(World world, Weather currentWeather, Weather nextWeather, long weatherDuration, float weatherIntensity, float weatherPower) {
      this(world);
      this.currentWeather = currentWeather;
      this.nextWeather = nextWeather;
      this.weatherDuration = weatherDuration;
      this.weatherPower = weatherPower;
      if (nextWeather != null) {
         this.weatherIntensity = 0.0F;
      } else {
         this.weatherIntensity = 1.0F;
      }
   }

   public Weather getLastWeather() {
      return this.lastWeather;
   }

   public Weather getCurrentWeather() {
      return this.currentWeather;
   }

   public Weather getNextWeather() {
      return this.nextWeather;
   }

   public long getWeatherDuration() {
      return this.weatherDuration;
   }

   public float getWeatherIntensity() {
      return this.weatherIntensity;
   }

   public float getWeatherPower() {
      return this.weatherPower;
   }

   public void overrideWeather(Weather currentWeather, Weather nextWeather, long weatherDuration, float weatherIntensity, float weatherPower) {
      this.lastWeather = this.currentWeather;
      this.currentWeather = currentWeather;
      this.nextWeather = nextWeather;
      this.weatherDuration = weatherDuration;
      this.weatherIntensity = weatherIntensity;
      this.weatherPower = weatherPower;
   }

   public void overrideWeather(Weather weather, long duration, float power) {
      this.lastWeather = this.currentWeather;
      this.currentWeather = weather;
      this.nextWeather = null;
      this.weatherDuration = duration;
      this.weatherIntensity = 1.0F;
      this.weatherPower = MathHelper.clamp(power, 0.0F, 1.0F);
   }

   public void overrideWeather(Weather weather, long duration) {
      this.overrideWeather(weather, duration, this.getRandomWeatherPower());
   }

   public void overrideWeather(Weather weather) {
      this.overrideWeather(weather, this.getRandomWeatherDuration());
   }

   public void skip() {
      this.weatherDuration = 0L;
   }

   public boolean tick() {
      if (this.world.worldType.hasCeiling()) {
         return false;
      } else if (this.world.getSeasonManager().getCurrentSeason() == null) {
         return false;
      } else {
         if (this.currentWeather == null) {
            this.lastWeather = null;
            this.currentWeather = this.world.worldType.getDefaultWeather();
            this.weatherDuration = this.getRandomWeatherDuration();
         }

         if (this.nextWeather == null && this.weatherIntensity <= 1.0F) {
            this.weatherIntensity += 0.002F;
            if (this.weatherIntensity > 1.0F) {
               this.weatherIntensity = 1.0F;
            }
         } else if (this.nextWeather != null && this.weatherIntensity >= 0.0F) {
            this.weatherIntensity -= 0.002F;
            if (!this.world.isClientSide
               && this.world.dayCanHaveRainbow
               && this.world.rainbowTicks <= 0
               && this.weatherIntensity < 0.5F
               && this.currentWeather.precipitationType == 0
               && !this.nextWeather.isPrecipitation) {
               int rainbowTicks = this.world.rand.nextInt(1800) + 600;
               this.world.addRainbow(rainbowTicks);
            }

            if (this.weatherIntensity <= 0.0F) {
               this.weatherIntensity = 0.0F;
               this.lastWeather = this.currentWeather;
               this.currentWeather = this.nextWeather;
               this.weatherPower = this.getRandomWeatherPower();
               this.nextWeather = null;
            }
         }

         if (this.world.getGameRuleValue(GameRules.DO_WEATHER_CYCLE)) {
            this.weatherDuration--;
         }

         if (this.weatherDuration <= 0L) {
            if (this.currentWeather != this.world.worldType.getDefaultWeather()) {
               this.nextWeather = this.world.worldType.getDefaultWeather();
            } else {
               Season season = this.world.getSeasonManager().getCurrentSeason();
               boolean foundWeather = false;
               float acc = 0.0F;
               float val = this.world.rand.nextFloat();

               for (int i = 0; i < season.allowedWeathers.size(); i++) {
                  acc += season.weatherProbability.get(season.allowedWeathers.get(i));
                  if (val < acc) {
                     this.nextWeather = season.allowedWeathers.get(i);
                     foundWeather = true;
                     break;
                  }
               }

               if (!foundWeather) {
                  this.nextWeather = this.world.worldType.getDefaultWeather();
               }
            }

            this.weatherDuration = this.getRandomWeatherDuration();
         }

         this.world.dimensionData.setCurrentWeather(this.currentWeather);
         this.world.dimensionData.setNextWeather(this.nextWeather);
         this.world.dimensionData.setWeatherDuration(this.weatherDuration);
         this.world.dimensionData.setWeatherIntensity(this.weatherIntensity);
         this.world.dimensionData.setWeatherPower(this.weatherPower);
         return true;
      }
   }

   private long getRandomWeatherDuration() {
      return (long)(this.world.rand.nextFloat() * 36000.0F + 6000.0F);
   }

   private float getRandomWeatherPower() {
      return this.world.rand.nextFloat() * 0.5F + 0.5F;
   }
}
