package net.minecraft.core.player.inventory.menu;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Global;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.IArmorItem;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.player.inventory.slot.SlotResult;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class MenuAbstract {
   private static final Logger LOGGER = LogUtils.getLogger();
   public List<ItemStack> lastSlots = new ArrayList<>();
   public List<Slot> slots = new ArrayList<>();
   public int containerId = 0;
   private short changeUid = 0;
   protected List<ContainerListener> containerListeners = new ArrayList<>();
   private final Set<Player> unSynchedPlayers = new HashSet<>();

   protected void addSlot(Slot slot) {
      slot.index = this.slots.size();
      this.slots.add(slot);
      this.lastSlots.add(null);
   }

   public void addSlotListener(ContainerListener containerListener) {
      if (this.containerListeners.contains(containerListener)) {
         throw new IllegalArgumentException("Listener already listening");
      } else {
         this.containerListeners.add(containerListener);
         containerListener.updateCraftingInventory(this, this.getSlotStackList());
         this.broadcastChanges();
      }
   }

   public List<ItemStack> getSlotStackList() {
      List<ItemStack> itemList = new ArrayList<>();

      for (Slot slot : this.slots) {
         itemList.add(slot.getItemStack());
      }

      return itemList;
   }

   public void broadcastChanges() {
      for (int i = 0; i < this.slots.size(); i++) {
         ItemStack itemstack = this.slots.get(i).getItemStack();
         ItemStack itemstack1 = this.lastSlots.get(i);
         if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
            itemstack1 = itemstack != null ? itemstack.copy() : null;
            this.lastSlots.set(i, itemstack1);

            for (ContainerListener crafter : this.containerListeners) {
               crafter.updateInventorySlot(this, i, itemstack1);
            }
         }
      }
   }

   @Nullable
   public Slot getSlotFor(Container container, int index) {
      for (Slot inventorySlot : this.slots) {
         if (inventorySlot.isAt(container, index)) {
            return inventorySlot;
         }
      }

      return null;
   }

   public Slot getSlot(int i) {
      try {
         return this.slots.get(i);
      } catch (Exception var3) {
         return null;
      }
   }

   public ItemStack clicked(InventoryAction action, int[] args, Player player) {
      if (action.requireCreative() && player.getGamemode().consumeBlocks()) {
         LOGGER.warn("Player {} used a creative inventory action but is not in creative mode!", player.username);
         return null;
      } else {
         ContainerInventory inventory = player.inventory;
         if (action != InventoryAction.DROP_HELD_SINGLE && action != InventoryAction.DROP_HELD_STACK) {
            if (action == InventoryAction.PICKUP_SIMILAR
               || action == InventoryAction.DRAG_ITEMS_ALL
               || action == InventoryAction.DRAG_ITEMS_ONE
               || action == InventoryAction.CREATIVE_DRAG) {
               ItemStack itemStack = inventory.getHeldItemStack();
               if (itemStack == null) {
                  return null;
               } else {
                  ItemStack controlStack = itemStack.copy();
                  if (action == InventoryAction.PICKUP_SIMILAR) {
                     this.pickupSimilarItems(player);
                  } else {
                     this.dragItemsAcrossSlots(player, action, args);
                  }

                  this.slotsChanged(player.inventory);
                  return controlStack;
               }
            } else if (args != null && args.length != 0) {
               int slotId = args[0];
               Slot slot = this.getSlot(slotId);
               if (slot == null) {
                  this.slotsChanged(inventory);
                  return null;
               } else {
                  ItemStack controlStack = null;
                  ItemStack stackInSlot = slot.getItemStack();
                  Item itemInSlot = stackInSlot != null ? stackInSlot.getItem() : null;
                  if (stackInSlot != null) {
                     controlStack = stackInSlot.copy();
                  }

                  if (action != InventoryAction.INTERACT_SLOT && action != InventoryAction.INTERACT_GRABBED) {
                     if (action == InventoryAction.EQUIP_ARMOR) {
                        this.handleArmorEquip(slot, player);
                        this.slotsChanged(inventory);
                        return controlStack;
                     } else if (action == InventoryAction.HOTBAR_ITEM_SWAP) {
                        this.handleHotbarSwap(args, player);
                        this.slotsChanged(inventory);
                        return controlStack;
                     } else if (action == InventoryAction.MOVE_STACK
                        || action == InventoryAction.MOVE_SINGLE_ITEM
                        || action == InventoryAction.MOVE_SIMILAR
                        || action == InventoryAction.MOVE_ALL) {
                        int target = args.length > 1 ? args[1] : 0;
                        this.handleItemMove(action, slot, target, player);
                        this.slotsChanged(player.inventory);
                        return controlStack;
                     } else if (action != InventoryAction.SORT) {
                        slot.setChanged();
                        ItemStack stackInHand = inventory.getHeldItemStack();
                        if (action == InventoryAction.DROP) {
                           if (stackInSlot == null) {
                              return null;
                           } else {
                              int amount = args.length > 1 ? args[1] : 1;
                              amount = Math.min(amount, stackInSlot.stackSize);
                              ItemStack dropStack = slot.remove(amount);
                              if (stackInSlot.stackSize <= 0) {
                                 slot.set(null);
                              }

                              slot.onTake(dropStack);
                              player.dropPlayerItem(dropStack);
                              this.slotsChanged(inventory);
                              return controlStack;
                           }
                        } else if (action != InventoryAction.CREATIVE_GRAB
                           && action != InventoryAction.CREATIVE_MOVE
                           && action != InventoryAction.CREATIVE_DELETE) {
                           if (stackInSlot == null) {
                              if (stackInHand != null && slot.mayPlace(stackInHand)) {
                                 int i1 = action != InventoryAction.CLICK_LEFT ? 1 : stackInHand.stackSize;
                                 if (i1 > slot.getMaxStackSize()) {
                                    i1 = slot.getMaxStackSize();
                                 }

                                 slot.set(stackInHand.splitStack(i1));
                                 if (stackInHand.stackSize <= 0) {
                                    inventory.setHeldItemStack(null);
                                 }
                              }
                           } else if (stackInHand == null) {
                              int j1 = action != InventoryAction.CLICK_LEFT ? (stackInSlot.stackSize + 1) / 2 : stackInSlot.stackSize;
                              ItemStack itemstack5 = slot.remove(j1);
                              inventory.setHeldItemStack(itemstack5);
                              if (stackInSlot.stackSize <= 0) {
                                 slot.set(null);
                              }

                              slot.onTake(inventory.getHeldItemStack());
                           } else if (slot.mayPlace(stackInHand)) {
                              if (!stackInSlot.canStackWith(stackInHand)) {
                                 if (stackInHand.stackSize <= slot.getMaxStackSize()) {
                                    slot.set(stackInHand);
                                    inventory.setHeldItemStack(stackInSlot);
                                 }
                              } else {
                                 int splitSize = action != InventoryAction.CLICK_LEFT ? 1 : stackInHand.stackSize;
                                 if (splitSize > slot.getMaxStackSize() - stackInSlot.stackSize) {
                                    splitSize = slot.getMaxStackSize() - stackInSlot.stackSize;
                                 }

                                 if (splitSize > stackInHand.getMaxStackSize() - stackInSlot.stackSize) {
                                    splitSize = stackInHand.getMaxStackSize() - stackInSlot.stackSize;
                                 }

                                 stackInHand.splitStack(splitSize);
                                 if (stackInHand.stackSize <= 0) {
                                    inventory.setHeldItemStack(null);
                                 }

                                 stackInSlot.stackSize += splitSize;
                              }
                           } else if (stackInSlot.canStackWith(stackInHand) && stackInSlot.stackSize + stackInHand.stackSize <= stackInHand.getMaxStackSize()) {
                              slot.set(null);
                              slot.onTake(stackInSlot);
                              if (stackInSlot.canStackWith(stackInHand) && stackInHand.stackSize + stackInSlot.stackSize <= stackInHand.getMaxStackSize()) {
                                 stackInHand.stackSize = stackInHand.stackSize + stackInSlot.stackSize;
                              } else {
                                 player.dropPlayerItem(stackInSlot);
                              }
                           }

                           if (inventory.getHeldItemStack() != null && inventory.getHeldItemStack().stackSize <= 0) {
                              inventory.setHeldItemStack(null);
                           }

                           this.slotsChanged(player.inventory);
                           return controlStack;
                        } else {
                           if (action == InventoryAction.CREATIVE_DELETE) {
                              int count = args.length > 1 ? args[1] : 1;

                              for (int i = 0; i < count; i++) {
                                 Slot slot1 = this.getSlot(slotId + i);
                                 if (slot1 != null) {
                                    slot1.set(null);
                                 }
                              }
                           } else {
                              int amount = args.length > 1 ? args[1] : 0;
                              if (stackInSlot != null) {
                                 amount = MathHelper.clamp(amount, 0, stackInSlot.getMaxStackSize());
                              } else {
                                 amount = 0;
                              }

                              if (action == InventoryAction.CREATIVE_GRAB) {
                                 ItemStack stack;
                                 if (amount > 0) {
                                    stack = slot.getItemStack().copy();
                                    stack.stackSize = amount;
                                 } else {
                                    stack = null;
                                 }

                                 inventory.setHeldItemStack(stack);
                              }

                              if (action == InventoryAction.CREATIVE_MOVE && amount > 0) {
                                 ItemStack stack = slot.getItemStack().copy();
                                 stack.stackSize = amount;
                                 player.inventory.insertItem(stack, false);
                              }
                           }

                           this.slotsChanged(player.inventory);
                           return controlStack;
                        }
                     } else if (player.world.isClientSide) {
                        return null;
                     } else {
                        this.handleSort(args, player);
                        this.slotsChanged(inventory);
                        return controlStack;
                     }
                  } else {
                     Item interactItem;
                     if (action == InventoryAction.INTERACT_SLOT) {
                        if (stackInSlot == null) {
                           return null;
                        }

                        interactItem = itemInSlot;
                     } else {
                        interactItem = player.inventory.getHeldItemStack().getItem();
                     }

                     if (interactItem.hasInventoryInteraction() && slot.allowItemInteraction()) {
                        ItemStack result = interactItem.onInventoryInteract(player, slot, stackInSlot, action == InventoryAction.INTERACT_GRABBED);
                        if (result != null && result.stackSize <= 0) {
                           result = null;
                        }

                        slot.set(result);
                        ItemStack grabbedItem = player.inventory.getHeldItemStack();
                        if (grabbedItem != null && grabbedItem.stackSize <= 0) {
                           player.inventory.setHeldItemStack(null);
                        }

                        this.slotsChanged(inventory);
                        return controlStack;
                     } else {
                        return controlStack;
                     }
                  }
               }
            } else {
               return null;
            }
         } else {
            if (inventory.getHeldItemStack() != null) {
               if (action == InventoryAction.DROP_HELD_STACK) {
                  player.dropPlayerItem(inventory.getHeldItemStack());
                  inventory.setHeldItemStack(null);
               }

               if (action == InventoryAction.DROP_HELD_SINGLE) {
                  player.dropPlayerItem(inventory.getHeldItemStack().splitStack(1));
                  if (inventory.getHeldItemStack().stackSize == 0) {
                     inventory.setHeldItemStack(null);
                  }
               }
            }

            this.slotsChanged(inventory);
            return null;
         }
      }
   }

   public void handleArmorEquip(Slot slot, Player player) {
      ItemStack stackInSlot = slot.getItemStack();
      if (stackInSlot != null) {
         Item itemInSlot = stackInSlot.getItem();
         if (itemInSlot instanceof IArmorItem) {
            IArmorItem armorItem = (IArmorItem)itemInSlot;
            int armorSlot = 3 - armorItem.getArmorPiece();
            slot.set(null);
            slot.onTake(stackInSlot);
            slot.setChanged();
            ItemStack oldArmor = player.inventory.armorInventory[armorSlot];
            player.inventory.armorInventory[armorSlot] = stackInSlot;
            this.mergeItems(oldArmor, slot.index);
            this.storeOrDropItem(player, oldArmor);
         }
      }
   }

   public void handleSort(int[] args, Player player) {
      if (args.length >= 1) {
         Slot slot = this.getSlot(args[0]);
         if (slot != null) {
            slot.sortSlotInventory();
            this.containerListeners.forEach(c -> c.updateCraftingInventory(this, this.getSlotStackList()));
         }
      }
   }

   public void handleHotbarSwap(int[] args, Player player) {
      if (args.length >= 2) {
         int hotbarSlotNumber = args[1];
         if (hotbarSlotNumber >= 1 && hotbarSlotNumber <= 9) {
            Slot slot = this.getSlot(args[0]);
            Slot hotbarSlot = this.getSlot(this.getHotbarSlotId(hotbarSlotNumber));
            if (hotbarSlot != null && slot != hotbarSlot) {
               ItemStack slotStack = slot.getItemStack();
               ItemStack hotbarStack = hotbarSlot.getItemStack();
               if (slotStack != null) {
                  slot.set(null);
                  slot.onTake(slotStack);
               }

               if (hotbarStack != null) {
                  hotbarSlot.set(null);
                  hotbarSlot.onTake(hotbarStack);
               }

               this.mergeItems(slotStack, hotbarSlot.index);
               this.storeOrDropItem(player, slotStack);
               this.mergeItems(hotbarStack, slot.index);
               this.storeOrDropItem(player, hotbarStack);
               slot.setChanged();
               hotbarSlot.setChanged();
            }
         }
      }
   }

   public void handleItemMove(InventoryAction action, Slot slot, int target, Player player) {
      ItemStack stackInSlot = slot.getItemStack();
      if (slot instanceof SlotResult) {
         this.handleCrafting(action, (SlotResult)slot, target, player);
      } else if (action != InventoryAction.MOVE_SINGLE_ITEM && action != InventoryAction.MOVE_STACK) {
         if (action == InventoryAction.MOVE_ALL || action == InventoryAction.MOVE_SIMILAR) {
            List<Integer> moveSlots = this.getMoveSlots(action, slot, target, player);
            List<Integer> targetSlots = this.getTargetSlots(action, slot, target, player);
            if (moveSlots == null || targetSlots == null) {
               return;
            }

            ItemStack compareStack = null;
            if (action == InventoryAction.MOVE_SIMILAR) {
               compareStack = stackInSlot;
               if (stackInSlot == null) {
                  return;
               }
            }

            for (int i = 0; i < moveSlots.size(); i++) {
               Slot slot1 = this.getSlotFromList(moveSlots, i);
               if (slot1 != null) {
                  ItemStack item = slot1.getItemStack();
                  if (this.compare(compareStack, item)) {
                     int freeSpace = this.getFreeSpace(item, targetSlots);
                     if (freeSpace > 0) {
                        int amount = Math.min(freeSpace, item.stackSize);
                        ItemStack takenItem = item.splitStack(amount);
                        slot.onTake(takenItem);
                        this.mergeItems(takenItem, targetSlots);
                        if (item.stackSize <= 0) {
                           slot1.set(null);
                        }

                        if (takenItem.stackSize > 0) {
                           player.dropPlayerItem(takenItem);
                        }
                     }
                  }
               }
            }
         }
      } else if (stackInSlot != null) {
         List<Integer> targetSlotsx = this.getTargetSlots(action, slot, target, player);
         if (targetSlotsx != null && targetSlotsx.size() != 0) {
            ItemStack item = null;
            if (action == InventoryAction.MOVE_SINGLE_ITEM) {
               item = stackInSlot.splitStack(1);
            }

            if (action == InventoryAction.MOVE_STACK) {
               item = stackInSlot.splitStack(stackInSlot.stackSize);
            }

            if (item != null) {
               slot.onTake(item);
               this.mergeItems(item, targetSlotsx);
               if (item.stackSize > 0) {
                  stackInSlot.stackSize = stackInSlot.stackSize + item.stackSize;
                  item.stackSize = 0;
               }

               if (stackInSlot.stackSize <= 0) {
                  slot.set(null);
               }
            }
         }
      }
   }

   private boolean compare(ItemStack compareStack, ItemStack stack) {
      if (stack == null) {
         return false;
      } else {
         return compareStack == null ? true : compareStack.canStackWith(stack);
      }
   }

   public void handleCrafting(InventoryAction action, SlotResult slot, int target, Player player) {
      ItemStack itemToCraft = slot.getItemStack();
      if (itemToCraft != null) {
         ItemStack originalCraftItem = itemToCraft.copy();
         Integer craftCount = null;
         if (action == InventoryAction.MOVE_SINGLE_ITEM) {
            craftCount = 1;
         }

         if (action == InventoryAction.MOVE_STACK) {
            craftCount = itemToCraft.getMaxStackSize() / itemToCraft.stackSize;
         }

         if (action == InventoryAction.MOVE_SIMILAR) {
            craftCount = 65536;
         }

         if (craftCount != null) {
            List<Integer> resultSlots = this.getTargetSlots(action, slot, target, player);
            if (resultSlots != null) {
               for (int i = 0; i < craftCount; i++) {
                  itemToCraft = slot.getItemStack();
                  if (itemToCraft == null
                     || !itemToCraft.canStackWith(originalCraftItem)
                     || itemToCraft.itemID != originalCraftItem.itemID
                     || itemToCraft.getMetadata() != originalCraftItem.getMetadata()) {
                     break;
                  }

                  int space = this.getFreeSpace(itemToCraft, resultSlots);
                  if (space >= itemToCraft.stackSize) {
                     slot.set(null);
                     slot.onTake(itemToCraft);
                     this.slotsChanged(player.inventory);
                     this.mergeItems(itemToCraft, resultSlots);
                     if (itemToCraft.stackSize > 0) {
                        this.storeOrDropItem(player, itemToCraft);
                     }
                  }
               }
            }
         }
      }
   }

   public int getFreeSpace(ItemStack item, List<Integer> slots) {
      int space = 0;
      int max = item.getMaxStackSize();

      for (int i = 0; i < slots.size(); i++) {
         Slot slot = this.getSlotFromList(slots, i);
         if (slot != null) {
            ItemStack stackInSlot = slot.getItemStack();
            if (stackInSlot == null) {
               return max;
            }

            if (item.canStackWith(stackInSlot)) {
               space += stackInSlot.getMaxStackSize() - stackInSlot.stackSize;
            }

            if (space >= max) {
               return space;
            }
         }
      }

      return space;
   }

   public abstract List<Integer> getMoveSlots(InventoryAction var1, Slot var2, int var3, Player var4);

   public abstract List<Integer> getTargetSlots(InventoryAction var1, Slot var2, int var3, Player var4);

   public void pickupSimilarItems(Player player) {
      ItemStack item = player.inventory.getHeldItemStack();
      if (item != null) {
         List<Slot> slots = this.slots;

         for (int i = 0; i < slots.size(); i++) {
            Slot slot1 = slots.get(i);
            if (slot1.enableDragAndPickup()) {
               ItemStack slotStack = slot1.getItemStack();
               if (slotStack != null && slotStack.stackSize < slotStack.getMaxStackSize()) {
                  if (slotStack.canStackWith(item)) {
                     int amount = Math.min(slotStack.stackSize, item.getMaxStackSize() - item.stackSize);
                     if (amount > 0) {
                        slotStack.stackSize -= amount;
                        item.stackSize += amount;
                        if (slotStack.stackSize <= 0) {
                           slot1.set(null);
                        }
                     }
                  }

                  if (item.stackSize >= item.getMaxStackSize()) {
                     break;
                  }
               }
            }
         }
      }
   }

   public void dragItemsAcrossSlots(Player player, InventoryAction action, int[] slots) {
      ItemStack draggingItemStack = player.inventory.getHeldItemStack();
      if (action == InventoryAction.CREATIVE_DRAG) {
         for (int i = 0; i < slots.length; i++) {
            Slot slot = this.getSlot(slots[i]);
            if (slot != null && slot.enableDragAndPickup() && slot.mayPlace(draggingItemStack) && slot.getItemStack() == null) {
               ItemStack stack = draggingItemStack.copy();
               stack.stackSize = stack.getMaxStackSize();
               slot.set(stack);
               slot.setChanged();
            }
         }

         player.inventory.setHeldItemStack(null);
      } else {
         int itemsPerSlot;
         if (action == InventoryAction.DRAG_ITEMS_ONE) {
            itemsPerSlot = 1;
         } else {
            if (action != InventoryAction.DRAG_ITEMS_ALL) {
               return;
            }

            itemsPerSlot = draggingItemStack.stackSize / slots.length;
         }

         if (itemsPerSlot > 0) {
            for (int ix = 0; ix < slots.length; ix++) {
               Slot slot = this.getSlot(slots[ix]);
               if (slot != null && slot.enableDragAndPickup() && slot.mayPlace(draggingItemStack)) {
                  ItemStack stackInSlot = slot.getItemStack();
                  int amount = Math.min(itemsPerSlot, slot.getMaxStackSize());
                  if (stackInSlot != null) {
                     amount = Math.min(amount, stackInSlot.getMaxStackSize() - stackInSlot.stackSize);
                  }

                  amount = Math.min(amount, draggingItemStack.stackSize);
                  if (amount > 0) {
                     if (stackInSlot == null) {
                        slot.set(draggingItemStack.splitStack(amount));
                     } else if (stackInSlot.canStackWith(draggingItemStack)) {
                        draggingItemStack.stackSize -= amount;
                        stackInSlot.stackSize += amount;
                     }

                     if (draggingItemStack.stackSize <= 0) {
                        break;
                     }
                  }
               }
            }

            if (draggingItemStack.stackSize <= 0) {
               player.inventory.setHeldItemStack(null);
            }
         }
      }
   }

   public void onCraftGuiClosed(Player player) {
      ContainerInventory inventory = player.inventory;
      if (inventory.getHeldItemStack() != null) {
         ItemStack stack = inventory.getHeldItemStack();
         inventory.setHeldItemStack(null);
         this.storeOrDropItem(player, stack);
      }
   }

   public void storeOrDropItem(Player player, ItemStack stack) {
      if (stack != null && stack.stackSize > 0) {
         ContainerInventory inventory = player.inventory;
         inventory.insertItem(stack, false);
         if (stack.stackSize > 0) {
            player.dropPlayerItem(stack);
         }
      }
   }

   public void slotsChanged(Container iinventory) {
      this.broadcastChanges();
   }

   public boolean isSynched(Player entityplayer) {
      return !this.unSynchedPlayers.contains(entityplayer);
   }

   public void setSynched(Player entityplayer, boolean flag) {
      if (flag) {
         this.unSynchedPlayers.remove(entityplayer);
      } else {
         this.unSynchedPlayers.add(entityplayer);
      }
   }

   public void setItem(int i, ItemStack itemstack) {
      this.getSlot(i).set(itemstack);
   }

   public void setAll(ItemStack[] aitemstack) {
      for (int i = 0; i < aitemstack.length; i++) {
         this.getSlot(i).set(aitemstack[i]);
      }
   }

   public void setData(int id, int value) {
   }

   public short backup(ContainerInventory inventoryplayer) {
      this.changeUid++;
      return this.changeUid;
   }

   public void deleteBackup(short word0) {
   }

   public void rollbackToBackup(short word0) {
   }

   public abstract boolean stillValid(Player var1);

   public final void mergeItems(ItemStack stack, List<Integer> targetSlots) {
      if (stack != null && targetSlots != null) {
         for (int i = 0; i < targetSlots.size(); i++) {
            Slot slot = this.getSlotFromList(targetSlots, i);
            if (slot != null) {
               ItemStack stackInSlot = slot.getItemStack();
               if (stackInSlot != null && slot.mayPlace(stack) && stack.canStackWith(stackInSlot)) {
                  int amount = Math.min(stack.stackSize, stack.getMaxStackSize(slot.getContainer()) - stackInSlot.stackSize);
                  amount = Math.min(amount, slot.getMaxStackSize() - stackInSlot.stackSize);
                  if (amount > 0) {
                     stack.stackSize -= amount;
                     stackInSlot.stackSize += amount;
                     slot.setChanged();
                     if (stack.stackSize == 0) {
                        return;
                     }
                  }
               }
            }
         }

         for (int ix = 0; ix < targetSlots.size(); ix++) {
            Slot slot = this.getSlotFromList(targetSlots, ix);
            if (slot != null) {
               ItemStack stackInSlot = slot.getItemStack();
               if (stackInSlot == null && slot.mayPlace(stack)) {
                  int amount = Math.min(stack.stackSize, stack.getMaxStackSize(slot.getContainer()));
                  amount = Math.min(amount, slot.getMaxStackSize());
                  if (amount > 0) {
                     slot.set(stack.splitStack(amount));
                     slot.setChanged();
                     if (stack.stackSize <= 0) {
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   public Slot getSlotFromList(List<Integer> slots, int i) {
      int slotId = slots.get(i);
      Slot slot = this.getSlot(slotId);
      if (slot == null && Global.BUILD_CHANNEL.isUnstableBuild()) {
         throw new NullPointerException("Slot " + slotId + " does not exist!");
      } else {
         return slot;
      }
   }

   public final void mergeItems(ItemStack stack, int targetSlot) {
      List<Integer> list = new ArrayList<>();
      list.add(targetSlot);
      this.mergeItems(stack, list);
   }

   public final void mergeItems(ItemStack stack, int minSlot, int maxSlot, boolean lastSlotFirst) {
      this.mergeItems(stack, this.getSlots(minSlot, maxSlot - minSlot + 1, lastSlotFirst));
   }

   public List<Integer> getSlots(int min, int count, boolean lastSlotsFirst) {
      return this.getSlots(new ArrayList<>(), min, count, lastSlotsFirst);
   }

   public List<Integer> getSlots(List<Integer> targetSlots, int min, int count, boolean lastSlotsFirst) {
      for (int i = 0; i < count; i++) {
         targetSlots.add(lastSlotsFirst ? min + count - 1 - i : min + i);
      }

      return targetSlots;
   }

   public int getHotbarSlotId(int number) {
      return this.slots.size() - 10 + number;
   }
}
