package net.minecraft.core.world.biome;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.world.weather.Weathers;

public class Biomes {
   public static final Biome OVERWORLD_RAINFOREST = register(
      "minecraft:overworld.rainforest", new BiomeRainforest("rainforest").setColor(1896704).setBlockedWeathers(Weathers.OVERWORLD_SNOW)
   );
   public static final Biome OVERWORLD_SWAMPLAND = register("minecraft:overworld.swampland", new BiomeSwamp("swampland").setColor(65400));
   public static final Biome OVERWORLD_SEASONAL_FOREST = register(
      "minecraft:overworld.seasonal_forest", new BiomeSeasonalForest("seasonal_forest").setColor(6606357)
   );
   public static final Biome OVERWORLD_FOREST = register("minecraft:overworld.forest", new BiomeForest("forest").setColor(2532885));
   public static final Biome OVERWORLD_GRASSLANDS = register(
      "minecraft:overworld.grasslands", new Biome("grasslands").setColor(15790152).setBlockedWeathers(Weathers.OVERWORLD_SNOW)
   );
   public static final Biome OVERWORLD_OUTBACK = register(
      "minecraft:overworld.outback",
      new BiomeOutback("outback")
         .setColor(14245408)
         .setBlockedWeathers(Weathers.OVERWORLD_SNOW)
         .setTopBlock(Blocks.DIRT_SCORCHED.id())
         .setFillerBlock(Blocks.DIRT_SCORCHED.id())
   );
   public static final Biome OVERWORLD_SHRUBLAND = register("minecraft:overworld.shrubland", new BiomeShrubland("shrubland").setColor(12237368));
   public static final Biome OVERWORLD_TAIGA = register("minecraft:overworld.taiga", new BiomeTaiga("taiga").setColor(9092351).setSurfaceSnow());
   public static final Biome OVERWORLD_BOREAL_FOREST = register("minecraft:overworld.boreal_forest", new BiomeBorealForest("boreal_forest").setColor(1344868));
   public static final Biome OVERWORLD_DESERT = register(
      "minecraft:overworld.desert",
      new BiomeDesert("desert")
         .setColor(15589999)
         .setBlockedWeathers(Weathers.OVERWORLD_RAIN, Weathers.OVERWORLD_SNOW, Weathers.OVERWORLD_STORM)
         .setTopBlock(Blocks.SAND.id())
         .setFillerBlock(Blocks.SAND.id())
   );
   public static final Biome OVERWORLD_PLAINS = register("minecraft:overworld.plains", new BiomePlains("plains").setColor(10733129));
   public static final Biome OVERWORLD_GLACIER = register(
      "minecraft:overworld.glacier",
      new Biome("glacier").setColor(13367039).setSurfaceSnow().setTopBlock(Blocks.BLOCK_SNOW.id()).setFillerBlock(Blocks.BLOCK_SNOW.id())
   );
   public static final Biome OVERWORLD_TUNDRA = register("minecraft:overworld.tundra", new Biome("tundra").setColor(5759231).setSurfaceSnow());
   public static final Biome OVERWORLD_MEADOW = register("minecraft:overworld.meadow", new BiomeBorealForest("meadow").setColor(9210914));
   public static final Biome NETHER_NETHER = register(
      "minecraft:nether.nether",
      new BiomeNether("nether")
         .setColor(14483456)
         .setBlockedWeathers(Weathers.OVERWORLD_RAIN, Weathers.OVERWORLD_SNOW, Weathers.OVERWORLD_STORM, Weathers.OVERWORLD_FOG)
   );
   public static final Biome PARADISE_PARADISE = register(
      "minecraft:paradise.paradise",
      new BiomeParadise("paradise")
         .setColor(8421631)
         .setBlockedWeathers(Weathers.OVERWORLD_RAIN, Weathers.OVERWORLD_SNOW, Weathers.OVERWORLD_STORM, Weathers.OVERWORLD_FOG)
   );
   public static final Biome OVERWORLD_BIRCH_FOREST = register("minecraft:overworld.birch_forest", new BiomeBirchForest("birch_forest").setColor(4967262));
   public static final Biome OVERWORLD_RETRO = register("minecraft:overworld.retro", new BiomeRetro("retro").setColor(7778634).setTopBlock(Blocks.GRASS.id()));
   public static final Biome OVERWORLD_HELL = register(
      "minecraft:overworld.hell", new Biome("hell").setColor(14246435).setTopBlock(Blocks.DIRT_SCORCHED.id()).setTopBlock(Blocks.DIRT_SCORCHED.id())
   );
   public static final Biome OVERWORLD_SWAMPLAND_MUDDY = register("minecraft:overworld.swampland.muddy", new BiomeSwamp("swampland.muddy").setColor(5383936));
   public static final Biome OVERWORLD_OUTBACK_GRASSY = register(
      "minecraft:overworld.outback.grassy",
      new BiomeOutback("outback.grassy")
         .setColor(15765559)
         .setBlockedWeathers(Weathers.OVERWORLD_SNOW)
         .setTopBlock(Blocks.GRASS_SCORCHED.id())
         .setFillerBlock(Blocks.DIRT_SCORCHED.id())
   );
   public static final Biome OVERWORLD_CAATINGA = register(
      "minecraft:overworld.caatinga",
      new BiomeCaatinga("caatinga")
         .setColor(14601638)
         .setTopBlock(Blocks.MUD_BAKED.id())
         .setFillerBlock(Blocks.MUD_BAKED.id())
         .setBlockedWeathers(Weathers.OVERWORLD_SNOW)
   );
   public static final Biome OVERWORLD_CAATINGA_PLAINS = register(
      "minecraft:overworld.caatinga.plains",
      new Biome("caatinga.plains")
         .setColor(10589303)
         .setTopBlock(Blocks.MUD_BAKED.id())
         .setFillerBlock(Blocks.MUD_BAKED.id())
         .setBlockedWeathers(Weathers.OVERWORLD_SNOW)
   );

   public static Biome register(String key, Biome biome) {
      Registries.BIOMES.register(key, biome);
      return biome;
   }

   public static void init() {
   }
}
