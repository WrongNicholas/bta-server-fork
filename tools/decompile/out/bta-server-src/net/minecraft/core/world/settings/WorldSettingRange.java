package net.minecraft.core.world.settings;

public class WorldSettingRange<T extends Comparable<T>> extends WorldSetting<T> {
   private T minValue = (T)null;
   private T maxValue = (T)null;

   public WorldSettingRange(String languageKey) {
      super(languageKey);
   }

   public T getMinValue() {
      return this.minValue;
   }

   public T getMaxValue() {
      return this.maxValue;
   }

   public void setValue(T newValue) {
      if (newValue.compareTo(this.minValue) < 0) {
         newValue = this.minValue;
      } else if (newValue.compareTo(this.maxValue) > 0) {
         newValue = this.maxValue;
      }

      super.setValue(newValue);
   }

   public WorldSettingRange<T> withRange(T minValue, T maxValue) {
      this.minValue = minValue;
      this.maxValue = maxValue;
      return this;
   }
}
