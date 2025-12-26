package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.arguments.ArgumentTypeItemStack;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.command.util.CommandHelper;

public class CommandGive implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("give")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderRequired.argument("target", ArgumentTypeEntity.usernames())
                  .then(
                     ((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("item", ArgumentTypeItemStack.itemStack())
                           .executes(
                              c -> {
                                 CommandSource source = (CommandSource)c.getSource();
                                 ItemStack itemStack = c.getArgument("item", ItemStack.class);
                                 int amount = itemStack.stackSize;
                                 EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);
                                 List<? extends Entity> entities = entitySelector.get(source);

                                 for (Entity player : entities) {
                                    ((Player)player).inventory.insertItem(itemStack, true);
                                    if (itemStack.stackSize > 0) {
                                       ((Player)player).dropPlayerItem(itemStack);
                                    }
                                 }

                                 if (entities.size() == 1) {
                                    source.sendTranslatableMessage(
                                       "command.commands.give.success_single_entity",
                                       CommandHelper.getEntityName(entities.get(0)),
                                       amount,
                                       itemStack.getDisplayName()
                                    );
                                 } else {
                                    source.sendTranslatableMessage(
                                       "command.commands.give.success_single_entity", entities.size(), amount, itemStack.getDisplayName()
                                    );
                                 }

                                 return 1;
                              }
                           ))
                        .then(
                           ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer(1, 6400))
                              .executes(
                                 c -> {
                                    CommandSource source = (CommandSource)c.getSource();
                                    ItemStack itemStack = c.getArgument("item", ItemStack.class);
                                    EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);
                                    List<? extends Entity> entities = entitySelector.get(source);
                                    int amount = c.getArgument("amount", Integer.class);

                                    for (Entity player : entities) {
                                       int incompleteStack = amount % 64;

                                       for (int i = 0; i < (amount - incompleteStack) / 64; i++) {
                                          ItemStack itemStack1 = ItemStack.copyItemStack(itemStack);
                                          itemStack1.stackSize = 64;
                                          ((Player)player).inventory.insertItem(itemStack1, true);
                                          if (itemStack1.stackSize > 0) {
                                             ((Player)player).dropPlayerItem(itemStack1);
                                          }
                                       }

                                       if (incompleteStack > 0) {
                                          ItemStack itemStack1 = ItemStack.copyItemStack(itemStack);
                                          itemStack1.stackSize = incompleteStack;
                                          ((Player)player).inventory.insertItem(itemStack1, true);
                                          if (itemStack1.stackSize > 0) {
                                             ((Player)player).dropPlayerItem(itemStack1);
                                          }
                                       }
                                    }

                                    if (entities.size() == 1) {
                                       source.sendTranslatableMessage(
                                          "command.commands.give.success_single_entity",
                                          CommandHelper.getEntityName(entities.get(0)),
                                          amount,
                                          itemStack.getDisplayName()
                                       );
                                    } else {
                                       source.sendTranslatableMessage(
                                          "command.commands.give.success_single_entity", entities.size(), amount, itemStack.getDisplayName()
                                       );
                                    }

                                    return 1;
                                 }
                              )
                        )
                  )
            )
      );
   }
}
