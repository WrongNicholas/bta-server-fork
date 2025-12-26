package net.minecraft.core.data;

public class Id implements Comparable<Id> {
   private static int highestId = 0;
   private final String stringId;
   private final int numericId;

   public static int getHighestId() {
      return highestId;
   }

   public Id(String stringId, int numericId) {
      this.stringId = stringId;
      this.numericId = numericId;
      if (highestId < this.numericId) {
         highestId = this.numericId;
      }
   }

   public Id(String stringId) {
      this.stringId = stringId;
      this.numericId = ++highestId;
   }

   public String getStringId() {
      return this.stringId;
   }

   public int getNumericId() {
      return this.numericId;
   }

   public int compareTo(Id otherId) {
      return Integer.compare(this.numericId, otherId.numericId);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Id)) {
         return false;
      } else {
         Id otherId = (Id)obj;
         return this.numericId == otherId.numericId && this.stringId.equals(otherId.stringId);
      }
   }
}
