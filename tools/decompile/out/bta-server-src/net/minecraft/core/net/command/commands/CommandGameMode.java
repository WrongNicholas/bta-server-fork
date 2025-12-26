package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.arguments.ArgumentTypeGameMode;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.player.gamemode.Gamemode;

public class CommandGameMode implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("gamemode")
            .requires(CommandSource::hasAdmin)
            .then(
               ((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("gamemode", ArgumentTypeGameMode.gameMode()).executes(c -> {
                     CommandSource source = (CommandSource)c.getSource();
                     Gamemode gameMode = c.getArgument("gamemode", Gamemode.class);
                     if (source.getSender() == null) {
                        throw CommandExceptions.notInWorld().create();
                     } else {
                        source.getSender().setGamemode(gameMode);
                        source.sendTranslatableMessage(
                           "command.commands.gamemode.success_self", I18n.getInstance().translateKey(gameMode.getLanguageKey() + ".name")
                        );
                        return 1;
                     }
                  }))
                  .then(
                     ArgumentBuilderRequired.argument("targets", ArgumentTypeEntity.usernames())
                        .executes(
                           c -> {
                              CommandSource source = (CommandSource)c.getSource();
                              Gamemode gameMode = c.getArgument("gamemode", Gamemode.class);
                              EntitySelector entitySelector = c.getArgument("targets", EntitySelector.class);
                              List<? extends Entity> entities = entitySelector.get((CommandSource)c.getSource());

                              for (Entity entity : entities) {
                                 ((Player)entity).setGamemode(gameMode);
                                 if (entity != source.getSender()) {
                                    source.sendTranslatableMessage((Player)entity, "command.commands.gamemode.success_receiver");
                                 }
                              }

                              if (entities.size() == 1) {
                                 if (entities.get(0) == source.getSender()) {
                                    source.sendTranslatableMessage(
                                       "command.commands.gamemode.success_self", I18n.getInstance().translateKey(gameMode.getLanguageKey() + ".name")
                                    );
                                 } else {
                                    source.sendTranslatableMessage(
                                       "command.commands.gamemode.success_other",
                                       CommandHelper.getEntityName(entities.get(0)),
                                       I18n.getInstance().translateKey(gameMode.getLanguageKey() + ".name")
                                    );
                                 }
                              }

                              return 1;
                           }
                        )
                  )
            )
      );
   }
}
