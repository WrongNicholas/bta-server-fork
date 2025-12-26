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
import net.minecraft.core.util.helper.DamageType;

public class ArgumentTypeDamageType implements ArgumentType<DamageType> {
   private static final Collection<String> EXAMPLES = Arrays.asList("damagetype.combat", "damagetype.blast");

   public static ArgumentTypeDamageType damageType() {
      return new ArgumentTypeDamageType();
   }

   public DamageType parse(StringReader reader) throws CommandSyntaxException {
      String string = reader.readString();

      for (DamageType damageType : DamageType.values()) {
         if (CommandHelper.matchesKeyString(damageType.getLanguageKey(), string)) {
            return damageType;
         }
      }

      throw new CommandSyntaxException(
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
         () -> I18n.getInstance().translateKey("command.argument_types.damage_type.invalid_damage_type")
      );
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      String remaining = builder.getRemainingLowerCase();

      for (DamageType damageType : DamageType.values()) {
         Optional<String> optional = CommandHelper.getStringToSuggest(damageType.getLanguageKey(), remaining);
         optional.ifPresent(builder::suggest);
      }

      return builder.buildFuture();
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
