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
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.world.biome.Biome;

public class ArgumentTypeBiome implements ArgumentType<Biome> {
   private static final Collection<String> EXAMPLES = Arrays.asList("overworld", "nether");

   public static ArgumentTypeBiome biome() {
      return new ArgumentTypeBiome();
   }

   public Biome parse(StringReader reader) throws CommandSyntaxException {
      String string = this.readResourceLocation(reader);

      for (Biome biome : Registries.BIOMES) {
         if (CommandHelper.matchesKeyString(Registries.BIOMES.getKey(biome), string)) {
            return biome;
         }
      }

      throw new CommandSyntaxException(
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
         () -> I18n.getInstance().translateKey("command.argument_types.biome.invalid_biome")
      );
   }

   public String readResourceLocation(StringReader reader) {
      if (!reader.canRead()) {
         return "";
      } else {
         int start = reader.getCursor();

         while (reader.canRead() && isAllowedInResourceLocation(reader.peek())) {
            reader.skip();
         }

         return reader.getString().substring(start, reader.getCursor());
      }
   }

   public static boolean isAllowedInResourceLocation(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      String remaining = builder.getRemainingLowerCase();

      for (Biome biome : Registries.BIOMES) {
         Optional<String> optional = CommandHelper.getStringToSuggest(Registries.BIOMES.getKey(biome), remaining);
         optional.ifPresent(builder::suggest);
      }

      return builder.buildFuture();
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
