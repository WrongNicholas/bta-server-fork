package net.minecraft.core.world.biome;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.animal.MobChicken;
import net.minecraft.core.entity.animal.MobCow;
import net.minecraft.core.entity.animal.MobFireflyCluster;
import net.minecraft.core.entity.animal.MobPig;
import net.minecraft.core.entity.animal.MobSheep;
import net.minecraft.core.entity.animal.MobSquid;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.entity.monster.MobSkeleton;
import net.minecraft.core.entity.monster.MobSlime;
import net.minecraft.core.entity.monster.MobSnowman;
import net.minecraft.core.entity.monster.MobSpider;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.entity.monster.MobZombieArmored;
import net.minecraft.core.enums.MobCategory;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancy;
import net.minecraft.core.world.weather.Weather;

public class Biome {
   public final String translationKey;
   public int color = 16711935;
   public short topBlock;
   public short fillerBlock;
   public Weather[] blockedWeathers = new Weather[0];
   protected List<SpawnListEntry> spawnableMonsterList;
   protected List<SpawnListEntry> spawnableCreatureList;
   protected List<SpawnListEntry> spawnableWaterCreatureList;
   protected List<SpawnListEntry> spawnableAmbientCreatureList;
   private boolean hasSurfaceSnow = false;

   public Biome(String key) {
      this.translationKey = "biome." + key;
      this.topBlock = (short)Blocks.GRASS.id();
      this.fillerBlock = (short)Blocks.DIRT.id();
      this.spawnableMonsterList = new ArrayList<>();
      this.spawnableCreatureList = new ArrayList<>();
      this.spawnableWaterCreatureList = new ArrayList<>();
      this.spawnableAmbientCreatureList = new ArrayList<>();
      this.spawnableMonsterList.add(new SpawnListEntry(MobSpider.class, 10));
      this.spawnableMonsterList.add(new SpawnListEntry(MobZombie.class, 10));
      this.spawnableMonsterList.add(new SpawnListEntry(MobZombieArmored.class, 2));
      this.spawnableMonsterList.add(new SpawnListEntry(MobSkeleton.class, 10));
      this.spawnableMonsterList.add(new SpawnListEntry(MobCreeper.class, 10));
      this.spawnableMonsterList.add(new SpawnListEntry(MobSlime.class, 10));
      this.spawnableMonsterList.add(new SpawnListEntry(MobSnowman.class, 2));
      this.spawnableCreatureList.add(new SpawnListEntry(MobSheep.class, 102));
      this.spawnableCreatureList.add(new SpawnListEntry(MobPig.class, 102));
      this.spawnableCreatureList.add(new SpawnListEntry(MobChicken.class, 102));
      this.spawnableCreatureList.add(new SpawnListEntry(MobCow.class, 102));
      this.spawnableWaterCreatureList.add(new SpawnListEntry(MobSquid.class, 10));
      this.spawnableAmbientCreatureList.add(new SpawnListEntry(MobFireflyCluster.class, 10));
   }

   public WorldFeature getRandomWorldGenForTrees(Random random) {
      return (WorldFeature)(random.nextInt(10) == 0
         ? new WorldFeatureTreeFancy(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id())
         : new WorldFeatureTree(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id(), 4));
   }

   public Biome setColor(int color) {
      this.color = color;
      return this;
   }

   public int getSkyColor(float temperature) {
      temperature /= 3.0F;
      if (temperature < -1.0F) {
         temperature = -1.0F;
      }

      if (temperature > 1.0F) {
         temperature = 1.0F;
      }

      return Color.getHSBColor(0.6222222F - temperature * 0.125F, 0.5F + temperature * 0.1F, 1.0F).getRGB();
   }

   public List<SpawnListEntry> getSpawnableList(MobCategory creatureType) {
      switch (creatureType) {
         case monster:
            return this.spawnableMonsterList;
         case creature:
            return this.spawnableCreatureList;
         case waterCreature:
            return this.spawnableWaterCreatureList;
         case ambientCreature:
            return this.spawnableAmbientCreatureList;
         default:
            return null;
      }
   }

   public boolean hasSurfaceSnow() {
      return this.hasSurfaceSnow;
   }

   public Biome setSurfaceSnow() {
      this.hasSurfaceSnow = true;
      return this;
   }

   public Biome setBlockedWeathers(Weather... blocked) {
      this.blockedWeathers = blocked;
      return this;
   }

   public Biome setTopBlock(int id) {
      this.topBlock = (short)id;
      return this;
   }

   public Biome setFillerBlock(int id) {
      this.fillerBlock = (short)id;
      return this;
   }
}
