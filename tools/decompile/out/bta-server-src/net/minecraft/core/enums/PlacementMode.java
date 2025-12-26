package net.minecraft.core.enums;

import net.minecraft.core.util.helper.ITranslatable;

public enum PlacementMode implements ITranslatable {
   DEFAULT(0),
   FACING(1),
   SIDE(2);

   private int index;

   private PlacementMode(int index) {
      this.index = index;
   }

   public int index() {
      return this.index;
   }

   public static PlacementMode get(int i) {
      if (i == 1) {
         return FACING;
      } else {
         return i == 2 ? SIDE : DEFAULT;
      }
   }

   @Override
   public String getTranslationKey() {
      return this.name().toLowerCase();
   }
}
