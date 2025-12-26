package net.minecraft.core.lang.text;

import net.minecraft.core.net.command.TextFormatting;

public abstract class Text {
   public static Text text() {
      return new ChainText();
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      this.toString(sb);
      return sb.toString();
   }

   abstract void toString(StringBuilder var1);

   public Text trans(String languageKey) {
      return new ChainText(this, new TranslatableText(languageKey));
   }

   public Text func(FunctionText.TextGenerator generator) {
      return new ChainText(this, new FunctionText(generator));
   }

   public Text lit(String literal) {
      return new ChainText(this, new LiteralText(literal));
   }

   public Text fmt(TextFormatting formatting, Text text) {
      return new ChainText(this, new FormattedText(formatting, text));
   }
}
