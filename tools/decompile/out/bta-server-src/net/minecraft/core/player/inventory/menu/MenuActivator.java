package net.minecraft.core.player.inventory.menu;

import java.util.List;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.slot.Slot;

public class MenuActivator extends MenuAbstract {
   public static final int ID_SELECTOR = 0;
   public static final int ID_LOCKED_SLOTS = 1;
   public TileEntityActivator activator;
   private final int activatorSlotsStart;
   private final int inventorySlotsStart;
   private final int hotbarSlotsStart;
   private int selectorSlot;
   private int lockedSlots;

   public MenuActivator(Container container, TileEntityActivator activator) {
      this.activator = activator;

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(activator, i, 8 + i * 18, 35));
      }

      for (int j = 0; j < 3; j++) {
         for (int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(container, i1 + j * 9 + 9, 8 + i1 * 18, 84 + j * 18));
         }
      }

      for (int k = 0; k < 9; k++) {
         this.addSlot(new Slot(container, k, 8 + k * 18, 142));
      }

      this.activatorSlotsStart = 0;
      this.inventorySlotsStart = 9;
      this.hotbarSlotsStart = 36;
      this.selectorSlot = 0;
      this.lockedSlots = 0;
   }

   @Override
   public void broadcastChanges() {
      super.broadcastChanges();

      for (ContainerListener crafter : this.containerListeners) {
         if (this.selectorSlot != this.activator.stackSelector) {
            crafter.updateCraftingInventoryInfo(this, 0, this.activator.stackSelector);
         }

         if (this.lockedSlots != this.activator.lockedSlotBitSet) {
            crafter.updateCraftingInventoryInfo(this, 1, this.activator.lockedSlotBitSet);
         }
      }

      this.selectorSlot = this.activator.stackSelector;
      this.lockedSlots = this.activator.lockedSlotBitSet;
   }

   @Override
   public void setData(int id, int value) {
      if (id == 0) {
         this.activator.stackSelector = value;
      } else if (id == 1) {
         this.activator.lockedSlotBitSet = (short)value;
      }
   }

   @Override
   public ItemStack clicked(InventoryAction action, int[] args, Player player) {
      if (action == InventoryAction.LOCK) {
         this.activator.lockSlot(args[0], !this.activator.locked(args[0]));
         return null;
      } else {
         return super.clicked(action, args, player);
      }
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return this.activator.stillValid(entityplayer);
   }

   @Override
   public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, Player player) {
      if (slot.index >= this.activatorSlotsStart && slot.index < this.inventorySlotsStart) {
         return this.getSlots(this.activatorSlotsStart, 9, false);
      } else {
         if (action == InventoryAction.MOVE_ALL) {
            if (slot.index >= this.inventorySlotsStart && slot.index < this.hotbarSlotsStart) {
               return this.getSlots(this.inventorySlotsStart, 27, false);
            }

            if (slot.index >= this.hotbarSlotsStart) {
               return this.getSlots(this.hotbarSlotsStart, 9, false);
            }
         }

         return action == InventoryAction.MOVE_SIMILAR && slot.index >= this.inventorySlotsStart ? this.getSlots(this.inventorySlotsStart, 36, false) : null;
      }
   }

   @Override
   public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, Player player) {
      return slot.index >= this.activatorSlotsStart && slot.index < this.inventorySlotsStart
         ? this.getSlots(this.inventorySlotsStart, 36, false)
         : this.getSlots(this.activatorSlotsStart, 9, false);
   }
}
