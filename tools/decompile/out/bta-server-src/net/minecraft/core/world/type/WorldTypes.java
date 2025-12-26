package net.minecraft.core.world.type;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.world.config.season.SeasonConfig;
import net.minecraft.core.world.season.Seasons;
import net.minecraft.core.world.type.nether.WorldTypeNether;
import net.minecraft.core.world.type.nether.WorldTypeNetherSkyblock;
import net.minecraft.core.world.type.overworld.WorldTypeFlat;
import net.minecraft.core.world.type.overworld.WorldTypeOverworld;
import net.minecraft.core.world.type.overworld.WorldTypeOverworldClassic;
import net.minecraft.core.world.type.overworld.WorldTypeOverworldFloating;
import net.minecraft.core.world.type.overworld.WorldTypeOverworldHell;
import net.minecraft.core.world.type.overworld.WorldTypeOverworldIndev;
import net.minecraft.core.world.type.overworld.WorldTypeOverworldInland;
import net.minecraft.core.world.type.overworld.WorldTypeOverworldParadise;
import net.minecraft.core.world.type.overworld.WorldTypeOverworldRetro;
import net.minecraft.core.world.type.overworld.WorldTypeOverworldSkyblock;
import net.minecraft.core.world.type.overworld.WorldTypeOverworldWoods;
import net.minecraft.core.world.type.paradise.WorldTypeParadise;
import net.minecraft.core.world.weather.Weathers;

