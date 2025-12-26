package net.minecraft.core.player.inventory.menu;

import java.util.List;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.slot.Slot;

public class MenuContainer extends MenuAbstract {
   private Container container;
   private int numberOfRows;

   public MenuContainer(Container playerContainer, Container container) {
      this.container = container;
      this.numberOfRows = container.getContainerSize() / 9;
      int i = (this.numberOfRows - 4) * 18;

      for (int j = 0; j < this.numberOfRows; j++) {
         for (int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(container, i1 + j * 9, 8 + i1 * 18, 18 + j * 18));
         }
      }

      for (int k = 0; k < 3; k++) {
         for (int j1 = 0; j1 < 9; j1++) {
            this.addSlot(new Slot(playerContainer, j1 + k * 9 + 9, 8 + j1 * 18, 103 + k * 18 + i));
         }
      }

      for (int l = 0; l < 9; l++) {
         this.addSlot(new Slot(playerContainer, l, 8 + l * 18, 161 + i));
      }
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return this.container.stillValid(entityplayer);
   }

   @Override
   public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, Player player) {
      int chestSize = this.numberOfRows * 9;
      if (slot.index >= 0 && slot.index < chestSize) {
         return this.getSlots(0, chestSize, false);
      } else {
         if (action == InventoryAction.MOVE_ALL) {
            if (slot.index >= chestSize && slot.index < chestSize + 27) {
               return this.getSlots(chestSize, 27, false);
            }

            if (slot.index >= chestSize + 27 && slot.index < chestSize + 36) {
               return this.getSlots(chestSize + 27, 9, false);
            }
         } else if (slot.index >= chestSize && slot.index < chestSize + 36) {
            return this.getSlots(chestSize, 36, false);
         }

         return null;
      }
   }

   @Override
   public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, Player player) {
      int chestSize = this.numberOfRows * 9;
      return slot.index < chestSize ? this.getSlots(chestSize, 36, true) : this.getSlots(0, chestSize, false);
   }
}
