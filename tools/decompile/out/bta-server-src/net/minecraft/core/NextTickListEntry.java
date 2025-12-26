package net.minecraft.core;

public class NextTickListEntry implements Comparable<NextTickListEntry> {
   private static long counter = 0L;
   public int x;
   public int y;
   public int z;
   public int blockId;
   public long delay;
   private final long entryId = counter++;

   public NextTickListEntry(int x, int y, int z, int blockId) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.blockId = blockId;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry nextticklistentry = (NextTickListEntry)obj;
         return this.x == nextticklistentry.x && this.y == nextticklistentry.y && this.z == nextticklistentry.z && this.blockId == nextticklistentry.blockId;
      }
   }

   @Override
   public int hashCode() {
      return (this.x * 128 * 1024 + this.z * 128 + this.y) * 256 + this.blockId;
   }

   public NextTickListEntry setDelay(long delay) {
      this.delay = delay;
      return this;
   }

   public int compareTo(NextTickListEntry other) {
      if (this.delay < other.delay) {
         return -1;
      } else if (this.delay > other.delay) {
         return 1;
      } else if (this.entryId < other.entryId) {
         return -1;
      } else {
         return this.entryId <= other.entryId ? 0 : 1;
      }
   }
}
