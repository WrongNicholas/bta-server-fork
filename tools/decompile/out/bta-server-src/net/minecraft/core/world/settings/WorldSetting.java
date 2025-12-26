package net.minecraft.core.world.settings;

public class WorldSetting<T> {
   private T value;
   private final String languageKey;

   public WorldSetting(String languageKey) {
      this.languageKey = languageKey;
   }

   public T getValue() {
      return this.value;
   }

   public void setValue(T newValue) {
      this.value = newValue;
   }

   public WorldSetting<T> withDefaultValue(T value) {
      this.value = value;
      return this;
   }

   public String getLanguageKey() {
      return this.languageKey;
   }
}
