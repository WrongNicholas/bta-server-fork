package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.net.command.IServerCommandSource;

public class CommandList implements CommandManager.CommandRegistry {
   private static final SimpleCommandExceptionType FAILURE = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.list.exception_failure")
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("list")
            .executes(c -> {
               CommandSource source = c.getSource();
               if (!(source instanceof IServerCommandSource)) {
                  throw CommandExceptions.multiplayerWorldOnly().create();
               }

               MinecraftServer server = ((IServerCommandSource) source).getServer();
               int playerCount = server.playerList.playerEntities.size();

               if (playerCount < 100) {
                  if (playerCount == 0) {
                     throw FAILURE.create();
                  }

                  StringBuilder builder = new StringBuilder();
                  for (int i = 0; i < playerCount; i++) {
                     if (i > 0) builder.append(", ");
                     builder.append(CommandHelper.getEntityName(server.playerList.playerEntities.get(i)));
                  }

                  if (playerCount == 1) {
                     source.sendTranslatableMessage("command.commands.list.success_single", playerCount, builder.toString());
                  } else {
                     source.sendTranslatableMessage("command.commands.list.success_multiple", playerCount, builder.toString());
                  }
               } else {
                  source.sendTranslatableMessage("command.commands.list.success_too_long", playerCount);
               }

               return 1;
            })
      );
   }
}
