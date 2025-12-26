package net.minecraft.core.world.generate.chunk.perlin;

import java.util.Arrays;
import java.util.List;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.LargeFeature;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;

public class ChunkGeneratorPerlin extends ChunkGenerator {
   private final TerrainGenerator terrainGenerator;
   private final SurfaceGenerator surfaceGenerator;
   private final List<LargeFeature> largeFeatures;

   protected ChunkGeneratorPerlin(
      World world, ChunkDecorator decorator, TerrainGenerator terrainGenerator, SurfaceGenerator surfaceGenerator, LargeFeature[] largeFeatures
   ) {
      super(world, decorator);
      this.terrainGenerator = terrainGenerator;
      this.surfaceGenerator = surfaceGenerator;
      this.largeFeatures = Arrays.asList(largeFeatures);
   }

   @Override
   protected ChunkGeneratorResult doBlockGeneration(Chunk chunk) {
      double[] densityMap = this.terrainGenerator.getDensityGenerator().generateDensityMap(chunk);
      ChunkGeneratorResult result = this.terrainGenerator.generateTerrain(chunk, densityMap);
      this.surfaceGenerator.generateSurface(chunk, result);

      for (LargeFeature largeFeature : this.largeFeatures) {
         largeFeature.generate(this.world, chunk.xPosition, chunk.zPosition, result);
      }

      return result;
   }
}
