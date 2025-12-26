package net.minecraft.core.net.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.sound.SoundCategory;

public class ArgumentTypeSoundCategory implements ArgumentType<SoundCategory> {
   private static final List<String> EXAMPLES = Arrays.asList("MUSIC", "WORLD_SOUNDS", "WEATHER_SOUNDS");

   public static ArgumentType<SoundCategory> soundCategory() {
      return new ArgumentTypeSoundCategory();
   }

   public SoundCategory parse(StringReader reader) throws CommandSyntaxException {
      String string = reader.readString();

      for (SoundCategory category : SoundCategory.values()) {
         if (category.name().equalsIgnoreCase(string)) {
            return category;
         }
      }

      throw new CommandSyntaxException(
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
         () -> I18n.getInstance().translateKeyAndFormat("command.argument_types.sound_category.invalid_name", string)
      );
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      for (SoundCategory category : SoundCategory.values()) {
         if (category.name().toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase())) {
            builder.suggest(category.name());
         }
      }

      return builder.buildFuture();
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
