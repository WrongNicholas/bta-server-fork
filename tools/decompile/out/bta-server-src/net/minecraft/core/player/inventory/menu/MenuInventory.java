package net.minecraft.core.player.inventory.menu;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.IArmorItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerCrafting;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.container.ContainerResult;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.player.inventory.slot.SlotArmor;
import net.minecraft.core.player.inventory.slot.SlotResult;

public class MenuInventory extends MenuAbstract {
   public ContainerCrafting craftSlots = new ContainerCrafting(this, 2, 2);
   public Container resultSlots = new ContainerResult();
   public boolean active;
   public ContainerInventory inventory;

   public MenuInventory(ContainerInventory inventory) {
      this(inventory, true);
   }

   public MenuInventory(ContainerInventory inventory, boolean active) {
      this.active = active;
      this.inventory = inventory;
      this.addSlot(new SlotResult(inventory.player, this.craftSlots, this.resultSlots, 0, 144, 36));

      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < 2; j++) {
            this.addSlot(new Slot(this.craftSlots, j + i * 2, 88 + j * 18, 26 + i * 18));
         }
      }

      for (int i = 0; i < 4; i++) {
         int armorType = 3 - i;
         this.addSlot(new SlotArmor(this, inventory, inventory.getContainerSize() - 1 - i, 8, 8 + i * 18, armorType));
      }

      for (int i = 0; i < 3; i++) {
         for (int k1 = 0; k1 < 9; k1++) {
            this.addSlot(new Slot(inventory, k1 + (i + 1) * 9, 8 + k1 * 18, 84 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
      }

      this.slotsChanged(this.craftSlots);
   }

   @Override
   public void slotsChanged(Container iinventory) {
      this.resultSlots.setItem(0, Registries.RECIPES.findMatchingRecipe(this.craftSlots));
   }

   @Override
   public void onCraftGuiClosed(Player player) {
      super.onCraftGuiClosed(player);
      boolean insert = false;

      for (int i = 0; i < 4; i++) {
         ItemStack itemstack = this.craftSlots.getItem(i);
         if (itemstack != null) {
            this.craftSlots.setItem(i, null);
            this.storeOrDropItem(player, itemstack);
            insert = true;
         }
      }

      if (insert) {
         player.world.playSoundAtEntity(null, player, "random.insert", 0.1F, 1.0F);
      }
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return true;
   }

   @Override
   public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, Player player) {
      if (slot.index == 0) {
         return this.getSlots(0, 1, false);
      } else if (slot.index >= 1 && slot.index <= 4) {
         return this.getSlots(1, 4, false);
      } else {
         if (action == InventoryAction.MOVE_SIMILAR) {
            if (slot.index >= 9 && slot.index <= 44) {
               return this.getSlots(9, 36, false);
            }
         } else {
            if (slot.index >= 9 && slot.index <= 35) {
               return this.getSlots(9, 27, false);
            }

            if (slot.index >= 36 && slot.index <= 44) {
               return this.getSlots(36, 9, false);
            }
         }

         return null;
      }
   }

   @Override
   public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, Player player) {
      if (slot.index >= 9 && slot.index <= 44) {
         if (target == 1) {
            return this.getSlots(1, 4, false);
         } else if (target == 2 && slot.getItemStack() != null && slot.getItemStack().getItem() instanceof IArmorItem) {
            IArmorItem armorItem = (IArmorItem)slot.getItemStack().getItem();
            List<Integer> ints = new ArrayList<>();
            ints.add(8 - armorItem.getArmorPiece());
            return ints;
         } else {
            return slot.index < 36 ? this.getSlots(36, 9, false) : this.getSlots(9, 27, false);
         }
      } else {
         return slot.index == 0 ? this.getSlots(9, 36, true) : this.getSlots(9, 36, false);
      }
   }
}
