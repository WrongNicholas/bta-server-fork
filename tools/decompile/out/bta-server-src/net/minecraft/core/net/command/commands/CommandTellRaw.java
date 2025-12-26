package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;

public class CommandTellRaw implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("tellraw")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderRequired.argument("targets", ArgumentTypeEntity.usernames())
                  .then(ArgumentBuilderRequired.argument("message", ArgumentTypeString.greedyString()).executes(c -> {
                     CommandSource source = (CommandSource)c.getSource();
                     EntitySelector entitySelector = c.getArgument("targets", EntitySelector.class);
                     String message = c.getArgument("message", String.class);

                     for (Entity player : entitySelector.get(source)) {
                        source.sendMessage((Player)player, message);
                     }

                     return 1;
                  }))
            )
      );
   }
}
