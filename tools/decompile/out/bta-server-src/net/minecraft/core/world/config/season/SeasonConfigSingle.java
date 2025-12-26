package net.minecraft.core.world.config.season;

import net.minecraft.core.world.season.Season;

public class SeasonConfigSingle extends SeasonConfig {
   private final Season singleSeason;

   protected SeasonConfigSingle(Season singleSeason) {
      this.singleSeason = singleSeason;
   }

   public Season getSingleSeason() {
      return this.singleSeason;
   }
}
