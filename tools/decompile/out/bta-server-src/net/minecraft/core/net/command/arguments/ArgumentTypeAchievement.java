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
import net.minecraft.core.achievement.Achievement;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.util.CommandHelper;

public class ArgumentTypeAchievement implements ArgumentType<Achievement> {
   private static final Collection<String> EXAMPLES = Arrays.asList("achievement.acquireIron", "acquireIron");

   public static ArgumentType<Achievement> achievement() {
      return new ArgumentTypeAchievement();
   }

   public Achievement parse(StringReader reader) throws CommandSyntaxException {
      String string = reader.readString();

      for (Achievement achievement : Achievements.achievementList) {
         if (CommandHelper.matchesKeyString(achievement.getStatKey(), string)) {
            return achievement;
         }
      }

      throw new CommandSyntaxException(
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
         () -> I18n.getInstance().translateKey("command.argument_types.achievement.invalid_achievement")
      );
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      String remaining = builder.getRemainingLowerCase();

      for (Achievement achievement : Achievements.achievementList) {
         Optional<String> optional = CommandHelper.getStringToSuggest(achievement.getStatKey(), remaining);
         optional.ifPresent(builder::suggest);
      }

      return builder.buildFuture();
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
