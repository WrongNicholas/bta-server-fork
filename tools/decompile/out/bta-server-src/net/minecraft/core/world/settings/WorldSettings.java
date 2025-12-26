package net.minecraft.core.world.settings;

import java.util.Random;

public class WorldSettings extends WorldSettingCollection {
   @WorldSettingCollection.Setting
   public WorldSetting<Integer> minY = new WorldSettingRange<Integer>("worldSettings.yLimit.minY").withRange(0, 255).withDefaultValue(0);
   @WorldSettingCollection.Setting
   public WorldSetting<Integer> maxY = new WorldSettingRange<Integer>("worldSettings.yLimit.maxY").withRange(0, 255).withDefaultValue(128);
   @WorldSettingCollection.Setting
   public WorldSetting<Long> seed = new WorldSetting<Long>("worldSettings.seed").withDefaultValue(new Random().nextLong());

   public WorldSettings withYLimit(Integer minY, Integer maxY) {
      if (minY != null) {
         this.minY.setValue(minY);
      }

      if (maxY != null) {
         this.maxY.setValue(maxY);
      }

      return this;
   }

   public WorldSettings withSeed(Long seed) {
      if (seed != null) {
         this.seed.setValue(seed);
      }

      return this;
   }
}
