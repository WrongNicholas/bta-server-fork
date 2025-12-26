package net.minecraft.core.lang.text;

import net.minecraft.core.net.command.TextFormatting;

public class FormattedText extends Text {
   private final TextFormatting formatting;
   private final Text text;

   FormattedText(TextFormatting formatting, Text text) {
      this.formatting = formatting;
      this.text = text;
   }

   @Override
   void toString(StringBuilder sb) {
      sb.append(this.formatting.toString());
      this.text.toString(sb);
      sb.append(TextFormatting.RESET);
   }
}
