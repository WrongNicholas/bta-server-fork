package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import net.minecraft.core.Global;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandSeed implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("seed").requires(CommandSource::hasAdmin).executes(c -> {
         CommandSource source = c.getSource();
         source.sendTranslatableMessage("command.commands.seed.success", source.getWorld().getRandomSeed());
         if (source.getSender() != null) {
            Global.accessor.copyToClipboard(Long.toString(source.getWorld().getRandomSeed()));
            source.sendTranslatableMessage("command.commands.seed.copied_to_clipboard");
         }

         return 1;
      }));
   }
}
