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
import net.minecraft.core.player.gamemode.Gamemode;

public class ArgumentTypeGameMode implements ArgumentType<Gamemode> {
   private static final Collection<String> EXAMPLES = Arrays.asList(Gamemode.creative.getLanguageKey(), Gamemode.survival.getLanguageKey());

   public static ArgumentType<Gamemode> gameMode() {
      return new ArgumentTypeGameMode();
   }

   public Gamemode parse(StringReader reader) throws CommandSyntaxException {
      String string = reader.readString();

      for (Gamemode gamemode : Gamemode.gamemodesList) {
         if (CommandHelper.matchesKeyString(gamemode.getLanguageKey(), string) || string.equals(String.valueOf(gamemode.getId()))) {
            return gamemode;
         }
      }

      throw new CommandSyntaxException(
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
         () -> I18n.getInstance().translateKey("command.argument_types.game_mode.invalid_game_mode")
      );
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      String remaining = builder.getRemainingLowerCase();

      for (Gamemode gamemode : Gamemode.gamemodesList) {
         Optional<String> optional = CommandHelper.getStringToSuggest(gamemode.getLanguageKey(), remaining);
         optional.ifPresent(builder::suggest);
      }

      return builder.buildFuture();
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
