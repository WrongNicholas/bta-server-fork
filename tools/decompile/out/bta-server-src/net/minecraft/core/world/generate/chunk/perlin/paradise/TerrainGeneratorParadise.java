package net.minecraft.core.world.generate.chunk.perlin.paradise;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.chunk.perlin.DensityGenerator;
import net.minecraft.core.world.generate.chunk.perlin.TerrainGeneratorLerp;
import net.minecraft.core.world.type.WorldType;

public class TerrainGeneratorParadise extends TerrainGeneratorLerp {
   private final DensityGenerator densityGenerator;

   public TerrainGeneratorParadise(World world) {
      super(world);
      this.densityGenerator = new DensityGeneratorParadise(world);
   }

   @Override
   public DensityGenerator getDensityGenerator() {
      return this.densityGenerator;
   }

   @Override
   protected int getBlockAt(int x, int y, int z, double density) {
      WorldType type = this.world.getWorldType();
      return density > 0.0 ? type.getFillerBlockId() : 0;
   }
}
