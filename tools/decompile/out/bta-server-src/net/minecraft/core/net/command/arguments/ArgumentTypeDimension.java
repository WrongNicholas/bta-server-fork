package net.minecraft.core.net.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.world.Dimension;

public class ArgumentTypeDimension implements ArgumentType<Dimension> {
   private static final Collection<String> EXAMPLES = Arrays.asList("overworld", "nether");

   public static ArgumentTypeDimension dimension() {
      return new ArgumentTypeDimension();
   }

   public Dimension parse(StringReader reader) throws CommandSyntaxException {
      String string = reader.readString();

      for (Entry<Integer, Dimension> dimension : Dimension.getDimensionList().entrySet()) {
         if (CommandHelper.matchesKeyString(dimension.getValue().languageKey, string) || string.equals(String.valueOf(dimension.getKey()))) {
            return dimension.getValue();
         }
      }

      throw new CommandSyntaxException(
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
         () -> I18n.getInstance().translateKey("command.argument_types.dimension.invalid_dimension")
      );
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      String remaining = builder.getRemainingLowerCase();

      for (Dimension dimension : Dimension.getDimensionList().values()) {
         Optional<String> optional = CommandHelper.getStringToSuggest(dimension.languageKey, remaining);
         optional.ifPresent(builder::suggest);
      }

      return builder.buildFuture();
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
