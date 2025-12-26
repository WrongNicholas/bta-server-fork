package net.minecraft.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.item.ItemStack;

public class WeightedRandomBag<T> {
   private final List<WeightedRandomBag<T>.Entry> entries = new ArrayList<>();
   private double accumulatedWeight;
   private final Random rand = new Random();

   public void addEntry(T object, double weight) {
      this.accumulatedWeight += weight;
      WeightedRandomBag<T>.Entry e = new WeightedRandomBag.Entry();
      e.object = object;
      e.weight = weight;
      e.accumulatedWeight = this.accumulatedWeight;
      this.entries.add(e);
   }

   public T getRandom() {
      return this.getRandom(this.rand);
   }

   public T getRandom(Random random) {
      double r = random.nextDouble() * this.accumulatedWeight;

      for (WeightedRandomBag<T>.Entry entry : this.entries) {
         if (entry.accumulatedWeight >= r) {
            return entry.object;
         }
      }

      return null;
   }

   public List<T> getEntries() {
      List<T> list = new ArrayList<>();

      for (WeightedRandomBag<T>.Entry entry : this.entries) {
         list.add(entry.object);
      }

      return Collections.unmodifiableList(list);
   }

   public List<WeightedRandomBag<T>.Entry> getEntriesWithWeights() {
      return Collections.unmodifiableList(this.entries);
   }

   public double getAsPercentage(ItemStack stack) {
      double weight = 0.0;

      for (WeightedRandomBag<T>.Entry entry : this.entries) {
         if (entry.object instanceof WeightedRandomLootObject && ((WeightedRandomLootObject)entry.object).getItemStack().isItemEqual(stack)) {
            weight = entry.weight;
         }
      }

      return weight / this.accumulatedWeight * 100.0;
   }

   public void clear() {
      this.entries.clear();
      this.accumulatedWeight = 0.0;
   }

   @Override
   public String toString() {
      return "WeightedRandomBag{entries=" + this.entries + ", accumulatedWeight=" + this.accumulatedWeight + '}';
   }

   public class Entry {
      private double accumulatedWeight;
      private T object;
      private double weight;

      public double getWeight() {
         return this.weight;
      }

      public T getObject() {
         return this.object;
      }
   }
}
