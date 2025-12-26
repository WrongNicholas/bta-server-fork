package net.minecraft.core.player.inventory.menu;

import java.util.List;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.entity.TileEntityFurnace;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.player.inventory.slot.SlotFurnace;

public class MenuFurnace extends MenuAbstract {
   public static final int ID_CURRENT_COOK_TIME = 0;
   public static final int ID_CURRENT_BURN_TIME = 1;
   public static final int ID_MAX_COOK_TIME = 2;
   public static final int ID_MAX_BURN_TIME = 3;
   public TileEntityFurnace furnace;
   private int currentCookTime = 0;
   private int currentBurnTime = 0;
   private int itemBurnTime;
   private int itemCookTime = 0;

   public MenuFurnace(ContainerInventory inventory, TileEntityFurnace tileEntity) {
      this.itemBurnTime = 0;
      this.furnace = tileEntity;
      this.addSlot(new Slot(tileEntity, 0, 56, 17));
      this.addSlot(new Slot(tileEntity, 1, 56, 53));
      this.addSlot(new SlotFurnace(inventory.player, tileEntity, 2, 116, 35));

      for (int i = 0; i < 3; i++) {
         for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(inventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
         }
      }

      for (int j = 0; j < 9; j++) {
         this.addSlot(new Slot(inventory, j, 8 + j * 18, 142));
      }
   }

   @Override
   public void broadcastChanges() {
      super.broadcastChanges();

      for (ContainerListener crafter : this.containerListeners) {
         if (this.currentCookTime != this.furnace.currentCookTime) {
            crafter.updateCraftingInventoryInfo(this, 0, this.furnace.currentCookTime);
         }

         if (this.currentBurnTime != this.furnace.currentBurnTime) {
            crafter.updateCraftingInventoryInfo(this, 1, this.furnace.currentBurnTime);
         }

         if (this.itemCookTime != this.furnace.maxCookTime) {
            crafter.updateCraftingInventoryInfo(this, 2, this.furnace.maxCookTime);
         }

         if (this.itemBurnTime != this.furnace.maxBurnTime) {
            crafter.updateCraftingInventoryInfo(this, 3, this.furnace.maxBurnTime);
         }
      }

      this.currentCookTime = this.furnace.currentCookTime;
      this.currentBurnTime = this.furnace.currentBurnTime;
      this.itemCookTime = this.furnace.maxCookTime;
      this.itemBurnTime = this.furnace.maxBurnTime;
   }

   @Override
   public void setData(int id, int value) {
      switch (id) {
         case 0:
            this.furnace.currentCookTime = value;
            break;
         case 1:
            this.furnace.currentBurnTime = value;
            break;
         case 2:
            this.furnace.maxCookTime = value;
            break;
         case 3:
            this.furnace.maxBurnTime = value;
      }
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return this.furnace.stillValid(entityplayer);
   }

   @Override
   public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, Player player) {
      if (slot.index >= 0 && slot.index <= 3) {
         return this.getSlots(slot.index, 1, false);
      } else {
         if (action == InventoryAction.MOVE_ALL) {
            if (slot.index >= 3 && slot.index <= 30) {
               return this.getSlots(3, 27, false);
            }

            if (slot.index >= 30 && slot.index <= 38) {
               return this.getSlots(30, 9, false);
            }
         }

         return slot.index >= 3 && slot.index <= 38 ? this.getSlots(3, 36, false) : null;
      }
   }

   @Override
   public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, Player player) {
      if (slot.index >= 3 && slot.index <= 39) {
         if (action != InventoryAction.MOVE_ALL) {
            if (target == 1) {
               return this.getSlots(0, 1, false);
            }

            if (target == 2) {
               return this.getSlots(1, 1, false);
            }
         }

         if (slot.index >= 3 && slot.index <= 29) {
            return this.getSlots(30, 9, false);
         }

         if (slot.index >= 31 && slot.index <= 38) {
            return this.getSlots(3, 27, false);
         }
      }

      if (slot.index < 0 || slot.index > 2) {
         return null;
      } else {
         return slot.index == 2 ? this.getSlots(3, 36, true) : this.getSlots(3, 36, false);
      }
   }
}
