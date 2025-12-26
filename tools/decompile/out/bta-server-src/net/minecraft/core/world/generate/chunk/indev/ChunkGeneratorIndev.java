package net.minecraft.core.world.generate.chunk.indev;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.CavesLargeFeature;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;
import net.minecraft.core.world.generate.chunk.perlin.SurfaceGenerator;
import net.minecraft.core.world.generate.chunk.perlin.overworld.retro.ChunkDecoratorOverworldRetro;
import net.minecraft.core.world.generate.chunk.perlin.overworld.retro.SurfaceGeneratorOverworldRetro;

public class ChunkGeneratorIndev extends ChunkGenerator {
   private final LevelGenerator lg;
   private final SurfaceGenerator sg;
   private final CavesLargeFeature cg;

   public ChunkGeneratorIndev(World world, int sizeX, int sizeY, int sizeZ, IndevWorldType worldType, IndevWorldTheme worldTheme) {
      super(world, new ChunkDecoratorOverworldRetro(world));
      this.lg = new LevelGenerator(sizeX, sizeY, sizeZ, worldType, worldTheme);
      this.sg = new SurfaceGeneratorOverworldRetro(world);
      this.cg = new CavesLargeFeature();
   }

   @Override
   protected ChunkGeneratorResult doBlockGeneration(Chunk chunk) {
      ChunkGeneratorResult result = new ChunkGeneratorResult();
      int chunkX = chunk.xPosition;
      int chunkZ = chunk.zPosition;
      this.lg.generate(this.world, result, chunkX * 16, chunkZ * 16);

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            result.setBlock(x, 0, z, Blocks.BEDROCK.id());
         }
      }

      this.sg.generateSurface(chunk, result);
      this.cg.generate(this.world, chunkX, chunkZ, result);
      return result;
   }
}
