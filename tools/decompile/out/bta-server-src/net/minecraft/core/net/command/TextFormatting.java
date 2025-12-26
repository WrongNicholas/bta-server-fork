package net.minecraft.core.net.command;

public class TextFormatting {
   public static final String FORMAT_CHARS = "0123456789abcdefklmnor+-";
   public static final TextFormatting[] FORMATTINGS = new TextFormatting[22];
   public static final TextFormatting WHITE = new TextFormatting(0).setNames("White");
   public static final TextFormatting ORANGE = new TextFormatting(1).setNames("Orange");
   public static final TextFormatting MAGENTA = new TextFormatting(2).setNames("Magenta");
   public static final TextFormatting LIGHT_BLUE = new TextFormatting(3).setNames("Light Blue", "Aqua");
   public static final TextFormatting YELLOW = new TextFormatting(4).setNames("Yellow");
   public static final TextFormatting LIME = new TextFormatting(5).setNames("Lime", "Lime Green");
   public static final TextFormatting PINK = new TextFormatting(6).setNames("Pink");
   public static final TextFormatting GRAY = new TextFormatting(7).setNames("Gray", "Grey");
   public static final TextFormatting LIGHT_GRAY = new TextFormatting(8).setNames("Light Gray", "Light Grey", "Silver");
   public static final TextFormatting CYAN = new TextFormatting(9).setNames("Cyan", "Turquoise");
   public static final TextFormatting PURPLE = new TextFormatting(10).setNames("Purple");
   public static final TextFormatting BLUE = new TextFormatting(11).setNames("Blue");
   public static final TextFormatting BROWN = new TextFormatting(12).setNames("Brown");
   public static final TextFormatting GREEN = new TextFormatting(13).setNames("Green");
   public static final TextFormatting RED = new TextFormatting(14).setNames("Red");
   public static final TextFormatting BLACK = new TextFormatting(15).setNames("Black");
   public static final TextFormatting OBFUSCATED = new TextFormatting(16).setNames("Obfuscated");
   public static final TextFormatting BOLD = new TextFormatting(17).setNames("Bold");
   public static final TextFormatting STRIKETHROUGH = new TextFormatting(18).setNames("Strikethrough");
   public static final TextFormatting UNDERLINE = new TextFormatting(19).setNames("Underline");
   public static final TextFormatting ITALIC = new TextFormatting(20).setNames("Italic");
   public static final TextFormatting RESET = new TextFormatting(21).setNames("Reset");
   public final int id;
   public final char code;
   private String[] names;

   public TextFormatting(int id) {
      FORMATTINGS[id] = this;
      this.id = id;
      this.code = "0123456789abcdefklmnor".charAt(id);
   }

   public TextFormatting setNames(String... names) {
      this.names = names;
      return this;
   }

   public static TextFormatting getColorFormatting(String name) {
      for (int i = 0; i < 16; i++) {
         TextFormatting color = get(i);

         for (String name2 : color.names) {
            if (name.equalsIgnoreCase(simpleName(name2))) {
               return color;
            }
         }
      }

      return null;
   }

   public static String removeAllFormatting(String name) {
      return name.replaceAll("§[0123456789abcdefklmnor]|§<(.*?)>", "");
   }

   public static String formatted(String string, TextFormatting... formattings) {
      StringBuilder sb = new StringBuilder();

      for (TextFormatting formatting : formattings) {
         if (formatting != RESET) {
            sb.append(formatting.toString());
         }
      }

      return sb.append(string).append(RESET).toString();
   }

   private static String simpleName(String str) {
      str = str.toLowerCase();
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         if (c >= 'a' && c <= 'z') {
            builder.append(c);
         }
      }

      return builder.toString();
   }

   public String[] getNames() {
      return this.names;
   }

   @Override
   public String toString() {
      return "§" + this.code;
   }

   public static TextFormatting get(int id) {
      return FORMATTINGS[id];
   }
}