public abstract class WorldTypes {
   public static final WorldType OVERWORLD_EXTENDED = register(
      "minecraft:overworld.extended", new WorldTypeOverworld(WorldTypeOverworld.defaultProperties("worldType.overworld.extended"))
   );
   public static final WorldType OVERWORLD_DEFAULT = register(
      "minecraft:overworld.default",
      new WorldTypeOverworld(WorldTypeOverworld.defaultProperties("worldType.overworld.default").bounds(0, 127, 64).portalBounds(0, 255))
   );
   public static final WorldType NETHER_DEFAULT = register(
      "minecraft:nether.default", new WorldTypeNether(WorldTypeNether.defaultProperties("worldType.nether.default").portalBounds(127, 255))
   );
   public static final WorldType NETHER_SKYBLOCK = register(
      "minecraft:nether.skyblock", new WorldTypeNetherSkyblock(WorldTypeNether.defaultProperties("worldType.nether.skyblock").bounds(0, 127, 0))
   );
   public static final WorldType PARADISE_DEFAULT = register(
      "minecraft:paradise.default", new WorldTypeParadise(WorldTypeParadise.defaultProperties("worldType.paradise.default"))
   );
   public static final WorldType FLAT = register(
      "minecraft:flat",
      new WorldTypeFlat(
         WorldType.Properties.of("worldType.flat")
            .brightnessRamp(WorldTypeOverworld.createLightRamp())
            .fillerBlock(Blocks.STONE)
            .allowRespawn()
            .bounds(0, 31, 0)
            .portalBounds(0, 255)
            .seasonConfig(SeasonConfig.builder().withSingleSeason(Seasons.OVERWORLD_SUMMER).build())
      )
   );
   public static final WorldType OVERWORLD_FLOATING = register(
      "minecraft:overworld.floating",
      new WorldTypeOverworldFloating(WorldTypeOverworld.defaultProperties("worldType.overworld.floating").bounds(0, 255, 0).oceanBlock(null))
   );
   public static final WorldType OVERWORLD_AMPLIFIED = register(
      "minecraft:overworld.amplified", new WorldTypeOverworld(WorldTypeOverworld.defaultProperties("worldType.overworld.amplified").bounds(0, 255, 64))
   );
   public static final WorldType OVERWORLD_RETRO = register(
      "minecraft:overworld.retro",
      new WorldTypeOverworldRetro(
         WorldTypeOverworld.defaultProperties("worldType.overworld.retro").seasonConfig(null).bounds(0, 127, 64).portalBounds(0, 255).setRetro()
      )
   );
   public static final WorldType OVERWORLD_ISLANDS = register(
      "minecraft:overworld.islands", new WorldTypeOverworld(WorldTypeOverworld.defaultProperties("worldType.overworld.islands").bounds(0, 255, 160))
   );
   public static final WorldType OVERWORLD_WINTER = register(
      "minecraft:overworld.winter",
      new WorldTypeOverworld(
         WorldTypeOverworld.defaultProperties("worldType.overworld.winter")
            .seasonConfig(SeasonConfig.builder().withSingleSeason(Seasons.OVERWORLD_WINTER_ENDLESS).build())
            .bounds(0, 127, 64)
            .portalBounds(0, 255)
      )
   );
   public static final WorldType OVERWORLD_HELL = register(
      "minecraft:overworld.hell",
      new WorldTypeOverworldHell(
         WorldType.Properties.of("worldType.overworld.hell")
            .brightnessRamp(WorldTypeOverworld.createLightRamp())
            .seasonConfig(SeasonConfig.builder().withSingleSeason(Seasons.OVERWORLD_HELL).build())
            .oceanBlock(Blocks.FLUID_LAVA_STILL)
            .fillerBlock(Blocks.STONE)
            .allowRespawn()
            .bounds(0, 127, 64)
            .portalBounds(0, 255)
            .defaultWeather(Weathers.OVERWORLD_CLEAR_HELL)
      )
   );
   public static final WorldType OVERWORLD_WOODS = register(
      "minecraft:overworld.woods",
      new WorldTypeOverworldWoods(WorldTypeOverworld.defaultProperties("worldType.overworld.woods").defaultWeather(Weathers.OVERWORLD_CLEAR_WOODS))
   );
   public static final WorldType OVERWORLD_PARADISE = register(
      "minecraft:overworld.paradise",
      new WorldTypeOverworldParadise(
         WorldTypeOverworld.defaultProperties("worldType.overworld.paradise")
            .seasonConfig(SeasonConfig.builder().withSingleSeason(Seasons.OVERWORLD_SPRING).build())
            .bounds(0, 127, 64)
            .portalBounds(0, 255)
      )
   );
   public static final WorldType OVERWORLD_INLAND = register(
      "minecraft:overworld.inland",
      new WorldTypeOverworldInland(WorldTypeOverworld.defaultProperties("worldType.overworld.inland").bounds(0, 255, 0).oceanBlock(null))
   );
   public static final WorldType OVERWORLD_CLASSIC = register(
      "minecraft:overworld.classic",
      new WorldTypeOverworldClassic(
         WorldTypeOverworld.defaultProperties("worldType.overworld.classic").seasonConfig(null).bounds(0, 127, 64).portalBounds(0, 255).setRetro()
      )
   );
   public static final WorldType OVERWORLD_INDEV = register(
      "minecraft:overworld.indev",
      new WorldTypeOverworldIndev(
         WorldTypeOverworld.defaultProperties("worldType.overworld.indev").seasonConfig(null).bounds(0, 127, 64).portalBounds(0, 255).setRetro()
      )
   );
   public static final WorldType OVERWORLD_SKYBLOCK = register(
      "minecraft:overworld.skyblock",
      new WorldTypeOverworldSkyblock(WorldTypeOverworld.defaultProperties("worldType.overworld.skyblock").bounds(0, 127, 0).portalBounds(0, 255))
   );
   public static final WorldType EMPTY = register(
      "minecraft:empty",
      new WorldTypeEmpty(WorldType.Properties.of("worldType.empty").brightnessRamp(WorldTypeEmpty.createLightRamp()).bounds(0, 255, 0).allowRespawn())
   );
   public static final WorldType DEBUG = register(
      "minecraft:debug",
      new WorldTypeDebug(WorldType.Properties.of("worldType.debug").brightnessRamp(WorldTypeDebug.createLightRamp()).bounds(0, 255, 0).allowRespawn())
   );

   public static WorldType register(String key, WorldType worldType) {
      Registries.WORLD_TYPES.register(key, worldType);
      return worldType;
   }

   public static void init() {
   }
}
