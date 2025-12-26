package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.util.helper.UUIDHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.command.IServerCommandSource;

public class CommandWhitelist implements CommandManager.CommandRegistry {
   private static final DynamicCommandExceptionType FAILURE = new DynamicCommandExceptionType(
      arg -> () -> I18n.getInstance().translateKeyAndFormat("command.commands.whitelist.exception_failure", arg)
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("whitelist")
            .requires(CommandSource::hasAdmin)

            .then(ArgumentBuilderLiteral.<CommandSource>literal("on").executes(c -> {
               CommandSource source = c.getSource();
               if (!(source instanceof IServerCommandSource)) {
                  throw CommandExceptions.multiplayerWorldOnly().create();
               }
               MinecraftServer server = ((IServerCommandSource) source).getServer();
               source.sendTranslatableMessage("command.commands.whitelist.on.success");
               server.propertyManager.setProperty("white-list", true);
               server.playerList.whitelistEnforced = true;
               return 1;
            }))

            .then(ArgumentBuilderLiteral.<CommandSource>literal("off").executes(c -> {
               CommandSource source = c.getSource();
               if (!(source instanceof IServerCommandSource)) {
                  throw CommandExceptions.multiplayerWorldOnly().create();
               }
               MinecraftServer server = ((IServerCommandSource) source).getServer();
               source.sendTranslatableMessage("command.commands.whitelist.off.success");
               server.propertyManager.setProperty("white-list", false);
               server.playerList.whitelistEnforced = false;
               return 1;
            }))

            .then(ArgumentBuilderLiteral.<CommandSource>literal("list").executes(c -> {
               CommandSource source = c.getSource();
               if (!(source instanceof IServerCommandSource)) {
                  throw CommandExceptions.multiplayerWorldOnly().create();
               }
               MinecraftServer server = ((IServerCommandSource) source).getServer();
               Set<UUID> set = server.playerList.getWhitelist();

               StringBuilder builder = new StringBuilder();
               for (UUID uuid : set) {
                  builder.append("\"").append(uuid).append("\"").append(", ");
               }

               String message = builder.toString();
               if (message.endsWith(", ")) {
                  message = message.substring(0, message.length() - 2);
               }

               source.sendTranslatableMessage("command.commands.whitelist.list", message);
               return 1;
            }))

            .then(ArgumentBuilderLiteral.<CommandSource>literal("reload").executes(c -> {
               CommandSource source = c.getSource();
               if (!(source instanceof IServerCommandSource)) {
                  throw CommandExceptions.multiplayerWorldOnly().create();
               }
               MinecraftServer server = ((IServerCommandSource) source).getServer();
               server.playerList.reloadWhiteList();
               source.sendTranslatableMessage("command.commands.whitelist.reload");
               return 1;
            }))

            .then(ArgumentBuilderLiteral.<CommandSource>literal("add")
               .then(ArgumentBuilderRequired.<CommandSource, String>argument("name", ArgumentTypeString.word())
                  .executes(c -> {
                     CommandSource source = c.getSource();
                     if (!(source instanceof IServerCommandSource)) {
                        throw CommandExceptions.multiplayerWorldOnly().create();
                     }

                     MinecraftServer server = ((IServerCommandSource) source).getServer();
                     String nameToAdd = c.getArgument("name", String.class);
                     PlayerServer player = server.playerList.getPlayerEntity(nameToAdd);

                     if (player != null) {
                        server.playerList.addToWhiteList(player.uuid);
                        source.sendTranslatableMessage("command.commands.whitelist.add.success", player.username);
                     } else {
                        UUIDHelper.runConversionAction(
                           nameToAdd,
                           uuid -> {
                              server.playerList.addToWhiteList(uuid);
                              source.sendTranslatableMessage("command.commands.whitelist.add.success", nameToAdd);
                           },
                           username -> source.sendTranslatableMessage("command.commands.whitelist.add.fail.wrong_name", username)
                        );
                     }

                     return 1;
                  })
               )
            )

            .then(ArgumentBuilderLiteral.<CommandSource>literal("remove")
               .then(ArgumentBuilderRequired.<CommandSource, String>argument("name", ArgumentTypeString.word())
                  .executes(c -> {
                     CommandSource source = c.getSource();
                     if (!(source instanceof IServerCommandSource)) {
                        throw CommandExceptions.multiplayerWorldOnly().create();
                     }

                     MinecraftServer server = ((IServerCommandSource) source).getServer();
                     String nameToRemove = c.getArgument("name", String.class);
                     PlayerServer player = server.playerList.getPlayerEntity(nameToRemove);

                     if (player != null) {
                        server.playerList.removeFromWhiteList(player.uuid);
                        source.sendTranslatableMessage("command.commands.whitelist.remove.success", player.username);
                     } else {
                        UUIDHelper.runConversionAction(
                           nameToRemove,
                           uuid -> {
                              server.playerList.removeFromWhiteList(uuid);
                              source.sendTranslatableMessage("command.commands.whitelist.remove.success", nameToRemove);
                           },
                           username -> source.sendTranslatableMessage("command.commands.whitelist.remove.fail.wrong_name", username)
                        );
                     }

                     return 1;
                  })
               )
            )
      );
   }
}
