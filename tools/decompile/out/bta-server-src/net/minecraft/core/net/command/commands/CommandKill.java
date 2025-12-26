package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.command.util.CommandHelper;

public class CommandKill implements CommandManager.CommandRegistry {
   private static final SimpleCommandExceptionType FAILURE = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.kill.exception_failure")
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("kill").requires(CommandSource::hasAdmin).executes(c -> {
         CommandSource source = c.getSource();
         Player sender = source.getSender();
         if (sender == null) {
            throw CommandExceptions.notInWorld().create();
         } else {
            sender.killPlayer();
            source.sendTranslatableMessage("command.commands.kill.single_entity", sender.getDisplayName());
            return 1;
         }
      }).then(ArgumentBuilderRequired.argument("entities", ArgumentTypeEntity.entities()).executes(c -> {
         CommandSource source = (CommandSource)c.getSource();
         EntitySelector entitySelector = c.getArgument("entities", EntitySelector.class);
         CopyOnWriteArrayList<? extends Entity> entities = new CopyOnWriteArrayList<>(entitySelector.get(source));
         int entityCount = entities.size();
         if (entityCount == 0) {
            throw FAILURE.create();
         } else {
            if (entityCount == 1) {
               source.sendTranslatableMessage("command.commands.kill.single_entity", CommandHelper.getEntityName(entities.get(0)));
            } else {
               source.sendTranslatableMessage("command.commands.kill.multiple_entities", entityCount);
            }

            for (Entity entity : entities) {
               if (entity instanceof Player) {
                  ((Player)entity).killPlayer();
               } else if (entity instanceof Mob) {
                  entity.hurt(null, 100, null);
               } else {
                  entity.remove();
               }
            }

            return 1;
         }
      })));
   }
}
