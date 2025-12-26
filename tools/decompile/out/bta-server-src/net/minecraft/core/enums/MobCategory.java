package net.minecraft.core.enums;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.animal.AmbientCreature;
import net.minecraft.core.entity.animal.Creature;
import net.minecraft.core.entity.animal.WaterCreature;
import net.minecraft.core.entity.monster.Enemy;

public enum MobCategory {
   monster("monster", 0, Enemy.class, 70, Material.air, false),
   creature("creature", 1, Creature.class, 15, Material.air, true),
   waterCreature("waterCreature", 2, WaterCreature.class, 5, Material.water, true),
   ambientCreature("ambient", 3, AmbientCreature.class, 2, Material.air, true);

   private final Class<?> baseClass;
   private final int maxPerChunk;
   private final Material spawnMaterial;
   private final boolean isPeaceful;

   private MobCategory(String name, int id, Class<?> baseClass, int maxPerChunk, Material spawnMaterial, boolean isPeaceful) {
      this.baseClass = baseClass;
      this.maxPerChunk = maxPerChunk;
      this.spawnMaterial = spawnMaterial;
      this.isPeaceful = isPeaceful;
   }

   public Class<?> getBaseClass() {
      return this.baseClass;
   }

   public int getMaxCreaturesPerChunk() {
      return this.maxPerChunk;
   }

   public Material getSpawnMaterial() {
      return this.spawnMaterial;
   }

   public boolean isPeaceful() {
      return this.isPeaceful;
   }
}
