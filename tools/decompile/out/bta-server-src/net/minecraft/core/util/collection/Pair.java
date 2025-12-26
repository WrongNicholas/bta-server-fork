package net.minecraft.core.util.collection;

public class Pair<A, B> {
   private final A left;
   private final B right;

   protected Pair(A left, B right) {
      this.left = left;
      this.right = right;
   }

   public static <A, B> Pair<A, B> of(A left, B right) {
      return new Pair<>(left, right);
   }

   public A getLeft() {
      return this.left;
   }

   public B getRight() {
      return this.right;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Pair<?, ?> pair = (Pair<?, ?>)o;
         if (this.getLeft() != null ? this.getLeft().equals(pair.getLeft()) : pair.getLeft() == null) {
            return this.getRight() != null ? this.getRight().equals(pair.getRight()) : pair.getRight() == null;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "Pair{left=" + this.left + ", right=" + this.right + '}';
   }
}
