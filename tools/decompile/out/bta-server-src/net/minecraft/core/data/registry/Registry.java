package net.minecraft.core.data.registry;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.IntTag;
import com.mojang.nbt.tags.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

public class Registry<T> implements Iterable<T> {
   private final List<T> items = new ArrayList<>();
   private final Map<String, T> keyItemMap = new HashMap<>();
   private final Map<T, String> itemKeyMap = new HashMap<>();
   private final List<Registry.ItemAddedCallback<T>> callbacks = new ArrayList<>();
   private Registry<?> parent = null;

   public void register(String key, T item) {
      Objects.requireNonNull(key);
      Objects.requireNonNull(item);
      this.items.add(item);
      this.keyItemMap.put(key, item);
      this.itemKeyMap.put(item, key);
      this.items.sort((t1, t2) -> String.valueOf(this.itemKeyMap.get(t1)).compareTo(String.valueOf(this.itemKeyMap.get(t2))));
      if (item instanceof Registry) {
         ((Registry)item).parent = this;
      }

      for (Registry.ItemAddedCallback<T> callback : this.callbacks) {
         callback.onItemAdded(this, item);
      }
   }

   public void unregister(String key) {
      Objects.requireNonNull(key);
      T item = this.keyItemMap.get(key);
      this.items.remove(item);
      this.keyItemMap.remove(key);
      this.itemKeyMap.remove(item);
      this.items.sort((t1, t2) -> String.valueOf(this.itemKeyMap.get(t1)).compareTo(String.valueOf(this.itemKeyMap.get(t2))));
      if (item instanceof Registry) {
         ((Registry)item).parent = null;
      }
   }

   public void addCallback(Registry.ItemAddedCallback<T> callback) {
      this.callbacks.add(callback);
   }

   public T getItem(String key) {
      Objects.requireNonNull(key);
      return this.keyItemMap.get(key);
   }

   public String getKey(T item) {
      Objects.requireNonNull(item);
      return this.itemKeyMap.get(item);
   }

   public Registry<?> getParent() {
      return this.parent;
   }

   public int getNumericIdOfItem(T item) {
      Objects.requireNonNull(item);
      return this.items.indexOf(item);
   }

   public int getNumericIdOfKey(String key) {
      Objects.requireNonNull(key);
      T item = this.getItem(key);
      return item != null ? this.getNumericIdOfItem(item) : -1;
   }

   public T getItemByNumericId(int id) {
      return id >= 0 && id < this.items.size() ? this.items.get(id) : null;
   }

   public int size() {
      return this.items.size();
   }

   @Override
   public Iterator<T> iterator() {
      return this.items.iterator();
   }

   public static <T> void writeIdMapToTag(Registry<T> registry, CompoundTag tag) {
      for (T item : registry) {
         tag.putInt(registry.getKey(item), registry.getNumericIdOfItem(item));
      }
   }

   public static Map<Integer, String> readIdMapFromTag(CompoundTag tag) {
      Map<Integer, String> map = new HashMap<>();

      for (Entry<String, Tag<?>> entry : tag.getValue().entrySet()) {
         if (entry.getValue() instanceof IntTag) {
            IntTag iTag = (IntTag)entry.getValue();
            map.put(iTag.getValue(), entry.getKey());
         }
      }

      return map;
   }

   public interface ItemAddedCallback<T> {
      void onItemAdded(Registry<T> var1, T var2);
   }
}
