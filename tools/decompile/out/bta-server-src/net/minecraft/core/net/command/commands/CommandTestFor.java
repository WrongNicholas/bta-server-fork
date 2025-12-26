package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.nbt.tags.CompoundTag;
import java.util.List;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeBlock;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.arguments.ArgumentTypeIntegerCoordinates;
import net.minecraft.core.net.command.helpers.BlockInput;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import net.minecraft.core.net.command.util.CommandHelper;

public class CommandTestFor implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("testfor")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderLiteral.literal("entity")
                  .then(
                     ArgumentBuilderRequired.argument("entities", ArgumentTypeEntity.entities())
                        .executes(
                           c -> {
                              List<? extends Entity> entities = c.getArgument("entities", EntitySelector.class).get((CommandSource)c.getSource());
                              ((CommandSource)c.getSource())
                                 .sendTranslatableMessage(
                                    "command.commands.testfor.entity." + (entities.isEmpty() ? "none" : (entities.size() == 1 ? "single" : "multiple")),
                                    entities.size()
                                 );
                              return entities.size();
                           }
                        )
                  )
            )
            .then(
               ((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal("block")
                     .then(
                        ArgumentBuilderRequired.argument("block", ArgumentTypeBlock.block())
                           .then(
                              ArgumentBuilderRequired.argument("second", ArgumentTypeIntegerCoordinates.intCoordinates())
                                 .executes(
                                    c -> {
                                       BlockInput block = c.getArgument("block", BlockInput.class);
                                       IntegerCoordinates secondPos = c.getArgument("second", IntegerCoordinates.class);
                                       CompoundTag blockInWorldCompoundTag = new CompoundTag();
                                       TileEntity tileEntity = ((CommandSource)c.getSource())
                                          .getWorld()
                                          .getTileEntity(
                                             secondPos.getX((CommandSource)c.getSource()),
                                             secondPos.getY((CommandSource)c.getSource(), true),
                                             secondPos.getZ((CommandSource)c.getSource())
                                          );
                                       if (tileEntity != null) {
                                          tileEntity.writeToNBT(blockInWorldCompoundTag);
                                       }

                                       if (((CommandSource)c.getSource())
                                                .getWorld()
                                                .getBlock(
                                                   secondPos.getX((CommandSource)c.getSource()),
                                                   secondPos.getY((CommandSource)c.getSource(), true),
                                                   secondPos.getZ((CommandSource)c.getSource())
                                                )
                                             != block.getBlock()
                                          || ((CommandSource)c.getSource())
                                                .getWorld()
                                                .getBlockMetadata(
                                                   secondPos.getX((CommandSource)c.getSource()),
                                                   secondPos.getY((CommandSource)c.getSource(), true),
                                                   secondPos.getZ((CommandSource)c.getSource())
                                                )
                                             != block.getMetadata()
                                          || blockInWorldCompoundTag != block.getTag() && !block.getTag().getValues().isEmpty()) {
                                          ((CommandSource)c.getSource()).sendTranslatableMessage("command.commands.testfor.block.failure");
                                          return 0;
                                       } else {
                                          ((CommandSource)c.getSource()).sendTranslatableMessage("command.commands.testfor.block.success");
                                          return 1;
                                       }
                                    }
                                 )
                           )
                     ))
                  .then(
                     ArgumentBuilderRequired.argument("first", ArgumentTypeIntegerCoordinates.intCoordinates())
                        .then(
                           ArgumentBuilderRequired.argument("second", ArgumentTypeIntegerCoordinates.intCoordinates())
                              .executes(
                                 c -> {
                                    IntegerCoordinates firstPos = c.getArgument("first", IntegerCoordinates.class);
                                    IntegerCoordinates secondPos = c.getArgument("second", IntegerCoordinates.class);
                                    Block<?> firstBlock = ((CommandSource)c.getSource())
                                       .getWorld()
                                       .getBlock(
                                          firstPos.getX((CommandSource)c.getSource()),
                                          firstPos.getY((CommandSource)c.getSource(), true),
                                          firstPos.getZ((CommandSource)c.getSource())
                                       );
                                    Block<?> secondBlock = ((CommandSource)c.getSource())
                                       .getWorld()
                                       .getBlock(
                                          secondPos.getX((CommandSource)c.getSource()),
                                          secondPos.getY((CommandSource)c.getSource(), true),
                                          secondPos.getZ((CommandSource)c.getSource())
                                       );
                                    if (firstBlock != secondBlock) {
                                       ((CommandSource)c.getSource()).sendTranslatableMessage("command.commands.testfor.block.failure");
                                       return 0;
                                    } else {
                                       int firstMetadata = ((CommandSource)c.getSource())
                                          .getWorld()
                                          .getBlockMetadata(
                                             firstPos.getX((CommandSource)c.getSource()),
                                             firstPos.getY((CommandSource)c.getSource(), true),
                                             firstPos.getZ((CommandSource)c.getSource())
                                          );
                                       int secondMetadata = ((CommandSource)c.getSource())
                                          .getWorld()
                                          .getBlockMetadata(
                                             secondPos.getX((CommandSource)c.getSource()),
                                             secondPos.getY((CommandSource)c.getSource(), true),
                                             secondPos.getZ((CommandSource)c.getSource())
                                          );
                                       if (firstMetadata != secondMetadata) {
                                          ((CommandSource)c.getSource()).sendTranslatableMessage("command.commands.testfor.block.failure");
                                          return 0;
                                       } else {
                                          CompoundTag firstTag = new CompoundTag();
                                          TileEntity firstTileEntity = ((CommandSource)c.getSource())
                                             .getWorld()
                                             .getTileEntity(
                                                firstPos.getX((CommandSource)c.getSource()),
                                                firstPos.getY((CommandSource)c.getSource(), true),
                                                firstPos.getZ((CommandSource)c.getSource())
                                             );
                                          if (firstTileEntity != null) {
                                             firstTileEntity.writeToNBT(firstTag);
                                          }

                                          CompoundTag secondTag = new CompoundTag();
                                          TileEntity secondTileEntity = ((CommandSource)c.getSource())
                                             .getWorld()
                                             .getTileEntity(
                                                secondPos.getX((CommandSource)c.getSource()),
                                                secondPos.getY((CommandSource)c.getSource(), true),
                                                secondPos.getZ((CommandSource)c.getSource())
                                             );
                                          if (secondTileEntity != null) {
                                             secondTileEntity.writeToNBT(secondTag);
                                          }

                                          if (!CommandHelper.blockEntitiesAreEqual(firstTag, secondTag)) {
                                             ((CommandSource)c.getSource()).sendTranslatableMessage("command.commands.testfor.block.failure");
                                             return 0;
                                          } else {
                                             ((CommandSource)c.getSource()).sendTranslatableMessage("command.commands.testfor.block.success");
                                             return 1;
                                          }
                                       }
                                    }
                                 }
                              )
                        )
                  )
            )
      );
   }
}
