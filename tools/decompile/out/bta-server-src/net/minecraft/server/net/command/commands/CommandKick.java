package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.command.IServerCommandSource;

public class CommandKick implements CommandManager.CommandRegistry {
   private static final DynamicCommandExceptionType FAILURE = new DynamicCommandExceptionType(
      arg -> () -> I18n.getInstance().translateKeyAndFormat("command.commands.kick.exception_failure", arg)
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("kick")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("player", ArgumentTypeEntity.username())
                  .executes(c -> {
                     CommandSource source = c.getSource();
                     if (!(source instanceof IServerCommandSource)) {
                        throw CommandExceptions.multiplayerWorldOnly().create();
                     }

                     EntitySelector entitySelector = c.getArgument("player", EntitySelector.class);
                     PlayerServer playerToKick = (PlayerServer) entitySelector.get(source).get(0);

                     if (playerToKick != null) {
                        playerToKick.playerNetServerHandler.kickPlayer("Kicked by admin");
                        source.sendTranslatableMessage("command.commands.kick.success", playerToKick.username);
                        return 1;
                     }

                     // Fallback: Brigadier still provides the raw input; use it for error text.
                     throw FAILURE.create(c.getInput());
                  })
            )
      );
   }
}
