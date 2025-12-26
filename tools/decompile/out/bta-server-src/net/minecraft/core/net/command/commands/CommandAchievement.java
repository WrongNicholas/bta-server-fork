package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.achievement.Achievement;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeAchievement;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;

public class CommandAchievement implements CommandManager.CommandRegistry {
   private static final SimpleCommandExceptionType PLAYER_ALREADY_HAS_ACHIEVEMENT = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.achievement.grant.exception_already_has_achievement")
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("achievement")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderLiteral.literal("grant")
                  .then(
                     ((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("entities", ArgumentTypeEntity.usernames())
                           .then(ArgumentBuilderRequired.argument("achievement", ArgumentTypeAchievement.achievement()).executes(c -> {
                              CommandSource source = (CommandSource)c.getSource();
                              List<? extends Entity> entities = c.getArgument("entities", EntitySelector.class).get(source);
                              Achievement achievement = c.getArgument("achievement", Achievement.class);
                              if (entities.size() == 1 && ((Player)entities.get(0)).getStat(achievement) != 0) {
                                 throw PLAYER_ALREADY_HAS_ACHIEVEMENT.create();
                              } else {
                                 List<Achievement> achievements = new ArrayList<>();
                                 achievements.add(achievement);

                                 while (achievements.get(achievements.size() - 1).parent != null) {
                                    achievements.add(achievements.get(achievements.size() - 1).parent);
                                 }

                                 for (int i = 0; i < achievements.size(); i++) {
                                    for (Entity entity : entities) {
                                       ((Player)entity).triggerAchievement(achievements.get(achievements.size() - 1 - i));
                                    }
                                 }

                                 sendContextualMessage(source, entities, achievement);
                                 return 1;
                              }
                           })))
                        .then(ArgumentBuilderLiteral.literal("*").executes(c -> {
                           CommandSource source = (CommandSource)c.getSource();
                           List<? extends Entity> entities = c.getArgument("entities", EntitySelector.class).get(source);

                           for (Achievement achievement : Achievements.achievementList) {
                              List<Achievement> achievements = new ArrayList<>();
                              achievements.add(achievement);

                              while (achievements.get(achievements.size() - 1).parent != null) {
                                 achievements.add(achievements.get(achievements.size() - 1).parent);
                              }

                              for (int i = 0; i < achievements.size(); i++) {
                                 for (Entity entity : entities) {
                                    ((Player)entity).triggerAchievement(achievements.get(achievements.size() - 1 - i));
                                 }
                              }
                           }

                           sendWildcardContextualMessage(source, entities);
                           return 1;
                        }))
                  )
            )
      );
   }

   private static void sendContextualMessage(CommandSource source, List<? extends Entity> entities, Achievement achievement) {
      if (entities.size() == 1) {
         source.sendTranslatableMessage(
            "command.commands.achievement.grant.success_single_entity", achievement.getStatName().trim(), ((Mob)entities.get(0)).getDisplayName()
         );
      } else {
         source.sendTranslatableMessage("command.commands.achievement.grant.success_multiple_entities", achievement.getStatName(), entities.size());
      }
   }

   private static void sendWildcardContextualMessage(CommandSource source, List<? extends Entity> entities) {
      if (entities.size() == 1) {
         source.sendTranslatableMessage("command.commands.achievement.grant.all.success_single_entity", ((Mob)entities.get(0)).getDisplayName());
      } else {
         source.sendTranslatableMessage("command.commands.achievement.grant.all.success_multiple_entities", entities.size());
      }
   }
}
