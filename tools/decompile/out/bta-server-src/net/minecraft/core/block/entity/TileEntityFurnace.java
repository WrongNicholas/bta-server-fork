package net.minecraft.core.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import java.util.List;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicFurnace;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.motion.CarriedBlock;
import net.minecraft.core.crafting.LookupFuelFurnace;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class TileEntityFurnace extends TileEntity implements Container {
   private final Random random = new Random();
   protected ItemStack[] furnaceItemStacks = new ItemStack[3];
   public int maxBurnTime;
   public int currentCookTime;
   public int maxCookTime;
   public int currentBurnTime = 0;

   public TileEntityFurnace() {
      this.maxBurnTime = 0;
      this.currentCookTime = 0;
      this.maxCookTime = 200;
   }

   @Override
   public int getContainerSize() {
      return this.furnaceItemStacks.length;
   }

   @Nullable
   @Override
   public ItemStack getItem(int index) {
      return this.furnaceItemStacks[index];
   }

   @Nullable
   @Override
   public ItemStack removeItem(int index, int takeAmount) {
      if (this.furnaceItemStacks[index] != null) {
         if (this.furnaceItemStacks[index].stackSize <= takeAmount) {
            ItemStack itemstack = this.furnaceItemStacks[index];
            this.furnaceItemStacks[index] = null;
            if (this.worldObj != null && index == 2) {
               this.worldObj.markBlockNeedsUpdate(this.x, this.y, this.z);
            }

            return itemstack;
         } else {
            ItemStack itemstack1 = this.furnaceItemStacks[index].splitStack(takeAmount);
            if (this.furnaceItemStacks[index].stackSize <= 0) {
               this.furnaceItemStacks[index] = null;
               if (this.worldObj != null && index == 2) {
                  this.worldObj.markBlockNeedsUpdate(this.x, this.y, this.z);
               }
            }

            return itemstack1;
         }
      } else {
         return null;
      }
   }

   @Override
   public void setItem(int index, @Nullable ItemStack itemstack) {
      this.furnaceItemStacks[index] = itemstack;
      if (itemstack != null && itemstack.stackSize > this.getMaxStackSize()) {
         itemstack.stackSize = this.getMaxStackSize();
      }

      if (this.worldObj != null && index == 2 && itemstack == null) {
         this.worldObj.markBlockNeedsUpdate(this.x, this.y, this.z);
      }
   }

   @Override
   public String getNameTranslationKey() {
      return "container.furnace.name";
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
            CompoundTag nbttagcompound1 = new CompoundTag();
            nbttagcompound1.putByte("Slot", (byte)i);
            this.furnaceItemStacks[i].writeToNBT(nbttagcompound1);
            nbttaglist.addTag(nbttagcompound1);
         }
      }

      nbttagcompound.put("Items", nbttaglist);
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   public int getCookProgressScaled(int i) {
      return this.maxCookTime == 0 ? 0 : this.currentCookTime * i / this.maxCookTime;
   }

   public int getBurnTimeRemainingScaled(int i) {
      return this.maxBurnTime == 0 ? 0 : this.currentBurnTime * i / this.maxBurnTime;
   }

   public boolean isBurning() {
      return this.currentBurnTime > 0;
   }

   @Override
   public void tick() {
      boolean isBurnTimeHigherThan0 = this.currentBurnTime > 0;
      boolean furnaceUpdated = false;
      if (this.currentBurnTime > 0) {
         this.currentBurnTime--;
      }

      if (this.worldObj == null || !this.worldObj.isClientSide) {
         if ((this.worldObj == null || this.worldObj.getBlockId(this.x, this.y, this.z) == Blocks.FURNACE_STONE_IDLE.id())
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

         if (this.currentBurnTime == 0 && this.furnaceItemStacks[1] != null && this.canSmelt()) {
            this.maxBurnTime = this.currentBurnTime = this.getBurnTimeFromItem(this.furnaceItemStacks[1]);
            if (this.currentBurnTime > 0) {
               furnaceUpdated = true;
               if (this.furnaceItemStacks[1] != null) {
                  if (this.furnaceItemStacks[1].getItem() == Items.BUCKET_LAVA) {
                     this.furnaceItemStacks[1] = new ItemStack(Items.BUCKET);
                  } else {
                     this.furnaceItemStacks[1].stackSize--;
                     if (this.furnaceItemStacks[1].stackSize <= 0) {
                        this.furnaceItemStacks[1] = null;
                     }
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

   private boolean canSmelt() {
      if (this.furnaceItemStacks[0] == null) {
         return false;
      } else {
         List<RecipeEntryFurnace> list = Registries.RECIPES.getAllFurnaceRecipes();
         ItemStack itemstack = null;

         for (RecipeEntryFurnace recipeEntryBase : list) {
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

   public void smeltItem() {
      if (this.canSmelt()) {
         List<RecipeEntryFurnace> list = Registries.RECIPES.getAllFurnaceRecipes();
         ItemStack itemstack = null;

         for (RecipeEntryFurnace recipeEntryBase : list) {
            if (recipeEntryBase != null && recipeEntryBase.matches(this.furnaceItemStacks[0])) {
               itemstack = recipeEntryBase.getOutput();
            }
         }

         boolean wasEmpty = this.furnaceItemStacks[2] == null;
         if (this.furnaceItemStacks[2] == null && itemstack != null) {
            this.furnaceItemStacks[2] = itemstack.copy();
         } else if (this.furnaceItemStacks[2] != null && itemstack != null && this.furnaceItemStacks[2].itemID == itemstack.itemID) {
            this.furnaceItemStacks[2].stackSize = this.furnaceItemStacks[2].stackSize + itemstack.stackSize;
         }

         this.furnaceItemStacks[0].stackSize--;
         if (this.furnaceItemStacks[0].stackSize <= 0) {
            this.furnaceItemStacks[0] = null;
         }

         if (this.worldObj != null && wasEmpty && this.furnaceItemStacks[2] != null) {
            this.worldObj.markBlockNeedsUpdate(this.x, this.y, this.z);
         }
      }
   }

   protected void updateFurnace(boolean forceLit) {
      if (this.worldObj != null) {
         BlockLogicFurnace.updateFurnaceBlockState(forceLit | this.currentBurnTime > 0, this.worldObj, this.x, this.y, this.z);
      } else if (this.carriedBlock != null) {
         this.carriedBlock.blockId = forceLit | this.currentBurnTime > 0 ? Blocks.FURNACE_STONE_ACTIVE.id() : Blocks.FURNACE_STONE_IDLE.id();
      }
   }

   private int getBurnTimeFromItem(ItemStack itemStack) {
      return itemStack == null ? 0 : LookupFuelFurnace.instance.getFuelYield(itemStack.getItem().id);
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return this.worldObj != null && this.worldObj.getTileEntity(this.x, this.y, this.z) == this
         ? entityplayer.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0
         : false;
   }

   @Override
   public void dropContents(World world, int x, int y, int z) {
      super.dropContents(world, x, y, z);
      if (!BlockLogicFurnace.keepFurnaceInventory) {
         for (int l = 0; l < this.getContainerSize(); l++) {
            ItemStack itemstack = this.getItem(l);
            if (itemstack != null) {
               float f = this.random.nextFloat() * 0.8F + 0.1F;
               float f1 = this.random.nextFloat() * 0.8F + 0.1F;
               float f2 = this.random.nextFloat() * 0.8F + 0.1F;

               while (itemstack.stackSize > 0) {
                  int i1 = this.random.nextInt(21) + 10;
                  if (i1 > itemstack.stackSize) {
                     i1 = itemstack.stackSize;
                  }

                  itemstack.stackSize -= i1;
                  EntityItem entityItem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.itemID, i1, itemstack.getMetadata()));
                  float f3 = 0.05F;
                  entityItem.xd = (float)this.random.nextGaussian() * f3;
                  entityItem.yd = (float)this.random.nextGaussian() * f3 + 0.2F;
                  entityItem.zd = (float)this.random.nextGaussian() * f3;
                  world.entityJoinedWorld(entityItem);
               }
            }
         }
      }
   }

   @Override
   public Packet getDescriptionPacket() {
      return this.furnaceItemStacks[2] != null ? new PacketTileEntityData(this) : null;
   }

   @Override
   public void sortContainer() {
   }

   @Override
   public void heldTick(World world, Entity holder) {
      this.tick();
   }

   @Override
   public boolean tryPlace(World world, Entity holder, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced) {
      boolean success = super.tryPlace(world, holder, blockX, blockY, blockZ, side, xPlaced, yPlaced);
      if (success) {
         this.updateFurnace(false);
      }

      return success;
   }

   @Override
   public boolean canBeCarried(World world, Entity potentialHolder) {
      return true;
   }

   @Override
   public CarriedBlock getCarriedEntry(World world, Entity holder, Block<?> currentBlock, int currentMeta) {
      return super.getCarriedEntry(world, holder, currentBlock, currentMeta & -8 | 2);
   }
}
