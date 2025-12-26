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
import net.minecraft.core.Global;
import net.minecraft.core.lang.I18n;

public class ArgumentTypeSoundId implements ArgumentType<String> {
   private static final List<String> EXAMPLES = Arrays.asList("random.fuse.lit", "note.snare", "mob.skeletonhurt");

   public static ArgumentType<String> soundId() {
      return new ArgumentTypeSoundId();
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      String string = reader.readString();

      for (String se : this.soundIds()) {
         if (se.equalsIgnoreCase(string)) {
            return string;
         }
      }

      throw new CommandSyntaxException(
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
         () -> I18n.getInstance().translateKeyAndFormat("command.argument_types.sound_id.invalid_id", string)
      );
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      for (String se : this.soundIds()) {
         if (se.startsWith(builder.getRemainingLowerCase())) {
            builder.suggest(se);
         }
      }

      return builder.buildFuture();
   }

   public Collection<String> soundIds() {
      return Global.accessor.getAvailableSoundKeys();
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
