package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancy;

public class BlockLogicSaplingOak extends BlockLogicSaplingBase {
   public BlockLogicSaplingOak(Block<?> block) {
      super(block);
   }

   @Override
   public void growTree(World world, int x, int y, int z, Random random) {
      WorldFeature treeBig = new WorldFeatureTreeFancy(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id());
      WorldFeature treeSmall = new WorldFeatureTree(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id(), 4);
      world.setBlock(x, y, z, 0);
      if (!treeSmall.place(world, random, x, y, z) && !treeBig.place(world, random, x, y, z)) {
         world.setBlock(x, y, z, this.id());
      }
   }
}
