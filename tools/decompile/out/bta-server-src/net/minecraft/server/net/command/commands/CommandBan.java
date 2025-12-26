package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.util.helper.UUIDHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.command.IServerCommandSource;

public class CommandBan implements CommandManager.CommandRegistry {
   private static final DynamicCommandExceptionType FAILURE = new DynamicCommandExceptionType(
      arg -> () -> I18n.getInstance().translateKeyAndFormat("command.commands.ban.exception_failure", arg)
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("ban")
            .requires(CommandSource::hasAdmin)

            .then(
               ArgumentBuilderLiteral.<CommandSource>literal("username")
                  .then(
                     ArgumentBuilderRequired.<CommandSource, String>argument("name", ArgumentTypeString.word())
                        .executes(c -> {
                           CommandSource source = c.getSource();
                           if (!(source instanceof IServerCommandSource)) {
                              throw CommandExceptions.multiplayerWorldOnly().create();
                           }

                           MinecraftServer server = ((IServerCommandSource) source).getServer();
                           String nameToBan = c.getArgument("name", String.class);
                           PlayerServer player = server.playerList.getPlayerEntity(nameToBan);

                           if (player != null) {
                              server.playerList.banPlayer(player.uuid);
                              source.sendTranslatableMessage("command.commands.ban.success", player.username);
                              player.playerNetServerHandler.kickPlayer("Banned by admin");
                           } else {
                              UUIDHelper.runConversionAction(
                                 nameToBan,
                                 uuid -> {
                                    server.playerList.banPlayer(uuid);
                                    source.sendTranslatableMessage("command.commands.ban.username.success", nameToBan);
                                 },
                                 username -> source.sendTranslatableMessage("command.commands.ban.username.fail.wrong_name", nameToBan)
                              );
                           }

                           return 1;
                        })
                  )
            )

            .then(
               ArgumentBuilderLiteral.<CommandSource>literal("ip")
                  .then(
                     ArgumentBuilderRequired.<CommandSource, String>argument("address", ArgumentTypeString.word())
                        .executes(c -> {
                           CommandSource source = c.getSource();
                           if (!(source instanceof IServerCommandSource)) {
                              throw CommandExceptions.multiplayerWorldOnly().create();
                           }

                           MinecraftServer server = ((IServerCommandSource) source).getServer();
                           String ip = c.getArgument("address", String.class);
                           server.playerList.banIP(ip);
                           source.sendTranslatableMessage("command.commands.ban.ip.success", ip);
                           return 1;
                        })
                  )
            )
      );
   }
}
