package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeBlock;
import net.minecraft.core.net.command.arguments.ArgumentTypeIntegerCoordinates;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.BlockInput;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class CommandFill implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("fill")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderRequired.argument("first", ArgumentTypeIntegerCoordinates.intCoordinates())
                  .then(
                     ArgumentBuilderRequired.argument("second", ArgumentTypeIntegerCoordinates.intCoordinates())
                        .then(
                           ((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)ArgumentBuilderRequired.argument(
                                                "block", ArgumentTypeBlock.block()
                                             )
                                             .executes(
                                                c -> {
                                                   IntegerCoordinates first = c.getArgument("first", IntegerCoordinates.class);
                                                   IntegerCoordinates second = c.getArgument("second", IntegerCoordinates.class);
                                                   BlockInput block = c.getArgument("block", BlockInput.class);
                                                   if (CommandHelper.getVolume((CommandSource)c.getSource(), first, second) > 32768) {
                                                      throw CommandExceptions.volumeTooLarge().create();
                                                   } else {
                                                      int blocksFilled = fillReplace(
                                                         (CommandSource)c.getSource(), ((CommandSource)c.getSource()).getWorld(), first, second, block
                                                      );
                                                      ((CommandSource)c.getSource())
                                                         .sendTranslatableMessage(
                                                            blocksFilled == 1
                                                               ? "command.commands.fill.success_single"
                                                               : "command.commands.fill.success_multiple",
                                                            blocksFilled
                                                         );
                                                      return blocksFilled;
                                                   }
                                                }
                                             ))
                                          .then(
                                             ((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal("replace")
                                                   .executes(
                                                      c -> {
                                                         IntegerCoordinates first = c.getArgument("first", IntegerCoordinates.class);
                                                         IntegerCoordinates second = c.getArgument("second", IntegerCoordinates.class);
                                                         BlockInput block = c.getArgument("block", BlockInput.class);
                                                         if (CommandHelper.getVolume((CommandSource)c.getSource(), first, second) > 32768) {
                                                            throw CommandExceptions.volumeTooLarge().create();
                                                         } else {
                                                            int blocksFilled = fillReplace(
                                                               (CommandSource)c.getSource(), ((CommandSource)c.getSource()).getWorld(), first, second, block
                                                            );
                                                            ((CommandSource)c.getSource())
                                                               .sendTranslatableMessage(
                                                                  blocksFilled == 1
                                                                     ? "command.commands.fill.success_single"
                                                                     : "command.commands.fill.success_multiple",
                                                                  blocksFilled
                                                               );
                                                            return blocksFilled;
                                                         }
                                                      }
                                                   ))
                                                .then(
                                                   ArgumentBuilderRequired.argument("filter", ArgumentTypeBlock.block())
                                                      .executes(
                                                         c -> {
                                                            IntegerCoordinates first = c.getArgument("first", IntegerCoordinates.class);
                                                            IntegerCoordinates second = c.getArgument("second", IntegerCoordinates.class);
                                                            BlockInput block = c.getArgument("block", BlockInput.class);
                                                            BlockInput filter = c.getArgument("filter", BlockInput.class);
                                                            if (CommandHelper.getVolume((CommandSource)c.getSource(), first, second) > 32768) {
                                                               throw CommandExceptions.volumeTooLarge().create();
                                                            } else {
                                                               int blocksFilled = fillReplace(
                                                                  (CommandSource)c.getSource(),
                                                                  ((CommandSource)c.getSource()).getWorld(),
                                                                  first,
                                                                  second,
                                                                  block,
                                                                  filter
                                                               );
                                                               ((CommandSource)c.getSource())
                                                                  .sendTranslatableMessage(
                                                                     blocksFilled == 1
                                                                        ? "command.commands.fill.success_single"
                                                                        : "command.commands.fill.success_multiple",
                                                                     blocksFilled
                                                                  );
                                                               return blocksFilled;
                                                            }
                                                         }
                                                      )
                                                )
                                          ))
                                       .then(
                                          ArgumentBuilderLiteral.literal("hollow")
                                             .executes(
                                                c -> {
                                                   IntegerCoordinates first = c.getArgument("first", IntegerCoordinates.class);
                                                   IntegerCoordinates second = c.getArgument("second", IntegerCoordinates.class);
                                                   BlockInput block = c.getArgument("block", BlockInput.class);
                                                   if (CommandHelper.getVolume((CommandSource)c.getSource(), first, second) > 32768) {
                                                      throw CommandExceptions.volumeTooLarge().create();
                                                   } else {
                                                      int blocksFilled = fillHollow(
                                                         (CommandSource)c.getSource(), ((CommandSource)c.getSource()).getWorld(), first, second, block
                                                      );
                                                      ((CommandSource)c.getSource())
                                                         .sendTranslatableMessage(
                                                            blocksFilled == 1
                                                               ? "command.commands.fill.success_single"
                                                               : "command.commands.fill.success_multiple",
                                                            blocksFilled
                                                         );
                                                      return blocksFilled;
                                                   }
                                                }
                                             )
                                       ))
                                    .then(
                                       ArgumentBuilderLiteral.literal("outline")
                                          .executes(
                                             c -> {
                                                IntegerCoordinates first = c.getArgument("first", IntegerCoordinates.class);
                                                IntegerCoordinates second = c.getArgument("second", IntegerCoordinates.class);
                                                BlockInput block = c.getArgument("block", BlockInput.class);
                                                if (CommandHelper.getVolume((CommandSource)c.getSource(), first, second) > 32768) {
                                                   throw CommandExceptions.volumeTooLarge().create();
                                                } else {
                                                   int blocksFilled = fillOutline(
                                                      (CommandSource)c.getSource(), ((CommandSource)c.getSource()).getWorld(), first, second, block
                                                   );
                                                   ((CommandSource)c.getSource())
                                                      .sendTranslatableMessage(
                                                         blocksFilled == 1 ? "command.commands.fill.success_single" : "command.commands.fill.success_multiple",
                                                         blocksFilled
                                                      );
                                                   return blocksFilled;
                                                }
                                             }
                                          )
                                    ))
                                 .then(
                                    ArgumentBuilderLiteral.literal("keep")
                                       .executes(
                                          c -> {
                                             IntegerCoordinates first = c.getArgument("first", IntegerCoordinates.class);
                                             IntegerCoordinates second = c.getArgument("second", IntegerCoordinates.class);
                                             BlockInput block = c.getArgument("block", BlockInput.class);
                                             if (CommandHelper.getVolume((CommandSource)c.getSource(), first, second) > 32768) {
                                                throw CommandExceptions.volumeTooLarge().create();
                                             } else {
                                                int blocksFilled = fillKeep(
                                                   (CommandSource)c.getSource(), ((CommandSource)c.getSource()).getWorld(), first, second, block
                                                );
                                                ((CommandSource)c.getSource())
                                                   .sendTranslatableMessage(
                                                      blocksFilled == 1 ? "command.commands.fill.success_single" : "command.commands.fill.success_multiple",
                                                      blocksFilled
                                                   );
                                                return blocksFilled;
                                             }
                                          }
                                       )
                                 ))
                              .then(
                                 ArgumentBuilderLiteral.literal("destroy")
                                    .executes(
                                       c -> {
                                          IntegerCoordinates first = c.getArgument("first", IntegerCoordinates.class);
                                          IntegerCoordinates second = c.getArgument("second", IntegerCoordinates.class);
                                          BlockInput block = c.getArgument("block", BlockInput.class);
                                          if (CommandHelper.getVolume((CommandSource)c.getSource(), first, second) > 32768) {
                                             throw CommandExceptions.volumeTooLarge().create();
                                          } else {
                                             int blocksFilled = fillDestroy(
                                                (CommandSource)c.getSource(), ((CommandSource)c.getSource()).getWorld(), first, second, block
                                             );
                                             ((CommandSource)c.getSource())
                                                .sendTranslatableMessage(
                                                   blocksFilled == 1 ? "command.commands.fill.success_single" : "command.commands.fill.success_multiple",
                                                   blocksFilled
                                                );
                                             return blocksFilled;
                                          }
                                       }
                                    )
                              )
                        )
                  )
            )
      );
   }

   public static int fillReplace(CommandSource source, World world, IntegerCoordinates first, IntegerCoordinates second, BlockInput block) throws CommandSyntaxException {
      return fillReplace(source, world, first, second, block, null);
   }

   public static int fillReplace(
      CommandSource source, World world, IntegerCoordinates first, IntegerCoordinates second, BlockInput block, @Nullable BlockInput filter
   ) throws CommandSyntaxException {
      return fillReplace(source, world, first, second, block, filter, false);
   }

   public static int fillReplace(
      CommandSource source, World world, IntegerCoordinates first, IntegerCoordinates second, BlockInput block, @Nullable BlockInput filter, boolean destroy
   ) throws CommandSyntaxException {
      world.noNeighborUpdate = true;
      int minX = Math.min(first.getX(source), second.getX(source));
      int minY = Math.min(first.getY(source, true), second.getY(source, true));
      int minZ = Math.min(first.getZ(source), second.getZ(source));
      int maxX = Math.max(first.getX(source), second.getX(source));
      int maxY = Math.max(first.getY(source, true), second.getY(source, true));
      int maxZ = Math.max(first.getZ(source), second.getZ(source));
      int blocksFilled = 0;

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               CompoundTag blockTag = new CompoundTag();
               TileEntity tileEntity = world.getTileEntity(x, y, z);
               if (tileEntity != null) {
                  tileEntity.writeToNBT(blockTag);
               }

               if ((
                     block.getBlockId() != world.getBlockId(x, y, z)
                        || block.getMetadata() != world.getBlockMetadata(x, y, z)
                        || !CommandHelper.blockEntitiesAreEqual(block.getTag(), CommandHelper.tagFrom(world.getTileEntity(x, y, z)))
                  )
                  && (
                     filter == null
                        || world.getBlockId(x, y, z) == filter.getBlockId()
                           && world.getBlockMetadata(x, y, z) == filter.getMetadata()
                           && (filter.getTag().getValues().isEmpty() || CommandHelper.blockEntitiesAreEqual(blockTag, filter.getTag()))
                  )) {
                  blocksFilled++;
                  if (destroy && world.getBlock(x, y, z) != null) {
                     world.getBlock(x, y, z).getBreakResult(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), world.getTileEntity(x, y, z));
                  }
               }

               if (filter == null
                  || world.getBlockId(x, y, z) == filter.getBlockId()
                     && world.getBlockMetadata(x, y, z) == filter.getMetadata()
                     && (
                        !filter.getTag().getValue().isEmpty()
                           || CommandHelper.blockEntitiesAreEqual(CommandHelper.tagFrom(world.getTileEntity(x, y, z)), filter.getTag())
                     )) {
                  world.setBlockWithNotify(x, y, z, block.getBlockId());
                  world.setBlockMetadataWithNotify(x, y, z, block.getMetadata());
                  CommandHelper.setTileEntity(world, x, y, z, block.getTag());
               }
            }
         }
      }

      world.noNeighborUpdate = false;
      return blocksFilled;
   }

   public static int fillHollow(CommandSource source, World world, IntegerCoordinates first, IntegerCoordinates second, BlockInput block) throws CommandSyntaxException {
      world.noNeighborUpdate = true;
      int minX = Math.min(first.getX(source), second.getX(source));
      int minY = Math.min(first.getY(source, true), second.getY(source, true));
      int minZ = Math.min(first.getZ(source), second.getZ(source));
      int maxX = Math.max(first.getX(source), second.getX(source));
      int maxY = Math.max(first.getY(source, true), second.getY(source, true));
      int maxZ = Math.max(first.getZ(source), second.getZ(source));
      int blocksFilled = 0;

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               boolean isOutline = x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ;
               if (isOutline
                     && (
                        block.getBlockId() != world.getBlockId(x, y, z)
                           || block.getMetadata() != world.getBlockMetadata(x, y, z)
                           || !CommandHelper.blockEntitiesAreEqual(block.getTag(), CommandHelper.tagFrom(world.getTileEntity(x, y, z)))
                     )
                  || !isOutline && world.getBlockId(x, y, z) != 0) {
                  blocksFilled++;
                  if (!isOutline && world.getBlock(x, y, z) != null) {
                     world.getBlock(x, y, z).getBreakResult(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), world.getTileEntity(x, y, z));
                  }
               }

               world.setBlockWithNotify(x, y, z, isOutline ? block.getBlockId() : 0);
               world.setBlockMetadataWithNotify(x, y, z, isOutline ? block.getMetadata() : 0);
               CommandHelper.setTileEntity(world, x, y, z, block.getTag());
            }
         }
      }

      world.noNeighborUpdate = false;
      return blocksFilled;
   }

   public static int fillOutline(CommandSource source, World world, IntegerCoordinates first, IntegerCoordinates second, BlockInput block) throws CommandSyntaxException {
      world.noNeighborUpdate = true;
      int minX = Math.min(first.getX(source), second.getX(source));
      int minY = Math.min(first.getY(source, true), second.getY(source, true));
      int minZ = Math.min(first.getZ(source), second.getZ(source));
      int maxX = Math.max(first.getX(source), second.getX(source));
      int maxY = Math.max(first.getY(source, true), second.getY(source, true));
      int maxZ = Math.max(first.getZ(source), second.getZ(source));
      int blocksFilled = 0;

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               boolean isOutline = x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ;
               if (isOutline
                  && (
                     block.getBlockId() != world.getBlockId(x, y, z)
                        || block.getMetadata() != world.getBlockMetadata(x, y, z)
                        || !CommandHelper.blockEntitiesAreEqual(block.getTag(), CommandHelper.tagFrom(world.getTileEntity(x, y, z)))
                  )) {
                  blocksFilled++;
               }

               if (isOutline) {
                  world.setBlockWithNotify(x, y, z, block.getBlockId());
                  world.setBlockMetadataWithNotify(x, y, z, block.getMetadata());
                  CommandHelper.setTileEntity(world, x, y, z, block.getTag());
               }
            }
         }
      }

      world.noNeighborUpdate = false;
      return blocksFilled;
   }

   public static int fillKeep(CommandSource source, World world, IntegerCoordinates first, IntegerCoordinates second, BlockInput block) throws CommandSyntaxException {
      return fillReplace(source, world, first, second, block, new BlockInput(null, 0, new CompoundTag()));
   }

   public static int fillDestroy(CommandSource source, World world, IntegerCoordinates first, IntegerCoordinates second, BlockInput block) throws CommandSyntaxException {
      return fillReplace(source, world, first, second, block, null, true);
   }
}
