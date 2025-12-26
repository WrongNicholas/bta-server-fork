package net.minecraft.core.net.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.net.command.helpers.WorldFeatureParser;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class ArgumentTypeWorldFeature implements ArgumentType<WorldFeature> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Dungeon[2, 2, 2]", "Cactus");

   public static ArgumentType<WorldFeature> worldFeature() {
      return new ArgumentTypeWorldFeature();
   }

   public WorldFeature parse(StringReader reader) throws CommandSyntaxException {
      WorldFeatureParser parser = new WorldFeatureParser(reader);
      return parser.parse();
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      StringReader stringReader = new StringReader(builder.getInput());
      stringReader.setCursor(builder.getStart());
      WorldFeatureParser worldFeatureParser = new WorldFeatureParser(stringReader);

      try {
         worldFeatureParser.parse();
      } catch (CommandSyntaxException var6) {
      }

      return worldFeatureParser.fillSuggestions(builder, suggestionsBuilder -> CommandHelper.suggest(CommandHelper.WORLD_FEATURES.keySet(), suggestionsBuilder));
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
