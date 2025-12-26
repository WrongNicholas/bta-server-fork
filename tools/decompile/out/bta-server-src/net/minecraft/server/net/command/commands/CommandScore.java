package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.command.IServerCommandSource;

public class CommandScore implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("score")

            .then(
               ArgumentBuilderLiteral.<CommandSource>literal("get")
                  .executes(c -> {
                     CommandSource source = c.getSource();
                     Player player = source.getSender();
                     if (player == null) {
                        throw CommandExceptions.notInWorld().create();
                     }
                     source.sendTranslatableMessage("command.commands.score.get.success", player.score);
                     return player.score;
                  })
                  .then(
                     ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("target", ArgumentTypeEntity.username())
                        .requires(CommandSource::hasAdmin)
                        .executes(c -> {
                           CommandSource source = c.getSource();
                           EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);
                           Player player = (Player) entitySelector.get(source).get(0);

                           if (player == source.getSender()) {
                              source.sendTranslatableMessage("command.commands.score.get.success", player.score);
                           } else {
                              source.sendTranslatableMessage(
                                 "command.commands.score.get.success_other",
                                 CommandHelper.getEntityName(player),
                                 player.score
                              );
                           }
                           return player.score;
                        })
                  )
            )

            .then(
               ArgumentBuilderLiteral.<CommandSource>literal("set")
                  .requires(CommandSource::hasAdmin)
                  .then(
                     ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("target", ArgumentTypeEntity.usernames())
                        .then(
                           ArgumentBuilderRequired.<CommandSource, Integer>argument("score", ArgumentTypeInteger.integer(0, Integer.MAX_VALUE))
                              .executes(c -> {
                                 CommandSource source = c.getSource();
                                 EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);
                                 List<? extends Entity> entities = entitySelector.get(source);
                                 int score = c.getArgument("score", Integer.class);

                                 for (Entity entity : entities) {
                                    setPlayerScore(source, (PlayerServer) entity, score);
                                 }
                                 return score;
                              })
                        )
                  )
            )

            .then(
               ArgumentBuilderLiteral.<CommandSource>literal("add")
                  .requires(CommandSource::hasAdmin)
                  .then(
                     ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("target", ArgumentTypeEntity.usernames())
                        .then(
                           ArgumentBuilderRequired.<CommandSource, Integer>argument("score", ArgumentTypeInteger.integer(0, Integer.MAX_VALUE))
                              .executes(c -> {
                                 CommandSource source = c.getSource();
                                 EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);
                                 List<? extends Entity> entities = entitySelector.get(source);
                                 int score = c.getArgument("score", Integer.class);

                                 for (Entity entity : entities) {
                                    PlayerServer ps = (PlayerServer) entity;
                                    setPlayerScore(source, ps, ps.score + score);
                                 }
                                 return score;
                              })
                        )
                  )
            )
      );
   }

   private static void setPlayerScore(CommandSource source, PlayerServer player, int score) throws CommandSyntaxException {
      if (!(source instanceof IServerCommandSource)) {
         throw CommandExceptions.multiplayerWorldOnly().create();
      }

      MinecraftServer server = ((IServerCommandSource) source).getServer();
      player.score = score;
      server.playerList.updatePlayerProfile(
         player.username,
         player.nickname,
         player.uuid,
         player.score,
         player.chatColor,
         true,
         player.isOperator()
      );

      if (player == source.getSender()) {
         source.sendTranslatableMessage("command.commands.score.set.success", score);
      } else {
         source.sendTranslatableMessage("command.commands.score.set.success_other", CommandHelper.getEntityName(player), score);
         source.sendTranslatableMessage(player, "command.commands.score.set.success_receiver", score);
      }
   }
}
