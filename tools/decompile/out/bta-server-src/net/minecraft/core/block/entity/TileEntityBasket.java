package net.minecraft.core.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;

public class TileEntityBasket extends TileEntity {
   private int numUnitsInside = 0;
   private final Map<TileEntityBasket.BasketEntry, Integer> contents = new HashMap<>();

   private void updateNumUnits() {
      int currentNumInside = this.calcNumUnitsInside();
      if (currentNumInside != this.numUnitsInside && this.worldObj != null) {
         this.numUnitsInside = currentNumInside;
         this.worldObj.notifyBlockChange(this.x, this.y, this.z, this.worldObj.getBlockId(this.x, this.y, this.z));
         this.setChanged();
      }
   }

   private int calcNumUnitsInside() {
      int numInside = 0;

      for (Entry<TileEntityBasket.BasketEntry, Integer> entry : this.contents.entrySet()) {
         TileEntityBasket.BasketEntry be = entry.getKey();
         int numItems = entry.getValue();
         int unitsPerItem = this.getItemSizeUnits(be.getItem());
         numInside += unitsPerItem * numItems;
      }

      return numInside;
   }

   private int getItemSizeUnits(Item item) {
      return 64 / item.getItemStackLimit(null);
   }

   private void dropItemStack(Random rand, ItemStack itemstack) {
      float f = rand.nextFloat() * 0.8F + 0.1F;
      float f1 = rand.nextFloat() * 0.8F + 0.1F;
      float f2 = rand.nextFloat() * 0.8F + 0.1F;
      World workingWorld;
      if (this.worldObj != null) {
         workingWorld = this.worldObj;
      } else {
         if (this.carriedBlock == null) {
            return;
         }

         workingWorld = this.carriedBlock.world;
      }

      EntityItem item = new EntityItem(workingWorld, this.x + f, this.y + f1, this.z + f2, itemstack);
      float f3 = 0.05F;
      item.xd = (float)rand.nextGaussian() * 0.05F;
      item.yd = (float)rand.nextGaussian() * 0.05F + 0.25F;
      item.zd = (float)rand.nextGaussian() * 0.05F;
      workingWorld.entityJoinedWorld(item);
   }

   public int getNumUnitsInside() {
      return this.numUnitsInside;
   }

   public int getMaxUnits() {
      return 1728;
   }

   public void givePlayerAllItems(World world, Player player) {
      List<TileEntityBasket.BasketEntry> toRemove = new ArrayList<>();

      for (Entry<TileEntityBasket.BasketEntry, Integer> entry : this.contents.entrySet()) {
         TileEntityBasket.BasketEntry basketEntry = entry.getKey();
         ItemStack basketEntryStack = new ItemStack(basketEntry.id, entry.getValue(), basketEntry.metadata, basketEntry.tag);
         player.inventory.insertItem(basketEntryStack, true);
         this.contents.put(basketEntry, basketEntryStack.stackSize);
         if (basketEntryStack.stackSize <= 0) {
            toRemove.add(basketEntry);
         }
      }

      for (TileEntityBasket.BasketEntry entryx : toRemove) {
         this.contents.remove(entryx);
      }

      this.updateNumUnits();
      world.notifyBlockChange(this.x, this.y, this.z, Blocks.BASKET.id());
   }

   @Override
   public void readFromNBT(CompoundTag tag) {
      super.readFromNBT(tag);
      ListTag itemsTag = tag.getList("Items");
      this.contents.clear();

      for (int i = 0; i < itemsTag.tagCount(); i++) {
         CompoundTag itemTag = (CompoundTag)itemsTag.tagAt(i);
         TileEntityBasket.BasketEntry entry = TileEntityBasket.BasketEntry.read(itemTag);
         int count = itemTag.getShort("Count");
         this.contents.put(entry, count);
      }

      this.numUnitsInside = this.calcNumUnitsInside();
   }

   @Override
   public void tick() {
      if (this.worldObj == null || !this.worldObj.isClientSide) {
         if (this.carriedBlock == null || !this.carriedBlock.world.isClientSide) {
            double posX;
            double posY;
            double posZ;
            World workingWorld;
            if (this.worldObj != null) {
               workingWorld = this.worldObj;
               posX = this.x;
               posY = this.y;
               posZ = this.z;
            } else {
               if (this.carriedBlock == null) {
                  return;
               }

               workingWorld = this.carriedBlock.world;
               posX = this.carriedBlock.holder.x - 0.5;
               posY = this.carriedBlock.holder.y - this.carriedBlock.holder.heightOffset;
               posZ = this.carriedBlock.holder.z - 0.5;
            }

            AABB aabb = AABB.getTemporaryBB(posX, posY, posZ, posX + 1.0, posY + 2.0, posZ + 1.0);
            List<EntityItem> entities = workingWorld.getEntitiesWithinAABB(EntityItem.class, aabb);
            boolean shouldUpdate = false;
            if (!entities.isEmpty()) {
               for (int i = 0; i < entities.size() && this.calcNumUnitsInside() < this.getMaxUnits(); i++) {
                  EntityItem e = entities.get(i);
                  if (e.item != null && e.item.stackSize > 0 && e.basketPickupDelay == 0) {
                     shouldUpdate = this.importItemStack(e.item);
                     if (e.item.stackSize <= 0) {
                        e.item.stackSize = 0;
                        e.outOfWorld();
                     }
                  }
               }
            }

            if (shouldUpdate) {
               if (this.worldObj != null) {
                  this.worldObj.notifyBlockChange(this.x, this.y, this.z, Blocks.BASKET.id());
               }

               this.updateNumUnits();
            }
         }
      }
   }

