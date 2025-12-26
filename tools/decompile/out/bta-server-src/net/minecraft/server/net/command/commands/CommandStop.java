package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.net.command.IServerCommandSource;

public class CommandStop implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("stop").requires(CommandSource::hasAdmin).executes(c -> {
         CommandSource source = c.getSource();
         if (!(source instanceof IServerCommandSource)) {
            throw CommandExceptions.multiplayerWorldOnly().create();
         } else {
            MinecraftServer server = ((IServerCommandSource)source).getServer();
            source.sendTranslatableMessage("command.commands.stop.success");
            if (server.playerList != null) {
               server.playerList.savePlayerStates();
            }

            for (World world : server.dimensionWorlds.values()) {
               world.saveWorld(true, null, true);
            }

            server.initiateShutdown();
            return 1;
         }
      }));
   }
}
