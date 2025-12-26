package net.minecraft.core.util.collection;

public class LongHashMap<V> {
   private static final int DEFAULT_INITIAL_CAPACITY = 16;
   private static final int MAXIMUM_CAPACITY = 1073741824;
   private static final float DEFAULT_LOAD_FACTOR = 0.75F;
   private transient LongHashMap.Entry<V>[] table;
   private transient int size;
   private int threshold;
   private final float loadFactor = 0.75F;
   private transient volatile int modCount;

   public LongHashMap() {
      this.threshold = 12;
      this.table = new LongHashMap.Entry[16];
   }

   private static int hash(long n) {
      return hash((int)(n ^ n >>> 32));
   }

   private static int hash(int n) {
      n ^= n >>> 20 ^ n >>> 12;
      return n ^ n >>> 7 ^ n >>> 4;
   }

   private static int indexFor(int n, int n2) {
      return n & n2 - 1;
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public V get(long n) {
      for (LongHashMap.Entry<V> next = this.table[indexFor(hash(n), this.table.length)]; next != null; next = next.next) {
         if (next.key == n) {
            return next.value;
         }
      }

      return null;
   }

   public boolean containsKey(long n) {
      return this.getEntry(n) != null;
   }

   final LongHashMap.Entry<V> getEntry(long n) {
      for (LongHashMap.Entry<V> next = this.table[indexFor(hash(n), this.table.length)]; next != null; next = next.next) {
         if (next.key == n) {
            return next;
         }
      }

      return null;
   }

   public void put(long n, V value) {
      int hash = hash(n);
      int index = indexFor(hash, this.table.length);

      for (LongHashMap.Entry<V> next = this.table[index]; next != null; next = next.next) {
         if (next.key == n) {
            next.value = value;
         }
      }

      this.modCount++;
      this.addEntry(hash, n, value, index);
   }

   private void resize(int n) {
      if (this.table.length == 1073741824) {
         this.threshold = Integer.MAX_VALUE;
      } else {
         LongHashMap.Entry[] table = new LongHashMap.Entry[n];
         this.transfer(table);
         this.table = table;
         this.threshold = (int)(n * this.loadFactor);
      }
   }

   private void transfer(LongHashMap.Entry<V>[] array) {
      LongHashMap.Entry<V>[] table = this.table;
      int length = array.length;

      for (int i = 0; i < table.length; i++) {
         LongHashMap.Entry<V> entry = table[i];
         if (entry != null) {
            table[i] = null;

            while (true) {
               LongHashMap.Entry<V> next = entry.next;
               int index = indexFor(entry.hash, length);
               entry.next = array[index];
               array[index] = entry;
               entry = next;
               if (next == null) {
                  break;
               }
            }
         }
      }
   }

   public V remove(long n) {
      LongHashMap.Entry<V> removeEntryForKey = this.removeEntryForKey(n);
      return removeEntryForKey == null ? null : removeEntryForKey.value;
   }

   final LongHashMap.Entry<V> removeEntryForKey(long n) {
      int index = indexFor(hash(n), this.table.length);
      LongHashMap.Entry<V> entry2;
      LongHashMap.Entry<V> entry = entry2 = this.table[index];

      while (entry2 != null) {
         LongHashMap.Entry<V> next = entry2.next;
         if (entry2.key == n) {
            this.modCount++;
            this.size--;
            if (entry == entry2) {
               this.table[index] = next;
            } else {
               entry.next = next;
            }

            return entry2;
         }

         entry = entry2;
         entry2 = next;
      }

      return entry2;
   }

   public void clear() {
      this.modCount++;
      LongHashMap.Entry<V>[] table = this.table;

      for (int i = 0; i < table.length; i++) {
         table[i] = null;
      }

      this.size = 0;
   }

   public boolean containsValue(Object o) {
      if (o == null) {
         return this.containsNullValue();
      } else {
         LongHashMap.Entry<V>[] table = this.table;

         for (int i = 0; i < table.length; i++) {
            for (LongHashMap.Entry<V> next = table[i]; next != null; next = next.next) {
               if (o.equals(next.value)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean containsNullValue() {
      LongHashMap.Entry<V>[] table = this.table;

      for (int i = 0; i < table.length; i++) {
         for (LongHashMap.Entry<V> next = table[i]; next != null; next = next.next) {
            if (next.value == null) {
               return true;
            }
         }
      }

      return false;
   }

   private void addEntry(int n, long n2, V v, int n3) {
      this.table[n3] = new LongHashMap.Entry<>(n, n2, v, this.table[n3]);
      if (this.size++ >= this.threshold) {
         this.resize(2 * this.table.length);
      }
   }

   private static class Entry<V> {
      final long key;
      V value;
      LongHashMap.Entry<V> next;
      final int hash;

      Entry(int hash, long key, V value, LongHashMap.Entry<V> next) {
         this.value = value;
         this.next = next;
         this.key = key;
         this.hash = hash;
      }

      public final long getKey() {
         return this.key;
      }

      public final V getValue() {
         return this.value;
      }

      @Override
      public final boolean equals(Object o) {
         if (!(o instanceof LongHashMap.Entry)) {
            return false;
         } else {
            LongHashMap.Entry entry = (LongHashMap.Entry)o;
            Long value = this.getKey();
            Long value2 = entry.getKey();
            if (value == value2 || value != null && value.equals(value2)) {
               Object value3 = this.getValue();
               Object value4 = entry.getValue();
               if (value3 == value4 || value3 != null && value3.equals(value4)) {
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public final int hashCode() {
         return LongHashMap.hash(this.key);
      }

      @Override
      public final String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }
}
