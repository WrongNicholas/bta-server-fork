package net.minecraft.core.item;

import net.minecraft.core.item.material.ArmorMaterial;
import org.jetbrains.annotations.Nullable;

public interface IArmorItem {
   int PIECE_HEAD = 3;
   int PIECE_CHEST = 2;
   int PIECE_LEGS = 1;
   int PIECE_BOOTS = 0;
   int[] ARMOR_PIECE_PROTECTION_VALUES = new int[]{3, 6, 8, 3};
   float[] ARMOR_PIECE_DURABILITY_MODIFIERS = new float[]{0.94F, 0.97F, 1.0F, 0.91F};

   Item asItem();

   @Nullable
   ArmorMaterial getArmorMaterial();

   int getArmorPiece();

   default int armorPieceProtection() {
      return ARMOR_PIECE_PROTECTION_VALUES[this.getArmorPiece()];
   }

   default float getArmorPieceProtectionPercentage() {
      return this.armorPieceProtection() / 20.0F;
   }
}
