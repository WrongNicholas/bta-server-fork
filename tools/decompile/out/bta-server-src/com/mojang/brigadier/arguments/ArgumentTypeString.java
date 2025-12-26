package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class ArgumentTypeString implements ArgumentType<String> {
   private final ArgumentTypeString.StringType type;

   private ArgumentTypeString(ArgumentTypeString.StringType type) {
      this.type = type;
   }

   public static ArgumentTypeString word() {
      return new ArgumentTypeString(ArgumentTypeString.StringType.SINGLE_WORD);
   }

   public static ArgumentTypeString string() {
      return new ArgumentTypeString(ArgumentTypeString.StringType.QUOTABLE_PHRASE);
   }

   public static ArgumentTypeString greedyString() {
      return new ArgumentTypeString(ArgumentTypeString.StringType.GREEDY_PHRASE);
   }

   public static String getString(CommandContext<?> context, String name) {
      return context.getArgument(name, String.class);
   }

   public ArgumentTypeString.StringType getType() {
      return this.type;
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      if (this.type == ArgumentTypeString.StringType.GREEDY_PHRASE) {
         String text = reader.getRemaining();
         reader.setCursor(reader.getTotalLength());
         return text;
      } else {
         return this.type == ArgumentTypeString.StringType.SINGLE_WORD ? reader.readUnquotedString() : reader.readString();
      }
   }

   @Override
   public String toString() {
      return "string()";
   }

   @Override
   public Collection<String> getExamples() {
      return this.type.getExamples();
   }

   public static String escapeIfRequired(String input) {
      for (char c : input.toCharArray()) {
         if (!StringReader.isAllowedInUnquotedString(c)) {
            return escape(input);
         }
      }

      return input;
   }

   private static String escape(String input) {
      StringBuilder result = new StringBuilder("\"");

      for (int i = 0; i < input.length(); i++) {
         char c = input.charAt(i);
         if (c == '\\' || c == '"') {
            result.append('\\');
         }

         result.append(c);
      }

      result.append("\"");
      return result.toString();
   }

   public static enum StringType {
      SINGLE_WORD("word", "words_with_underscores"),
      QUOTABLE_PHRASE("\"quoted phrase\"", "word", "\"\""),
      GREEDY_PHRASE("word", "words with spaces", "\"and symbols\"");

      private final Collection<String> examples;

      private StringType(String... examples) {
         this.examples = Arrays.asList(examples);
      }

      public Collection<String> getExamples() {
         return this.examples;
      }
   }
}
