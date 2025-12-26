package net.minecraft.core.world.weather;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLayerBase;
import net.minecraft.core.block.BlockLogicLayerSnow;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.Chunk;

public class WeatherSnow extends Weather {
   public WeatherSnow(int id) {
      super(id);
   }

   @Override
   public String getPrecipitationTexture(World world) {
      return world.weatherManager.getWeatherPower() <= 0.6F
         ? "/assets/minecraft/textures/environment/snow_light.png"
         : "/assets/minecraft/textures/environment/snow.png";
   }

   @Override
   public void doChunkLoadEffect(World world, Chunk chunk) {
      if (!(world.weatherManager.getWeatherPower() <= 0.6F)) {
         for (int x = 0; x < 16; x++) {
            int worldX = chunk.xPosition * 16 + x;

            label57:
            for (int z = 0; z < 16; z++) {
               int worldZ = chunk.zPosition * 16 + z;
               int y = world.findTopSolidBlock(worldX, worldZ);
               Biome biome = world.getBlockBiome(worldX, y, worldZ);

               for (int i = 0; i < biome.blockedWeathers.length; i++) {
                  if (biome.blockedWeathers[i] == this) {
                     continue label57;
                  }
               }

               int blockId = chunk.getBlockID(x, y, z);
               int blockIdBelow = chunk.getBlockID(x, y - 1, z);
               if (y >= 0 && y < world.getHeightBlocks() && chunk.getBrightness(LightLayer.Block, x, y, z) < 10) {
                  if (blockId == 0 && Blocks.LAYER_SNOW.canPlaceBlockAt(world, worldX, y, worldZ) && blockIdBelow != Blocks.ICE.id()) {
                     chunk.setBlockIDWithMetadataRaw(x, y, z, Blocks.LAYER_SNOW.id(), chunk.getBlockMetadata(x, y, z));
                  } else if (blockIdBelow == Blocks.FLUID_WATER_STILL.id() && chunk.getBlockMetadata(x, y - 1, z) == 0) {
                     chunk.setBlockIDWithMetadataRaw(x, y - 1, z, Blocks.ICE.id(), chunk.getBlockMetadata(x, y - 1, z));
                     if (chunk.getBlockID(x, y - 2, z) == Blocks.FLUID_WATER_STILL.id()) {
                        chunk.setBlockIDWithMetadataRaw(x, y - 2, z, Blocks.FLUID_WATER_FLOWING.id(), chunk.getBlockMetadata(x, y - 2, z));
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void doEnvironmentUpdate(World world, Random rand, int x, int z) {
      int probability = (int)(64.0F * (1.0F / world.weatherManager.getWeatherPower()));
      if (world.getSeasonManager().getCurrentSeason() != null && world.getSeasonManager().getCurrentSeason().hasDeeperSnow) {
         probability /= 2;
      }

      boolean snow = rand.nextInt(probability) == 0;
      int y = world.findTopSolidBlock(x, z);
      int blockId = world.getBlockId(x, y, z);
      int blockIdBelow = world.getBlockId(x, y - 1, z);
      Biome biome = world.getBlockBiome(x, y, z);

      for (int i = 0; i < biome.blockedWeathers.length; i++) {
         if (biome.blockedWeathers[i] == this) {
            return;
         }
      }

      if (world.weatherManager.getWeatherPower() > 0.6F && y >= 0 && y < world.getHeightBlocks() && world.getSavedLightValue(LightLayer.Block, x, y, z) < 10) {
         if (blockId != 0 || blockIdBelow == 0 || !Blocks.LAYER_SNOW.canPlaceBlockAt(world, x, y, z) || blockIdBelow == Blocks.ICE.id()) {
            if (!(world.weatherManager.getWeatherPower() > 0.5F)
               || blockId != Blocks.LAYER_SNOW.id()
               || world.getSeasonManager().getCurrentSeason() == null
               || !world.getSeasonManager().getCurrentSeason().hasDeeperSnow && biome != Biomes.OVERWORLD_GLACIER) {
               if (blockIdBelow == Blocks.FLUID_WATER_STILL.id()
                  && world.getBlockMetadata(x, y - 1, z) == 0
                  && rand.nextFloat() < world.weatherManager.getWeatherPower() * world.weatherManager.getWeatherIntensity()) {
                  for (int ix = 0; ix < 4; ix++) {
                     Direction direction = Direction.horizontalDirections[ix];
                     Block<?> block = world.getBlock(x + direction.getOffsetX(), y - 1, z + direction.getOffsetZ());
                     if (block == Blocks.ICE || block != null && block.isSolidRender()) {
                        world.setBlockWithNotify(x, y - 1, z, Blocks.ICE.id());
                        break;
                     }
                  }
               }
            } else if (snow) {
              ((BlockLogicLayerSnow) Blocks.LAYER_SNOW.getLogic()).accumulate(world, x, y, z);
            }
         } else if (snow) {
            world.setBlockWithNotify(x, y, z, Blocks.LAYER_SNOW.id());
         }
      }
   }
}
