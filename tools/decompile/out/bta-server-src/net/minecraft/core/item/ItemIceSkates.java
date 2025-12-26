package net.minecraft.core.item;

import net.minecraft.core.item.material.ArmorMaterial;
import org.jetbrains.annotations.Nullable;

public class ItemIceSkates extends Item implements IArmorItem {
   public ItemIceSkates(String translationKey, String namespaceId, int id) {
      super(translationKey, namespaceId, id);
      this.setMaxStackSize(1);
   }

   @Nullable
   @Override
   public ArmorMaterial getArmorMaterial() {
      return null;
   }

   @Override
   public int getArmorPiece() {
      return 0;
   }
}
