package net.minecraft.core.lang.text;

public class LiteralText extends Text {
   private final String literal;

   LiteralText(String literal) {
      this.literal = literal;
   }

   @Override
   void toString(StringBuilder sb) {
      sb.append(this.literal);
   }
}
