package net.minecraft.core.player.inventory.menu;

import java.util.List;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerCrafting;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.container.ContainerResult;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.player.inventory.slot.SlotResult;
import net.minecraft.core.world.World;

public class MenuCrafting extends MenuAbstract {
   public ContainerCrafting craftSlots = new ContainerCrafting(this, 3, 3);
   public Container resultSlots = new ContainerResult();
   private World world;
   private int x;
   private int y;
   private int z;

   public MenuCrafting(ContainerInventory inventory, World world, int x, int y, int z) {
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
      this.addSlot(new SlotResult(inventory.player, this.craftSlots, this.resultSlots, 0, 124, 35));

      for (int l = 0; l < 3; l++) {
         for (int k1 = 0; k1 < 3; k1++) {
            this.addSlot(new Slot(this.craftSlots, k1 + l * 3, 30 + k1 * 18, 17 + l * 18));
         }
      }

      for (int i1 = 0; i1 < 3; i1++) {
         for (int l1 = 0; l1 < 9; l1++) {
            this.addSlot(new Slot(inventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 84 + i1 * 18));
         }
      }

      for (int j1 = 0; j1 < 9; j1++) {
         this.addSlot(new Slot(inventory, j1, 8 + j1 * 18, 142));
      }

      this.slotsChanged(this.craftSlots);
   }

   @Override
   public void slotsChanged(Container iinventory) {
      this.resultSlots.setItem(0, Registries.RECIPES.findMatchingRecipe(this.craftSlots));
   }

   @Override
   public void onCraftGuiClosed(Player player) {
      super.onCraftGuiClosed(player);
      boolean insert = false;

      for (int i = 0; i < 9; i++) {
         ItemStack itemstack = this.craftSlots.getItem(i);
         if (itemstack != null) {
            this.storeOrDropItem(player, itemstack);
            insert = true;
         }

         this.craftSlots.setItem(i, null);
      }

      this.resultSlots.setItem(0, null);
      if (insert) {
         player.world.playSoundAtEntity(null, player, "random.insert", 0.1F, 1.0F);
      }
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return this.world.getBlockId(this.x, this.y, this.z) != Blocks.WORKBENCH.id()
         ? false
         : entityplayer.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0;
   }

   @Override
   public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, Player player) {
      if (slot.index == 0) {
         return this.getSlots(0, 1, false);
      } else if (slot.index >= 1 && slot.index < 9) {
         return this.getSlots(1, 9, false);
      } else {
         if (action == InventoryAction.MOVE_SIMILAR) {
            if (slot.index >= 10 && slot.index <= 45) {
               return this.getSlots(10, 36, false);
            }
         } else {
            if (slot.index >= 10 && slot.index <= 36) {
               return this.getSlots(10, 27, false);
            }

            if (slot.index >= 37 && slot.index <= 45) {
               return this.getSlots(37, 9, false);
            }
         }

         return null;
      }
   }

   @Override
   public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, Player player) {
      if (slot.index >= 10 && slot.index <= 45) {
         if (target == 1) {
            return this.getSlots(1, 9, false);
         } else if (slot.index >= 10 && slot.index <= 36) {
            return this.getSlots(37, 9, false);
         } else {
            return slot.index >= 37 && slot.index <= 45 ? this.getSlots(10, 27, false) : null;
         }
      } else {
         return slot.index == 0 ? this.getSlots(10, 36, true) : this.getSlots(10, 36, false);
      }
   }
}
