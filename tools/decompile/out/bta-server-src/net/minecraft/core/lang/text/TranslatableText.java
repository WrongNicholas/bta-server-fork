package net.minecraft.core.lang.text;

import net.minecraft.core.lang.I18n;

public class TranslatableText extends Text {
   private final String languageKey;

   TranslatableText(String languageKey) {
      this.languageKey = languageKey;
   }

   @Override
   void toString(StringBuilder sb) {
      sb.append(I18n.getInstance().translateKey(this.languageKey));
   }
}
