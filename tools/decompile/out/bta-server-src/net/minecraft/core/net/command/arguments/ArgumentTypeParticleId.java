package net.minecraft.core.net.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.lang.I18n;

public class ArgumentTypeParticleId implements ArgumentType<String> {
   public static final Set<String> particleIds = new HashSet<>();
   private static final List<String> EXAMPLES = Arrays.asList("fireflyGreen", "explode", "soulflame");

   public static ArgumentType<String> particleId() {
      return new ArgumentTypeParticleId();
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      String string = reader.readString();

      for (String particleId : particleIds) {
         if (particleId.equalsIgnoreCase(string)) {
            return string;
         }
      }

      throw new CommandSyntaxException(
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
         () -> I18n.getInstance().translateKeyAndFormat("command.argument_types.particle_id.invalid_id", string)
      );
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      for (String particleId : particleIds) {
         if (particleId.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase())) {
            builder.suggest(particleId);
         }
      }

      return builder.buildFuture();
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      particleIds.add("note");
      particleIds.add("dripLava");
      particleIds.add("puffrgb");
      particleIds.add("blueflame");
      particleIds.add("lava");
      particleIds.add("arrowtrail");
      particleIds.add("reddust");
      particleIds.add("soulflame");
      particleIds.add("bubble");
      particleIds.add("explode");
      particleIds.add("slimechunk");
      particleIds.add("block");
      particleIds.add("portal");
      particleIds.add("snowshovel");
      particleIds.add("item");
      particleIds.add("dripWater");
      particleIds.add("largesmoke");
      particleIds.add("smoke");
      particleIds.add("fireflyBlue");
      particleIds.add("heart");
      particleIds.add("fireflyOrange");
      particleIds.add("footstep");
      particleIds.add("fallingleaf");
      particleIds.add("flame");
      particleIds.add("fireflyGreen");
      particleIds.add("fireflyRed");
      particleIds.add("splash");
   }
}
