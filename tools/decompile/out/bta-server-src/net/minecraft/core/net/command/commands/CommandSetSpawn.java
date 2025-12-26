package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeIntegerCoordinates;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import net.minecraft.core.world.chunk.ChunkCoordinates;

public class CommandSetSpawn implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      CommandNode<CommandSource> command = dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("setworldspawn")
            .requires(CommandSource::hasAdmin)
            .then(ArgumentBuilderRequired.argument("position", ArgumentTypeIntegerCoordinates.intCoordinates()).executes(c -> {
               CommandSource source = (CommandSource)c.getSource();
               IntegerCoordinates coordinates = c.getArgument("position", IntegerCoordinates.class);
               int x = coordinates.getX(source);
               int y = coordinates.getY(source, true);
               int z = coordinates.getZ(source);
               source.getWorld().setSpawnPoint(new ChunkCoordinates(x, y, z));
               source.sendTranslatableMessage("command.commands.setspawn.success", x, y, z);
               return 1;
            }))
      );
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("setspawn").requires(CommandSource::hasAdmin).redirect(command));
   }
}
