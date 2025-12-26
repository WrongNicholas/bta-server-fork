package net.minecraft.core.world.type;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.config.season.SeasonConfig;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.save.LevelData;
import net.minecraft.core.world.season.Seasons;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.Weathers;
import net.minecraft.core.world.wind.WindProvider;
import net.minecraft.core.world.wind.WindProviderGeneric;

public abstract class WorldType {
   private final String languageKey;
   private final Weather defaultWeather;
   private final WindProvider windProvider;
   private final boolean hasCeiling;
   private final float[] brightnessRamp;
   private final SeasonConfig seasonConfig;
   private final int minY;
   private final int minPortalY;
   private final int maxY;
   private final int maxPortalY;
   private final int oceanY;
   private final int oceanBlockId;
   private final int fillerBlockId;
   private final int dayNightCycleTicks;
   private final boolean mayRespawn;
   private final boolean isRetro;

   public WorldType(WorldType.Properties properties) {
      this.languageKey = properties.languageKey;
      this.defaultWeather = properties.defaultWeather == null ? Weathers.OVERWORLD_CLEAR : properties.defaultWeather;
      this.windProvider = (WindProvider)(properties.windProvider == null ? new WindProviderGeneric() : properties.windProvider);
      this.hasCeiling = properties.hasCeiling;
      this.brightnessRamp = properties.brightnessRamp;
      this.seasonConfig = properties.seasonConfig == null ? SeasonConfig.builder().withSingleSeason(Seasons.NULL).build() : properties.seasonConfig;
      this.minY = properties.minY;
      if (properties.minPortalY != null) {
         this.minPortalY = properties.minPortalY;
      } else {
         this.minPortalY = this.minY;
      }

      this.maxY = properties.maxY;
      if (properties.maxPortalY != null) {
         this.maxPortalY = properties.maxPortalY;
      } else {
         this.maxPortalY = this.maxY;
      }

      this.oceanY = properties.oceanY;
      this.oceanBlockId = properties.oceanBlock == null ? 0 : properties.oceanBlock.id();
      this.fillerBlockId = properties.fillerBlock == null ? 0 : properties.fillerBlock.id();
      this.dayNightCycleTicks = properties.dayNightCycleTicks;
      this.mayRespawn = properties.mayRespawn;
      this.isRetro = properties.isRetro;
   }

   public String getLanguageKey() {
      return this.languageKey;
   }

   public Weather getDefaultWeather() {
      return this.defaultWeather;
   }

   public WindProvider getWindManager() {
      return this.windProvider;
   }

   public boolean hasCeiling() {
      return this.hasCeiling;
   }

   public float[] getBrightnessRamp() {
      return this.brightnessRamp;
   }

   public SeasonConfig getSeasonConfig() {
      return this.seasonConfig;
   }

   public int getMinY() {
      return this.minY;
   }

