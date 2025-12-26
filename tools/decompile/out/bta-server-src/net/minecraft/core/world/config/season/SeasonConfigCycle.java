package net.minecraft.core.world.config.season;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.world.season.Season;
import org.jetbrains.annotations.NotNull;

public class SeasonConfigCycle extends SeasonConfig {
   private final List<Season> seasons;
   private final SeasonConfigCycle.SeasonMapInternal seasonLengthMap;

   protected SeasonConfigCycle(List<Season> seasons, @NotNull Map<Season, Integer> seasonLengthMap) {
      this.seasons = seasons;
      this.seasonLengthMap = new SeasonConfigCycle.SeasonMapInternal(seasonLengthMap);
   }

   public List<Season> getSeasons() {
      return this.seasons;
   }

   public int getSeasonLength(Season season) {
      return this.seasonLengthMap.getOrDefault(season, 0);
   }

   private static class SeasonMapInternal {
      private final Season[] seasons;
      private final int[] lengths;

      public SeasonMapInternal(@NotNull Map<Season, Integer> seasonLengthMap) {
         this.seasons = new Season[seasonLengthMap.size()];
         this.lengths = new int[seasonLengthMap.size()];
         int i = 0;

         for (Entry<Season, Integer> entry : seasonLengthMap.entrySet()) {
            this.seasons[i] = entry.getKey();
            this.lengths[i] = entry.getValue();
            i++;
         }
      }

      public int getOrDefault(Season season, int defaultValue) {
         for (int i = 0; i < this.seasons.length; i++) {
            if (this.seasons[i].equals(season)) {
               return this.lengths[i];
            }
         }

         return defaultValue;
      }
   }
}
