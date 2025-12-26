package net.minecraft.core.world.generate.chunk.perlin.overworld;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.chunk.perlin.DensityGenerator;
import net.minecraft.core.world.generate.chunk.perlin.TerrainGeneratorLerp;
import net.minecraft.core.world.type.WorldType;

public class TerrainGeneratorOverworld extends TerrainGeneratorLerp {
   private final DensityGenerator densityGenerator;

   protected TerrainGeneratorOverworld(World world, DensityGenerator densityGenerator) {
      super(world);
      this.densityGenerator = densityGenerator;
   }

   public TerrainGeneratorOverworld(World world) {
      this(world, new DensityGeneratorOverworld(world));
   }

   @Override
   protected int getBlockAt(int x, int y, int z, double density) {
      WorldType type = this.world.getWorldType();
      if (y <= type.getMinY() + this.rand.nextInt(5)) {
         return Blocks.BEDROCK.id();
      } else if (density > 0.0) {
         return type.getFillerBlockId();
      } else {
         return y >= type.getMinY() && y < type.getMinY() + type.getOceanY() ? type.getOceanBlockId() : 0;
      }
   }

   @Override
   public DensityGenerator getDensityGenerator() {
      return this.densityGenerator;
   }
}
