package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.world.Dimension;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.net.command.IServerCommandSource;
import net.minecraft.server.world.WorldServer;

public class CommandSave implements CommandManager.CommandRegistry {
   private static final DynamicCommandExceptionType FAILURE = new DynamicCommandExceptionType(
      arg -> () -> I18n.getInstance().translateKeyAndFormat("command.commands.save.exception_failure", arg)
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("save")
            .requires(CommandSource::hasAdmin)

            .then(ArgumentBuilderLiteral.<CommandSource>literal("all")
               .executes(c -> {
                  CommandSource source = c.getSource();
                  if (!(source instanceof IServerCommandSource)) {
                     throw CommandExceptions.multiplayerWorldOnly().create();
                  }

                  MinecraftServer server = ((IServerCommandSource) source).getServer();
                  source.sendTranslatableMessage("command.commands.save.start");

                  if (server.playerList != null) {
                     server.playerList.savePlayerStates();
                  }

                  for (WorldServer worldServer : server.dimensionWorlds.values()) {
                     worldServer.saveWorld(true, null, worldServer.dimension == Dimension.OVERWORLD);
                  }

                  source.sendTranslatableMessage("command.commands.save.end");
                  return 1;
               })
            )

            .then(ArgumentBuilderLiteral.<CommandSource>literal("on")
               .executes(c -> {
                  CommandSource source = c.getSource();
                  if (!(source instanceof IServerCommandSource)) {
                     throw CommandExceptions.multiplayerWorldOnly().create();
                  }

                  MinecraftServer server = ((IServerCommandSource) source).getServer();
                  source.sendTranslatableMessage("command.commands.save.on");

                  for (WorldServer worldServer : server.dimensionWorlds.values()) {
                     worldServer.dontSave = false;
                  }

                  return 1;
               })
            )

            .then(ArgumentBuilderLiteral.<CommandSource>literal("off")
               .executes(c -> {
                  CommandSource source = c.getSource();
                  if (!(source instanceof IServerCommandSource)) {
                     throw CommandExceptions.multiplayerWorldOnly().create();
                  }

                  MinecraftServer server = ((IServerCommandSource) source).getServer();
                  source.sendTranslatableMessage("command.commands.save.off");

                  for (WorldServer worldServer : server.dimensionWorlds.values()) {
                     worldServer.dontSave = true;
                  }

                  return 1;
               })
            )

            .then(ArgumentBuilderLiteral.<CommandSource>literal("interval")
               .then(ArgumentBuilderRequired.<CommandSource, Integer>argument("value", ArgumentTypeInteger.integer())
                  .executes(c -> {
                     CommandSource source = c.getSource();
                     if (!(source instanceof IServerCommandSource)) {
                        throw CommandExceptions.multiplayerWorldOnly().create();
                     }

                     MinecraftServer server = ((IServerCommandSource) source).getServer();
                     int interval = c.getArgument("value", Integer.class);

                     server.propertyManager.setProperty("autosaveInterval", interval);
                     server.autoSaveInterval = interval;

                     source.sendTranslatableMessage("command.commands.save.interval", interval);
                     return 1;
                  })
               )
            )

            .then(ArgumentBuilderLiteral.<CommandSource>literal("amount")
               .then(ArgumentBuilderRequired.<CommandSource, Integer>argument("value", ArgumentTypeInteger.integer())
                  .executes(c -> {
                     CommandSource source = c.getSource();
                     if (!(source instanceof IServerCommandSource)) {
                        throw CommandExceptions.multiplayerWorldOnly().create();
                     }

                     MinecraftServer server = ((IServerCommandSource) source).getServer();
                     int amount = c.getArgument("value", Integer.class);

                     server.propertyManager.setProperty("maxChunksSavedPerAutosave", amount);
                     server.chunksSavedPerAutosave = amount;

                     source.sendTranslatableMessage("command.commands.save.amount", amount);
                     return 1;
                  })
               )
            )
      );
   }
}
