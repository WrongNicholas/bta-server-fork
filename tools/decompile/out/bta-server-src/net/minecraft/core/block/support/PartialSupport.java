package net.minecraft.core.block.support;

import net.minecraft.core.util.helper.Side;

public class PartialSupport implements ISupport {
   private static final byte UP = 1;
   private static final byte DOWN = 2;
   private static final byte LEFT = 4;
   private static final byte RIGHT = 8;
   private static final byte CENTER = 16;
   public static final PartialSupport INSTANCE;
   private static final PartialSupport[] PERMUTATIONS = new PartialSupport[32];
   private final byte supportField;

   public PartialSupport(byte supportField) {
      this.supportField = supportField;
   }

   public PartialSupport up() {
      return PERMUTATIONS[this.supportField | 1];
   }

   public PartialSupport down() {
      return PERMUTATIONS[this.supportField | 2];
   }

   public PartialSupport left() {
      return PERMUTATIONS[this.supportField | 4];
   }

   public PartialSupport right() {
      return PERMUTATIONS[this.supportField | 8];
   }

   public PartialSupport center() {
      return PERMUTATIONS[this.supportField | 16];
   }

   @Override
   public boolean canSupport(ISupport other, Side side) {
      if (!(other instanceof PartialSupport)) {
         return false;
      } else {
         byte supportField = this.supportField;
         if (side == Side.TOP || side == Side.SOUTH || side == Side.EAST) {
            boolean left = (this.supportField & 4) > 0;
            boolean right = (this.supportField & 8) > 0;
            supportField &= -13;
            if (left) {
               supportField = (byte)(supportField | 8);
            }

            if (right) {
               supportField = (byte)(supportField | 4);
            }
         }

         PartialSupport partialOther = (PartialSupport)other;
         return (supportField & partialOther.supportField) == partialOther.supportField;
      }
   }

   static {
      for (int i = 0; i < 32; i++) {
         PERMUTATIONS[i] = new PartialSupport((byte)i);
      }

      INSTANCE = PERMUTATIONS[0];
   }
}
