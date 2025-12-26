package net.minecraft.core.world.generate.feature.tree;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;

public class WorldFeatureTreeCherry extends WorldFeatureTree {
   @MethodParametersAnnotation(names = {"leavesID", "logID", "heightMod"})
   public WorldFeatureTreeCherry(int leavesID, int logID, int heightMod) {
      super(leavesID, logID, heightMod);
   }

   @Override
   public void placeLeaves(World world, int x, int y, int z, Random rand) {
      if (rand.nextInt(5) == 0) {
         world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LEAVES_CHERRY_FLOWERING.id(), 0);
      } else {
         world.setBlockWithNotify(x, y, z, Blocks.LEAVES_CHERRY.id());
      }
   }

   @Override
   public boolean isLeaf(int id) {
      return id == Blocks.LEAVES_CHERRY_FLOWERING.id() || id == Blocks.LEAVES_CHERRY.id();
   }
}
