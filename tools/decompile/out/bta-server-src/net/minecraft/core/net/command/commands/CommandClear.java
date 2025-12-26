package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import com.mojang.nbt.tags.Tag;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.arguments.ArgumentTypeItemStack;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.EntitySelector;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CommandClear implements CommandManager.CommandRegistry {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final SimpleCommandExceptionType FAILURE = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.clear.exception_failure")
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("clear").requires(CommandSource::hasAdmin).executes(c -> {
         CommandSource source = c.getSource();
         Player sender = source.getSender();
         if (sender == null) {
            throw CommandExceptions.notInWorld().create();
         } else {
            int itemsCleared = clearItemsFromEntity(sender, null);
            if (itemsCleared == 0) {
               throw FAILURE.create();
            } else {
               source.sendMessage(getMessage(itemsCleared, Collections.singletonList(sender)));
               return 1;
            }
         }
      }).then(((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("players", ArgumentTypeEntity.usernames()).executes(c -> {
         CommandSource source = (CommandSource)c.getSource();
         List<? extends Entity> players = c.getArgument("players", EntitySelector.class).get(source);
         int itemsCleared = 0;

         for (Entity player : players) {
            itemsCleared += clearItemsFromEntity((Player)player, null);
         }

         if (itemsCleared == 0) {
            throw FAILURE.create();
         } else {
            source.sendMessage(getMessage(itemsCleared, players));
            return 1;
         }
      })).then(ArgumentBuilderRequired.argument("item", ArgumentTypeItemStack.itemStack()).executes(c -> {
         CommandSource source = (CommandSource)c.getSource();
         List<? extends Entity> players = c.getArgument("players", EntitySelector.class).get(source);
         ItemStack itemStack = c.getArgument("item", ItemStack.class);
         int itemsCleared = 0;

         for (Entity player : players) {
            itemsCleared += clearItemsFromEntity((Player)player, itemStack);
         }

         if (itemsCleared == 0) {
            throw FAILURE.create();
         } else {
            source.sendMessage(getMessage(itemsCleared, players));
            return 1;
         }
      }))));
   }

   private static int clearItemsFromEntity(Player entityPlayer, @Nullable ItemStack itemStack) {
      int itemsCleared = 0;

      for (int i = 0; i < entityPlayer.inventory.getContainerSize(); i++) {
         ItemStack stackInSlot = entityPlayer.inventory.getItem(i);
         if (stackInSlot != null && (itemStack == null || matchesItemStack(itemStack, stackInSlot))) {
            itemsCleared++;
            entityPlayer.inventory.setItem(i, null);
         }
      }

      return itemsCleared;
   }

   private static boolean matchesItemStack(ItemStack checkedStack, ItemStack input) {
      ItemStack checkedStackCopy = checkedStack.copy();
      ItemStack inputCopy = input.copy();
      checkedStackCopy.stackSize = 1;
      inputCopy.stackSize = 1;
      if (checkedStackCopy.getData().getValues().isEmpty()) {
         return checkedStackCopy.isStackEqual(inputCopy);
      } else {
         LOGGER.info(checkedStackCopy.itemID + ", " + inputCopy.itemID + ", " + (checkedStackCopy.itemID == inputCopy.itemID));
         LOGGER.info(checkedStackCopy.getMetadata() + ", " + inputCopy.getMetadata() + ", " + (checkedStackCopy.getMetadata() == inputCopy.getMetadata()));
         LOGGER.info(
            checkedStackCopy.getData().getValues()
               + ", "
               + inputCopy.getData().getValues()
               + ", "
               + inputCopy.getData().getValues().containsAll(checkedStackCopy.getData().getValues())
         );
         return checkedStackCopy.isStackEqual(inputCopy) && matchesTag(checkedStackCopy, inputCopy);
      }
   }

   private static boolean matchesTag(ItemStack checkedStack, ItemStack input) {
      for (Entry<String, Tag<?>> entry : checkedStack.getData().getValue().entrySet()) {
         if (!input.getData().getValue().containsKey(entry.getKey())
            || input.getData().getValue().get(entry.getKey()).getValue() != entry.getValue().getValue()
               && !input.getData().getValue().get(entry.getKey()).getValue().equals(entry.getValue().getValue())) {
            return false;
         }
      }

      return true;
   }

   private static String getMessage(int itemsCleared, List<? extends Entity> players) {
      StringBuilder keyBuilder = new StringBuilder("command.commands.clear.success_");
      if (itemsCleared > 1) {
         keyBuilder.append("multiple_items_");
      } else if (itemsCleared == 1) {
         keyBuilder.append("single_item_");
      }

      if (players.size() > 1) {
         keyBuilder.append("multiple_entities");
      } else if (players.size() == 1) {
         keyBuilder.append("single_entity");
      }

      return I18n.getInstance().translateKeyAndFormat(keyBuilder.toString(), itemsCleared, players.size());
   }
}
