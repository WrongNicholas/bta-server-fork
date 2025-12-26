package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeDamageType;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.util.helper.DamageType;

public class CommandDamage implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("damage")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderRequired.argument("entities", ArgumentTypeEntity.entities())
                  .then(
                     ArgumentBuilderRequired.argument("type", ArgumentTypeDamageType.damageType())
                        .then(
                           ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer(0, 32768))
                              .executes(
                                 c -> {
                                    List<? extends Entity> entities = c.getArgument("entities", EntitySelector.class).get((CommandSource)c.getSource());
                                    DamageType type = c.getArgument("type", DamageType.class);
                                    int amount = c.getArgument("amount", Integer.class);
                                    int entitiesAffected = 0;

                                    for (Entity entity : entities) {
                                       if (entity.hurt(null, amount, type)) {
                                          entitiesAffected++;
                                       }
                                    }

                                    ((CommandSource)c.getSource())
                                       .sendTranslatableMessage(
                                          "command.commands.damage.success_" + (entitiesAffected == 1 ? "single" : "multiple"), entitiesAffected
                                       );
                                    return entitiesAffected;
                                 }
                              )
                        )
                  )
            )
      );
   }
}
