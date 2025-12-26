package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.nbt.tags.CompoundTag;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeBlock;
import net.minecraft.core.net.command.arguments.ArgumentTypeDimension;
import net.minecraft.core.net.command.arguments.ArgumentTypeIntegerCoordinates;
import net.minecraft.core.net.command.helpers.BlockInput;
import net.minecraft.core.net.command.helpers.ClonedBlock;
import net.minecraft.core.net.command.helpers.IntegerCoordinate;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class CommandClone implements CommandManager.CommandRegistry {
   private static final SimpleCommandExceptionType INSIDE_CLONED_AREA = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.clone.exception_inside_cloned_area")
   );
   private static final SimpleCommandExceptionType DESTINATION_NOT_LOADED = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.clone.exception_destination_not_loaded")
   );
   private static final SimpleCommandExceptionType SOURCE_NOT_LOADED = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.clone.exception_source_not_loaded")
   );
   private static final SimpleCommandExceptionType NOWHERE_LOADED = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.commands.clone.exception_nowhere_loaded")
   );

   @Override
   public void register(CommandDispatcher<CommandSource> commandDispatcher) {
      commandDispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("clone")
            .requires(CommandSource::hasAdmin)
            .then(beginEndDestinationAndModeSuffix(c -> c.getSource().getWorld()))
            .then(
               ArgumentBuilderLiteral.literal("from")
                  .then(
                     ArgumentBuilderRequired.<CommandSource, Dimension>argument("sourceDimension", ArgumentTypeDimension.dimension())
                        .then(beginEndDestinationAndModeSuffix(c -> c.getSource().getWorld(c.getArgument("sourceDimension", Dimension.class).id)))
                  )
            )
      );
   }

   private static ArgumentBuilder<CommandSource, ?> beginEndDestinationAndModeSuffix(
      CommandClone.CommandFunction<CommandContext<CommandSource>, World> commandFunction
   ) {
      return ArgumentBuilderRequired.argument("begin", ArgumentTypeIntegerCoordinates.intCoordinates())
         .then(
            ((ArgumentBuilderRequired)ArgumentBuilderRequired.<CommandSource, IntegerCoordinates>argument(
                     "end", ArgumentTypeIntegerCoordinates.intCoordinates()
                  )
                  .then(destinationAndModeSuffix(commandFunction, c -> c.getSource().getWorld())))
               .then(
                  ArgumentBuilderLiteral.literal("to")
                     .then(
                        ArgumentBuilderRequired.<CommandSource, Dimension>argument("targetDimension", ArgumentTypeDimension.dimension())
                           .then(destinationAndModeSuffix(commandFunction, c -> c.getSource().getWorld(c.getArgument("targetDimension", Dimension.class).id)))
                     )
               )
         );
   }

   private static CommandClone.WorldAndPosition getWorldAndPosition(CommandContext<CommandSource> commandContext, World world, String string) {
      IntegerCoordinates coordinates = commandContext.getArgument(string, IntegerCoordinates.class);
      return new CommandClone.WorldAndPosition(world, coordinates);
   }

   private static ArgumentBuilder<CommandSource, ?> destinationAndModeSuffix(
      CommandClone.CommandFunction<CommandContext<CommandSource>, World> commandFunction,
      CommandClone.CommandFunction<CommandContext<CommandSource>, World> commandFunction2
   ) {
      CommandClone.CommandFunction<CommandContext<CommandSource>, CommandClone.WorldAndPosition> commandFunction3 = c -> getWorldAndPosition(
         c, commandFunction.apply(c), "begin"
      );
      CommandClone.CommandFunction<CommandContext<CommandSource>, CommandClone.WorldAndPosition> commandFunction4 = c -> getWorldAndPosition(
         c, commandFunction.apply(c), "end"
      );
      CommandClone.CommandFunction<CommandContext<CommandSource>, CommandClone.WorldAndPosition> commandFunction5 = c -> getWorldAndPosition(
         c, commandFunction2.apply(c), "destination"
      );
      return ((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)ArgumentBuilderRequired.argument(
                     "destination", ArgumentTypeIntegerCoordinates.intCoordinates()
                  )
                  .executes(
                     c -> clone(
                        (CommandSource)c.getSource(),
                        commandFunction3.apply(c),
                        commandFunction4.apply(c),
                        commandFunction5.apply(c),
                        null,
                        CommandClone.CloneMode.NORMAL
                     )
                  ))
               .then(
                  wrapWithCloneMode(
                     commandFunction3,
                     commandFunction4,
                     commandFunction5,
                     commandContext -> null,
                     ArgumentBuilderLiteral.literal("replace")
                        .executes(
                           c -> clone(
                              (CommandSource)c.getSource(),
                              commandFunction3.apply(c),
                              commandFunction4.apply(c),
                              commandFunction5.apply(c),
                              null,
                              CommandClone.CloneMode.NORMAL
                           )
                        )
                  )
               ))
            .then(
               wrapWithCloneMode(
                  commandFunction3,
                  commandFunction4,
                  commandFunction5,
                  commandContext -> new BlockInput(null, 0, new CompoundTag()),
                  ArgumentBuilderLiteral.literal("masked")
                     .executes(
                        c -> clone(
                           (CommandSource)c.getSource(),
                           commandFunction3.apply(c),
                           commandFunction4.apply(c),
                           commandFunction5.apply(c),
                           new BlockInput(null, 0, new CompoundTag()),
                           CommandClone.CloneMode.NORMAL
                        )
                     )
               )
            ))
         .then(
            ArgumentBuilderLiteral.<CommandSource>literal("filtered")
               .then(
                  wrapWithCloneMode(
                     commandFunction3,
                     commandFunction4,
                     commandFunction5,
                     c -> c.getArgument("filter", BlockInput.class),
                     ArgumentBuilderRequired.argument("filter", ArgumentTypeBlock.block())
                        .executes(
                           c -> clone(
                              (CommandSource)c.getSource(),
                              commandFunction3.apply(c),
                              commandFunction4.apply(c),
                              commandFunction5.apply(c),
                              c.getArgument("filter", BlockInput.class),
                              CommandClone.CloneMode.NORMAL
                           )
                        )
                  )
               )
         );
   }

   private static ArgumentBuilder<CommandSource, ?> wrapWithCloneMode(
      CommandClone.CommandFunction<CommandContext<CommandSource>, CommandClone.WorldAndPosition> commandFunction,
      CommandClone.CommandFunction<CommandContext<CommandSource>, CommandClone.WorldAndPosition> commandFunction2,
      CommandClone.CommandFunction<CommandContext<CommandSource>, CommandClone.WorldAndPosition> commandFunction3,
      CommandClone.CommandFunction<CommandContext<CommandSource>, BlockInput> commandFunction4,
      ArgumentBuilder<CommandSource, ?> argumentBuilder
   ) {
      return argumentBuilder.then(
            ArgumentBuilderLiteral.literal("force")
               .executes(
                  c -> clone(
                     (CommandSource)c.getSource(),
                     commandFunction.apply(c),
                     commandFunction2.apply(c),
                     commandFunction3.apply(c),
                     commandFunction4.apply(c),
                     CommandClone.CloneMode.FORCE
                  )
               )
         )
         .then(
            ArgumentBuilderLiteral.literal("move")
               .executes(
                  c -> clone(
                     (CommandSource)c.getSource(),
                     commandFunction.apply(c),
                     commandFunction2.apply(c),
                     commandFunction3.apply(c),
                     commandFunction4.apply(c),
                     CommandClone.CloneMode.MOVE
                  )
               )
         )
         .then(
            ArgumentBuilderLiteral.literal("normal")
               .executes(
                  c -> clone(
                     (CommandSource)c.getSource(),
                     commandFunction.apply(c),
                     commandFunction2.apply(c),
                     commandFunction3.apply(c),
                     commandFunction4.apply(c),
                     CommandClone.CloneMode.NORMAL
                  )
               )
         );
   }

   public static int clone(
      CommandSource source,
      CommandClone.WorldAndPosition start,
      CommandClone.WorldAndPosition end,
      CommandClone.WorldAndPosition destination,
      @Nullable BlockInput filter,
      CommandClone.CloneMode cloneMode
   ) throws CommandSyntaxException {
      int minX = Math.min(start.getPosition().getX(source), end.getPosition().getX(source));
      int minY = Math.min(start.getPosition().getY(source, true), end.getPosition().getY(source, true));
      int minZ = Math.min(start.getPosition().getZ(source), end.getPosition().getZ(source));
      int maxX = Math.max(start.getPosition().getX(source), end.getPosition().getX(source));
      int maxY = Math.max(start.getPosition().getY(source, true), end.getPosition().getY(source, true));
      int maxZ = Math.max(start.getPosition().getZ(source), end.getPosition().getZ(source));
      int destinationX = destination.getPosition().getX(source);
      int destinationY = destination.getPosition().getY(source, true);
      int destinationZ = destination.getPosition().getZ(source);
      if (cloneMode == CommandClone.CloneMode.NORMAL
         && AABB.getTemporaryBB(minX, minY, minZ, maxX, maxY, maxZ).contains(Vec3.getTempVec3(destinationX, destinationY, destinationZ))
         && start.getWorld() == destination.getWorld()) {
         throw INSIDE_CLONED_AREA.create();
      } else {
         start.getWorld().noNeighborUpdate = true;
         Map<IntegerCoordinates, ClonedBlock> map = new HashMap<>();

         for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
               for (int z = minZ; z <= maxZ; z++) {
                  if (!start.getWorld().isBlockLoaded(x, y, z)) {
                     if (!destination.getWorld().isBlockLoaded(destinationX, destinationY, destinationZ)) {
                        throw NOWHERE_LOADED.create();
                     }

                     throw SOURCE_NOT_LOADED.create();
                  }

                  map.put(
                     new IntegerCoordinates(
                        new IntegerCoordinate(false, x - minX), new IntegerCoordinate(false, y - minY), new IntegerCoordinate(false, z - minZ)
                     ),
                     new ClonedBlock(start.getWorld().getBlock(x, y, z), start.getWorld().getBlockMetadata(x, y, z), start.getWorld().getTileEntity(x, y, z))
                  );
                  if (cloneMode == CommandClone.CloneMode.MOVE) {
                     start.getWorld().setBlockWithNotify(x, y, z, 0);
                  }
               }
            }
         }

         if (!destination.getWorld().isBlockLoaded(destinationX, destinationY, destinationZ)) {
            throw DESTINATION_NOT_LOADED.create();
         } else {
            start.getWorld().noNeighborUpdate = false;
            int clonedBlocks = 0;
            destination.getWorld().noNeighborUpdate = true;

            for (Entry<IntegerCoordinates, ClonedBlock> entry : map.entrySet()) {
               int offsetDestinationX = destinationX + entry.getKey().getX(source);
               int offsetDestinationY = destinationY + entry.getKey().getY(source, true);
               int offsetDestinationZ = destinationZ + entry.getKey().getZ(source);
               CompoundTag blockTag = new CompoundTag();
               TileEntity tileEntity = destination.getWorld().getTileEntity(offsetDestinationX, offsetDestinationY, offsetDestinationZ);
               if (tileEntity != null) {
                  tileEntity.writeToNBT(blockTag);
               }

               if (filter == null
                  || destination.getWorld().getBlockId(offsetDestinationX, offsetDestinationY, offsetDestinationZ) == filter.getBlockId()
                     && destination.getWorld().getBlockMetadata(offsetDestinationX, offsetDestinationY, offsetDestinationZ) == filter.getMetadata()
                     && (filter.getTag().getValues().isEmpty() || CommandHelper.blockEntitiesAreEqual(blockTag, filter.getTag()))) {
                  if (destination.getWorld().getBlockId(offsetDestinationX, offsetDestinationY, offsetDestinationZ) != entry.getValue().getBlockId()
                     || destination.getWorld().getBlockMetadata(offsetDestinationX, offsetDestinationY, offsetDestinationZ) != entry.getValue().getMetadata()) {
                     clonedBlocks++;
                  }

                  destination.getWorld().setBlockWithNotify(offsetDestinationX, offsetDestinationY, offsetDestinationZ, entry.getValue().getBlockId());
                  destination.getWorld().setBlockMetadataWithNotify(offsetDestinationX, offsetDestinationY, offsetDestinationZ, entry.getValue().getMetadata());
                  CommandHelper.setTileEntity(
                     destination.getWorld(), offsetDestinationX, offsetDestinationY, offsetDestinationZ, entry.getValue().getTileEntity()
                  );
               }
            }

            destination.getWorld().noNeighborUpdate = false;
            source.sendTranslatableMessage(
               clonedBlocks == 1 ? "command.commands.clone.success_single" : "command.commands.clone.success_multiple", clonedBlocks
            );
            return clonedBlocks;
         }
      }
   }

   public static enum CloneMode {
      FORCE,
      MOVE,
      NORMAL;
   }

   @FunctionalInterface
   interface CommandFunction<T, R> {
      R apply(T var1) throws CommandSyntaxException;
   }

   public static class WorldAndPosition {
      private final World world;
      private final IntegerCoordinates position;

      WorldAndPosition(World world, IntegerCoordinates position) {
         this.world = world;
         this.position = position;
      }

      public World getWorld() {
         return this.world;
      }

      public IntegerCoordinates getPosition() {
         return this.position;
      }
   }
}
