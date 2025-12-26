package net.minecraft.core.util.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ChatAllowedCharacters {
   public static final String ALLOWED_CHARACTERS = getAllowedCharacters();
   public static final char[] ALLOWED_CHARACTERS_ARRAY = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};

   private static String getAllowedCharacters() {
      StringBuilder s = new StringBuilder();

      try {
         BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(ChatAllowedCharacters.class.getResourceAsStream("/font.txt"), StandardCharsets.UTF_8)
         );

         try {
            while (true) {
               String s2 = bufferedReader.readLine();
               if (s2 == null) {
                  break;
               }

               if (!s2.startsWith("#")) {
                  s.append(s2);
               }
            }
         } catch (Throwable var5) {
            try {
               bufferedReader.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }

            throw var5;
         }

         bufferedReader.close();
      } catch (Exception var6) {
      }

      return s.toString();
   }
}
