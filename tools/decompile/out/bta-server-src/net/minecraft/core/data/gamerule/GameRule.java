package net.minecraft.core.data.gamerule;

import com.mojang.nbt.tags.CompoundTag;

public abstract class GameRule<T> {
   private final String key;
   private final T defaultValue;

   public GameRule(String key, T defaultValue) {
      this.key = key;
      this.defaultValue = defaultValue;
   }

   public String getKey() {
      return this.key;
   }

   public T getDefaultValue() {
      return this.defaultValue;
   }

   public abstract void writeToNBT(CompoundTag var1, T var2);

   public abstract T readFromNBT(CompoundTag var1);

   public abstract T parseFromString(String var1);
}
