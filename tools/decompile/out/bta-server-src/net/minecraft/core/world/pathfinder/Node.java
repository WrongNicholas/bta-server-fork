package net.minecraft.core.world.pathfinder;

import net.minecraft.core.util.helper.MathHelper;

public class Node {
   public final int x;
   public final int y;
   public final int z;
   private final int hash;
   int heapIdx = -1;
   float g;
   float h;
   float f;
   Node cameFrom;
   public boolean closed = false;

   public Node(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.hash = createHash(x, y, z);
   }

   public static int createHash(int x, int y, int z) {
      return y & 0xFF | (x & 32767) << 8 | (z & 32767) << 24 | (x >= 0 ? 0 : Integer.MIN_VALUE) | (z >= 0 ? 0 : 32768);
   }

   public float distanceTo(Node other) {
      float f = other.x - this.x;
      float f1 = other.y - this.y;
      float f2 = other.z - this.z;
      return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
   }

   @Override
   public boolean equals(Object that) {
      if (!(that instanceof Node)) {
         return false;
      } else {
         Node nThat = (Node)that;
         return this.hash == nThat.hash && this.x == nThat.x && this.y == nThat.y && this.z == nThat.z;
      }
   }

   @Override
   public int hashCode() {
      return this.hash;
   }

   public boolean inOpenSet() {
      return this.heapIdx >= 0;
   }

   @Override
   public String toString() {
      return this.x + ", " + this.y + ", " + this.z;
   }
}
