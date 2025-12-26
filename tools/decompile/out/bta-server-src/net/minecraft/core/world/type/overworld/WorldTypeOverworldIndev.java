package net.minecraft.core.world.type.overworld;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderSingleBiome;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.indev.ChunkGeneratorIndev;
import net.minecraft.core.world.generate.chunk.indev.IndevWorldTheme;
import net.minecraft.core.world.generate.chunk.indev.IndevWorldType;
import net.minecraft.core.world.generate.chunk.indev.LevelGenerator;
import net.minecraft.core.world.type.WorldType;

public class WorldTypeOverworldIndev extends WorldTypeOverworld {
   public WorldTypeOverworldIndev(WorldType.Properties properties) {
      super(properties);
   }

   @Override
   public boolean isValidSpawn(World world, int x, int y, int z) {
      return world.getBlockId(x, y, z) == Blocks.GRASS.id()
         || world.getBlockId(x, y, z) == Blocks.SAND.id()
         || world.getBlockId(x, y, z) == Blocks.GRASS_RETRO.id();
   }

   @Override
   public void getInitialSpawnLocation(World world) {
      Random random = new Random(world.getRandomSeed());

      while (true) {
         int x = random.nextInt(256);
         int z = random.nextInt(256);

         for (int y = this.getMaxY(); y >= this.getMinY(); y--) {
            if (world.getBlockId(x, y + 1, z) == 0 && this.isValidSpawn(world, x, y, z)) {
               world.getLevelData().setSpawn(x, y, z);
               LevelGenerator.generateHouse(world);
               return;
            }
         }
      }
   }

   @Override
   public void getRespawnLocation(World world) {
   }

   @Override
   public BiomeProvider createBiomeProvider(World world) {
      return new BiomeProviderSingleBiome(Biomes.OVERWORLD_RETRO, 1.0, 1.0, 1.0);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorIndev(world, 256, this.getMaxY() + 1, 256, IndevWorldType.ISLAND, IndevWorldTheme.NORMAL);
   }
}
