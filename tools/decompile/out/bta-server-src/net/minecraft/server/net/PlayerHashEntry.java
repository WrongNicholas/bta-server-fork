package net.minecraft.server.net;

class PlayerHashEntry {
   final long key;
   Object value;
   PlayerHashEntry nextEntry;
   final int field_1026_d;

   PlayerHashEntry(int i, long l, Object obj, PlayerHashEntry playerhashentry) {
      this.value = obj;
      this.nextEntry = playerhashentry;
      this.key = l;
      this.field_1026_d = i;
   }

   public final long func_736_a() {
      return this.key;
   }

   public final Object func_735_b() {
      return this.value;
   }

   @Override
   public final boolean equals(Object obj) {
      if (!(obj instanceof PlayerHashEntry)) {
         return false;
      } else {
         PlayerHashEntry playerhashentry = (PlayerHashEntry)obj;
         Long long1 = this.func_736_a();
         Long long2 = playerhashentry.func_736_a();
         if (long1 == long2 || long1 != null && long1.equals(long2)) {
            Object obj1 = this.func_735_b();
            Object obj2 = playerhashentry.func_735_b();
            if (obj1 == obj2 || obj1 != null && obj1.equals(obj2)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public final int hashCode() {
      return PlayerHash.getHashCode(this.key);
   }

   @Override
   public final String toString() {
      return this.func_736_a() + "=" + this.func_735_b();
   }
}
