package net.minecraft.server.net;

public class PlayerHash {
   private transient PlayerHashEntry[] hashArray;
   private transient int numHashElements;
   private int capacity;
   private final float percentUsable = 0.75F;
   private transient volatile int field_950_e;

   public PlayerHash() {
      this.capacity = 12;
      this.hashArray = new PlayerHashEntry[16];
   }

   private static int getHashedKey(long l) {
      return hash((int)(l ^ l >>> 32));
   }

   private static int hash(int i) {
      i ^= i >>> 20 ^ i >>> 12;
      return i ^ i >>> 7 ^ i >>> 4;
   }

   private static int getHashIndex(int i, int j) {
      return i & j - 1;
   }

   public Object getValueByKey(long l) {
      int i = getHashedKey(l);

      for (PlayerHashEntry playerhashentry = this.hashArray[getHashIndex(i, this.hashArray.length)];
         playerhashentry != null;
         playerhashentry = playerhashentry.nextEntry
      ) {
         if (playerhashentry.key == l) {
            return playerhashentry.value;
         }
      }

      return null;
   }

   public void add(long l, Object obj) {
      int i = getHashedKey(l);
      int j = getHashIndex(i, this.hashArray.length);

      for (PlayerHashEntry playerhashentry = this.hashArray[j]; playerhashentry != null; playerhashentry = playerhashentry.nextEntry) {
         if (playerhashentry.key == l) {
            playerhashentry.value = obj;
         }
      }

      this.field_950_e++;
      this.createKey(i, l, obj, j);
   }

   private void resizeTable(int i) {
      PlayerHashEntry[] aplayerhashentry = this.hashArray;
      int j = aplayerhashentry.length;
      if (j == 1073741824) {
         this.capacity = Integer.MAX_VALUE;
      } else {
         PlayerHashEntry[] aplayerhashentry1 = new PlayerHashEntry[i];
         this.copyHashTableTo(aplayerhashentry1);
         this.hashArray = aplayerhashentry1;
         this.capacity = (int)(i * 0.75F);
      }
   }

   private void copyHashTableTo(PlayerHashEntry[] aplayerhashentry) {
      PlayerHashEntry[] aplayerhashentry1 = this.hashArray;
      int i = aplayerhashentry.length;

      for (int j = 0; j < aplayerhashentry1.length; j++) {
         PlayerHashEntry playerhashentry = aplayerhashentry1[j];
         if (playerhashentry != null) {
            aplayerhashentry1[j] = null;

            while (true) {
               PlayerHashEntry playerhashentry1 = playerhashentry.nextEntry;
               int k = getHashIndex(playerhashentry.field_1026_d, i);
               playerhashentry.nextEntry = aplayerhashentry[k];
               aplayerhashentry[k] = playerhashentry;
               playerhashentry = playerhashentry1;
               if (playerhashentry1 == null) {
                  break;
               }
            }
         }
      }
   }

   public Object remove(long l) {
      PlayerHashEntry playerhashentry = this.removeKey(l);
      return playerhashentry != null ? playerhashentry.value : null;
   }

   final PlayerHashEntry removeKey(long l) {
      int i = getHashedKey(l);
      int j = getHashIndex(i, this.hashArray.length);
      PlayerHashEntry playerhashentry = this.hashArray[j];
      PlayerHashEntry playerhashentry1 = playerhashentry;

      while (playerhashentry1 != null) {
         PlayerHashEntry playerhashentry2 = playerhashentry1.nextEntry;
         if (playerhashentry1.key == l) {
            this.field_950_e++;
            this.numHashElements--;
            if (playerhashentry == playerhashentry1) {
               this.hashArray[j] = playerhashentry2;
            } else {
               playerhashentry.nextEntry = playerhashentry2;
            }

            return playerhashentry1;
         }

         playerhashentry = playerhashentry1;
         playerhashentry1 = playerhashentry2;
      }

      return playerhashentry1;
   }

   private void createKey(int i, long l, Object obj, int j) {
      PlayerHashEntry playerhashentry = this.hashArray[j];
      this.hashArray[j] = new PlayerHashEntry(i, l, obj, playerhashentry);
      if (this.numHashElements++ >= this.capacity) {
         this.resizeTable(2 * this.hashArray.length);
      }
   }

   static int getHashCode(long l) {
      return getHashedKey(l);
   }
}
