package net.minecraft.core.world.season;

import java.util.Collections;
import java.util.List;
import net.minecraft.core.world.World;
import net.minecraft.core.world.config.season.SeasonConfigCycle;

public final class SeasonManagerCycle extends SeasonManager {
   private final SeasonConfigCycle config;
   private final int yearLengthTicks;

   public SeasonManagerCycle(World world, SeasonConfigCycle config) {
      super(world);
      this.config = config;
      int acc = 0;

      for (Season season : config.getSeasons()) {
         acc += config.getSeasonLength(season) * 24000;
      }

      this.yearLengthTicks = acc;
   }

   public SeasonConfigCycle getConfig() {
      return this.config;
   }

   public int getSeasonLengthTicks(Season season) {
      return this.config.getSeasonLength(season) * 24000;
   }

   private Season getSeasonByIndex(int index) {
      return this.config.getSeasons().get(Math.floorMod(index, this.config.getSeasons().size()));
   }

   private int getSeasonIndex(Season season) {
      for (int i = 0; i < this.config.getSeasons().size(); i++) {
         if (this.config.getSeasons().get(i) == season) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public List<Season> getSeasons() {
      return Collections.unmodifiableList(this.config.getSeasons());
   }

   @Override
   public Season getPreviousSeason() {
      Season currentSeason = this.getCurrentSeason();
      int currentSeasonIndex = this.getSeasonIndex(currentSeason);
      return this.getSeasonByIndex(currentSeasonIndex - 1);
   }

   @Override
   public Season getCurrentSeason() {
      long worldTime = this.world.getWorldTime();
      int yearTime = (int)(worldTime % this.yearLengthTicks);
      int acc = 0;

      for (Season season : this.config.getSeasons()) {
         if (acc + this.getSeasonLengthTicks(season) > yearTime) {
            return season;
         }

         acc += this.getSeasonLengthTicks(season);
      }

      return null;
   }

   @Override
   public Season getNextSeason() {
      Season currentSeason = this.getCurrentSeason();
      int currentSeasonIndex = this.getSeasonIndex(currentSeason);
      return this.getSeasonByIndex(currentSeasonIndex + 1);
   }

   @Override
   public float getSeasonProgress() {
      long worldTime = this.world.getWorldTime();
      int yearTime = (int)(worldTime % this.yearLengthTicks);
      int acc = 0;
      int seasonTime = -1;
      int seasonLength = -1;

      for (Season season : this.config.getSeasons()) {
         if (acc + this.getSeasonLengthTicks(season) > yearTime) {
            seasonTime = yearTime - acc;
            seasonLength = this.getSeasonLengthTicks(season);
            break;
         }

         acc += this.getSeasonLengthTicks(season);
      }

      return seasonTime == -1 ? Float.NaN : (float)seasonTime / seasonLength;
   }

   public float getYearProgress() {
      long worldTime = this.world.getWorldTime();
      int yearTime = (int)(worldTime % this.yearLengthTicks);
      return (float)yearTime / this.yearLengthTicks;
   }

   @Override
   public int getDayInSeason() {
      Season season = this.getCurrentSeason();
      float progress = this.getSeasonProgress();
      int seasonTime = (int)(this.getSeasonLengthTicks(season) * progress);
      return seasonTime / 24000;
   }

   public int getYearLengthTicks() {
      return this.yearLengthTicks;
   }
}
