package net.minecraft.core.player.gamemode;

import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuInventory;

public class GamemodeSurvival extends Gamemode {
   public GamemodeSurvival(int id, String languageKey) {
      super(id, languageKey);
   }

   @Override
   public MenuInventory getContainer(ContainerInventory inventory, boolean isNotClientSide) {
      return new MenuInventory(inventory, isNotClientSide);
   }
}
