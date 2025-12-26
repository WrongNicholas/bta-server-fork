package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.command.IServerCommandSource;

public class CommandDeop implements CommandManager.CommandRegistry {
   private static final SimpleCommandExceptionType FAILURE = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.deop.exception_failure")
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("deop")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("target", ArgumentTypeEntity.usernames())
                  .executes(c -> {
                     CommandSource source = c.getSource();
                     if (!(source instanceof IServerCommandSource)) {
                        throw CommandExceptions.multiplayerWorldOnly().create();
                     }

                     EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);
                     List<? extends Entity> entities = entitySelector.get(source);
                     MinecraftServer server = ((IServerCommandSource) source).getServer();
                     boolean deoppedSomeone = false;

                     for (Entity entity : entities) {
                        PlayerServer player = (PlayerServer) entity;
                        if (player.isOperator()) {
                           deoppedSomeone = true;
                           server.playerList.deopPlayer(player.uuid);
                           source.sendTranslatableMessage("command.commands.deop.success", player.username);
                           source.sendTranslatableMessage(player, "command.commands.deop.success_receiver");
                        }

                        server.playerList.updatePlayerProfile(
                           player.username,
                           player.nickname,
                           player.uuid,
                           player.score,
                           player.chatColor,
                           true,
                           player.isOperator()
                        );
                     }

                     if (!deoppedSomeone) {
                        throw FAILURE.create();
                     }
                     return 1;
                  })
            )
      );
   }
}
