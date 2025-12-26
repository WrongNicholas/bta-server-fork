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
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.helpers.ItemStackArgumentParser;
import net.minecraft.core.net.command.util.CommandHelper;

public class ArgumentTypeItemStack implements ArgumentType<ItemStack> {
   private static final List<String> EXAMPLES = Arrays.asList(
      "minecraft:item/iron_sword", "minecraft:item/iron_sword{name:\"Sword\", overrideName: true}", "minecraft:block/log_oak"
   );

   public static ArgumentType<ItemStack> itemStack() {
      return new ArgumentTypeItemStack();
   }

   public ItemStack parse(StringReader reader) throws CommandSyntaxException {
      ItemStackArgumentParser parser = new ItemStackArgumentParser(reader);
      return parser.parse();
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      StringReader stringReader = new StringReader(builder.getInput());
      stringReader.setCursor(builder.getStart());
      ItemStackArgumentParser parser = new ItemStackArgumentParser(stringReader);

      try {
         parser.parse();
      } catch (CommandSyntaxException var6) {
      }

      return parser.fillSuggestions(builder, suggestionsBuilder -> {
         String remaining = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

         for (Item item : Item.itemsList) {
            if (item != null) {
               CommandHelper.getStringToSuggest(item.namespaceID.toString().toLowerCase(Locale.ROOT), remaining).ifPresent(suggestionsBuilder::suggest);
            }
         }

         suggestionsBuilder.buildFuture();
      });
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
