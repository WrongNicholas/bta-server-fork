package net.minecraft.core.player.inventory.menu;

import java.util.List;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.entity.TileEntityTrommel;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.slot.Slot;

public class MenuTrommel extends MenuAbstract {
   private TileEntityTrommel trommel;
   private int cookTime = 0;
   private int burnTime = 0;
   private int itemBurnTime = 0;

   public MenuTrommel(ContainerInventory inventoryplayer, TileEntityTrommel tileEntityTrommel) {
      this.trommel = tileEntityTrommel;
      this.addSlot(new Slot(tileEntityTrommel, 0, 105, 30));
      this.addSlot(new Slot(tileEntityTrommel, 1, 85, 50));
      this.addSlot(new Slot(tileEntityTrommel, 2, 125, 50));
      this.addSlot(new Slot(tileEntityTrommel, 3, 105, 70));
      this.addSlot(new Slot(tileEntityTrommel, 4, 33, 50));

      for (int i = 0; i < 3; i++) {
         for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 110 + i * 18));
         }
      }

      for (int j = 0; j < 9; j++) {
         this.addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 168));
      }
   }

   @Override
   public void broadcastChanges() {
      super.broadcastChanges();

      for (int i = 0; i < this.containerListeners.size(); i++) {
         ContainerListener icrafting = this.containerListeners.get(i);
         if (this.cookTime != this.trommel.itemPopTime) {
            icrafting.updateCraftingInventoryInfo(this, 0, this.trommel.itemPopTime);
         }

         if (this.burnTime != this.trommel.burnTime) {
            icrafting.updateCraftingInventoryInfo(this, 1, this.trommel.burnTime);
         }

         if (this.itemBurnTime != this.trommel.currentItemBurnTime) {
            icrafting.updateCraftingInventoryInfo(this, 2, this.trommel.currentItemBurnTime);
         }
      }

      this.cookTime = this.trommel.itemPopTime;
      this.burnTime = this.trommel.burnTime;
      this.itemBurnTime = this.trommel.currentItemBurnTime;
   }

   @Override
   public void setData(int id, int value) {
      if (id == 0) {
         this.trommel.itemPopTime = value;
      }

      if (id == 1) {
         this.trommel.burnTime = value;
      }

      if (id == 2) {
         this.trommel.currentItemBurnTime = value;
      }
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return this.trommel.stillValid(entityplayer);
   }

   @Override
   public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, Player player) {
      if (slot.index >= 0 && slot.index <= 3) {
         return this.getSlots(0, 4, false);
      } else if (slot.index == 4) {
         return this.getSlots(4, 1, false);
      } else {
         if (action == InventoryAction.MOVE_ALL) {
            if (slot.index >= 5 && slot.index <= 31) {
               return this.getSlots(5, 27, false);
            }

            if (slot.index >= 32 && slot.index <= 40) {
               return this.getSlots(32, 9, false);
            }
         }

         return action == InventoryAction.MOVE_SIMILAR && slot.index >= 5 && slot.index <= 40 ? this.getSlots(5, 36, false) : null;
      }
   }

   @Override
   public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, Player player) {
      if (slot.index >= 0 && slot.index <= 4) {
         return this.getSlots(5, 36, false);
      } else {
         if (slot.index >= 5 && slot.index <= 40) {
            if (target == 1) {
               return this.getSlots(0, 4, false);
            }

            if (target == 2) {
               return this.getSlots(4, 1, false);
            }

            if (slot.index >= 5 && slot.index <= 31) {
               return this.getSlots(32, 9, false);
            }

            if (slot.index >= 32 && slot.index <= 40) {
               return this.getSlots(5, 27, false);
            }
         }

         return null;
      }
   }
}
