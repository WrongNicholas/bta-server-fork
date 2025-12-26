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
import net.minecraft.core.net.command.helpers.DoubleCoordinate;
import net.minecraft.core.net.command.helpers.DoubleCoordinates;
import net.minecraft.core.util.phys.Vec3;

public class ArgumentTypeVec3 implements ArgumentType<DoubleCoordinates> {
   private static final List<String> EXAMPLES = Arrays.asList("~ ~ ~", "0 0 0", "~ ~60 ~", "~-20 ~10 ~-25.5");

   public static ArgumentTypeVec3 vec3d() {
      return new ArgumentTypeVec3();
   }

   public DoubleCoordinates parse(StringReader reader) throws CommandSyntaxException {
      int i = reader.getCursor();
      DoubleCoordinate x = DoubleCoordinate.parse(reader);
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
      DoubleCoordinate y = DoubleCoordinate.parse(reader);
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
      DoubleCoordinate z = DoubleCoordinate.parse(reader);
      return new DoubleCoordinates(x, y, z);
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      String string = builder.getRemaining();
      Vec3 coordinates = ((CommandSource)context.getSource()).getCoordinates(true);
      if (coordinates == null) {
         return builder.buildFuture();
      } else {
         if (string.isEmpty()) {
            String allCoordinates = coordinates.x + " " + coordinates.y + " " + coordinates.z;

            try {
               this.parse(new StringReader(allCoordinates));
               builder.suggest(String.valueOf(coordinates.x));
               builder.suggest(coordinates.x + " " + coordinates.y);
               builder.suggest(allCoordinates);
            } catch (CommandSyntaxException var10) {
            }
         } else {
            String[] strings = string.split(" ");
            switch (strings.length) {
               case 1:
                  String allCoordinates = strings[0] + " " + coordinates.y + " " + coordinates.z;

                  try {
                     this.parse(new StringReader(allCoordinates));
                     builder.suggest(strings[0] + " " + coordinates.y);
                     builder.suggest(allCoordinates);
                  } catch (CommandSyntaxException var9) {
                  }
                  break;
               case 2:
                  String allCoordinates = strings[0] + " " + strings[1] + " " + coordinates.z;

                  try {
                     this.parse(new StringReader(allCoordinates));
                     builder.suggest(allCoordinates);
                  } catch (CommandSyntaxException var8) {
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
