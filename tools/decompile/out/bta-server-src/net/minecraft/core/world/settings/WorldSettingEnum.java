package net.minecraft.core.world.settings;

public class WorldSettingEnum extends WorldSetting<Integer> {
   private int numEnums = 0;
   private String[] enumLanguageKeys = null;
   private int enumIndex = -1;

   public WorldSettingEnum(String languageKey) {
      super(languageKey);
   }

   public Integer getValue() {
      return this.numEnums != 0 && this.enumLanguageKeys != null && this.enumLanguageKeys.length != 0 ? this.enumIndex : null;
   }

   public void setValue(Integer newValue) {
      if (newValue >= 0 && newValue < this.numEnums && this.enumLanguageKeys != null && this.enumLanguageKeys.length != 0) {
         super.setValue(newValue);
      }
   }

   public WorldSettingEnum withEnums(String... enums) {
      if (enums != null) {
         this.numEnums = enums.length;
         this.enumLanguageKeys = enums;
         this.enumIndex = 0;
      }

      return this;
   }
}
