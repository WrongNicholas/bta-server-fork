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
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.IntegerCoordinate;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;

public class ArgumentTypeIntegerCoordinates implements ArgumentType<IntegerCoordinates> {
   private static final List<String> EXAMPLES = Arrays.asList("~ ~ ~", "0 0 0", "~ ~60 ~", "~-20 ~10 -25");

   public static ArgumentTypeIntegerCoordinates intCoordinates() {
      return new ArgumentTypeIntegerCoordinates();
   }

   public IntegerCoordinates parse(StringReader reader) throws CommandSyntaxException {
      int i = reader.getCursor();
      IntegerCoordinate x = IntegerCoordinate.parse(reader);
      if (!reader.canRead() || reader.peek() != ' ') {
         if (reader.peek() != 'f' && reader.peek() != 'd') {
            reader.setCursor(i);
            throw CommandExceptions.incomplete().createWithContext(reader);
         }

         reader.skip();
         if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(i);
            throw CommandExceptions.incomplete().createWithContext(reader);
         }
      }

      reader.skip();
      IntegerCoordinate y = IntegerCoordinate.parse(reader);
      if (!reader.canRead() || reader.peek() != ' ') {
         if (reader.peek() != 'f' && reader.peek() != 'd') {
            reader.setCursor(i);
            throw CommandExceptions.incomplete().createWithContext(reader);
         }

         reader.skip();
         if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(i);
            throw CommandExceptions.incomplete().createWithContext(reader);
         }
      }

      reader.skip();
      IntegerCoordinate z = IntegerCoordinate.parse(reader);
      return new IntegerCoordinates(x, y, z);
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      String string = builder.getRemaining();
      Vec3 coordinates = ((CommandSource)context.getSource()).getBlockCoordinates();
      if (coordinates == null) {
         return builder.buildFuture();
      } else {
         int[] roundedCoordinates = new int[]{MathHelper.floor(coordinates.x), MathHelper.floor(coordinates.y), MathHelper.floor(coordinates.z)};
         if (string.isEmpty()) {
            String allCoordinates = roundedCoordinates[0] + " " + roundedCoordinates[1] + " " + roundedCoordinates[2];

            try {
               this.parse(new StringReader(allCoordinates));
               builder.suggest(String.valueOf(roundedCoordinates[0]));
               builder.suggest(roundedCoordinates[0] + " " + roundedCoordinates[1]);
               builder.suggest(allCoordinates);
            } catch (CommandSyntaxException var11) {
            }
         } else {
            String[] strings = string.split(" ");
            switch (strings.length) {
               case 1:
                  String allCoordinates = strings[0] + " " + roundedCoordinates[1] + " " + roundedCoordinates[2];

                  try {
                     this.parse(new StringReader(allCoordinates));
                     builder.suggest(strings[0] + " " + roundedCoordinates[1]);
                     builder.suggest(allCoordinates);
                  } catch (CommandSyntaxException var10) {
                  }
                  break;
               case 2:
                  String allCoordinates = strings[0] + " " + strings[1] + " " + roundedCoordinates[2];

                  try {
                     this.parse(new StringReader(allCoordinates));
                     builder.suggest(allCoordinates);
                  } catch (CommandSyntaxException var9) {
                  }
            }
         }

         return builder.buildFuture();
      }
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
