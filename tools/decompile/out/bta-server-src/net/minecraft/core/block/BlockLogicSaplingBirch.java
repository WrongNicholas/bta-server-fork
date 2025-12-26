package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;

public class BlockLogicSaplingBirch extends BlockLogicSaplingBase {
   public BlockLogicSaplingBirch(Block<?> block) {
      super(block);
   }

   @Override
   public void growTree(World world, int x, int y, int z, Random random) {
      world.setBlock(x, y, z, 0);
      WorldFeature tree = new WorldFeatureTree(Blocks.LEAVES_BIRCH.id(), Blocks.LOG_BIRCH.id(), 5);
      if (!tree.place(world, random, x, y, z)) {
         world.setBlock(x, y, z, this.id());
      }
   }
}
