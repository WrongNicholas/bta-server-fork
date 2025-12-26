package net.minecraft.core.net.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.util.CommandHelper;

public class ArgumentTypeTime implements ArgumentType<Integer> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0d", "0s", "0t", "0");
   private static final SimpleCommandExceptionType ERROR_INVALID_UNIT = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.time.invalid_unit")
   );
   private static final Dynamic2CommandExceptionType ERROR_TICK_COUNT_TOO_LOW = new Dynamic2CommandExceptionType(
      (object, object2) -> () -> I18n.getInstance().translateKeyAndFormat("command.argument_types.time.tick_count_too_low", object2, object)
   );
   private static final Map<String, Integer> UNITS = new HashMap<>();
   final int minimum;

   private ArgumentTypeTime(int i) {
      this.minimum = i;
   }

   public static ArgumentTypeTime time() {
      return new ArgumentTypeTime(Integer.MIN_VALUE);
   }

   public static ArgumentTypeTime time(int i) {
      return new ArgumentTypeTime(i);
   }

   public Integer parse(StringReader stringReader) throws CommandSyntaxException {
      float f = stringReader.readFloat();
      String string = stringReader.readUnquotedString();
      int i = UNITS.getOrDefault(string, 0);
      if (i == 0) {
         throw ERROR_INVALID_UNIT.create();
      } else {
         int j = Math.round(f * i);
         if (j < this.minimum) {
            throw ERROR_TICK_COUNT_TOO_LOW.create(j, this.minimum);
         } else {
            return j;
         }
      }
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
      StringReader stringReader = new StringReader(suggestionsBuilder.getRemaining());

      try {
         stringReader.readFloat();
      } catch (CommandSyntaxException var5) {
         return suggestionsBuilder.buildFuture();
      }

      return CommandHelper.suggest(UNITS.keySet(), suggestionsBuilder.createOffset(suggestionsBuilder.getStart() + stringReader.getCursor()));
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      UNITS.put("d", 24000);
      UNITS.put("s", 20);
      UNITS.put("t", 1);
      UNITS.put("", 1);
   }
}
