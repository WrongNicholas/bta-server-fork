package net.minecraft.core.lang.text;

public class FunctionText extends Text {
   private final FunctionText.TextGenerator generator;

   FunctionText(FunctionText.TextGenerator generator) {
      this.generator = generator;
   }

   @Override
   void toString(StringBuilder sb) {
      sb.append(this.generator.getString());
   }

   public interface TextGenerator {
      String getString();
   }
}
