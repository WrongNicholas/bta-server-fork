package net.minecraft.core.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import java.util.List;
import net.minecraft.core.block.BlockLogicFurnaceBlast;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.crafting.LookupFuelFurnaceBlast;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;

public class TileEntityFurnaceBlast extends TileEntityFurnace implements Container {
   public TileEntityFurnaceBlast() {
      this.maxCookTime = 100;
   }

   @Override
   public String getNameTranslationKey() {
      return "container.furnace_blast.name";
   }

   @Override
   public void readFromNBT(CompoundTag nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      ListTag nbttaglist = nbttagcompound.getList("Items");
      this.furnaceItemStacks = new ItemStack[this.getContainerSize()];

      for (int i = 0; i < nbttaglist.tagCount(); i++) {
         CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
         byte byte0 = nbttagcompound1.getByte("Slot");
         if (byte0 >= 0 && byte0 < this.furnaceItemStacks.length) {
            this.furnaceItemStacks[byte0] = ItemStack.readItemStackFromNbt(nbttagcompound1);
         }
      }

      this.currentBurnTime = nbttagcompound.getShort("BurnTime");
      this.currentCookTime = nbttagcompound.getShort("CookTime");
      this.maxBurnTime = nbttagcompound.getShort("MaxBurnTime");
   }

   @Override
   public void writeToNBT(CompoundTag nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.putShort("BurnTime", (short)this.currentBurnTime);
      nbttagcompound.putShort("CookTime", (short)this.currentCookTime);
      nbttagcompound.putShort("MaxBurnTime", (short)this.maxBurnTime);
      ListTag nbttaglist = new ListTag();

      for (int i = 0; i < this.furnaceItemStacks.length; i++) {
         if (this.furnaceItemStacks[i] != null) {
            CompoundTag itemStackAtSlot = new CompoundTag();
            itemStackAtSlot.putByte("Slot", (byte)i);
            this.furnaceItemStacks[i].writeToNBT(itemStackAtSlot);
            nbttaglist.addTag(itemStackAtSlot);
         }
      }

      nbttagcompound.put("Items", nbttaglist);
   }

   @Override
   public void tick() {
      boolean isBurnTimeHigherThan0 = this.currentBurnTime > 0;
      boolean furnaceUpdated = false;
      if (this.currentBurnTime > 0) {
         this.currentBurnTime--;
      }

      if (this.worldObj == null || !this.worldObj.isClientSide) {
         if ((this.worldObj == null || this.worldObj.getBlockId(this.x, this.y, this.z) == Blocks.FURNACE_BLAST_IDLE.id())
            && this.currentBurnTime == 0
            && this.furnaceItemStacks[0] == null
            && this.furnaceItemStacks[1] != null
            && this.furnaceItemStacks[1].itemID == Blocks.COBBLE_NETHERRACK.id()) {
            this.furnaceItemStacks[1].stackSize--;
            if (this.furnaceItemStacks[1].stackSize <= 0) {
               this.furnaceItemStacks[1] = null;
            }

            this.updateFurnace(true);
            furnaceUpdated = true;
         }

         if (this.currentBurnTime == 0 && this.canSmelt()) {
            this.maxBurnTime = this.currentBurnTime = this.getBurnTimeFromItem(this.furnaceItemStacks[1]);
            if (this.currentBurnTime > 0) {
               furnaceUpdated = true;
               if (this.furnaceItemStacks[1] != null) {
                  this.furnaceItemStacks[1].stackSize--;
                  if (this.furnaceItemStacks[1].stackSize <= 0) {
                     this.furnaceItemStacks[1] = null;
                  }
               }
            }
         }

         if (this.isBurning() && this.canSmelt()) {
            this.currentCookTime++;
            if (this.currentCookTime == this.maxCookTime) {
               this.currentCookTime = 0;
               this.smeltItem();
               furnaceUpdated = true;
            }
         } else {
            this.currentCookTime = 0;
         }

         if (isBurnTimeHigherThan0 != this.currentBurnTime > 0) {
            furnaceUpdated = true;
            this.updateFurnace(false);
         }
      }

      if (furnaceUpdated) {
         this.setChanged();
      }
   }

   @Override
   protected void updateFurnace(boolean forceLit) {
      if (this.worldObj != null) {
         BlockLogicFurnaceBlast.updateFurnaceBlockState(forceLit | this.currentBurnTime > 0, this.worldObj, this.x, this.y, this.z);
      } else if (this.carriedBlock != null) {
         this.carriedBlock.blockId = forceLit | this.currentBurnTime > 0 ? Blocks.FURNACE_BLAST_ACTIVE.id() : Blocks.FURNACE_BLAST_IDLE.id();
      }
   }

   private boolean canSmelt() {
      if (this.furnaceItemStacks[0] == null) {
         return false;
      } else {
         List<RecipeEntryBlastFurnace> list = Registries.RECIPES.getAllBlastFurnaceRecipes();
         ItemStack itemstack = null;

         for (RecipeEntryBlastFurnace recipeEntryBase : list) {
            if (recipeEntryBase != null && recipeEntryBase.matches(this.furnaceItemStacks[0])) {
               itemstack = recipeEntryBase.getOutput();
            }
         }

         if (itemstack == null) {
            return false;
         } else if (this.furnaceItemStacks[2] == null) {
            return true;
         } else if (!this.furnaceItemStacks[2].isItemEqual(itemstack)) {
            return false;
         } else {
            return this.furnaceItemStacks[2].stackSize < this.getMaxStackSize()
                  && this.furnaceItemStacks[2].stackSize < this.furnaceItemStacks[2].getMaxStackSize()
               ? true
               : this.furnaceItemStacks[2].stackSize < itemstack.getMaxStackSize();
         }
      }
   }

   @Override
   public void smeltItem() {
      if (this.canSmelt()) {
         List<RecipeEntryBlastFurnace> list = Registries.RECIPES.getAllBlastFurnaceRecipes();
         ItemStack itemstack = null;

         for (RecipeEntryBlastFurnace recipeEntryBase : list) {
            if (recipeEntryBase != null && recipeEntryBase.matches(this.furnaceItemStacks[0])) {
               itemstack = recipeEntryBase.getOutput();
            }
         }

         if (this.furnaceItemStacks[2] == null && itemstack != null) {
            this.furnaceItemStacks[2] = itemstack.copy();
         } else if (this.furnaceItemStacks[2] != null && itemstack != null && this.furnaceItemStacks[2].itemID == itemstack.itemID) {
            this.furnaceItemStacks[2].stackSize = this.furnaceItemStacks[2].stackSize + itemstack.stackSize;
         }

         this.furnaceItemStacks[0].stackSize--;
         if (this.furnaceItemStacks[0].stackSize <= 0) {
            this.furnaceItemStacks[0] = null;
         }
      }
   }

   private int getBurnTimeFromItem(ItemStack itemStack) {
      return itemStack == null ? 0 : LookupFuelFurnaceBlast.instance.getFuelYield(itemStack.getItem().id);
   }
}
