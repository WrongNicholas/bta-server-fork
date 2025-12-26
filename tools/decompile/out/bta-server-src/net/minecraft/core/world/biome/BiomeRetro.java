package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.animal.MobChicken;
import net.minecraft.core.entity.animal.MobCow;
import net.minecraft.core.entity.animal.MobPig;
import net.minecraft.core.entity.animal.MobSheep;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.entity.monster.MobSkeleton;
import net.minecraft.core.entity.monster.MobSlime;
import net.minecraft.core.entity.monster.MobSpider;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancy;

public class BiomeRetro extends Biome {
   protected BiomeRetro(String key) {
      super(key);
      this.spawnableMonsterList.clear();
      this.spawnableCreatureList.clear();
      this.spawnableWaterCreatureList.clear();
      this.spawnableAmbientCreatureList.clear();
      this.spawnableMonsterList.add(new SpawnListEntry(MobSpider.class, 10));
      this.spawnableMonsterList.add(new SpawnListEntry(MobZombie.class, 10));
      this.spawnableMonsterList.add(new SpawnListEntry(MobSkeleton.class, 10));
      this.spawnableMonsterList.add(new SpawnListEntry(MobCreeper.class, 10));
      this.spawnableMonsterList.add(new SpawnListEntry(MobSlime.class, 10));
      this.spawnableCreatureList.add(new SpawnListEntry(MobSheep.class, 102));
      this.spawnableCreatureList.add(new SpawnListEntry(MobPig.class, 102));
      this.spawnableCreatureList.add(new SpawnListEntry(MobChicken.class, 102));
      this.spawnableCreatureList.add(new SpawnListEntry(MobCow.class, 102));
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      WorldFeature feature = new WorldFeatureTree(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id(), 4);
      if (random.nextInt(10) == 0) {
         feature = new WorldFeatureTreeFancy(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id());
      }

      return feature;
   }
}
