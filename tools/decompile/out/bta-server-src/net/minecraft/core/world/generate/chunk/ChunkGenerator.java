package net.minecraft.core.world.generate.chunk;

import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkSection;

public abstract class ChunkGenerator {
   protected final World world;
   private final ChunkDecorator decorator;

   public ChunkGenerator(World world, ChunkDecorator decorator) {
      this.world = world;
      this.decorator = decorator;
   }

   public final Chunk generate(int chunkX, int chunkZ) {
      Chunk chunk = new Chunk(this.world, chunkX, chunkZ);
      chunk.temperature = this.world.getBiomeProvider().getTemperatures(null, chunkX * 16, chunkZ * 16, 16, 16);
      chunk.humidity = this.world.getBiomeProvider().getHumidities(null, chunkX * 16, chunkZ * 16, 16, 16);
      chunk.variety = this.world.getBiomeProvider().getVarieties(null, chunkX * 16, chunkZ * 16, 16, 16);
      Biome[] biomes = new Biome[512];

      for (int sectionY = 0; sectionY < 16; sectionY++) {
         this.world.getBiomeProvider().getBiomes(biomes, chunk.temperature, chunk.humidity, chunk.variety, chunkX * 16, sectionY * 16, chunkZ * 16, 16, 2, 16);
         ChunkSection section = chunk.getSection(sectionY);

         for (int i = 0; i < biomes.length; i++) {
            section.biome[i] = (byte)Registries.BIOMES.getNumericIdOfItem(biomes[i]);
         }
      }

      ChunkGeneratorResult result = this.doBlockGeneration(chunk);
      int blocksInSection = 4096;

      for (int sectionY = 0; sectionY < 16; sectionY++) {
         ChunkSection section = chunk.getSection(sectionY);
         section.blocks = result.getSectionBlocks(sectionY);
      }

      chunk.recalcHeightmap();
      return chunk;
   }

   public final void decorate(Chunk chunk) {
      this.decorator.decorate(chunk);
   }

   protected abstract ChunkGeneratorResult doBlockGeneration(Chunk var1);
}
