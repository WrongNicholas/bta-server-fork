package net.minecraft.core.world.generate.chunk.perlin.overworld.retro;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.chunk.perlin.overworld.SurfaceGeneratorOverworld;
import net.minecraft.core.world.noise.RetroPerlinNoise;

public class SurfaceGeneratorOverworldRetro extends SurfaceGeneratorOverworld {
   public SurfaceGeneratorOverworldRetro(World world) {
      super(
         world,
         new RetroPerlinNoise(world.getRandomSeed(), 4, 40),
         new RetroPerlinNoise(world.getRandomSeed(), 4, 44),
         new RetroPerlinNoise(world.getRandomSeed(), 8, 32),
         false
      );
   }
}
