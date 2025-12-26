package net.minecraft.core.player.gamemode;

import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuInventory;
import net.minecraft.core.player.inventory.menu.MenuInventoryCreative;

public class GamemodeCreative extends Gamemode {
   public GamemodeCreative(int id, String languageKey) {
      super(id, languageKey);
   }

   @Override
   public MenuInventory getContainer(ContainerInventory inventory, boolean isNotClientSide) {
      return new MenuInventoryCreative(inventory, isNotClientSide);
   }
}
