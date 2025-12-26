package net.minecraft.core.crafting;

import java.util.List;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public interface ContainerListener {
   void updateCraftingInventory(MenuAbstract var1, List<ItemStack> var2);

   void updateInventorySlot(MenuAbstract var1, int var2, ItemStack var3);

   void updateCraftingInventoryInfo(MenuAbstract var1, int var2, int var3);
}
