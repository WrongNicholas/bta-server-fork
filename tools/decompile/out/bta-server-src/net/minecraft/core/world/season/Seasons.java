package net.minecraft.core.world.season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.world.weather.Weathers;

public abstract class Seasons {
   private static final Map<String, Season> SEASONS_MAP = new HashMap<>();
   public static final Season NULL = register(new SeasonNull("null"));
   public static final Season OVERWORLD_SPRING = register(
      new SeasonSpring("overworld.spring")
         .allowWeather(Weathers.OVERWORLD_RAIN, 0.3F)
         .allowWeather(Weathers.OVERWORLD_FOG, 0.3F)
         .setDayLength(0.5F)
         .setCropGrowthFactor(1.5F)
         .setGrowFlowers()
         .setSaplingDropFactor(2.0F)
   );
   public static final Season OVERWORLD_SUMMER = register(
      new SeasonSummer("overworld.summer").allowWeather(Weathers.OVERWORLD_RAIN, 0.1F).allowWeather(Weathers.OVERWORLD_STORM, 0.1F).setDayLength(0.6F)
   );
   public static final Season OVERWORLD_FALL = register(
      new SeasonFall("overworld.fall")
         .allowWeather(Weathers.OVERWORLD_RAIN, 0.2F)
         .allowWeather(Weathers.OVERWORLD_STORM, 0.05F)
         .allowWeather(Weathers.OVERWORLD_FOG, 0.5F)
         .setDayLength(0.5F)
         .setSaplingDropFactor(0.5F)
   );
   public static final Season OVERWORLD_WINTER = register(
      new SeasonWinter("overworld.winter")
         .allowWeather(Weathers.OVERWORLD_SNOW, 0.5F)
         .allowWeather(Weathers.OVERWORLD_FOG, 0.25F)
         .setLetWeatherCleanUpSnow(false)
         .setDayLength(0.3F)
         .setCropGrowthFactor(0.25F)
         .setKillFlowers()
         .setSaplingDropFactor(0.1F)
   );
   public static final Season OVERWORLD_HELL = register(new SeasonOverworldHell("overworld.hell"));
   public static final Season OVERWORLD_WINTER_ENDLESS = register(
      new SeasonWinter("overworld.winter.endless")
         .allowWeather(Weathers.OVERWORLD_WINTER_SNOW, 1.0F)
         .setLetWeatherCleanUpSnow(false)
         .setDayLength(0.3F)
         .setCropGrowthFactor(0.25F)
         .setKillFlowers()
         .setSaplingDropFactor(0.1F)
   );
   public static final Season PARADISE_GOLD = register(new SeasonParadiseGold("paradise.gold").setLetWeatherCleanUpSnow(false));
   public static final Season PARADISE_SILVER = register(new SeasonParadiseSilver("paradise.silver").setLetWeatherCleanUpSnow(false));

   public static Season getSeason(String key) {
      return SEASONS_MAP.getOrDefault(key, NULL);
   }

   public static Season register(Season season) {
      if (SEASONS_MAP.containsKey(season.getId())) {
         throw new IllegalArgumentException("Season with ID \"" + season.getId() + "\" is already registered!");
      } else {
         SEASONS_MAP.put(season.getId(), season);
         return season;
      }
   }

   public static List<Season> getAllSeasons() {
      List<String> seasonNames = new ArrayList<>(SEASONS_MAP.keySet());
      seasonNames.sort(String.CASE_INSENSITIVE_ORDER);
      List<Season> seasons = new ArrayList<>();

      for (int i = 0; i < seasonNames.size(); i++) {
         seasons.add(getSeason(seasonNames.get(i)));
      }

      return seasons;
   }
}
