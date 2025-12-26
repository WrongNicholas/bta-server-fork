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
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.Weathers;

public class ArgumentTypeWeather implements ArgumentType<Weather> {
   private static final Collection<String> EXAMPLES = Arrays.asList(Weathers.OVERWORLD_CLEAR.languageKey, Weathers.OVERWORLD_FOG.languageKey);

   public static ArgumentTypeWeather weather() {
      return new ArgumentTypeWeather();
   }

   public Weather parse(StringReader reader) throws CommandSyntaxException {
      String string = reader.readString();

      for (Weather weather : Weathers.WEATHERS) {
         if (weather != null && CommandHelper.matchesKeyString(weather.languageKey, string)) {
            return weather;
         }
      }

      throw new CommandSyntaxException(
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
         () -> I18n.getInstance().translateKey("command.argument_types.weather.invalid_weather")
      );
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      String remaining = builder.getRemainingLowerCase();

      for (Weather weather : Weathers.WEATHERS) {
         if (weather != null) {
            Optional<String> optional = CommandHelper.getStringToSuggest(weather.languageKey, remaining);
            optional.ifPresent(builder::suggest);
         }
      }

      return builder.buildFuture();
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
