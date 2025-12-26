package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeBlock;
import net.minecraft.core.net.command.arguments.ArgumentTypeIntegerCoordinates;
import net.minecraft.core.net.command.helpers.BlockInput;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.world.World;

public class CommandSetBlock implements CommandManager.CommandRegistry {
   private static final SimpleCommandExceptionType FAILURE = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.setblock.exception_failure")
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("setblock")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderRequired.argument("position", ArgumentTypeIntegerCoordinates.intCoordinates())
                  .then(
                     ArgumentBuilderRequired.argument("block", ArgumentTypeBlock.block())
                        .executes(
                           c -> {
                              CommandSource source = (CommandSource)c.getSource();
                              IntegerCoordinates coordinates = c.getArgument("position", IntegerCoordinates.class);
                              BlockInput blockInput = c.getArgument("block", BlockInput.class);
                              World world = source.getWorld();
                              int x = coordinates.getX(source);
                              int y = coordinates.getY(source, true);
                              int z = coordinates.getZ(source);
                              if (!world.isBlockLoaded(x, y, z)) {
                                 throw FAILURE.create();
                              } else {
                                 world.setBlockWithNotify(x, y, z, blockInput.getBlockId());
                                 world.setBlockMetadataWithNotify(x, y, z, blockInput.getMetadata());
                                 CommandHelper.setTileEntity(world, x, y, z, blockInput.getTag());
                                 source.sendTranslatableMessage(
                                    "command.commands.setblock.success", coordinates.getX(source), coordinates.getY(source, true), coordinates.getZ(source)
                                 );
                                 return 1;
                              }
                           }
                        )
                  )
            )
      );
   }
}
