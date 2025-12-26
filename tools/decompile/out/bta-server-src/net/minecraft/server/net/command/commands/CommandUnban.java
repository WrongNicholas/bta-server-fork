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
import net.minecraft.server.net.command.IServerCommandSource;

public class CommandUnban implements CommandManager.CommandRegistry {
   private static final DynamicCommandExceptionType FAILURE = new DynamicCommandExceptionType(
      arg -> () -> I18n.getInstance().translateKeyAndFormat("command.commands.unban.exception_failure", arg)
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("unban")
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
                           String nameToUnban = c.getArgument("name", String.class);

                           UUIDHelper.runConversionAction(
                              nameToUnban,
                              uuid -> {
                                 // TODO: likely wrong in decompile (see note below)
                                 server.playerList.pardonPlayer(uuid);
                                 source.sendTranslatableMessage("command.commands.unban.username.success", nameToUnban);
                              },
                              username -> source.sendTranslatableMessage("command.commands.unban.username.fail.wrong_name", nameToUnban)
                           );

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
                           server.playerList.pardonIP(ip);
                           source.sendTranslatableMessage("command.commands.unban.ip.success", ip);
                           return 1;
                        })
                  )
            )
      );
   }
}
