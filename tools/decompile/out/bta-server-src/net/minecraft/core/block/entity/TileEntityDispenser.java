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
import net.minecraft.core.item.IDispensable;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class TileEntityDispenser extends TileEntity implements Container {
   private ItemStack[] dispenserContents = new ItemStack[9];
   private final Random random = new Random();

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

   public ItemStack getRandomStackFromInventory() {
      int i = -1;
      int j = 1;

      for (int k = 0; k < this.dispenserContents.length; k++) {
         if (this.dispenserContents[k] != null && this.random.nextInt(j++) == 0) {
            i = k;
         }
      }

      if (i >= 0) {
         ItemStack stack = this.getItem(i);
         if (stack.getItem() instanceof IDispensable) {
            IDispensable dispensable = (IDispensable)stack.getItem();
            return dispensable.isRemovedOnDispense() ? this.removeItem(i, 1) : stack;
         } else {
            return this.removeItem(i, 1);
         }
      } else {
         return null;
      }
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
      return "container.dispenser.name";
   }

   @Override
   public void readFromNBT(CompoundTag nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      ListTag nbttaglist = nbttagcompound.getList("Items");
      this.dispenserContents = new ItemStack[this.getContainerSize()];

      for (int i = 0; i < nbttaglist.tagCount(); i++) {
         CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
         int j = nbttagcompound1.getByte("Slot") & 255;
         if (j < this.dispenserContents.length) {
            this.dispenserContents[j] = ItemStack.readItemStackFromNbt(nbttagcompound1);
         }
      }
   }

   @Override
   public void writeToNBT(CompoundTag nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      ListTag nbttaglist = new ListTag();

      for (int i = 0; i < this.dispenserContents.length; i++) {
         if (this.dispenserContents[i] != null) {
            CompoundTag nbttagcompound1 = new CompoundTag();
            nbttagcompound1.putByte("Slot", (byte)i);
            this.dispenserContents[i].writeToNBT(nbttagcompound1);
            nbttaglist.addTag(nbttagcompound1);
         }
      }

      nbttagcompound.put("Items", nbttaglist);
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
               EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.itemID, i1, itemstack.getMetadata()));
               float f3 = 0.05F;
               entityitem.xd = (float)this.random.nextGaussian() * f3;
               entityitem.yd = (float)this.random.nextGaussian() * f3 + 0.2F;
               entityitem.zd = (float)this.random.nextGaussian() * f3;
               world.entityJoinedWorld(entityitem);
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
