package net.minecraft.core.enums;

import org.jetbrains.annotations.NotNull;

public enum Difficulty {
   PEACEFUL(0, "peaceful"),
   EASY(1, "easy"),
   NORMAL(2, "normal"),
   HARD(3, "hard");

   private final int id;
   @NotNull
   private final String key;

   private Difficulty(int id, @NotNull String key) {
      this.id = id;
      this.key = key;
   }

   public boolean canHostileMobsSpawn() {
      return this != PEACEFUL;
   }

   public int id() {
      return this.id;
   }

   public String getTranslationKey() {
      return "difficulty." + this.key;
   }
}
