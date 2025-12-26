package net.minecraft.core.world.generate.chunk.perlin.overworld.retro;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.chunk.perlin.overworld.DensityGeneratorOverworld;
import net.minecraft.core.world.noise.RetroPerlinNoise;

public class DensityGeneratorOverworldRetro extends DensityGeneratorOverworld {
   public DensityGeneratorOverworldRetro(World world) {
      super(
         world,
         new RetroPerlinNoise(world.getRandomSeed(), 16, 0),
         new RetroPerlinNoise(world.getRandomSeed(), 16, 16),
         new RetroPerlinNoise(world.getRandomSeed(), 8, 32),
         new RetroPerlinNoise(world.getRandomSeed(), 10, 48),
         new RetroPerlinNoise(world.getRandomSeed(), 16, 58),
         false
      );
   }
}
