package net.minecraft.core.world.config.season;

public abstract class SeasonConfig {
   public static SeasonConfigBuilder builder() {
      return new SeasonConfigBuilder();
   }
}
