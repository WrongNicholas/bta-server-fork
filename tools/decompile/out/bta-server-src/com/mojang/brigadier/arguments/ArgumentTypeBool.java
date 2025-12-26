package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ArgumentTypeBool implements ArgumentType<Boolean> {
   private static final Collection<String> EXAMPLES = Arrays.asList("true", "false");

   private ArgumentTypeBool() {
   }

   public static ArgumentTypeBool bool() {
      return new ArgumentTypeBool();
   }

   public static boolean getBool(CommandContext<?> context, String name) {
      return context.getArgument(name, Boolean.class);
   }

   public Boolean parse(StringReader reader) throws CommandSyntaxException {
      return reader.readBoolean();
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      if ("true".startsWith(builder.getRemainingLowerCase())) {
         builder.suggest("true");
      }

      if ("false".startsWith(builder.getRemainingLowerCase())) {
         builder.suggest("false");
      }

      return builder.buildFuture();
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
