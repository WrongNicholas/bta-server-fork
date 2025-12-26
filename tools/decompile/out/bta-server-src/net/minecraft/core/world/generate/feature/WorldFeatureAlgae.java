package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;

public class WorldFeatureAlgae extends WorldFeature {
   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      for (int l = 0; l < 128; l++) {
         int i1 = x + random.nextInt(8) - random.nextInt(8);
         int k1 = z + random.nextInt(8) - random.nextInt(8);
         if (world.getBlockId(i1, y, k1) == Blocks.FLUID_WATER_STILL.id() && world.getBlockMaterial(i1, y + 1, k1) == Material.air) {
            world.setBlock(i1, y + 1, k1, Blocks.ALGAE.id());
         }
      }

      return true;
   }
}
