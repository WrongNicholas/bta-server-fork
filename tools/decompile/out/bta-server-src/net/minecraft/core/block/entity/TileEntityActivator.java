package net.minecraft.core.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.motion.CarriedBlock;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class TileEntityActivator extends TileEntity implements Container {
   public static final int CONTAINER_SIZE = 9;
   private ItemStack[] dispenserContents;
   private final Random random;
   public short lockedSlotBitSet;
   public int stackSelector = 0;

   public TileEntityActivator() {
      this.dispenserContents = new ItemStack[9];
      this.random = new Random();
   }

   @Override
   public int getContainerSize() {
      return 9;
   }

   @Nullable
   @Override
   public ItemStack getItem(int index) {
      return this.dispenserContents[index];
   }

   @Nullable
   @Override
   public ItemStack removeItem(int index, int takeAmount) {
      if (this.dispenserContents[index] != null) {
         if (this.dispenserContents[index].stackSize <= takeAmount) {
            ItemStack itemstack = this.dispenserContents[index];
            this.dispenserContents[index] = null;
            this.setChanged();
            return itemstack;
         } else {
            ItemStack itemstack1 = this.dispenserContents[index].splitStack(takeAmount);
            if (this.dispenserContents[index].stackSize <= 0) {
               this.dispenserContents[index] = null;
            }

            this.setChanged();
            return itemstack1;
         }
      } else {
         return null;
      }
   }

   public ItemStack getNextStack() {
      if (this.locked(this.stackSelector)) {
         this.shiftSelector();
         return null;
      } else {
         int itemIndex = this.stackSelector;
         this.shiftSelector();
         return this.getItem(itemIndex);
      }
   }

   public void shiftSelector() {
      this.stackSelector = (this.stackSelector + 1) % 9;
      int i = 0;

      while (this.locked(this.stackSelector)) {
         i++;
         this.stackSelector = (this.stackSelector + 1) % 9;
         if (i >= 9) {
            this.stackSelector = 0;
            break;
         }
      }
   }

   public void nullDeadItems() {
      for (int i = 0; i < this.dispenserContents.length; i++) {
         if (this.dispenserContents[i] != null && this.dispenserContents[i].stackSize <= 0) {
            this.dispenserContents[i] = null;
         }
      }
   }

   public void lockSlot(int slot, boolean flag) {
      assert slot >= 0 && slot < 9 : "Target slot to lock is outside the container bounds";

      int mask = 1 << slot;
      if (flag) {
         this.lockedSlotBitSet |= (short)mask;
      } else {
         this.lockedSlotBitSet &= (short)(~mask);
      }

      if (this.locked(this.stackSelector)) {
         this.shiftSelector();
      }
   }

   @Override
   public boolean locked(int slot) {
      assert slot >= 0 && slot < 9 : "Target slot to lock is outside the container bounds";

      int mask = 1 << slot;
      return (this.lockedSlotBitSet & mask) != 0;
   }

   @Override
   public void setItem(int index, @Nullable ItemStack itemstack) {
      this.dispenserContents[index] = itemstack;
      if (itemstack != null && itemstack.stackSize > this.getMaxStackSize()) {
         itemstack.stackSize = this.getMaxStackSize();
      }

      this.setChanged();
   }

   @Override
   public String getNameTranslationKey() {
      return "container.activator.name";
   }

   @Override
   public void readFromNBT(CompoundTag compoundTag) {
      super.readFromNBT(compoundTag);
      this.stackSelector = compoundTag.getByte("Selector");
      this.lockedSlotBitSet = compoundTag.getShort("LockedSlots");
      ListTag itemListTag = compoundTag.getList("Items");
      this.dispenserContents = new ItemStack[this.getContainerSize()];

      for (int i = 0; i < itemListTag.tagCount(); i++) {
         CompoundTag slotTag = (CompoundTag)itemListTag.tagAt(i);
         int j = slotTag.getByte("Slot") & 255;
         if (j < this.dispenserContents.length) {
            this.dispenserContents[j] = ItemStack.readItemStackFromNbt(slotTag);
         }
      }
   }

   @Override
   public void writeToNBT(CompoundTag compoundTag) {
      super.writeToNBT(compoundTag);
      compoundTag.putByte("Selector", (byte)this.stackSelector);
      compoundTag.putShort("LockedSlots", this.lockedSlotBitSet);
      ListTag itemListTag = new ListTag();

      for (int i = 0; i < this.dispenserContents.length; i++) {
         if (this.dispenserContents[i] != null) {
            CompoundTag slotTag = new CompoundTag();
            slotTag.putByte("Slot", (byte)i);
            this.dispenserContents[i].writeToNBT(slotTag);
            itemListTag.addTag(slotTag);
         }
      }

      compoundTag.put("Items", itemListTag);
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   @Override
   public boolean stillValid(Player entityplayer) {
      return this.worldObj != null && this.worldObj.getTileEntity(this.x, this.y, this.z) == this
         ? entityplayer.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0
         : false;
   }

   @Override
   public void sortContainer() {
   }

   @Override
   public void dropContents(World world, int x, int y, int z) {
      super.dropContents(world, x, y, z);

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
               EntityItem item = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.itemID, i1, itemstack.getMetadata()));
               float f3 = 0.05F;
               item.xd = (float)this.random.nextGaussian() * f3;
               item.yd = (float)this.random.nextGaussian() * f3 + 0.2F;
               item.zd = (float)this.random.nextGaussian() * f3;
               world.entityJoinedWorld(item);
            }
         }
      }
   }

   @Override
   public boolean canBeCarried(World world, Entity potentialHolder) {
      return true;
   }

   @Override
   public CarriedBlock getCarriedEntry(World world, Entity holder, Block<?> currentBlock, int currentMeta) {
      return super.getCarriedEntry(world, holder, currentBlock, BlockLogicRotatable.setDirection(currentMeta, Direction.NORTH));
   }
}