   private boolean importItemStack(ItemStack stack) {
      TileEntityBasket.BasketEntry entry = new TileEntityBasket.BasketEntry(stack.itemID, stack.getMetadata(), stack.getData());
      int sizeUnits = this.getItemSizeUnits(stack.getItem());
      int freeUnits = this.getMaxUnits() - this.numUnitsInside;
      int itemsToTake = Math.min(freeUnits / sizeUnits, stack.stackSize);
      if (itemsToTake <= 0) {
         return false;
      } else {
         stack.stackSize -= itemsToTake;
         int currentItemsInBE = this.contents.getOrDefault(entry, 0);
         currentItemsInBE += itemsToTake;
         this.contents.put(entry, currentItemsInBE);
         return true;
      }
   }

   @Override
   public void writeToNBT(CompoundTag tag) {
      super.writeToNBT(tag);
      ListTag itemsTag = new ListTag();

      for (Entry<TileEntityBasket.BasketEntry, Integer> entry : this.contents.entrySet()) {
         CompoundTag itemTag = new CompoundTag();
         itemTag.putShort("Count", (short)entry.getValue().intValue());
         TileEntityBasket.BasketEntry.write(itemTag, entry.getKey());
         itemsTag.addTag(itemTag);
      }

      tag.put("Items", itemsTag);
   }

   @Override
   public Packet getDescriptionPacket() {
      return this.numUnitsInside > 0 ? new PacketTileEntityData(this) : null;
   }

   @Override
   public void dropContents(World world, int x, int y, int z) {
      super.dropContents(world, x, y, z);
      Random rand = new Random();

      for (Entry<TileEntityBasket.BasketEntry, Integer> entry : this.contents.entrySet()) {
         TileEntityBasket.BasketEntry be = entry.getKey();
         int numItems = entry.getValue();

         while (numItems > 0) {
            int maxStackSize = be.getItem().getItemStackLimit(null);
            int stackSize = maxStackSize;
            int remainingItems = numItems - maxStackSize;
            if (remainingItems < 0) {
               stackSize = numItems;
            }

            numItems -= stackSize;
            this.dropItemStack(rand, new ItemStack(be.id, stackSize, be.metadata, be.tag));
         }
      }

      this.contents.clear();
      world.notifyBlockChange(x, y, z, Blocks.BASKET.id());
      this.updateNumUnits();
   }

   @Override
   public void heldTick(World world, Entity holder) {
      this.tick();
   }

   @Override
   public boolean canBeCarried(World world, Entity potentialHolder) {
      return true;
   }

   private static final class BasketEntry {
      public final int id;
      public final int metadata;
      public final CompoundTag tag;

      public BasketEntry(int id, int metadata, CompoundTag tag) {
         this.id = id;
         this.metadata = metadata;
         this.tag = tag;
      }

      public static TileEntityBasket.BasketEntry read(CompoundTag tag) {
         int id = tag.getShort("id");
         int damage = tag.getShort("Damage");
         CompoundTag data = tag.getCompound("Data");
         return new TileEntityBasket.BasketEntry(id, damage, data);
      }

      public static void write(CompoundTag tag, TileEntityBasket.BasketEntry entry) {
         tag.putShort("id", (short)entry.id);
         tag.putShort("Damage", (short)entry.metadata);
         tag.putCompound("Data", entry.tag);
      }

      public Item getItem() {
         return Item.itemsList[this.id];
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof TileEntityBasket.BasketEntry)) {
            return false;
         } else {
            TileEntityBasket.BasketEntry other = (TileEntityBasket.BasketEntry)obj;
            return this.id == other.id && this.metadata == other.metadata ? this.tag.getValues().size() <= 2 && other.tag.getValues().size() <= 2 : false;
         }
      }

      @Override
      public int hashCode() {
         return this.tag.getValues().size() <= 2 ? Objects.hash(this.id, this.metadata) : Objects.hash(this.id, this.metadata, this.tag);
      }
   }
}
