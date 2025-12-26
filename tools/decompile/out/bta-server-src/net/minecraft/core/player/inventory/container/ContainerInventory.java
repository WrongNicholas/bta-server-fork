package net.minecraft.core.player.inventory.container;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.IArmorItem;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ArmorMaterial;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.player.inventory.InventorySorter;
import net.minecraft.core.util.helper.DamageType;
import org.jetbrains.annotations.Nullable;

public class ContainerInventory implements Container {
   protected int hotbarOffset = 0;
   public ItemStack[] mainInventory = new ItemStack[36];
   public ItemStack[] armorInventory = new ItemStack[4];
   protected int currentItem = 0;
   public Player player;
   private ItemStack heldItem;
   public boolean inventoryChanged = false;

   public ContainerInventory(Player player) {
      this.player = player;
   }

   public int getHotbarOffset() {
      return this.hotbarOffset;
   }

   public void setHotbarOffset(int offset, boolean overrideLock) {
      if (overrideLock || !this.currentItemLocked()) {
         this.hotbarOffset = offset;
      }
   }

   @Nullable
   public ItemStack getCurrentItem() {
      return this.currentItem < 9 + this.hotbarOffset && this.currentItem >= this.hotbarOffset ? this.mainInventory[this.currentItem] : null;
   }

   public int getCurrentItemIndex() {
      return this.currentItem;
   }

   public void setCurrentItemIndex(int index, boolean overrideLock) {
      if (overrideLock || !this.currentItemLocked()) {
         this.currentItem = index;
      }
   }

   public static int playerMainInventorySize() {
      return 36;
   }

   private int getInventorySlotContainItem(int i) {
      for (int j = 0; j < this.mainInventory.length; j++) {
         if (this.mainInventory[j] != null && this.mainInventory[j].itemID == i) {
            return j;
         }
      }

      return -1;
   }

