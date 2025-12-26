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
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.util.collection.NamespaceID;

public class ArgumentTypeEntitySummon implements ArgumentType<Class<? extends Entity>> {
   private static final List<String> EXAMPLES = Arrays.asList("minecraft:creeper", "skeleton", "minecraft:slime");

   public static ArgumentType<Class<? extends Entity>> entity() {
      return new ArgumentTypeEntitySummon();
   }

   public Class<? extends Entity> parse(StringReader reader) throws CommandSyntaxException {
      StringBuilder builder = new StringBuilder();

      while (reader.canRead()) {
         char peak = reader.peek();
         if (peak == '[' || peak == '{' || peak == ' ') {
            break;
         }

         builder.append(reader.read());
      }

      String string = builder.toString();

      for (NamespaceID entityId : EntityDispatcher.idToClassMap.keySet()) {
         if (CommandHelper.matchesNamespaceId(entityId, string)) {
            return EntityDispatcher.classForId(entityId);
         }
      }

      throw new CommandSyntaxException(
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
         () -> I18n.getInstance().translateKey("command.argument_types.entity_summon.invalid_entity")
      );
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

      for (NamespaceID entityId : EntityDispatcher.idToClassMap.keySet()) {
         CommandHelper.getStringToSuggest(entityId.toString().toLowerCase(Locale.ROOT), remaining).ifPresent(builder::suggest);
      }

      return builder.buildFuture();
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
