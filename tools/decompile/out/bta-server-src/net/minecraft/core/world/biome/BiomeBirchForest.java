package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.animal.MobWolf;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeTall;

public class BiomeBirchForest extends Biome {
   public BiomeBirchForest(String key) {
      super(key);
      this.spawnableCreatureList.add(new SpawnListEntry(MobWolf.class, 25));
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      return (WorldFeature)(random.nextInt(10) == 0
         ? new WorldFeatureTreeTall(Blocks.LEAVES_BIRCH.id(), Blocks.LOG_BIRCH.id())
         : new WorldFeatureTree(Blocks.LEAVES_BIRCH.id(), Blocks.LOG_BIRCH.id(), 8));
   }
}
