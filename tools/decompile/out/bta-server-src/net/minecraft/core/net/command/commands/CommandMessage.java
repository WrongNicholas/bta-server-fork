package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;

public class CommandMessage implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      CommandNode<CommandSource> command = dispatcher.register(
         (ArgumentBuilderLiteral<CommandSource>)ArgumentBuilderLiteral.literal("message")
            .then(
               ArgumentBuilderRequired.argument("targets", ArgumentTypeEntity.usernames())
                  .then(
                     ArgumentBuilderRequired.argument("message", ArgumentTypeString.greedyString())
                        .executes(
                           c -> {
                              CommandSource source = (CommandSource)c.getSource();
                              EntitySelector entitySelector = c.getArgument("targets", EntitySelector.class);
                              String message = c.getArgument("message", String.class);
                              if (source.getSender() == null) {
                                 return 0;
                              } else {
                                 String senderName = source.getSender().getDisplayName();

                                 for (Entity player : entitySelector.get(source)) {
                                    source.sendMessage(
                                       TextFormatting.LIGHT_GRAY.toString()
                                          + TextFormatting.ITALIC.toString()
                                          + I18n.getInstance()
                                             .translateKeyAndFormat(
                                                "command.commands.message.outgoing",
                                                TextFormatting.removeAllFormatting(((Player)player).getDisplayName()),
                                                message
                                             )
                                    );
                                    source.sendMessage(
                                       (Player)player,
                                       ""
                                          + TextFormatting.LIGHT_GRAY
                                          + TextFormatting.ITALIC
                                          + I18n.getInstance()
                                             .translateKeyAndFormat(
                                                "command.commands.message.incoming", TextFormatting.removeAllFormatting(senderName), message
                                             )
                                    );
                                 }

                                 return 1;
                              }
                           }
                        )
                  )
            )
      );
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("msg").redirect(command));
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("whisper").redirect(command));
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("tell").redirect(command));
   }
}
