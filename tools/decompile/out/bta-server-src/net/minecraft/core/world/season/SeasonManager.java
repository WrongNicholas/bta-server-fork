package net.minecraft.core.world.season;

import java.util.List;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.config.season.SeasonConfig;
import net.minecraft.core.world.config.season.SeasonConfigCycle;
import net.minecraft.core.world.config.season.SeasonConfigSingle;

public abstract class SeasonManager {
   protected final World world;

   public static SeasonManager fromConfig(World world, SeasonConfig config) {
      if (config instanceof SeasonConfigSingle) {
         return new SeasonManagerSingle(world, (SeasonConfigSingle)config);
      } else {
         return config instanceof SeasonConfigCycle ? new SeasonManagerCycle(world, (SeasonConfigCycle)config) : null;
      }
   }

   public SeasonManager(World world) {
      this.world = world;
   }

   public abstract List<Season> getSeasons();

   public abstract Season getPreviousSeason();

   public abstract Season getCurrentSeason();

   public abstract Season getNextSeason();

   public abstract float getSeasonProgress();

   public abstract int getDayInSeason();

   public float getSeasonModifier() {
      float progress = this.getSeasonProgress();
      progress = MathHelper.cos((progress * 2.0F - 1.0F) * (float) Math.PI);
      return (progress + 1.0F) / 2.0F;
   }
}
