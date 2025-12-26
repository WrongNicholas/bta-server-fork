package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeChunkCoordinates;
import net.minecraft.core.net.command.helpers.Coordinates2D;
import net.minecraft.core.world.LevelListener;
import net.minecraft.core.world.World;

public class CommandChunk implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("chunk")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderLiteral.literal("reset")
                  .then(ArgumentBuilderRequired.argument("position", ArgumentTypeChunkCoordinates.chunkCoordinates()).executes(c -> {
                     CommandSource source = (CommandSource)c.getSource();
                     Coordinates2D coordinates2D = c.getArgument("position", Coordinates2D.class);
                     World world = source.getWorld();
                     int x = coordinates2D.getX(source);
                     int z = coordinates2D.getZ(source);
                     world.getChunkProvider().regenerateChunk(x, z);

                     for (LevelListener listener : world.listeners) {
                        listener.allChanged(false, true);
                     }

                     source.sendTranslatableMessage("command.commands.chunk.reset.success", x, z);
                     return 1;
                  }))
            )
      );
   }
}
