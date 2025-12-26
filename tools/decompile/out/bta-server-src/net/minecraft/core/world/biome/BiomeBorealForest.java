package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.animal.MobWolf;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeTaigaBushy;

public class BiomeBorealForest extends Biome {
   public BiomeBorealForest(String key) {
      super(key);
      this.spawnableCreatureList.add(new SpawnListEntry(MobWolf.class, 25));
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      return new WorldFeatureTreeTaigaBushy(Blocks.LEAVES_PINE.id(), Blocks.LOG_PINE.id());
   }
}
