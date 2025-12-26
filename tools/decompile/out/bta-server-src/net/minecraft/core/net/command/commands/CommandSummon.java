package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntitySummon;
import net.minecraft.core.net.command.arguments.ArgumentTypeVec3;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.DoubleCoordinates;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;

public class CommandSummon implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("summon")
            .requires(CommandSource::hasAdmin)
            .then(((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("entity", ArgumentTypeEntitySummon.entity()).executes(c -> {
               CommandSource source = (CommandSource)c.getSource();
               Vec3 coordinates = source.getCoordinates(false);
               if (source.getSender() != null && coordinates != null) {
                  Entity entity = summonEntityAt(c, coordinates.x, coordinates.y - source.getSender().heightOffset, coordinates.z, 0.0F, 0.0F);
                  source.sendTranslatableMessage("command.commands.summon.success_single_entity", CommandHelper.getEntityName(entity));
                  return 1;
               } else {
                  throw CommandExceptions.notInWorld().create();
               }
            })).then(((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("position", ArgumentTypeVec3.vec3d()).executes(c -> {
               CommandSource source = (CommandSource)c.getSource();
               DoubleCoordinates coordinates = c.getArgument("position", DoubleCoordinates.class);
               Entity entity = summonEntityAt(c, coordinates.getX(source), coordinates.getY(source, true), coordinates.getZ(source), 0.0F, 0.0F);
               source.sendTranslatableMessage("command.commands.summon.success_single_entity", CommandHelper.getEntityName(entity));
               return 1;
            })).then(ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer(1, 255)).executes(c -> {
               CommandSource source = (CommandSource)c.getSource();
               DoubleCoordinates coordinates = c.getArgument("position", DoubleCoordinates.class);
               int amount = c.getArgument("amount", Integer.class);

               for (int i = 0; i < amount; i++) {
                  summonEntityAt(c, coordinates.getX(source), coordinates.getY(source, true), coordinates.getZ(source), 0.0F, 0.0F);
               }

               source.sendTranslatableMessage("command.commands.summon.success_multiple_entities", amount);
               return 1;
            }))))
      );
   }

   private static Entity summonEntityAt(CommandContext<CommandSource> c, double x, double y, double z, float yaw, float pitch) {
      Class<? extends Entity> entityClass = c.getArgument("entity", Class.class);

      Entity entity;
      try {
         entity = entityClass.getConstructor(World.class).newInstance(c.getSource().getWorld());
      } catch (Exception var12) {
         throw new RuntimeException(var12);
      }

      entity.spawnInit();
      entity.moveTo(x, y, z, yaw, pitch);
      c.getSource().getWorld().entityJoinedWorld(entity);
      return entity;
   }
}