   private int storeItemStack(ItemStack itemstack) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null
            && this.mainInventory[i].itemID == itemstack.itemID
            && this.mainInventory[i].isStackable()
            && this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize()
            && this.mainInventory[i].stackSize < this.getMaxStackSize()
            && (!this.mainInventory[i].getHasSubtypes() || this.mainInventory[i].getMetadata() == itemstack.getMetadata())) {
            return i;
         }
      }

      return -1;
   }

   public void insertItem(ItemStack stackToAdd, boolean useHotbarOffset) {
      if (this.player.getGamemode().canInteract()) {
         for (int stage = 0; stage < 2; stage++) {
            for (int i = 0; i < this.mainInventory.length; i++) {
               int slotId = useHotbarOffset ? (i + this.hotbarOffset) % this.mainInventory.length : i;
               if (!this.locked(slotId)) {
                  ItemStack stackInSlot = this.mainInventory[slotId];
                  if (stage == 0) {
                     if (stackInSlot != null && stackInSlot.canStackWith(stackToAdd)) {
                        int transferAmount = Math.min(stackToAdd.stackSize, stackInSlot.getMaxStackSize() - stackInSlot.stackSize);
                        transferAmount = Math.min(transferAmount, stackToAdd.getMaxStackSize());
                        if (transferAmount == 0) {
                           continue;
                        }

                        stackInSlot.stackSize += transferAmount;
                        stackToAdd.stackSize -= transferAmount;
                        stackInSlot.animationsToGo = 5;
                     }
                  } else if (stage == 1 && stackInSlot == null) {
                     int transferAmount = Math.min(stackToAdd.stackSize, stackToAdd.getMaxStackSize());
                     this.mainInventory[slotId] = stackToAdd.copy();
                     this.mainInventory[slotId].stackSize = transferAmount;
                     this.mainInventory[slotId].animationsToGo = 5;
                     stackToAdd.stackSize -= transferAmount;
                  }

                  if (stackToAdd.stackSize <= 0) {
                     return;
                  }
               }
            }
         }
      }
   }

   public int setCurrentItem(ItemStack itemstack, boolean flag) {
      if (this.currentItemLocked()) {
         return this.currentItem;
      } else if (!flag) {
         return this.currentItem;
      } else {
         if (itemstack.itemID < Blocks.blocksList.length) {
            if (Blocks.blocksList[itemstack.itemID].hasTag(BlockTags.NOT_IN_CREATIVE_MENU)) {
               return this.currentItem;
            }
         } else if (Item.itemsList[itemstack.itemID].hasTag(ItemTags.NOT_IN_CREATIVE_MENU)) {
            return this.currentItem;
         }

         for (int j = this.hotbarOffset; j < 9 + this.hotbarOffset; j++) {
            if (this.mainInventory[j] != null && this.mainInventory[j].itemID == itemstack.itemID) {
               this.currentItem = j;
               return this.currentItem;
            }
         }

         this.mainInventory[this.currentItem] = itemstack;
         return this.currentItem;
      }
   }

   public void changeCurrentItem(int i) {
      if (!this.currentItemLocked()) {
         if (i > 0) {
            i = 1;
         }

         if (i < 0) {
            i = -1;
         }

         this.currentItem -= i;

         while (this.currentItem < this.hotbarOffset) {
            this.currentItem += 9;
         }

         while (this.currentItem >= 9 + this.hotbarOffset) {
            this.currentItem -= 9;
         }
      }
   }

   public void decrementAnimations() {
      for (int slot = 0; slot < this.mainInventory.length; slot++) {
         if (this.mainInventory[slot] != null) {
            this.mainInventory[slot].updateAnimation(this.player.world, this.player, slot, this.currentItem == slot);
         }
      }
   }

   public boolean consumeInventoryItem(int i) {
      int j = this.getInventorySlotContainItem(i);
      if (j < 0) {
         return false;
      } else {
         if (this.player.getGamemode().consumeBlocks() && --this.mainInventory[j].stackSize <= 0) {
            this.mainInventory[j] = null;
         }

         return true;
      }
   }

   public boolean currentItemLocked() {
      return this.player.getHeldObject() != null;
   }

   @Override
   public boolean locked(int index) {
      return this.currentItemLocked() && index == this.currentItem;
   }

   @Nullable
   @Override
   public ItemStack removeItem(int index, int takeAmount) {
      ItemStack[] aitemstack = this.mainInventory;
      if (index >= this.mainInventory.length) {
         aitemstack = this.armorInventory;
         index -= this.mainInventory.length;
      }

      if (aitemstack[index] != null) {
         if (aitemstack[index].stackSize <= takeAmount) {
            ItemStack itemstack = aitemstack[index];
            aitemstack[index] = null;
            return itemstack;
         } else {
            ItemStack itemstack1 = aitemstack[index].splitStack(takeAmount);
            if (aitemstack[index].stackSize <= 0) {
               aitemstack[index] = null;
            }

            return itemstack1;
         }
      } else {
         return null;
      }
   }

   @Override
   public void setItem(int index, @Nullable ItemStack itemstack) {
      ItemStack[] aitemstack = this.mainInventory;
      if (index >= aitemstack.length) {
         index -= aitemstack.length;
         aitemstack = this.armorInventory;
      }

      aitemstack[index] = itemstack;
   }

   public float getStrVsBlock(Block<?> block) {
      float f = 1.0F;
      if (this.mainInventory[this.currentItem] != null) {
         f *= this.mainInventory[this.currentItem].getStrVsBlock(block);
      }

      return f;
   }

   public ListTag writeToNBT(ListTag nbttaglist) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null) {
            CompoundTag nbttagcompound = new CompoundTag();
            nbttagcompound.putByte("Slot", (byte)i);
            this.mainInventory[i].writeToNBT(nbttagcompound);
            nbttaglist.addTag(nbttagcompound);
         }
      }

      for (int j = 0; j < this.armorInventory.length; j++) {
         if (this.armorInventory[j] != null) {
            CompoundTag nbttagcompound1 = new CompoundTag();
            nbttagcompound1.putByte("Slot", (byte)(j + 100));
            this.armorInventory[j].writeToNBT(nbttagcompound1);
            nbttaglist.addTag(nbttagcompound1);
         }
      }

      return nbttaglist;
   }

   public void readFromNBT(ListTag nbttaglist) {
      this.mainInventory = new ItemStack[36];
      this.armorInventory = new ItemStack[4];

      for (int i = 0; i < nbttaglist.tagCount(); i++) {
         CompoundTag nbttagcompound = (CompoundTag)nbttaglist.tagAt(i);
         int j = nbttagcompound.getByte("Slot") & 255;
         ItemStack itemstack = ItemStack.readItemStackFromNbt(nbttagcompound);
         if (itemstack != null) {
            if (j >= 0 && j < this.mainInventory.length) {
               this.mainInventory[j] = itemstack;
            }

            if (j >= 100 && j < this.armorInventory.length + 100) {
               this.armorInventory[j - 100] = itemstack;
            }
         }
      }
   }

   @Override
   public int getContainerSize() {
      return this.mainInventory.length + 4;
   }

   @Nullable
   @Override
   public ItemStack getItem(int index) {
      ItemStack[] aitemstack = this.mainInventory;
      if (index >= aitemstack.length) {
         index -= aitemstack.length;
         aitemstack = this.armorInventory;
      }

      return aitemstack[index];
   }

   @Override
   public String getNameTranslationKey() {
      return "container.inventory.name";
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   public int getDamageVsEntity(Entity entity) {
      ItemStack itemstack = this.getItem(this.currentItem);
      return itemstack != null ? itemstack.getDamageVsEntity(entity) : 1;
   }

   public boolean canHarvestBlock(Block<?> block) {
      if (block == null) {
         return false;
      } else if (block.getMaterial().isAlwaysDestroyable()) {
         return true;
      } else {
         ItemStack itemstack = this.getItem(this.currentItem);
         return itemstack != null ? itemstack.canHarvestBlock(this.player, block) : false;
      }
   }

   public ItemStack armorItemInSlot(int i) {
      return this.armorInventory[i];
   }

   public int getTotalArmourPoints() {
      float armorDurability = 0.0F;
      float maxDurability = 0.0F;

      for (int armorPiece = 0; armorPiece < this.armorInventory.length; armorPiece++) {
         ItemStack itemStack = this.armorInventory[armorPiece];
         if (itemStack != null && itemStack.getItem() instanceof IArmorItem) {
            Item armor = itemStack.getItem();
            maxDurability += armor.getMaxDamage();
            armorDurability += armor.getMaxDamage() - itemStack.getMetadata();
         }
      }

      return (int)(20.0F * (armorDurability / maxDurability));
   }

   public float getTotalProtectionAmount(DamageType damageType) {
      float protectionPercentage = 0.0F;

      for (int i = 0; i < this.armorInventory.length; i++) {
         ItemStack itemStack = this.armorInventory[i];
         if (itemStack != null && itemStack.getItem() instanceof IArmorItem) {
            IArmorItem armor = (IArmorItem)itemStack.getItem();
            if (armor.getArmorPiece() == i) {
               ArmorMaterial material = armor.getArmorMaterial();
               if (material != null) {
                  protectionPercentage += material.getProtection(damageType) * armor.getArmorPieceProtectionPercentage();
               }
            }
         }
      }

      return protectionPercentage;
   }

   public void damageArmor(int damage) {
      for (int j = 0; j < this.armorInventory.length; j++) {
         if (this.armorInventory[j] != null && this.armorInventory[j].getItem() instanceof IArmorItem) {
            this.armorInventory[j].damageItem(damage, this.player);
            if (this.armorInventory[j].stackSize <= 0) {
               this.armorInventory[j] = null;
            }
         }
      }
   }

   public void damageArmor(int damage, int armorSlot) {
      if (this.armorInventory[armorSlot] != null) {
         this.armorInventory[armorSlot].damageItem(damage, this.player);
         if (this.armorInventory[armorSlot].stackSize <= 0) {
            this.armorInventory[armorSlot] = null;
         }
      }
   }

   public void dropAllItems() {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null) {
            this.player.dropPlayerItemWithRandomChoice(this.mainInventory[i], true);
            this.mainInventory[i] = null;
         }
      }

      for (int j = 0; j < this.armorInventory.length; j++) {
         if (this.armorInventory[j] != null) {
            this.player.dropPlayerItemWithRandomChoice(this.armorInventory[j], true);
            this.armorInventory[j] = null;
         }
      }
   }

   @Override
   public void setChanged() {
      this.inventoryChanged = true;
   }

   public void setHeldItemStack(ItemStack itemstack) {
      this.heldItem = itemstack;
      if (itemstack != null && itemstack.stackSize <= 0) {
         this.heldItem = null;
      }

      this.player.onItemStackChanged(itemstack);
   }

   public ItemStack getHeldItemStack() {
      return this.heldItem;
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return this.player.removed ? false : entityplayer.distanceToSqr(this.player) <= 64.0;
   }

   public boolean func_28018_c(ItemStack itemstack) {
      for (int i = 0; i < this.armorInventory.length; i++) {
         if (this.armorInventory[i] != null && this.armorInventory[i].isStackEqual(itemstack)) {
            return true;
         }
      }

      for (int j = 0; j < this.mainInventory.length; j++) {
         if (this.mainInventory[j] != null && this.mainInventory[j].isStackEqual(itemstack)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void sortContainer() {
      InventorySorter.sortInventory(this.mainInventory, 9, this.mainInventory.length - 1);
   }

   public void transferAllContents(ContainerInventory inventory) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         this.mainInventory[i] = inventory.mainInventory[i];
         inventory.mainInventory[i] = null;
      }

      for (int i = 0; i < this.armorInventory.length; i++) {
         this.armorInventory[i] = inventory.armorInventory[i];
         inventory.armorInventory[i] = null;
      }
   }
}
