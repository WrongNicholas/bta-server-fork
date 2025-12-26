package net.minecraft.core.player.inventory.slot;

import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.Container;

public class SlotFurnace extends Slot {
   private Player thePlayer;

   public SlotFurnace(Player player, Container container, int index, int x, int y) {
      super(container, index, x, y);
      this.thePlayer = player;
   }

   @Override
   public boolean mayPlace(ItemStack itemstack) {
      return false;
   }

   @Override
   public void onTake(ItemStack itemstack) {
      itemstack.onCrafting(this.thePlayer.world, this.thePlayer);
      if (itemstack.itemID == Items.INGOT_IRON.id) {
         this.thePlayer.addStat(Achievements.ACQUIRE_IRON, 1);
      }

      if (itemstack.itemID == Items.FOOD_FISH_COOKED.id) {
         this.thePlayer.addStat(Achievements.COOK_FISH, 1);
      }

      super.onTake(itemstack);
   }

   @Override
   public boolean enableDragAndPickup() {
      return false;
   }

   @Override
   public boolean allowItemInteraction() {
      return false;
   }
}
