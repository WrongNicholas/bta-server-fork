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
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.season.Season;
import net.minecraft.core.world.season.SeasonManager;
import net.minecraft.core.world.season.SeasonManagerCycle;
import net.minecraft.core.world.season.Seasons;

public class ArgumentTypeSeason implements ArgumentType<String> {
   private static final List<String> EXAMPLES = Arrays.asList("overworld.winter", "overworld.summer", "paradise.gold");

   public static ArgumentType<String> season() {
      return new ArgumentTypeSeason();
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      String string = reader.readString();

      for (Season s : Seasons.getAllSeasons()) {
         if (s.getId().equalsIgnoreCase(string)) {
            return s.getId();
         }
      }

      throw new CommandSyntaxException(
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
         () -> I18n.getInstance().translateKeyAndFormat("command.argument_types.season.invalid_id", string)
      );
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      if (context.getSource() instanceof CommandSource) {
         CommandSource commandSource = (CommandSource)context.getSource();
         SeasonManager seasonManager = commandSource.getWorld().getSeasonManager();
         if (seasonManager instanceof SeasonManagerCycle) {
            for (Season s : seasonManager.getSeasons()) {
               builder.suggest(s.getId());
            }
         }
      } else {
         for (Season s : Seasons.getAllSeasons()) {
            builder.suggest(s.getId());
         }
      }

      return builder.buildFuture();
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
