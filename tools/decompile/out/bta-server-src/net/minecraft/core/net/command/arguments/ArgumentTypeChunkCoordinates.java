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
import net.minecraft.core.net.command.helpers.Coordinates2D;
import net.minecraft.core.net.command.helpers.IntegerCoordinate;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;

public class ArgumentTypeChunkCoordinates implements ArgumentType<Coordinates2D> {
   private static final List<String> EXAMPLES = Arrays.asList("~ ~", "0 0 0", "~60 ~", "~-20 -25");

   public static ArgumentTypeChunkCoordinates chunkCoordinates() {
      return new ArgumentTypeChunkCoordinates();
   }

   public Coordinates2D parse(StringReader reader) throws CommandSyntaxException {
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
      IntegerCoordinate z = IntegerCoordinate.parse(reader);
      return new Coordinates2D(x, z);
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      String string = builder.getRemaining();
      Vec3 coordinates = ((CommandSource)context.getSource()).getBlockCoordinates();
      if (coordinates == null) {
         return builder.buildFuture();
      } else {
         int[] roundedCoordinates = new int[]{MathHelper.floor(coordinates.x / 16.0), MathHelper.floor(coordinates.z / 16.0)};
         if (string.isEmpty()) {
            String allCoordinates = roundedCoordinates[0] + " " + roundedCoordinates[1];

            try {
               this.parse(new StringReader(allCoordinates));
               builder.suggest(String.valueOf(roundedCoordinates[0]));
               builder.suggest(roundedCoordinates[0] + " " + roundedCoordinates[1]);
               builder.suggest(allCoordinates);
            } catch (CommandSyntaxException var10) {
            }
         } else {
            String[] strings = string.split(" ");
            if (strings.length == 1) {
               String allCoordinates = strings[0] + " " + roundedCoordinates[1];

               try {
                  this.parse(new StringReader(allCoordinates));
                  builder.suggest(strings[0] + " " + roundedCoordinates[1]);
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
