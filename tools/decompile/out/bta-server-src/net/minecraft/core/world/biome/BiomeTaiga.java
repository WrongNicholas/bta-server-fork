package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.animal.MobWolf;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeTaigaBushy;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeTaigaTall;

public class BiomeTaiga extends Biome {
   public BiomeTaiga(String key) {
      super(key);
      this.spawnableCreatureList.add(new SpawnListEntry(MobWolf.class, 25));
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      return (WorldFeature)(random.nextInt(3) == 0
         ? new WorldFeatureTreeTaigaTall(Blocks.LEAVES_PINE.id(), Blocks.LOG_PINE.id())
         : new WorldFeatureTreeTaigaBushy(Blocks.LEAVES_PINE.id(), Blocks.LOG_PINE.id()));
   }
}
