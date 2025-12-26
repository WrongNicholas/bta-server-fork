package net.minecraft.core.world.weather;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

public abstract class Weather {
   public int weatherId;
   public String languageKey;
   public boolean isPrecipitation = false;
   private String texturePath = null;
   public int subtractLightLevel = 0;
   public int precipitationType = 0;
   public boolean spawnRainParticles = false;
   public boolean doMobsSpawnInDaylight = false;
   public boolean isDamp = false;
   public float fogDistance = 1.0F;

   public Weather(int id) {
      if (Weathers.WEATHERS[id] != null) {
         throw new RuntimeException();
      } else {
         Weathers.WEATHERS[id] = this;
         this.weatherId = id;
      }
   }

   public Weather setLanguageKey(String key) {
      this.languageKey = "weather." + key;
      return this;
   }

   public Weather setPrecipitation(String texturePath, int type) {
      this.isPrecipitation = true;
      this.texturePath = texturePath;
      this.precipitationType = type;
      return this;
   }

   public Weather setPrecipitationTexture(String texturePath) {
      this.texturePath = texturePath;
      return this;
   }

   public String getPrecipitationTexture(World world) {
      return this.texturePath;
   }

   public Weather setSubtractLightLevel(int subtractLightLevel) {
      this.subtractLightLevel = subtractLightLevel;
      return this;
   }

   public Weather setSpawnRainParticles(boolean spawnRainParticles) {
      this.spawnRainParticles = spawnRainParticles;
      return this;
   }

   public Weather setFogDistance(float f) {
      this.fogDistance = f;
      return this;
   }

   public Weather setMobsSpawnInDaylight() {
      this.doMobsSpawnInDaylight = true;
      return this;
   }

   public Weather setDamp() {
      this.isDamp = true;
      return this;
   }

   public String getTranslatedName() {
      return I18n.getInstance().translateKey(this.languageKey + ".name");
   }

   public String getLanguageKey() {
      return this.languageKey;
   }

   public float[] modifyFogColor(float r, float g, float b, float intensity) {
      return new float[]{r, g, b};
   }

   public void doEnvironmentUpdate(World world, Random rand, int x, int z) {
      if (world.getSeasonManager().getCurrentSeason() == null || world.getSeasonManager().getCurrentSeason().letWeatherCleanUpSnow) {
         if (rand.nextInt(4) == 0) {
            int y = world.getHeightValue(x, z);
            int blockId = world.getBlockId(x, y, z);
            int blockIdBelow = world.getBlockId(x, y - 1, z);
            if (world.getBlockBiome(x, y, z).hasSurfaceSnow()) {
               if (blockId == Blocks.LAYER_SNOW.id()) {
                  int meta = world.getBlockMetadata(x, y, z);
                  if (meta != 0) {
                     world.setBlockMetadata(x, y, z, meta - 1);
                     world.markBlockNeedsUpdate(x, y, z);
                  }
               }
            } else if (blockId == Blocks.LAYER_SNOW.id()) {
               int meta = world.getBlockMetadata(x, y, z);
               if (meta != 0) {
                  world.setBlockMetadata(x, y, z, meta - 1);
                  world.markBlockNeedsUpdate(x, y, z);
               } else {
                  world.setBlockWithNotify(x, y, z, 0);
               }
            } else if (blockIdBelow == Blocks.ICE.id()) {
               world.setBlockWithNotify(x, y - 1, z, Blocks.FLUID_WATER_STILL.id());
            }
         }
      }
   }

   public void doChunkLoadEffect(World world, Chunk chunk) {
      if (world.getSeasonManager().getCurrentSeason() == null || world.getSeasonManager().getCurrentSeason().letWeatherCleanUpSnow) {
         for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
               int y = chunk.getHeightValue(x, z);
               if (y != 0) {
                  int blockId = chunk.getBlockID(x, y, z);
                  int blockIdBelow = chunk.getBlockID(x, y - 1, z);
                  if (world.getBlockBiome(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z).hasSurfaceSnow()) {
                     if (blockId == Blocks.LAYER_SNOW.id()) {
                        chunk.setBlockMetadata(x, y, z, 0);
                     }
                  } else if (blockId == Blocks.LAYER_SNOW.id()) {
                     chunk.setBlockID(x, y, z, 0);
                  } else if (blockIdBelow == Blocks.ICE.id()) {
                     chunk.setBlockID(x, y - 1, z, Blocks.FLUID_WATER_STILL.id());
                  }
               }
            }
         }
      }
   }
}
