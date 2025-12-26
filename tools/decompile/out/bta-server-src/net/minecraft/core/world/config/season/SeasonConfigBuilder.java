package net.minecraft.core.world.config.season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.world.season.Season;

public class SeasonConfigBuilder {
   private SeasonConfigBuilder.Type type = null;
   private List<Season> seasons = null;
   private Map<Season, Integer> seasonLengthMap = null;
   private Season singleSeason = null;

   protected SeasonConfigBuilder() {
   }

   public SeasonConfig build() {
      if (this.type == SeasonConfigBuilder.Type.SINGLE && this.singleSeason != null) {
         return new SeasonConfigSingle(this.singleSeason);
      } else {
         return this.type == SeasonConfigBuilder.Type.CYCLE && this.seasons != null && this.seasonLengthMap != null
            ? new SeasonConfigCycle(this.seasons, this.seasonLengthMap)
            : null;
      }
   }

   public SeasonConfigBuilder withSingleSeason(Season season) {
      this.type = SeasonConfigBuilder.Type.SINGLE;
      this.seasons = null;
      this.seasonLengthMap = null;
      this.singleSeason = season;
      return this;
   }

   public SeasonConfigBuilder withSeasonInCycle(Season season, int length) {
      this.type = SeasonConfigBuilder.Type.CYCLE;
      if (this.seasons == null) {
         this.seasons = new ArrayList<>();
      }

      this.seasons.add(season);
      if (this.seasonLengthMap == null) {
         this.seasonLengthMap = new HashMap<>();
      }

      this.seasonLengthMap.put(season, length);
      this.singleSeason = null;
      return this;
   }

   private static enum Type {
      SINGLE,
      CYCLE;
   }
}
