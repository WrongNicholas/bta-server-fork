package net.minecraft.core.item;

import com.mojang.nbt.tags.CompoundTag;
import java.util.Objects;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.save.conversion.ChunkConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemStack {
   public int stackSize;
   public int animationsToGo;
   public int itemID;
   private int metadata;
   @NotNull
   private CompoundTag tag;

   public ItemStack(@NotNull IItemConvertible item) {
      this(item, 1);
   }

   public ItemStack(@NotNull IItemConvertible item, int stackSize) {
      this(item, stackSize, 0);
   }

   public ItemStack(@NotNull IItemConvertible item, int stackSize, int metadata) {
      this(item.asItem(), stackSize, metadata);
   }

   public ItemStack(@NotNull Block<?> block) {
      this(block, 1);
   }

   public ItemStack(@NotNull Block<?> block, int stackSize) {
      this(block.id(), stackSize, 0);
   }

   public ItemStack(@NotNull Block<?> block, int stackSize, int metadata) {
      this(block.id(), stackSize, metadata);
   }

   public ItemStack(@NotNull Block<?> block, int stackSize, int metadata, @Nullable CompoundTag tag) {
      this(block.id(), stackSize, metadata, tag);
   }

   public ItemStack(@NotNull Item item) {
      this(item.id, 1, 0);
   }

   public ItemStack(@NotNull Item item, int stackSize) {
      this(item.id, stackSize, 0);
   }

   public ItemStack(@NotNull Item item, int stackSize, int metadata) {
      this(item.id, stackSize, metadata);
   }

   public ItemStack(@NotNull Item item, int stackSize, int metadata, @Nullable CompoundTag tag) {
      this(item.id, stackSize, metadata, tag);
   }

   public ItemStack(int itemID, int stackSize, int metadata) {
      this(itemID, stackSize, metadata, Item.itemsList[itemID].getDefaultTag());
   }

   public ItemStack(@NotNull ItemStack itemStack) {
      this.itemID = itemStack.itemID;
      this.metadata = itemStack.metadata;
      this.stackSize = itemStack.stackSize;
      this.tag = new CompoundTag(itemStack.tag);
      this.animationsToGo = 0;
   }

   public ItemStack(int itemID, int stackSize, int metadata, @Nullable CompoundTag tag) {
      this.itemID = itemID;
      this.stackSize = stackSize;
      this.metadata = metadata;
      if (tag == null) {
         tag = new CompoundTag();
      }

      this.tag = tag;
   }

   private ItemStack() {
      this.tag = new CompoundTag();
   }

   @NotNull
   public ItemStack splitStack(int i) {
      ItemStack itemStack = new ItemStack(this);
      itemStack.stackSize = i;
      this.stackSize -= i;
      return itemStack;
   }

   @NotNull
   public Item getItem() {
      return Objects.requireNonNull(Item.itemsList[this.itemID]);
   }

   public boolean useItem(@NotNull Player entityplayer, @NotNull World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced) {
      boolean flag = this.getItem().onUseItemOnBlock(this, entityplayer, world, blockX, blockY, blockZ, side, xPlaced, yPlaced);
      if (flag) {
         entityplayer.addStat(this.getItem().getStat("stat_used"), 1);
      }

      return flag;
   }

   public float getStrVsBlock(@NotNull Block<?> block) {
      return this.getItem().getStrVsBlock(this, block);
   }

   public ItemStack useItemRightClick(@NotNull World world, @NotNull Player entityplayer) {
      return this.getItem().onUseItem(this, world, entityplayer);
   }

   @NotNull
   public CompoundTag writeToNBT(@NotNull CompoundTag nbt) {
      nbt.putShort("id", (short)this.itemID);
      nbt.putByte("Count", (byte)this.stackSize);
      nbt.putShort("Damage", (short)this.metadata);
      nbt.putByte("Expanded", (byte)1);
      nbt.putInt("Version", 19134);
      if (!this.tag.getValue().isEmpty()) {
         nbt.putCompound("Data", this.tag);
      }

      return nbt;
   }

   public void readFromNBT(@NotNull CompoundTag nbt) {
      this.itemID = nbt.getShort("id");
      this.stackSize = nbt.getByte("Count");
      this.metadata = nbt.getShort("Damage");
      byte expanded = nbt.getByte("Expanded");
      int version = nbt.getInteger("Version");
      if (expanded == 0 && this.itemID >= 256) {
         this.itemID = this.itemID + (Blocks.blocksList.length - 256);
      }

      if (version < 19133 && this.itemID < Blocks.blocksList.length) {
         short[] id = new short[]{(short)this.itemID};
         byte[] meta = new byte[]{(byte)this.metadata};
         ChunkConverter.converters[0].convertBlocksAndMetadata(id, meta);
         this.itemID = id[0];
         this.metadata = meta[0];
      }

      this.tag = nbt.getCompound("Data");
      if (this.tag == null) {
         this.tag = Item.itemsList[this.itemID].getDefaultTag();
      }

      if (this.tag.containsKey("color") && !this.tag.getBoolean("overrideColor")) {
         this.tag.getValue().remove("color");
      }

      if (this.tag.containsKey("name") && !this.tag.getBoolean("overrideName")) {
         this.tag.getValue().remove("name");
      }

      if (this.tag.getBoolean("overrideColor") && !this.tag.containsKey("color")) {
         System.err.println("Item has override color tag but no custom color!");
         Thread.dumpStack();
         this.tag.getValue().remove("overrideColor");
      }

      if (this.tag.getBoolean("overrideName") && (!this.tag.containsKey("name") || this.tag.getString("name").isEmpty())) {
         System.err.println("Item has override name tag but no custom name!");
         Thread.dumpStack();
         this.tag.getValue().remove("overrideName");
      }

      if (Item.itemsList[this.itemID] == null) {
         this.itemID = 0;
         this.stackSize = 0;
      }

      if (this.itemID == Blocks.CHEST_LEGACY.id()) {
         this.itemID = Blocks.CHEST_PLANKS_OAK.id();
      }

      if (this.itemID == Blocks.CHEST_LEGACY_PAINTED.id()) {
         this.itemID = Blocks.CHEST_PLANKS_OAK_PAINTED.id();
      }
   }

   public int getMaxStackSize() {
      return this.getItem().getItemStackLimit(this);
   }

   public int getMaxStackSize(Container inv) {
      return this.getMaxStackSize();
   }

   public boolean isStackable() {
      return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
   }

   public boolean isItemStackDamageable() {
      return Item.itemsList[this.itemID].getMaxDamageForStack(this) > 0;
   }

   @Deprecated
   public boolean getHasSubtypes() {
      return Item.itemsList[this.itemID].getHasSubtypes();
   }

   public boolean isItemDamaged() {
      return this.isItemStackDamageable() && this.metadata > 0;
   }

   public int getItemDamageForDisplay() {
      return this.metadata;
   }

   public int getMetadata() {
      return this.metadata;
   }

   public void setMetadata(int i) {
      this.metadata = i;
   }

   public int getMaxDamage() {
      return Item.itemsList[this.itemID].getMaxDamageForStack(this);
   }

   public void damageItem(int i, @Nullable Entity entity) {
      if (!(entity instanceof Player) || ((Player)entity).getGamemode().toolDurability()) {
         if (this.isItemStackDamageable()) {
            this.metadata += i;
            if (this.metadata > this.getMaxDamage()) {
               if (entity instanceof Player) {
                  ((Player)entity).addStat(this.getItem().getStat("stat_broken"), 1);
               }

               this.stackSize--;
               if (this.stackSize < 0) {
                  this.stackSize = 0;
               }

               this.metadata = 0;
            }
         }
      }
   }

   public void repairItem(int i) {
      if (this.isItemStackDamageable()) {
         if (this.metadata <= this.getMaxDamage() && this.metadata >= 0) {
            this.metadata -= i;
         }
      }
   }

   public void hitEntity(Mob target, Player attacker) {
      boolean flag = Item.itemsList[this.itemID].hitEntity(this, target, attacker);
      if (flag) {
         attacker.addStat(this.getItem().getStat("stat_used"), 1);
      }
   }

   public boolean beforeDestroyBlock(World world, int id, int x, int y, int z, Side side, Player player) {
      return this.getItem().beforeDestroyBlock(world, this, id, x, y, z, side, player);
   }

   public void onDestroyBlock(World world, int id, int x, int y, int z, Side side, Player entityplayer) {
      boolean flag = Item.itemsList[this.itemID].onBlockDestroyed(world, this, id, x, y, z, side, entityplayer);
      if (flag) {
         entityplayer.addStat(this.getItem().getStat("stat_used"), 1);
      }
   }

   public boolean consumeItem(@Nullable Player entityplayer) {
      if (this.stackSize <= 0) {
         return false;
      } else {
         if (entityplayer == null || entityplayer.getGamemode().consumeBlocks()) {
            this.stackSize--;
         }

         return true;
      }
   }

   public int getDamageVsEntity(Entity entity) {
      return Item.itemsList[this.itemID].getDamageVsEntity(entity, this);
   }

   public boolean canHarvestBlock(Mob mob, Block<?> block) {
      return Item.itemsList[this.itemID].canHarvestBlock(mob, this, block);
   }

   public boolean useItemOnEntity(Mob mob, Player entityPlayer) {
      return Item.itemsList[this.itemID].useItemOnEntity(this, mob, entityPlayer);
   }

   @NotNull
   public ItemStack copy() {
      return new ItemStack(this.itemID, this.stackSize, this.metadata, new CompoundTag(this.tag));
   }

   public static boolean areItemStacksEqual(@Nullable ItemStack stack1, @Nullable ItemStack stack2) {
      if (stack1 == null && stack2 == null) {
         return true;
      } else {
         return stack1 != null && stack2 != null ? stack1.isItemStackEqual(stack2) : false;
      }
   }

   private boolean isItemStackEqual(@NotNull ItemStack itemstack) {
      if (this.stackSize != itemstack.stackSize) {
         return false;
      } else {
         return this.itemID != itemstack.itemID ? false : this.metadata == itemstack.metadata;
      }
   }

   public boolean isItemEqual(@NotNull ItemStack itemstack) {
      return this.itemID == itemstack.itemID && this.metadata == itemstack.metadata;
   }

   public String getItemKey() {
      return Item.itemsList[this.itemID].getLanguageKey(this);
   }

   public String getItemDescription() {
      return Item.itemsList[this.itemID].getTranslatedDescription(this);
   }

   public static ItemStack copyItemStack(@Nullable ItemStack itemstack) {
      return itemstack != null ? itemstack.copy() : null;
   }

   @Override
   public String toString() {
      return this.stackSize + " * " + Item.itemsList[this.itemID].getKey() + ":" + this.metadata;
   }

   public void updateAnimation(@NotNull World world, Entity entity, int slotId, boolean flag) {
      if (this.animationsToGo > 0) {
         this.animationsToGo--;
      }

      Item.itemsList[this.itemID].inventoryTick(this, world, entity, slotId, flag);
   }

   public void onCrafting(@NotNull World world, @NotNull Player entityplayer) {
      entityplayer.addStat(this.getItem().getStat("stat_crafted"), this.stackSize);
      Item.itemsList[this.itemID].onCraftedBy(this, world, entityplayer);
   }

   public boolean isStackEqual(@NotNull ItemStack itemstack) {
      return this.itemID == itemstack.itemID && this.stackSize == itemstack.stackSize && this.metadata == itemstack.metadata;
   }

   public boolean canStackWith(@Nullable ItemStack itemStack) {
      if (itemStack == null) {
         return true;
      } else if (this.itemID == itemStack.itemID && this.metadata == itemStack.metadata) {
         CompoundTag nbt1 = this.tag;
         CompoundTag nbt2 = itemStack.tag;
         return nbt1.equals(nbt2);
      } else {
         return false;
      }
   }

   public boolean canItemBeRenamed() {
      return this.getMaxStackSize() == 1 && this.isItemStackDamageable();
   }

   public static ItemStack readItemStackFromNbt(@Nullable CompoundTag nbt) {
      if (nbt == null) {
         return null;
      } else {
         ItemStack stack = new ItemStack();
         stack.readFromNBT(nbt);
         return stack.stackSize <= 0 ? null : stack;
      }
   }

   public boolean hasCustomName() {
      return this.tag.getBoolean("overrideName");
   }

   public boolean hasCustomColor() {
      return this.tag.getBoolean("overrideColor");
   }

   public String getCustomName() {
      return this.tag.getBoolean("overrideName") ? this.tag.getString("name") : null;
   }

   public byte getCustomColor() {
      return this.tag.getBoolean("overrideColor") ? this.tag.getByte("color") : -1;
   }

   public String getDisplayName() {
      return this.tag.getBoolean("overrideName") ? TextFormatting.ITALIC + this.tag.getString("name") : this.getItem().getTranslatedName(this);
   }

   public byte getDisplayColor() {
      return this.tag.getBoolean("overrideColor") ? this.tag.getByte("color") : 0;
   }

   public void setCustomName(@Nullable String name) {
      if (name != null && !name.isEmpty()) {
         this.tag.putBoolean("overrideName", true);
         this.tag.putString("name", name);
      } else {
         this.removeCustomName();
      }
   }

   public void setCustomColor(byte color) {
      if (color < 0) {
         this.removeCustomColor();
      } else {
         this.tag.putBoolean("overrideColor", true);
         this.tag.putByte("color", color);
      }
   }

   public void removeCustomName() {
      this.tag.getValue().remove("overrideName");
      this.tag.getValue().remove("name");
   }

   public void removeCustomColor() {
      this.tag.getValue().remove("overrideColor");
      this.tag.getValue().remove("color");
   }

   @NotNull
   public CompoundTag getData() {
      return this.tag;
   }

   public void setData(@Nullable CompoundTag tag) {
      if (tag == null) {
         tag = new CompoundTag();
      }

      this.tag = tag;
   }
}
