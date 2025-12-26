package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;

public class CommandHeal implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("heal")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderRequired.argument("entities", ArgumentTypeEntity.entities())
                  .then(
                     ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer(0, 32768))
                        .executes(
                           c -> {
                              List<? extends Entity> entities = c.getArgument("entities", EntitySelector.class).get((CommandSource)c.getSource());
                              int amount = c.getArgument("amount", Integer.class);
                              int entitiesAffected = 0;

                              for (Entity entity : entities) {
                                 if (entity instanceof Mob) {
                                    int maxHealth = 20;

                                    try {
                                       maxHealth = ((Mob)entity.getClass().getConstructor(World.class).newInstance(((CommandSource)c.getSource()).getWorld()))
                                          .getMaxHealth();
                                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException var8) {
                                    }

                                    int originalHealth = ((Mob)entity).getHealth();
                                    ((Mob)entity).setHealthRaw(MathHelper.clamp(((Mob)entity).getHealth() + amount, 0, maxHealth));
                                    if (((Mob)entity).getHealth() != originalHealth) {
                                       entitiesAffected++;
                                    }
                                 }
                              }

                              ((CommandSource)c.getSource())
                                 .sendTranslatableMessage("command.commands.heal.success_" + (entitiesAffected == 1 ? "single" : "multiple"), entitiesAffected);
                              return entitiesAffected;
                           }
                        )
                  )
            )
      );
   }
}
