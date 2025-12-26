package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeIntegerCoordinates;
import net.minecraft.core.net.command.arguments.ArgumentTypeWorldFeature;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import net.minecraft.core.world.LevelListener;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class CommandPlace implements CommandManager.CommandRegistry {
   private static final SimpleCommandExceptionType FAILURE = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.place.exception_failure")
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      CommandNode<CommandSource> command = dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("place")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderRequired.argument("feature", ArgumentTypeWorldFeature.worldFeature())
                  .then(ArgumentBuilderRequired.argument("position", ArgumentTypeIntegerCoordinates.intCoordinates()).executes(c -> {
                     CommandSource source = (CommandSource)c.getSource();
                     WorldFeature feature = c.getArgument("feature", WorldFeature.class);
                     IntegerCoordinates coordinates = c.getArgument("position", IntegerCoordinates.class);
                     World world = source.getWorld();
                     if (coordinates == null) {
                        throw CommandExceptions.notInWorld().create();
                     } else {
                        int x = coordinates.getX(source);
                        int y = coordinates.getY(source, true);
                        int z = coordinates.getZ(source);
                        boolean success = feature.place(world, source.getWorld().rand, x, y, z);
                        if (!success) {
                           throw FAILURE.create();
                        } else {
                           for (LevelListener listener : world.listeners) {
                              listener.allChanged(true, false);
                           }

                           source.sendTranslatableMessage("command.commands.place.success", feature.getClass().getSimpleName().substring(12), x, y, z);
                           return 1;
                        }
                     }
                  }))
            )
      );
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("generate").requires(CommandSource::hasAdmin).redirect(command));
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("gen").requires(CommandSource::hasAdmin).redirect(command));
   }
}
