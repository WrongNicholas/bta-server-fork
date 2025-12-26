package net.minecraft.core.entity;

public class SpawnListEntry {
   public Class<? extends Entity> entityClass;
   public int spawnFrequency;

   public SpawnListEntry(Class<? extends Entity> clazz, int frequency) {
      this.entityClass = clazz;
      this.spawnFrequency = frequency;
   }
}
