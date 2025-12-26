package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.animal.MobWolf;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancy;

public class BiomeForest extends Biome {
   public BiomeForest(String key) {
      super(key);
      this.spawnableCreatureList.add(new SpawnListEntry(MobWolf.class, 25));
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      if (random.nextInt(5) == 0) {
         return new WorldFeatureTree(Blocks.LEAVES_BIRCH.id(), Blocks.LOG_BIRCH.id(), 5);
      } else if (random.nextInt(3) == 0) {
         return new WorldFeatureTreeFancy(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id());
      } else {
         return random.nextInt(25) == 0
            ? new WorldFeatureTree(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK_MOSSY.id(), 4)
            : new WorldFeatureTree(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id(), 4);
      }
   }
}
