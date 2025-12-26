package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeBool;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntitySummon;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.World;
import net.minecraft.core.world.config.spawning.SpawnerConfig;

public class CommandMobSpawning implements CommandManager.CommandRegistry {
   private static final DynamicCommandExceptionType FAILURE = new DynamicCommandExceptionType(
      arg -> () -> I18n.getInstance().translateKeyAndFormat("command.commands.mobspawning.exception_failure", arg)
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("mobspawning")
            .requires(CommandSource::hasAdmin)
            .then(ArgumentBuilderLiteral.literal("list").executes(c -> {
               Collection<NamespaceID> ids = EntityDispatcher.idToClassMap.keySet();
               World world = ((CommandSource)c.getSource()).getWorld();
               SpawnerConfig config = world.getSpawnerConfig();

               for (NamespaceID s : ids) {
                  ((CommandSource)c.getSource()).sendMessage(String.format("%s : %s", s.toString(), config.canMobSpawn(s)));
               }

               ((CommandSource)c.getSource()).sendMessage("Passive Spawning : " + config.canPassiveSpawn(world));
               ((CommandSource)c.getSource()).sendMessage("Hostile Spawning : " + config.canHostileSpawn(world));
               return 1;
            }))
            .then(
               ArgumentBuilderLiteral.literal("set")
                  .then(
                     ArgumentBuilderRequired.argument("entity", ArgumentTypeEntitySummon.entity())
                        .then(ArgumentBuilderRequired.argument("value", ArgumentTypeBool.bool()).executes(c -> {
                           World world = ((CommandSource)c.getSource()).getWorld();
                           SpawnerConfig config = world.getSpawnerConfig();
                           NamespaceID mobId = EntityDispatcher.idForClass(c.getArgument("entity", Class.class));
                           boolean value = c.getArgument("value", Boolean.class);
                           config.setMobSpawn(mobId, value);
                           ((CommandSource)c.getSource()).sendTranslatableMessage("command.commands.mobspawning.set", mobId, value);
                           return 1;
                        }))
                  )
            )
            .then(ArgumentBuilderLiteral.literal("passive").then(ArgumentBuilderRequired.argument("value", ArgumentTypeBool.bool()).executes(c -> {
               World world = ((CommandSource)c.getSource()).getWorld();
               SpawnerConfig config = world.getSpawnerConfig();
               boolean value = c.getArgument("value", Boolean.class);
               config.setPassiveSpawning(value);
               ((CommandSource)c.getSource()).sendTranslatableMessage("command.commands.mobspawning.passive", value);
               return 1;
            })))
            .then(ArgumentBuilderLiteral.literal("hostile").then(ArgumentBuilderRequired.argument("value", ArgumentTypeBool.bool()).executes(c -> {
               World world = ((CommandSource)c.getSource()).getWorld();
               SpawnerConfig config = world.getSpawnerConfig();
               boolean value = c.getArgument("value", Boolean.class);
               config.setHostileSpawning(value);
               ((CommandSource)c.getSource()).sendTranslatableMessage("command.commands.mobspawning.hostile", value);
               return 1;
            })))
      );
   }
}
