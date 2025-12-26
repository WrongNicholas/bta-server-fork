package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandSay implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("say")
            .requires(CommandSource::hasAdmin)
            .then(ArgumentBuilderRequired.argument("message", ArgumentTypeString.greedyString()).executes(c -> {
               CommandSource source = (CommandSource)c.getSource();
               String message = ArgumentTypeString.getString(c, "message");
               String senderName = source.getName();
               source.sendMessageToAllPlayers("[" + senderName + "§r] " + message);
               return 1;
            }))
      );
   }
}
