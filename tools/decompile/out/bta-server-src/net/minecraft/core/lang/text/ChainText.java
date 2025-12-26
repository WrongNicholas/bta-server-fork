package net.minecraft.core.lang.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.net.command.TextFormatting;

public class ChainText extends Text {
   private final List<Text> chain = new ArrayList<>();

   ChainText(Text... chain) {
      this.chain.addAll(Arrays.asList(chain));
   }

   @Override
   void toString(StringBuilder sb) {
      for (Text text : this.chain) {
         text.toString(sb);
      }
   }

   @Override
   public Text trans(String languageKey) {
      this.chain.add(new TranslatableText(languageKey));
      return this;
   }

   @Override
   public Text func(FunctionText.TextGenerator generator) {
      this.chain.add(new FunctionText(generator));
      return this;
   }

   @Override
   public Text lit(String literal) {
      this.chain.add(new LiteralText(literal));
      return this;
   }

   @Override
   public Text fmt(TextFormatting formatting, Text text) {
      this.chain.add(new FormattedText(formatting, text));
      return this;
   }
}
