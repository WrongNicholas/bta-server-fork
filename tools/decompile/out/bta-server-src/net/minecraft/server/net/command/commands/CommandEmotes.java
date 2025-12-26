package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import java.util.ArrayList;
import java.util.Map.Entry;
import net.minecraft.core.net.ChatEmotes;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.TextFormatting;

public class CommandEmotes implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("emotes")
            .executes(c -> {
               CommandSource source = c.getSource();
               source.sendTranslatableMessage("command.commands.emotes.success");

               ArrayList<Entry<String, Character>> entryList =
                  new ArrayList<>(ChatEmotes.getEmotes().entrySet());
               entryList.sort(Entry.comparingByKey());

               for (Entry<String, Character> entry : entryList) {
                  source.sendMessage(
                     ":" +
                     TextFormatting.CYAN +
                     entry.getKey().substring(1, entry.getKey().length() - 1) +
                     TextFormatting.RESET +
                     ": -> " +
                     entry.getValue()
                  );
               }

               return entryList.size();
            })
      );
   }
}
