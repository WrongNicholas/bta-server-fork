package net.minecraft.core;

import java.util.Random;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WeightedRandomLootObject {
   @Nullable
   private final ItemStack itemStack;
   private int yieldMin;
   private int yieldMax;
   private int yieldFixed;
   private final boolean isRandomYield;
   private int metaMin;
   private int metaMax;
   private boolean isRandomMeta;
   private final Random random = new Random();

   public WeightedRandomLootObject(@Nullable ItemStack itemStack, int yieldMin, int yieldMax) {
      this.itemStack = itemStack;
      this.yieldMin = yieldMin;
      this.yieldMax = yieldMax;
      this.isRandomYield = true;
      this.isRandomMeta = false;
   }

   public WeightedRandomLootObject(@Nullable ItemStack itemStack, int yieldFixed) {
      this.itemStack = itemStack;
      this.yieldFixed = yieldFixed;
      this.isRandomYield = false;
      this.isRandomMeta = false;
   }

   public WeightedRandomLootObject(@Nullable ItemStack itemStack) {
      this.itemStack = itemStack;
      this.yieldFixed = 1;
      this.isRandomYield = false;
      this.isRandomMeta = false;
   }

   public WeightedRandomLootObject setRandomMetadata(int metaMin, int metaMax) {
      this.metaMin = metaMin;
      this.metaMax = metaMax;
      this.isRandomMeta = true;
      return this;
   }

   public ItemStack getDefinedItemStack() {
      return this.itemStack;
   }

   public int getMinYield() {
      return this.yieldMin;
   }

   public int getMaxYield() {
      return this.yieldMax;
   }

   public int getFixedYield() {
      return this.yieldFixed;
   }

   public boolean isRandomYield() {
      return this.isRandomYield;
   }

   public int getMinMeta() {
      return this.metaMin;
   }

   public int getMaxMeta() {
      return this.metaMax;
   }

   public int getFixedMeta() {
      return this.itemStack.getMetadata();
   }

   public boolean isRandomMeta() {
      return this.isRandomMeta;
   }

   @Nullable
   public ItemStack getItemStack() {
      return this.getItemStack(this.random);
   }

   @Nullable
   public ItemStack getItemStack(Random random) {
      if (this.itemStack == null) {
         return null;
      } else {
         int amount;
         if (this.isRandomYield) {
            amount = random.nextInt(this.yieldMax - this.yieldMin + 1) + this.yieldMin;
         } else {
            amount = this.yieldFixed;
         }

         int meta;
         if (this.isRandomMeta) {
            meta = random.nextInt(this.metaMax - this.metaMin + 1) + this.metaMin;
         } else {
            meta = this.itemStack.getMetadata();
         }

         return new ItemStack(this.itemStack.itemID, amount, meta, this.itemStack.getData());
      }
   }
}
