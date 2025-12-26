package net.minecraft.core.util.collection;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.util.HardIllegalArgumentException;
import org.jetbrains.annotations.NotNull;

public final class NamespaceID implements Comparable<NamespaceID>, CharSequence {
   private static final List<NamespaceID> pool = new ArrayList<>();
   private static int poolPointer = 0;
   @NotNull
   private String fullString;
   @NotNull
   private String namespace;
   @NotNull
   private String value;

   @NotNull
   public static NamespaceID getPermanent(@NotNull String formattedKey) throws HardIllegalArgumentException {
      return new NamespaceID(formattedKey);
   }

   @NotNull
   public static NamespaceID getPermanent(@NotNull String namespace, @NotNull String value) {
      return new NamespaceID(namespace, value);
   }

   @NotNull
   public static NamespaceID getTemp(@NotNull String formattedKey) throws HardIllegalArgumentException {
      if (poolPointer >= pool.size()) {
         pool.add(getPermanent(formattedKey));
      }

      return pool.get(poolPointer++).set(formattedKey);
   }

   @NotNull
   public static NamespaceID getTemp(@NotNull String namespace, @NotNull String value) {
      if (poolPointer >= pool.size()) {
         pool.add(getPermanent(namespace, value));
      }

      return pool.get(poolPointer++).set(namespace, value);
   }

   public static void deinitializePool() {
      pool.clear();
      poolPointer = 0;
   }

   public static void initializePool() {
      poolPointer = 0;
   }

   NamespaceID(@NotNull String formattedKey) throws HardIllegalArgumentException {
      this.set(formattedKey);
   }

   NamespaceID(@NotNull String namespace, @NotNull String value) {
      this.set(namespace, value);
   }

   @NotNull
   private NamespaceID set(@NotNull String formattedKey) throws HardIllegalArgumentException {
      int firstIndex;
      if ((firstIndex = formattedKey.indexOf(":")) != -1 && firstIndex == formattedKey.lastIndexOf(":")) {
         this.fullString = formattedKey;
         this.namespace = formattedKey.substring(0, firstIndex);
         this.value = formattedKey.substring(firstIndex + 1);
         return this;
      } else {
         throw new HardIllegalArgumentException("Namespace id key must have exactly 1 ':' character in format <namespace>:<value>! " + formattedKey);
      }
   }

   @NotNull
   private NamespaceID set(@NotNull String namespace, @NotNull String value) {
      this.fullString = namespace + ":" + value;
      this.namespace = namespace;
      this.value = value;
      return this;
   }

   @NotNull
   public NamespaceID makePermanent() {
      if (pool.contains(this)) {
         pool.remove(this);
         poolPointer--;
      }

      return this;
   }

   @NotNull
   public String namespace() {
      return this.namespace;
   }

   @NotNull
   public String value() {
      return this.value;
   }

   @Override
   public int length() {
      return this.fullString.length();
   }

   @Override
   public char charAt(int index) {
      return this.fullString.charAt(index);
   }

   @NotNull
   @Override
   public CharSequence subSequence(int start, int end) {
      return this.fullString.subSequence(start, end);
   }

   @NotNull
   @Override
   public String toString() {
      return this.fullString;
   }

   public int compareTo(@NotNull NamespaceID o) {
      return this.fullString.compareTo(o.fullString);
   }

   @Override
   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         NamespaceID that = (NamespaceID)o;
         return this.fullString.equals(that.fullString);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.fullString.hashCode();
   }
}
