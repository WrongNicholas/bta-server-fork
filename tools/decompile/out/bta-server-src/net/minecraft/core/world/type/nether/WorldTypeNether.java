package net.minecraft.core.world.type.nether;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderSingleBiome;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.perlin.nether.ChunkGeneratorNether;
import net.minecraft.core.world.type.WorldType;

public class WorldTypeNether extends WorldType {
   public WorldTypeNether(WorldType.Properties properties) {
      super(properties);
   }

   public static WorldType.Properties defaultProperties(String translationKey) {
      return WorldType.Properties.of(translationKey)
         .brightnessRamp(getLightRamp())
         .oceanBlock(Blocks.FLUID_LAVA_STILL)
         .fillerBlock(Blocks.COBBLE_NETHERRACK)
         .withCeiling()
         .bounds(0, 255, 160);
   }

   private static float[] getLightRamp() {
      float[] brightnessRamp = new float[32];
      float f = 0.1F;

      for (int i = 0; i <= 15; i++) {
         float f1 = 1.0F - i / 15.0F;
         brightnessRamp[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
      }

      return brightnessRamp;
   }

   @Override
   public void onWorldCreation(World world) {
      super.onWorldCreation(world);
      world.setWorldTime(72000L);
   }

   @Override
   public BiomeProvider createBiomeProvider(World world) {
      return new BiomeProviderSingleBiome(Biomes.NETHER_NETHER, 1.0, 0.0, 1.0);
   }

   @Override
   public ChunkGenerator createChunkGenerator(World world) {
      return new ChunkGeneratorNether(world);
   }

   @Override
   public boolean isValidSpawn(World world, int x, int y, int z) {
      return true;
   }

   @Override
   public float getCelestialAngle(World world, long tick, float partialTick) {
      return 0.5F;
   }

   @Override
   public int getSkyDarken(World world, long tick, float partialTick) {
      return 0;
   }
}
