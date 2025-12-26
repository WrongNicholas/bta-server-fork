package net.minecraft.core.net.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.Nullable;

public class CommandManager {
   private final boolean isServer;
   private final CommandDispatcher<CommandSource> DISPATCHER = new CommandDispatcher<>();
   private static final Collection<CommandManager.CommandRegistry> externalCommands = new ArrayList<>();
   private static final Collection<CommandManager.CommandRegistry> externalServerCommands = new ArrayList<>();

   public CommandManager(boolean isServer) {
      this.isServer = isServer;
   }

   public void init() {
      if (this.isServer) {
         for (CommandManager.CommandRegistry registry : externalServerCommands) {
            registry.register(this.DISPATCHER);
         }
      }

      for (CommandManager.CommandRegistry registry : externalCommands) {
         registry.register(this.DISPATCHER);
      }
   }

   public int execute(String s, CommandSource commandSource) throws CommandSyntaxException {
      return this.DISPATCHER.execute(s, commandSource);
   }

   public CommandDispatcher<CommandSource> getDispatcher() {
      return this.DISPATCHER;
   }

   @Nullable
   public static <S> CommandSyntaxException getParseException(ParseResults<S> parseResults) {
      if (!parseResults.getReader().canRead()) {
         return null;
      } else if (parseResults.getExceptions().size() == 1) {
         return parseResults.getExceptions().values().iterator().next();
      } else {
         return parseResults.getContext().getRange().isEmpty()
            ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults.getReader())
            : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseResults.getReader());
      }
   }

   public static void registerCommand(CommandManager.CommandRegistry registry) {
      externalCommands.add(registry);
   }

   public static void registerServerCommand(CommandManager.CommandRegistry registry) {
      externalServerCommands.add(registry);
   }

   @FunctionalInterface
   public interface CommandRegistry {
      void register(CommandDispatcher<CommandSource> var1);
   }
}
