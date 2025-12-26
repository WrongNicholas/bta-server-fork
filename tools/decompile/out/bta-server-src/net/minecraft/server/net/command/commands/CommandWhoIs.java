package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.command.util.CommandHelper;

public class CommandWhoIs implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      CommandNode<CommandSource> command = dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("whois")
            .then(
               ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("target", ArgumentTypeEntity.nickname())
                  .executes(c -> {
                     CommandSource source = c.getSource();
                     EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);
                     Player player = (Player) entitySelector.get(source).get(0);
                     source.sendTranslatableMessage(
                        "command.commands.whois.success",
                        CommandHelper.getEntityName(player),
                        player.username
                     );
                     return 1;
                  })
            )
      );

      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("realname").redirect(command)
      );
   }
}
