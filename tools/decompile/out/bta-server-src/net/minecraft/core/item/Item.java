package net.minecraft.core.item;

import com.mojang.nbt.tags.CompoundTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.achievement.stat.Stat;
import net.minecraft.core.achievement.stat.StatItem;
import net.minecraft.core.achievement.stat.StatList;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.data.tag.ITaggable;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Item implements ITaggable<Item>, IItemConvertible {
   protected static final Random itemRand = new Random();
   public static final Map<String, Integer> nameToIdMap = new HashMap<>();
   @Nullable
   public static final Item @NotNull [] itemsList = new Item[32768];
   public static final Map<NamespaceID, Item> itemsMap = new LinkedHashMap<>();
   public static int highestItemId = 0;
   public final int id;
   public final NamespaceID namespaceID;
   protected int maxStackSize;
   private int maxDamage;
   protected boolean hasSubtypes;
   private Item containerItem;
   @Nullable
   private Supplier<@NotNull IItemConvertible> statParent = null;
   private String key;
   protected static final Map<String, List<Stat>> statsInCategory = new HashMap<>();
   protected final Map<String, Stat> stats = new HashMap<>();

   public Item(NamespaceID namespaceId, int id) {
      this.maxStackSize = 64;
      this.maxDamage = 0;
      this.hasSubtypes = false;
      this.containerItem = null;
      this.id = id;
      this.namespaceID = namespaceId.makePermanent();
      if (itemsList[id] != null) {
         throw new IllegalArgumentException("Item slot '" + id + "' is already occupied by '" + itemsList[id] + "' when adding " + this);
      } else if (itemsMap.containsKey(this.namespaceID)) {
         throw new IllegalArgumentException(
            "Item id '" + this.namespaceID + "' is already used by '" + itemsMap.get(this.namespaceID) + "' when adding " + this
         );
      } else {
         itemsList[this.id] = this;
         itemsMap.put(this.namespaceID, this);
         if (this.id > highestItemId) {
            highestItemId = this.id;
         }
      }
   }

   public Item(String translationKey, String namespaceId, int id) {
      this(makeNamespaceID(namespaceId), id);
      this.setKey(translationKey);
   }

   private static NamespaceID makeNamespaceID(String string) {
      try {
         return NamespaceID.getPermanent(string);
      } catch (HardIllegalArgumentException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   public Stat createStat(String statID, String nameKey) {
      if (this.getStat(statID) != null) {
         return this.getStat(statID);
      } else {
         Stat stat;
         if (StatList.replacementMap.containsKey(this.id)) {
            Item replaceItem = getItem(StatList.replacementMap.get(this.id));
            stat = replaceItem.getStat(statID);
            if (stat == null) {
               stat = replaceItem.createStat(statID, nameKey);
            }
         } else {
            stat = new StatItem(NamespaceID.getPermanent(this.namespaceID.namespace(), this.namespaceID.value() + "_" + statID), nameKey, this.id)
               .registerStat();
         }

         this.putStat(statID, stat);
         return stat;
      }
   }

   public void putStat(String statID, Stat stat) {
      this.stats.put(statID, stat);
      statsInCategory.putIfAbsent(statID, new ArrayList<>());
      if (!statsInCategory.get(statID).contains(stat)) {
         statsInCategory.get(statID).add(stat);
      }
   }

   @Nullable
   public Stat getStat(String statID) {
      return this.stats.get(statID);
   }

   public Item setStatParent(@NotNull Supplier<IItemConvertible> icon) {
      this.statParent = icon;
      return this;
   }

   @Nullable
   public Item getStatParent() {
      return this.statParent != null ? this.statParent.get().asItem() : null;
   }

   @Nullable
   public static List<Stat> getStats(String statID) {
      return statsInCategory.get(statID);
   }

   public Item withTags(Tag<Item>... tags) {
      for (Tag<Item> tag : tags) {
         tag.tag(this);
      }

      return this;
   }

   public boolean hasTag(Tag<Item> tag) {
      return tag.appliesTo(this);
   }

   public Item setMaxStackSize(int maxStackSize) {
      this.maxStackSize = maxStackSize;
      return this;
   }

   public boolean onUseItemOnBlock(
      ItemStack itemstack, Player entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      return false;
   }

   public void onUseByActivator(
      ItemStack itemStack,
      TileEntityActivator activatorBlock,
      World world,
      Random random,
      int blockX,
      int blockY,
      int blockZ,
      double offX,
      double offY,
      double offZ,
      Direction direction
   ) {
   }

   public float getStrVsBlock(ItemStack itemstack, Block<?> block) {
      return 1.0F;
   }

   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      return itemstack;
   }

   public int getItemStackLimit(@Nullable ItemStack itemStack) {
      return this.maxStackSize;
   }

   public boolean getHasSubtypes() {
      return this.hasSubtypes;
   }

   protected Item setHasSubtypes(boolean hasSubtypes) {
      this.hasSubtypes = hasSubtypes;
      return this;
   }

   public int getMaxDamage() {
      return this.getMaxDamageForStack(null);
   }

   public int getMaxDamageForStack(@Nullable ItemStack itemStack) {
      return this.maxDamage;
   }

   protected Item setMaxDamage(int i) {
      this.maxDamage = i;
      return this;
   }

   public boolean isDamagable() {
      return this.maxDamage > 0 && !this.hasSubtypes;
   }

   public boolean hitEntity(ItemStack itemstack, Mob target, Mob attacker) {
      return false;
   }

   public boolean beforeDestroyBlock(World world, ItemStack itemStack, int blockId, int x, int y, int z, Side side, Player player) {
      return true;
   }

   public boolean onBlockDestroyed(World world, ItemStack itemstack, int removedBlockId, int x, int y, int z, Side side, Mob mob) {
      return false;
   }

   public int getDamageVsEntity(Entity entity, ItemStack itemStack) {
      return 1;
   }

   public boolean canHarvestBlock(Mob mob, ItemStack itemStack, Block<?> block) {
      return false;
   }

   public boolean useItemOnEntity(ItemStack itemstack, Mob mob, Player player) {
      return false;
   }

   public Item setKey(String s) {
      this.key = "item." + s;
      nameToIdMap.put(this.key, this.id);
      return this;
   }

   public String getKey() {
      return this.key;
   }

   public String getLanguageKey(ItemStack itemstack) {
      return this.key;
   }

   public String getTranslatedName(ItemStack itemstack) {
      return I18n.getInstance().translateKey(itemstack.getItemKey() + ".name");
   }

   public String getTranslatedDescription(ItemStack itemstack) {
      return I18n.getInstance().translateKey(itemstack.getItemKey() + ".desc");
   }

   public CompoundTag getDefaultTag() {
      return new CompoundTag();
   }

   public Item setContainerItem(Item item) {
      if (this.maxStackSize > 1) {
         throw new IllegalArgumentException("Max stack size must be 1 for items with crafting results");
      } else {
         this.containerItem = item;
         return this;
      }
   }

   public Item getContainerItem() {
      return this.containerItem;
   }

   public boolean hasContainerItem() {
      return this.containerItem != null;
   }

   public String getStatName() {
      return I18n.getInstance().translateNameKey(this.getKey());
   }

   public void inventoryTick(ItemStack itemstack, World world, Entity entity, int slotId, boolean flag) {
   }

   public void onCraftedBy(ItemStack itemstack, World world, Player entityplayer) {
   }

   public boolean isSilkTouch() {
      return this.hasTag(ItemTags.IS_SILK_TOUCH);
   }

   public boolean hasInventoryInteraction() {
      return false;
   }

   public ItemStack onInventoryInteract(Player player, Slot slot, ItemStack stackInSlot, boolean isItemGrabbed) {
      return stackInSlot;
   }

   public boolean showFullDurability() {
      return false;
   }

   @Override
   public boolean isIn(Tag<Item> tag) {
      return tag.appliesTo(this);
   }

   @Override
   public Item asItem() {
      return this;
   }

   @Override
   public ItemStack getDefaultStack() {
      return new ItemStack(this);
   }

   public static boolean hasTag(ItemStack itemStack, Tag<Item> tag) {
      return itemStack == null ? false : tag.appliesTo(itemsList[itemStack.itemID]);
   }

   @Nullable
   public static Item getItem(int id) {
      return itemsList[Math.max(id, 0)];
   }
}
