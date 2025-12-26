package net.minecraft.core.player.inventory.slot;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.IArmorItem;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.material.ArmorMaterial;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuInventory;
import org.jetbrains.annotations.Nullable;

public class SlotArmor extends Slot {
   public static String[] armorOutlines = new String[]{
      "minecraft:item/armor_boots_outline",
      "minecraft:item/armor_leggings_outline",
      "minecraft:item/armor_chestplate_outline",
      "minecraft:item/armor_helmet_outline"
   };
   final int armorType;
   final MenuInventory menu;

   public SlotArmor(MenuInventory menu, Container container, int index, int x, int y, int armorType) {
      super(container, index, x, y);
      this.menu = menu;
      this.armorType = armorType;
   }

   @Override
   public int getMaxStackSize() {
      return 1;
   }

   @Override
   public boolean mayPlace(ItemStack itemstack) {
      return this.armorType == 3 ? true : itemstack.getItem() instanceof IArmorItem && ((IArmorItem)itemstack.getItem()).getArmorPiece() == this.armorType;
   }

   @Override
   public void setChanged() {
      super.setChanged();
      Set<ArmorMaterial> wornMaterials = new HashSet<>();
      int count = 0;

      for (int i = 0; i < this.menu.slots.size(); i++) {
         if (this.menu.slots.get(i) instanceof SlotArmor) {
            ItemStack stack = this.menu.slots.get(i).getItemStack();
            if (stack != null
               && (
                  stack.itemID == Items.ARMOR_BOOTS_CHAINMAIL.id
                     || stack.itemID == Items.ARMOR_HELMET_CHAINMAIL.id
                     || stack.itemID == Items.ARMOR_CHESTPLATE_CHAINMAIL.id
                     || stack.itemID == Items.ARMOR_LEGGINGS_CHAINMAIL.id
               )
               && stack.getMetadata() == 0) {
               count++;
            }

            if (stack != null) {
               Item item = stack.getItem();
               if (item instanceof IArmorItem) {
                  wornMaterials.add(((IArmorItem)item).getArmorMaterial());
               }
            }
         }
      }

      if (count == 4) {
         this.menu.inventory.player.triggerAchievement(Achievements.GET_CHAINMAIL);
      }

      if (wornMaterials.size() >= 4) {
         this.menu.inventory.player.triggerAchievement(Achievements.ALL_ARMOR_TYPES);
      }

      if (this.getItemStack() != null && this.container instanceof ContainerInventory) {
         Player player = ((ContainerInventory)this.container).player;
         player.world.playSoundAtEntity(player, player, "random.equip", 2.0F, 1.0F);
      }
   }

   @Override
   public void set(@Nullable ItemStack itemstack) {
      super.set(itemstack);
   }

   @Override
   public String getItemIcon() {
      return armorOutlines[this.armorType];
   }
}
