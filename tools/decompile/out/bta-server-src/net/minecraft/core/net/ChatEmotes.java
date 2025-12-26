package net.minecraft.core.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ChatEmotes {
   private static final Map<String, Character> emotes = new HashMap<>();

   public static void register(String name, char substitute) {
      emotes.put(":" + name + ":", substitute);
   }

   public static String process(String s) {
      for (Entry<String, Character> entry : emotes.entrySet()) {
         s = s.replaceAll(entry.getKey(), entry.getValue().toString());
      }

      return s;
   }

   public static Map<String, Character> getEmotes() {
      return emotes;
   }

   static {
      register("skull", '☠');
      register("smile", '☺');
      register("smile2", '☻');
      register("heart", '❤');
      register("diamond", '♦');
      register("club", '♣');
      register("spade", '♠');
      register("male", '♂');
      register("female", '♀');
      register("note", '♪');
      register("note2", '♫');
      register("sun", '☀');
      register("up", '↑');
      register("down", '↓');
      register("right", '→');
      register("left", '←');
      register("cloud", '☁');
      register("moon", '☽');
      register("letter", '✉');
      register("umbrella", '☂');
      register("snowman", '⛄');
      register("hourglass", '⌛');
      register("time", '⌚');
      register("flag", '⚐');
      register("electric", '⚡');
      register("pickaxe", '⛏');
      register("tick", '✔');
      register("snowflake", '❄');
      register("cross", '❌');
      register("star", '⭐');
   }
}
