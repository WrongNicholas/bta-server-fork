package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.tree.CommandNode;
import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeDimension;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.arguments.ArgumentTypeVec3;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.DoubleCoordinates;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.world.Dimension;

public class CommandTeleport implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      CommandNode<CommandSource> command = dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("teleport")
            .requires(CommandSource::hasAdmin)
            .then(
               ((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("dimension", ArgumentTypeDimension.dimension())
                     .then(
                        ArgumentBuilderRequired.argument("position", ArgumentTypeVec3.vec3d())
                           .executes(
                              c -> {
                                 CommandSource source = (CommandSource)c.getSource();
                                 DoubleCoordinates targetCoordinates = c.getArgument("position", DoubleCoordinates.class);
                                 Dimension dimension = c.getArgument("dimension", Dimension.class);
                                 if (source.getSender() != null) {
                                    source.movePlayerToDimension(source.getSender(), dimension.id);
                                    source.teleportPlayerToPos(
                                       source.getSender(), targetCoordinates.getX(source), targetCoordinates.getY(source, true), targetCoordinates.getZ(source)
                                    );
                                    source.sendTranslatableMessage(
                                       "command.commands.teleport.dimension_location.success_single_entity",
                                       source.getSender().getDisplayName(),
                                       dimension.languageKey,
                                       targetCoordinates.getX(source),
                                       targetCoordinates.getY(source, true),
                                       targetCoordinates.getZ(source)
                                    );
                                    return 1;
                                 } else {
                                    throw CommandExceptions.notInWorld().create();
                                 }
                              }
                           )
                     ))
                  .executes(
                     c -> {
                        CommandSource source = (CommandSource)c.getSource();
                        Dimension dimension = c.getArgument("dimension", Dimension.class);
                        if (source.getSender() != null) {
                           source.movePlayerToDimension(source.getSender(), dimension.id);
                           source.sendTranslatableMessage(
                              "command.commands.teleport.dimension.success_single_entity", source.getSender().getDisplayName(), dimension.languageKey
                           );
                           return 1;
                        } else {
                           throw CommandExceptions.notInWorld().create();
                        }
                     }
                  )
            )
            .then(
               ArgumentBuilderRequired.argument("position", ArgumentTypeVec3.vec3d())
                  .executes(
                     c -> {
                        CommandSource source = (CommandSource)c.getSource();
                        DoubleCoordinates targetCoordinates = c.getArgument("position", DoubleCoordinates.class);
                        if (source.getSender() != null) {
                           source.teleportPlayerToPos(
                              source.getSender(), targetCoordinates.getX(source), targetCoordinates.getY(source, true), targetCoordinates.getZ(source)
                           );
                           source.sendTranslatableMessage(
                              "command.commands.teleport.location.success_single_entity",
                              source.getSender().getDisplayName(),
                              targetCoordinates.getX(source),
                              targetCoordinates.getY(source, true),
                              targetCoordinates.getZ(source)
                           );
                           return 1;
                        } else {
                           throw CommandExceptions.notInWorld().create();
                        }
                     }
                  )
            )
            .then(
               ((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("entity", ArgumentTypeEntity.entities())
                     .then(
                        ArgumentBuilderRequired.argument("position", ArgumentTypeVec3.vec3d())
                           .executes(
                              c -> {
                                 CommandSource source = (CommandSource)c.getSource();
                                 EntitySelector entitySelector = c.getArgument("entity", EntitySelector.class);
                                 DoubleCoordinates targetCoordinates = c.getArgument("position", DoubleCoordinates.class);
                                 List<? extends Entity> entities = entitySelector.get(source);

                                 for (Entity entity : entities) {
                                    if (entity instanceof Player) {
                                       source.teleportPlayerToPos(
                                          (Player)entity, targetCoordinates.getX(source), targetCoordinates.getY(source, true), targetCoordinates.getZ(source)
                                       );
                                    } else {
                                       entity.moveTo(
                                          targetCoordinates.getX(source),
                                          targetCoordinates.getY(source, true),
                                          targetCoordinates.getZ(source),
                                          entity.yRot,
                                          entity.xRot
                                       );
                                    }
                                 }

                                 double x = targetCoordinates.getX(source);
                                 double y = targetCoordinates.getY(source, true);
                                 double z = targetCoordinates.getZ(source);
                                 if (entities.size() == 1) {
                                    source.sendTranslatableMessage(
                                       "command.commands.teleport.location.success_single_entity", CommandHelper.getEntityName(entities.get(0)), x, y, z
                                    );
                                 } else if (entities.size() > 1) {
                                    source.sendTranslatableMessage("command.commands.teleport.location.success_multiple_entities", entities.size(), x, y, z);
                                 }

                                 return 1;
                              }
                           )
                     ))
                  .then(
                     ArgumentBuilderRequired.argument("target", ArgumentTypeEntity.entity())
                        .executes(
                           c -> {
                              CommandSource source = (CommandSource)c.getSource();
                              EntitySelector entitySelector = c.getArgument("entity", EntitySelector.class);
                              EntitySelector targetEntitySelector = c.getArgument("target", EntitySelector.class);
                              Entity targetEntity = targetEntitySelector.get((CommandSource)c.getSource()).get(0);
                              List<? extends Entity> entities = entitySelector.get((CommandSource)c.getSource());

                              for (Entity entity : entities) {
                                 entity.moveTo(targetEntity.x, targetEntity.y - targetEntity.heightOffset, targetEntity.z, entity.yRot, entity.xRot);
                              }

                              if (entities.size() == 1) {
                                 source.sendTranslatableMessage(
                                    "command.commands.teleport.entity.success_single_entity",
                                    CommandHelper.getEntityName(entities.get(0)),
                                    CommandHelper.getEntityName(targetEntity)
                                 );
                              } else if (entities.size() > 1) {
                                 source.sendTranslatableMessage(
                                    "command.commands.teleport.entity.success_multiple_entities", entities.size(), CommandHelper.getEntityName(targetEntity)
                                 );
                              }

                              return 1;
                           }
                        )
                  )
            )
      );
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("tp").requires(CommandSource::hasAdmin).redirect(command));
   }
}