   public int getMinPortalY() {
      return this.minPortalY;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public int getMaxPortalY() {
      return this.maxPortalY;
   }

   public int getOceanY() {
      return this.oceanY;
   }

   public int getOceanBlockId() {
      return this.oceanBlockId;
   }

   public int getFillerBlockId() {
      return this.fillerBlockId;
   }

   public int getDayNightCycleTicks() {
      return this.dayNightCycleTicks;
   }

   public boolean mayRespawn() {
      return this.mayRespawn;
   }

   public boolean isRetro() {
      return this.isRetro;
   }

   public void onWorldCreation(World world) {
   }

   public final int getYRange() {
      return this.getMaxY() - this.getMinY();
   }

   public final double getYPercentage(int y) {
      int range = this.getYRange();
      y -= this.getMinY();
      return (double)y / range;
   }

   public abstract BiomeProvider createBiomeProvider(World var1);

   public abstract ChunkGenerator createChunkGenerator(World var1);

   public abstract boolean isValidSpawn(World var1, int var2, int var3, int var4);

   public void getInitialSpawnLocation(World world) {
      int x = 0;
      int y = 0;
      int z = 0;
      Random random = new Random(world.getRandomSeed());
      int attemptsRemaining = 10000;

      label25:
      while (true) {
         if (attemptsRemaining <= 0) {
            x = 0;
            z = 0;
            y = world.getHeightValue(x, z);
            break;
         }

         x += random.nextInt(64) - random.nextInt(64);
         z += random.nextInt(64) - random.nextInt(64);

         for (y = this.getMaxY(); y >= this.getMinY(); y--) {
            if (world.getBlockId(x, y + 1, z) == 0 && this.isValidSpawn(world, x, y, z)) {
               break label25;
            }
         }

         attemptsRemaining--;
      }

      world.getLevelData().setSpawn(x, y, z);
   }

   public void getRespawnLocation(World world) {
      LevelData levelData = world.getLevelData();
      if (levelData.getSpawnY() <= 0) {
         levelData.setSpawnY(this.getOceanY());
      }

      int x = levelData.getSpawnX();

      int z;
      for (z = levelData.getSpawnZ(); world.getTopBlock(x, z) == 0; z += world.rand.nextInt(8) - world.rand.nextInt(8)) {
         x += world.rand.nextInt(8) - world.rand.nextInt(8);
      }

      levelData.setSpawnX(x);
      levelData.setSpawnZ(z);
   }

   protected int getDayLengthTicks(World world) {
      float seasonProgress = world.getSeasonManager().getSeasonProgress();
      float dayLength;
      if (seasonProgress < 0.5F) {
         float lastSeasonDayLength = world.getSeasonManager().getPreviousSeason().dayLength;
         float thisSeasonDayLength = world.getSeasonManager().getCurrentSeason().dayLength;
         float seasonModifier = world.getSeasonManager().getSeasonModifier() * 0.5F + 0.5F;
         dayLength = lastSeasonDayLength * (1.0F - seasonModifier) + thisSeasonDayLength * seasonModifier;
      } else {
         float thisSeasonDayLength = world.getSeasonManager().getCurrentSeason().dayLength;
         float nextSeasonDayLength = world.getSeasonManager().getNextSeason().dayLength;
         float seasonModifier = world.getSeasonManager().getSeasonModifier() * 0.5F + 0.5F;
         dayLength = thisSeasonDayLength * seasonModifier + nextSeasonDayLength * (1.0F - seasonModifier);
      }

      int cycleTicks = this.getDayNightCycleTicks();
      return (int)(dayLength * cycleTicks);
   }

   public int getSunriseTick(World world) {
      int dayLengthTicks = this.getDayLengthTicks(world);
      int cycleTicks = this.getDayNightCycleTicks();
      return cycleTicks / 4 - dayLengthTicks / 2;
   }

   public float getTimeOfDay(World world, long tick, float partialTick) {
      tick += 18000L;
      int cycleTicks = this.getDayNightCycleTicks();
      int dayTicks = this.getDayLengthTicks(world);
      int nightTicks = cycleTicks - dayTicks;
      float cycleTick = (int)(tick % cycleTicks) + partialTick;
      float time;
      if (cycleTick < dayTicks / 2.0F) {
         time = cycleTick / (dayTicks / 2.0F) * 0.25F;
      } else if (cycleTick < dayTicks / 2.0F + nightTicks / 2.0F) {
         time = 0.25F + (cycleTick - dayTicks / 2.0F) / (nightTicks / 2.0F) * 0.25F;
      } else if (cycleTick < dayTicks / 2.0F + nightTicks / 2.0F + nightTicks / 2.0F) {
         time = 0.5F + (cycleTick - dayTicks / 2.0F - nightTicks / 2.0F) / (nightTicks / 2.0F) * 0.25F;
      } else {
         time = 0.75F + (cycleTick - dayTicks / 2.0F - nightTicks / 2.0F - nightTicks / 2.0F) / (dayTicks / 2.0F) * 0.25F;
      }

      while (time < 0.0F) {
         time++;
      }

      while (time >= 1.0F) {
         time--;
      }

      return time;
   }

   public abstract float getCelestialAngle(World var1, long var2, float var4);

   public abstract int getSkyDarken(World var1, long var2, float var4);

   public static class Properties {
      private final String languageKey;
      private Weather defaultWeather = null;
      private WindProvider windProvider = null;
      private boolean hasCeiling = false;
      private float[] brightnessRamp = null;
      private SeasonConfig seasonConfig = null;
      private int minY = 0;
      private Integer minPortalY = null;
      private int maxY = 255;
      private Integer maxPortalY = null;
      private int oceanY = 128;
      private Block<?> oceanBlock = null;
      private Block<?> fillerBlock = null;
      private int dayNightCycleTicks = 24000;
      private boolean mayRespawn = false;
      private boolean isRetro = false;

      private Properties(String languageKey) {
         this.languageKey = languageKey;
      }

      public static WorldType.Properties of(String languageKey) {
         return new WorldType.Properties(languageKey);
      }

      public WorldType.Properties defaultWeather(Weather weather) {
         this.defaultWeather = weather;
         return this;
      }

      public WorldType.Properties windManager(WindProvider windProvider) {
         this.windProvider = windProvider;
         return this;
      }

      public WorldType.Properties withCeiling() {
         this.hasCeiling = true;
         return this;
      }

      public WorldType.Properties brightnessRamp(float[] brightnessRamp) {
         this.brightnessRamp = brightnessRamp;
         return this;
      }

      public WorldType.Properties seasonConfig(SeasonConfig seasonConfig) {
         this.seasonConfig = seasonConfig;
         return this;
      }

      public WorldType.Properties bounds(int minY, int maxY, int oceanY) {
         if (minY < 0) {
            throw new IllegalArgumentException("minY cannot be negative");
         } else if (maxY < 0) {
            throw new IllegalArgumentException("maxY cannot be negative");
         } else if (maxY <= minY) {
            throw new IllegalArgumentException("maxY cannot be less than or equal to minY");
         } else if (oceanY < 0) {
            throw new IllegalArgumentException("oceanY cannot be negative");
         } else {
            this.minY = minY;
            this.maxY = maxY;
            this.oceanY = oceanY;
            return this;
         }
      }

      public WorldType.Properties portalBounds(int minPortalY, int maxPortalY) {
         if (minPortalY < 0) {
            throw new IllegalArgumentException("minPortalY cannot be negative");
         } else if (maxPortalY < 0) {
            throw new IllegalArgumentException("maxPortalY cannot be negative");
         } else if (maxPortalY <= minPortalY) {
            throw new IllegalArgumentException("maxPortalY cannot be less than or equal to minPortalY");
         } else {
            this.minPortalY = minPortalY;
            this.maxPortalY = maxPortalY;
            return this;
         }
      }

      public WorldType.Properties oceanBlock(Block<?> oceanBlock) {
         this.oceanBlock = oceanBlock;
         return this;
      }

      public WorldType.Properties fillerBlock(Block<?> fillerBlock) {
         this.fillerBlock = fillerBlock;
         return this;
      }

      public WorldType.Properties dayNightCycleTicks(int ticks) {
         this.dayNightCycleTicks = ticks;
         return this;
      }

      public WorldType.Properties allowRespawn() {
         this.mayRespawn = true;
         return this;
      }

      public WorldType.Properties setRetro() {
         this.isRetro = true;
         return this;
      }
   }
}
