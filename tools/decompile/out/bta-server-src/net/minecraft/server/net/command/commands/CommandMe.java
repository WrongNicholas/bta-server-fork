package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.packet.PacketChat;
import net.minecraft.server.net.command.IServerCommandSource;

public class CommandMe implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("me")
            .then(
               ArgumentBuilderRequired.<CommandSource, String>argument("message", ArgumentTypeString.greedyString())
                  .executes(c -> {
                     CommandSource source = c.getSource();
                     String message = ArgumentTypeString.getString(c, "message");

                     if (!(source instanceof IServerCommandSource)) {
                        throw CommandExceptions.multiplayerWorldOnly().create();
                     }
                     if (source.getSender() == null) {
                        return 0;
                     }

                     String senderName = source.getSender().getDisplayName();
                     ((IServerCommandSource) source)
                        .getServer()
                        .playerList
                        .sendPacketToAllPlayers(
                           new PacketChat(
                              TextFormatting.LIGHT_GRAY
                                 + "* "
                                 + (source.getSender().nickname.isEmpty() ? "" : TextFormatting.ITALIC)
                                 + TextFormatting.removeAllFormatting(senderName)
                                 + " "
                                 + TextFormatting.RESET
                                 + TextFormatting.LIGHT_GRAY
                                 + message
                           )
                        );

                     return 1;
                  })
            )
      );
   }
}
