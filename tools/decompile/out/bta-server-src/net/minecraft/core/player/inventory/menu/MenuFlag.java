package net.minecraft.core.player.inventory.menu;

import java.util.List;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.player.inventory.slot.SlotDye;

public class MenuFlag extends MenuAbstract {
   public TileEntityFlag flag;

   public MenuFlag(Container inv, TileEntityFlag flag) {
      this.flag = flag;
      this.addSlot(new SlotDye(flag, 36, 129, 10));
      this.addSlot(new SlotDye(flag, 37, 129, 32));
      this.addSlot(new SlotDye(flag, 38, 129, 54));

      for (int y = 0; y < 3; y++) {
         for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 113 + y * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(inv, i, 8 + i * 18, 171));
      }
   }

   @Override
   public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, Player player) {
      if (slot.index >= 0 && slot.index <= 2) {
         return this.getSlots(0, 3, false);
      } else if (action == InventoryAction.MOVE_SIMILAR) {
         return this.getSlots(3, 36, false);
      } else if (action == InventoryAction.MOVE_ALL) {
         return slot.index < 27 ? this.getSlots(3, 27, false) : this.getSlots(30, 9, false);
      } else {
         return null;
      }
   }

   @Override
   public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, Player player) {
      return slot.index >= 0 && slot.index <= 2 ? this.getSlots(3, 36, false) : this.getSlots(0, 3, false);
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return this.flag.stillValid(entityplayer);
   }
}
