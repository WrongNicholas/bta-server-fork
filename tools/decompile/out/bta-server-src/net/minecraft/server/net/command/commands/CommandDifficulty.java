package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.net.command.IServerCommandSource;

public class CommandDifficulty implements CommandManager.CommandRegistry {
   private static final List<String> difficultyStrings = Arrays.asList("peaceful", "easy", "normal", "hard");

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("difficulty")
            .requires(CommandSource::hasAdmin)
            .executes(c -> {
               CommandSource source = c.getSource();
               switch (source.getWorld().getDifficulty()) {
                  case PEACEFUL:
                     source.sendTranslatableMessage(
                        "command.commands.difficulty.query.success",
                        I18n.getInstance().translateKey("options.difficulty.peaceful")
                     );
                     break;
                  case EASY:
                     source.sendTranslatableMessage(
                        "command.commands.difficulty.query.success",
                        I18n.getInstance().translateKey("options.difficulty.easy")
                     );
                     break;
                  case NORMAL:
                     source.sendTranslatableMessage(
                        "command.commands.difficulty.query.success",
                        I18n.getInstance().translateKey("options.difficulty.normal")
                     );
                     break;
                  case HARD:
                     source.sendTranslatableMessage(
                        "command.commands.difficulty.query.success",
                        I18n.getInstance().translateKey("options.difficulty.hard")
                     );
                     break;
                  default:
                     source.sendTranslatableMessage("command.commands.difficulty.query.success_unknown");
               }
               return 1;
            })

            .then(ArgumentBuilderLiteral.<CommandSource>literal("peaceful").executes(c -> setDifficulty(c.getSource(), 0)))
            .then(ArgumentBuilderLiteral.<CommandSource>literal("easy").executes(c -> setDifficulty(c.getSource(), 1)))
            .then(ArgumentBuilderLiteral.<CommandSource>literal("normal").executes(c -> setDifficulty(c.getSource(), 2)))
            .then(ArgumentBuilderLiteral.<CommandSource>literal("hard").executes(c -> setDifficulty(c.getSource(), 3)))
      );
   }

   private static int setDifficulty(CommandSource source, int difficulty) throws CommandSyntaxException {
      if (!(source instanceof IServerCommandSource)) {
         throw CommandExceptions.multiplayerWorldOnly().create();
      }

      MinecraftServer server = ((IServerCommandSource) source).getServer();
      for (World world : server.dimensionWorlds.values()) {
         world.setDifficulty(difficulty, true);
      }

      server.difficulty = difficulty;
      source.sendTranslatableMessage(
         "command.commands.difficulty.set.success",
         I18n.getInstance().translateKey("options.difficulty." + difficultyStrings.get(difficulty))
      );
      return 1;
   }
}
