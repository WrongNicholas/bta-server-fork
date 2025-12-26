package net.minecraft.core.player.inventory.menu;

import java.util.List;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.slot.Slot;

public class MenuGuidebook extends MenuAbstract {
   @Override
   public boolean stillValid(Player entityplayer) {
      return true;
   }

   @Override
   public ItemStack clicked(InventoryAction action, int[] args, Player player) {
      return null;
   }

   @Override
   public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, Player player) {
      return null;
   }

   @Override
   public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, Player player) {
      return null;
   }
}
