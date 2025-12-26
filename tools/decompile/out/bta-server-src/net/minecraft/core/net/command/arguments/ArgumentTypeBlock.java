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
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.net.command.helpers.BlockArgumentParser;
import net.minecraft.core.net.command.helpers.BlockInput;
import net.minecraft.core.net.command.util.CommandHelper;

public class ArgumentTypeBlock implements ArgumentType<BlockInput> {
   private static final List<String> EXAMPLES = Arrays.asList("minecraft:block/stone", "block/stone", "minecraft:block/log_oak[1]");

   public static ArgumentType<BlockInput> block() {
      return new ArgumentTypeBlock();
   }

   public BlockInput parse(StringReader reader) throws CommandSyntaxException {
      BlockArgumentParser parser = new BlockArgumentParser(reader);
      return parser.parse();
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      StringReader stringReader = new StringReader(builder.getInput());
      stringReader.setCursor(builder.getStart());
      BlockArgumentParser parser = new BlockArgumentParser(stringReader);

      try {
         parser.parse();
      } catch (CommandSyntaxException var6) {
      }

      return parser.fillSuggestions(builder, suggestionsBuilder -> {
         String remaining = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

         for (Block<?> block : Blocks.blocksList) {
            if (block != null) {
               CommandHelper.getStringToSuggest(block.namespaceId().toString(), remaining).ifPresent(suggestionsBuilder::suggest);
            }
         }

         CommandHelper.getStringToSuggest(BlockArgumentParser.AIR_BLOCK.toString(), remaining).ifPresent(suggestionsBuilder::suggest);
         suggestionsBuilder.buildFuture();
      });
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
