package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.server.entity.player.PlayerServer;

public class CommandNickname implements CommandManager.CommandRegistry {
   private static final SimpleCommandExceptionType NICKNAME_TOO_LARGE = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.nickname.exception_too_large")
   );
   private static final SimpleCommandExceptionType NICKNAME_TOO_SMALL = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.nickname.exception_too_small")
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      CommandNode<CommandSource> command = dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("nickname")

            // nickname set <nickname>
            .then(
               ArgumentBuilderLiteral.<CommandSource>literal("set")
                  .then(
                     // admin: nickname set <target> <nickname>
                     ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("target", ArgumentTypeEntity.username())
                        .requires(CommandSource::hasAdmin)
                        .then(
                           ArgumentBuilderRequired.<CommandSource, String>argument("nickname", ArgumentTypeString.string())
                              .executes(c -> {
                                 CommandSource source = c.getSource();
                                 EntitySelector selector = c.getArgument("target", EntitySelector.class);
                                 String nickname = c.getArgument("nickname", String.class);

                                 validateNickname(nickname);

                                 List<? extends Entity> entities = selector.get(source);
                                 PlayerServer player = (PlayerServer) entities.get(0);

                                 setNickname(source, player, nickname);
                                 return 1;
                              })
                        )
                  )
                  // non-admin self: nickname set <nickname>
                  .then(
                     ArgumentBuilderRequired.<CommandSource, String>argument("nickname", ArgumentTypeString.string())
                        .executes(c -> {
                           CommandSource source = c.getSource();
                           String nickname = c.getArgument("nickname", String.class);

                           validateNickname(nickname);

                           PlayerServer player = (PlayerServer) source.getSender();
                           if (player == null) {
                              throw CommandExceptions.notInWorld().create();
                           }

                           setNickname(source, player, nickname);
                           // original behavior: always "set.success" for self set
                           source.sendTranslatableMessage("command.commands.nickname.set.success", nickname);
                           return 1;
                        })
                  )
            )

            // nickname get <target>
            .then(
               ArgumentBuilderLiteral.<CommandSource>literal("get")
                  .then(
                     ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("target", ArgumentTypeEntity.username())
                        .executes(c -> {
                           CommandSource source = c.getSource();
                           EntitySelector selector = c.getArgument("target", EntitySelector.class);

                           List<? extends Entity> entities = selector.get(source);
                           PlayerServer player = (PlayerServer) entities.get(0);

                           source.sendTranslatableMessage("command.commands.nickname.get.success", player.username, player.nickname);
                           return 1;
                        })
                  )
            )

            // nickname reset [<target>]
            .then(
               ArgumentBuilderLiteral.<CommandSource>literal("reset")
                  // admin: nickname reset <target>
                  .then(
                     ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("target", ArgumentTypeEntity.username())
                        .requires(CommandSource::hasAdmin)
                        .executes(c -> {
                           CommandSource source = c.getSource();
                           EntitySelector selector = c.getArgument("target", EntitySelector.class);

                           List<? extends Entity> entities = selector.get(source);
                           PlayerServer player = (PlayerServer) entities.get(0);

                           resetNickname(source, player);
                           return 1;
                        })
                  )
                  // self: nickname reset
                  .executes(c -> {
                     CommandSource source = c.getSource();
                     PlayerServer player = (PlayerServer) source.getSender();
                     if (player == null) {
                        throw CommandExceptions.notInWorld().create();
                     }

                     player.nickname = "";
                     player.hadNicknameSet = false;
                     player.mcServer.playerList.updatePlayerProfile(
                        player.username, player.nickname, player.uuid, player.score, player.chatColor, true, player.isOperator()
                     );

                     // keep original message key/signature used by your decompile
                     source.sendTranslatableMessage("command.commands.nickname.reset.success", player.username);
                     return 1;
                  })
            )
      );

      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("nick").redirect(command));
   }

   private static void validateNickname(String nickname) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
      if (nickname.length() > 16) throw NICKNAME_TOO_LARGE.create();
      if (nickname.isEmpty()) throw NICKNAME_TOO_SMALL.create();
   }

   private static void setNickname(CommandSource source, PlayerServer player, String nickname) {
      player.nickname = nickname;
      player.hadNicknameSet = true;
      player.mcServer.playerList.updatePlayerProfile(
         player.username, player.nickname, player.uuid, player.score, player.chatColor, true, player.isOperator()
      );

      if (source.getSender() == player) {
         source.sendTranslatableMessage("command.commands.nickname.set.success", nickname);
      } else {
         source.sendTranslatableMessage("command.commands.nickname.set.success_other", player.username, nickname);
         source.sendTranslatableMessage(player, "command.commands.nickname.set.success_receiver", nickname);
      }
   }

   private static void resetNickname(CommandSource source, PlayerServer player) {
      player.nickname = "";
      player.hadNicknameSet = false;
      player.mcServer.playerList.updatePlayerProfile(
         player.username, player.nickname, player.uuid, player.score, player.chatColor, true, player.isOperator()
      );

      if (source.getSender() == player) {
         source.sendTranslatableMessage("command.commands.nickname.reset.success");
      } else {
         source.sendTranslatableMessage("command.commands.nickname.reset.success_other", player.username);
         source.sendTranslatableMessage(player, "command.commands.nickname.reset.success_receiver");
      }
   }
}
