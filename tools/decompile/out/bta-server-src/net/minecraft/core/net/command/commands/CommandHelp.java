package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.util.Iterables;

public class CommandHelp implements CommandManager.CommandRegistry {
   private static final SimpleCommandExceptionType FAILURE = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.help.exception_failure")
   );

   @Override
   public void register(CommandDispatcher<CommandSource> commandDispatcher) {
      commandDispatcher.register(
         (ArgumentBuilderLiteral<CommandSource>)((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal("help").executes(c -> {
               CommandSource source = (CommandSource)c.getSource();
               Map<CommandNode<CommandSource>, String> map = commandDispatcher.getSmartUsage(commandDispatcher.getRoot(), source);
               if (!source.messageMayBeMultiline()) {
                  source.sendMessage("Cannot display help on single-line command source");
                  return 0;
               } else {
                  for (String string : map.values()) {
                     source.sendMessage("/" + string);
                  }

                  return map.size();
               }
            }))
            .then(
               ArgumentBuilderRequired.argument("command", ArgumentTypeString.greedyString())
                  .executes(
                     commandContext -> {
                        ParseResults<CommandSource> parseResults = commandDispatcher.parse(
                           ArgumentTypeString.getString(commandContext, "command"), (CommandSource)commandContext.getSource()
                        );
                        if (parseResults.getContext().getNodes().isEmpty()) {
                           throw FAILURE.create();
                        } else {
                           Map<CommandNode<CommandSource>, String> map = commandDispatcher.getSmartUsage(
                              Iterables.getLast(parseResults.getContext().getNodes()).getNode(), (CommandSource)commandContext.getSource()
                           );

                           for (String string : map.values()) {
                              ((CommandSource)commandContext.getSource()).sendMessage("/" + parseResults.getReader().getString() + " " + string);
                           }

                           return map.size();
                        }
                     }
                  )
            )
      );
   }
}
