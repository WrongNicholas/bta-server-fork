package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeBool;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.core.data.gamerule.GameRule;
import net.minecraft.core.data.gamerule.GameRuleBoolean;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeGameruleGeneric;
import net.minecraft.core.net.packet.PacketGameRule;

public class CommandGameRule implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      ArgumentBuilderLiteral<CommandSource> argumentBuilder = ArgumentBuilderLiteral.<CommandSource>literal("gamerule").requires(CommandSource::hasAdmin);

      for (GameRule<?> gameRule : Registries.GAME_RULES) {
         ArgumentBuilderRequired<CommandSource, ?> gameRuleValueArgument;
         if (gameRule instanceof GameRuleBoolean) {
            gameRuleValueArgument = (ArgumentBuilderRequired<CommandSource, ?>)ArgumentBuilderRequired.argument("value", ArgumentTypeBool.bool())
               .executes(
                  c -> {
                     ((CommandSource)c.getSource()).getWorld().getLevelData().getGameRules().setValue(gameRule, ArgumentTypeBool.getBool(c, "value"));
                     ((CommandSource)c.getSource())
                        .sendPacketToAllPlayers(() -> new PacketGameRule(((CommandSource)c.getSource()).getWorld().getLevelData().getGameRules()));
                     ((CommandSource)c.getSource())
                        .sendTranslatableMessage("command.commands.gamerule.set", gameRule.getKey(), ArgumentTypeBool.getBool(c, "value"));
                     return 1;
                  }
               );
         } else {
            gameRuleValueArgument = (ArgumentBuilderRequired<CommandSource, ?>)ArgumentBuilderRequired.argument(
                  "value", ArgumentTypeGameruleGeneric.gameRule(gameRule)
               )
               .executes(
                  c -> {
                     Object o = c.getArgument("value", Object.class);
                     ((CommandSource)c.getSource()).getWorld().getLevelData().getGameRules().setValue(gameRule, o);
                     ((CommandSource)c.getSource())
                        .sendPacketToAllPlayers(() -> new PacketGameRule(((CommandSource)c.getSource()).getWorld().getLevelData().getGameRules()));
                     ((CommandSource)c.getSource()).sendTranslatableMessage("command.commands.gamerule.set", gameRule.getKey(), o);
                     return 1;
                  }
               );
         }

         argumentBuilder.then(
            ((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal(gameRule.getKey())
                  .executes(
                     c -> {
                        ((CommandSource)c.getSource())
                           .sendTranslatableMessage(
                              "command.commands.gamerule.get", gameRule.getKey(), ((CommandSource)c.getSource()).getWorld().getGameRuleValue(gameRule)
                           );
                        return 1;
                     }
                  ))
               .then(gameRuleValueArgument)
         );
      }

      CommandNode<CommandSource> commandNode = dispatcher.register(argumentBuilder);
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("gr").requires(CommandSource::hasAdmin).redirect(commandNode));
   }
}
