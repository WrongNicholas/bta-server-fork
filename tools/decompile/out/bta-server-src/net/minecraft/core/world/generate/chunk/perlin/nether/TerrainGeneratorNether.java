package net.minecraft.core.world.generate.chunk.perlin.nether;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.chunk.perlin.DensityGenerator;
import net.minecraft.core.world.generate.chunk.perlin.TerrainGeneratorLerp;
import net.minecraft.core.world.type.WorldType;

public class TerrainGeneratorNether extends TerrainGeneratorLerp {
   private final DensityGenerator densityGenerator;

   public TerrainGeneratorNether(World world) {
      super(world);
      this.densityGenerator = new DensityGeneratorNether(world);
   }

   @Override
   protected int getBlockAt(int x, int y, int z, double density) {
      WorldType type = this.world.getWorldType();
      int halfHeight = type.getMaxY() / 2;
      if (y < halfHeight) {
         return 0;
      } else if (y >= type.getMaxY() - this.rand.nextInt(5)) {
         return Blocks.BEDROCK.id();
      } else if (y <= halfHeight + this.rand.nextInt(5)) {
         return Blocks.BEDROCK.id();
      } else if (density > 0.0) {
         return type.getFillerBlockId();
      } else {
         return y >= halfHeight && y < type.getOceanY() ? type.getOceanBlockId() : 0;
      }
   }

   @Override
   public DensityGenerator getDensityGenerator() {
      return this.densityGenerator;
   }
}
