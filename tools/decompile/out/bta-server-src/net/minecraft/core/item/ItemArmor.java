package net.minecraft.core.item;

import net.minecraft.core.item.material.ArmorMaterial;

public class ItemArmor extends Item implements IArmorItem {
   private final ArmorMaterial material;
   private final int armorPiece;

   public ItemArmor(String name, String namespaceId, int id, ArmorMaterial material, int armorPiece) {
      super(name, namespaceId, id);
      this.material = material;
      this.armorPiece = armorPiece;
      this.setMaxDamage((int)(ARMOR_PIECE_DURABILITY_MODIFIERS[armorPiece] * material.durability));
      this.maxStackSize = 1;
   }

   @Override
   public ArmorMaterial getArmorMaterial() {
      return this.material;
   }

   @Override
   public int getArmorPiece() {
      return this.armorPiece;
   }
}
