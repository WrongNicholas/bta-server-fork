package net.minecraft.core.world.weather;

public abstract class Weathers {
   public static final Weather[] WEATHERS = new Weather[16];
   public static final Weather OVERWORLD_CLEAR = new WeatherClear(0).setLanguageKey("overworld.clear");
   public static final Weather OVERWORLD_RAIN = new WeatherRain(1)
      .setLanguageKey("overworld.rain")
      .setPrecipitation("/assets/minecraft/textures/environment/rain.png", 0)
      .setSubtractLightLevel(3)
      .setSpawnRainParticles(true)
      .setFogDistance(0.5F)
      .setDamp();
   public static final Weather OVERWORLD_SNOW = new WeatherSnow(2)
      .setLanguageKey("overworld.snow")
      .setPrecipitation("/assets/minecraft/textures/environment/snow.png", 1)
      .setSubtractLightLevel(1)
      .setFogDistance(0.5F)
      .setDamp();
   public static final Weather OVERWORLD_STORM = new WeatherStorm(3)
      .setLanguageKey("overworld.storm")
      .setPrecipitation("/assets/minecraft/textures/environment/rain.png", 0)
      .setSubtractLightLevel(5)
      .setSpawnRainParticles(true)
      .setMobsSpawnInDaylight()
      .setFogDistance(0.4F)
      .setDamp();
   public static final Weather OVERWORLD_FOG = new WeatherClear(4).setLanguageKey("overworld.fog").setFogDistance(0.125F);
   public static final Weather OVERWORLD_CLEAR_HELL = new WeatherClear(5).setLanguageKey("overworld.clear.hell").setSubtractLightLevel(7);
   public static final Weather OVERWORLD_CLEAR_WOODS = new WeatherClear(6).setLanguageKey("overworld.clear.woods").setSubtractLightLevel(2);
   public static final Weather OVERWORLD_WINTER_SNOW = new WeatherSnow(7)
      .setLanguageKey("overworld.winter.snow")
      .setPrecipitation("/assets/minecraft/textures/environment/snow.png", 1)
      .setDamp();
   public static final Weather OVERWORLD_RAIN_BLOOD = new WeatherStorm(8)
      .setLanguageKey("overworld.rain.blood")
      .setPrecipitation("/assets/minecraft/textures/environment/rain_blood.png", 2)
      .setSubtractLightLevel(5)
      .setMobsSpawnInDaylight()
      .setFogDistance(0.4F)
      .setDamp();

   public static Weather getWeatherByLanguageKey(String string) {
      for (Weather weather : WEATHERS) {
         if (weather != null && weather.languageKey.substring(8).equalsIgnoreCase(string)) {
            return weather;
         }
      }

      return null;
   }

   public static Weather getWeather(int i) {
      return WEATHERS[i];
   }
}
