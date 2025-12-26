package net.minecraft.core.item;

import net.minecraft.core.item.material.ArmorMaterial;

public class ItemQuiverEndless extends Item implements IArmorItem {
   public ItemQuiverEndless(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.setMaxStackSize(1);
   }

   @Override
   public ArmorMaterial getArmorMaterial() {
      return null;
   }

   @Override
   public int getArmorPiece() {
      return 2;
   }
}
