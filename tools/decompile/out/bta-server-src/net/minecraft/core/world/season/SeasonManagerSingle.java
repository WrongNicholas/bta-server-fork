package net.minecraft.core.world.season;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.world.World;
import net.minecraft.core.world.config.season.SeasonConfigSingle;

public class SeasonManagerSingle extends SeasonManager {
   private final SeasonConfigSingle config;
   private final List<Season> singleSeasonList = new ArrayList<>();

   public SeasonManagerSingle(World world, SeasonConfigSingle config) {
      super(world);
      this.config = config;
      this.singleSeasonList.add(this.config.getSingleSeason());
   }

   @Override
   public List<Season> getSeasons() {
      return Collections.unmodifiableList(this.singleSeasonList);
   }

   @Override
   public Season getPreviousSeason() {
      return this.config.getSingleSeason();
   }

   @Override
   public Season getCurrentSeason() {
      return this.config.getSingleSeason();
   }

   @Override
   public Season getNextSeason() {
      return this.config.getSingleSeason();
   }

   @Override
   public float getSeasonProgress() {
      return 0.5F;
   }

   @Override
   public int getDayInSeason() {
      return 0;
   }
}
