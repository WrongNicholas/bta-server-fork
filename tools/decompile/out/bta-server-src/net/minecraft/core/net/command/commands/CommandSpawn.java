package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;

public class CommandSpawn implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("spawn")
            .requires(CommandSource::hasAdmin)
            .executes(
               c -> {
                  CommandSource source = c.getSource();
                  Player sender = source.getSender();
                  World world = source.getWorld(0);
                  ChunkCoordinates spawnCoordinates = world.getSpawnPoint();
                  if (sender == null) {
                     throw CommandExceptions.notInWorld().create();
                  } else {
                     if (sender.dimension != 0) {
                        source.movePlayerToDimension(sender, 0);
                     }

                     source.teleportPlayerToPos(
                        sender,
                        spawnCoordinates.x + 0.5,
                        world.findTopSolidBlock(spawnCoordinates.x, spawnCoordinates.z) + sender.heightOffset,
                        spawnCoordinates.z + 0.5
                     );
                     source.sendTranslatableMessage("command.commands.spawn.success");
                     return 1;
                  }
               }
            )
            .then(
               ArgumentBuilderRequired.argument("players", ArgumentTypeEntity.usernames())
                  .executes(
                     c -> {
                        CommandSource source = (CommandSource)c.getSource();
                        Player sender = source.getSender();
                        World world = source.getWorld(0);
                        ChunkCoordinates spawnCoordinates = world.getSpawnPoint();
                        EntitySelector entitySelector = c.getArgument("players", EntitySelector.class);

                        for (Entity entity : entitySelector.get(source)) {
                           if (((Player)entity).dimension != 0) {
                              source.movePlayerToDimension((Player)entity, 0);
                           }

                           source.teleportPlayerToPos(
                              (Player)entity,
                              spawnCoordinates.x + 0.5,
                              world.findTopSolidBlock(spawnCoordinates.x, spawnCoordinates.z) + entity.heightOffset,
                              spawnCoordinates.z + 0.5
                           );
                           if (entity == sender) {
                              source.sendTranslatableMessage("command.commands.spawn.success");
                           } else {
                              source.sendTranslatableMessage("command.commands.spawn.success_receiver");
                              source.sendTranslatableMessage("command.commands.spawn.success_other", CommandHelper.getEntityName(entity));
                           }
                        }

                        return 1;
                     }
                  )
            )
            .then(
               ArgumentBuilderLiteral.literal("get")
                  .executes(
                     c -> {
                        CommandSource source = (CommandSource)c.getSource();
                        ChunkCoordinates spawnCoordinates = source.getWorld(0).getSpawnPoint();
                        source.sendMessage(
                           I18n.getInstance().translateKeyAndFormat("command.commands.spawn.get", spawnCoordinates.x, spawnCoordinates.y, spawnCoordinates.z)
                        );
                        return 1;
                     }
                  )
            )
      );
   }
}
