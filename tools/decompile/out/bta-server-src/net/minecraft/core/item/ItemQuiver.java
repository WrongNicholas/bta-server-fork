package net.minecraft.core.item;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.material.ArmorMaterial;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.Nullable;

public class ItemQuiver extends Item implements IArmorItem {
   public ItemQuiver(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.setMaxStackSize(1);
      this.setMaxDamage(192);
   }

   @Override
   public boolean hasInventoryInteraction() {
      return true;
   }

   @Override
   public ItemStack onInventoryInteract(Player player, Slot slot, ItemStack stackInSlot, boolean isItemGrabbed) {
      ItemStack quiverItem;
      if (isItemGrabbed) {
         quiverItem = player.inventory.getHeldItemStack();
      } else {
         quiverItem = stackInSlot;
      }

      int totalSpace = this.getMaxDamageForStack(stackInSlot);
      int arrowCount = this.getArrowCount(quiverItem);
      int freeSpace = totalSpace - arrowCount;
      if (isItemGrabbed) {
         if (stackInSlot == null) {
            int amount = Math.min(64, arrowCount);
            if (amount > 0) {
               ItemStack arrowStack = new ItemStack(Items.AMMO_ARROW, amount, 0);
               if (slot.mayPlace(arrowStack)) {
                  this.setArrowCount(quiverItem, arrowCount - amount);
                  stackInSlot = arrowStack;
               }
            }
         } else if (stackInSlot != null && stackInSlot.itemID == Items.AMMO_ARROW.id) {
            int amount = Math.min(freeSpace, stackInSlot.stackSize);
            if (amount > 0) {
               this.setArrowCount(quiverItem, arrowCount + amount);
               stackInSlot.stackSize -= amount;
            }
         }
      } else {
         ItemStack grabbedItem = player.inventory.getHeldItemStack();
         if (grabbedItem != null && grabbedItem.itemID == Items.AMMO_ARROW.id) {
            int amount = Math.min(grabbedItem.stackSize, freeSpace);
            if (amount > 0) {
               grabbedItem.stackSize -= amount;
               this.setArrowCount(quiverItem, arrowCount + amount);
               if (grabbedItem.stackSize <= 0) {
                  player.inventory.setHeldItemStack(null);
               }
            }
         } else if (grabbedItem == null) {
            int amount = Math.min(64, arrowCount);
            if (amount > 0) {
               this.setArrowCount(quiverItem, arrowCount - amount);
               player.inventory.setHeldItemStack(new ItemStack(Items.AMMO_ARROW, amount, 0));
            }
         }
      }

      return stackInSlot;
   }

   private int getArrowCount(ItemStack stack) {
      return stack.getMaxDamage() - stack.getMetadata();
   }

   private void setArrowCount(ItemStack stack, int count) {
      stack.setMetadata(stack.getMaxDamage() - count);
   }

   @Override
   public boolean showFullDurability() {
      return true;
   }

   @Nullable
   @Override
   public ArmorMaterial getArmorMaterial() {
      return null;
   }

   @Override
   public int getArmorPiece() {
      return 2;
   }
}
