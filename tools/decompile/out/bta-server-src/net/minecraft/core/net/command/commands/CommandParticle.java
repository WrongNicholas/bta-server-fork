package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeDouble;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.arguments.ArgumentTypeParticleId;
import net.minecraft.core.net.command.arguments.ArgumentTypeVec3;
import net.minecraft.core.net.command.helpers.DoubleCoordinates;
import net.minecraft.core.net.command.helpers.EntitySelector;
import org.jetbrains.annotations.Nullable;

public class CommandParticle implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("particle")
            .requires(CommandSource::hasAdmin)
            .then(
               ((ArgumentBuilderRequired)((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("particleId", ArgumentTypeParticleId.particleId())
                        .executes(c -> {
                           String particleId = c.getArgument("particleId", String.class);
                           Player player = ((CommandSource)c.getSource()).getSender();
                           if (player != null) {
                              spawnParticle((CommandSource)c.getSource(), player.x, player.y, player.z, null, null, null, particleId, null, null);
                           }

                           return 1;
                        }))
                     .then(
                        ((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("position", ArgumentTypeVec3.vec3d())
                              .executes(
                                 c -> {
                                    String particleId = c.getArgument("particleId", String.class);
                                    DoubleCoordinates pos = c.getArgument("position", DoubleCoordinates.class);
                                    spawnParticle(
                                       (CommandSource)c.getSource(),
                                       pos.getX((CommandSource)c.getSource()),
                                       pos.getY((CommandSource)c.getSource(), true),
                                       pos.getZ((CommandSource)c.getSource()),
                                       null,
                                       null,
                                       null,
                                       particleId,
                                       null,
                                       null
                                    );
                                    return 1;
                                 }
                              ))
                           .then(
                              ((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("motion", ArgumentTypeVec3.vec3d())
                                    .executes(
                                       c -> {
                                          String particleId = c.getArgument("particleId", String.class);
                                          DoubleCoordinates pos = c.getArgument("position", DoubleCoordinates.class);
                                          DoubleCoordinates motion = c.getArgument("motion", DoubleCoordinates.class);
                                          spawnParticle(
                                             (CommandSource)c.getSource(),
                                             pos.getX((CommandSource)c.getSource()),
                                             pos.getY((CommandSource)c.getSource(), true),
                                             pos.getZ((CommandSource)c.getSource()),
                                             motion.getX(0.0),
                                             motion.getY(0.0),
                                             motion.getZ(0.0),
                                             particleId,
                                             null,
                                             null
                                          );
                                          return 1;
                                       }
                                    ))
                                 .then(
                                    ((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("data", ArgumentTypeInteger.integer())
                                          .executes(
                                             c -> {
                                                String particleId = c.getArgument("particleId", String.class);
                                                DoubleCoordinates pos = c.getArgument("position", DoubleCoordinates.class);
                                                DoubleCoordinates motion = c.getArgument("motion", DoubleCoordinates.class);
                                                int data = c.getArgument("data", Integer.class);
                                                spawnParticle(
                                                   (CommandSource)c.getSource(),
                                                   pos.getX((CommandSource)c.getSource()),
                                                   pos.getY((CommandSource)c.getSource(), true),
                                                   pos.getZ((CommandSource)c.getSource()),
                                                   motion.getX(0.0),
                                                   motion.getY(0.0),
                                                   motion.getZ(0.0),
                                                   particleId,
                                                   data,
                                                   null
                                                );
                                                return 1;
                                             }
                                          ))
                                       .then(
                                          ArgumentBuilderRequired.argument("maxDistance", ArgumentTypeDouble.doubleArg())
                                             .executes(
                                                c -> {
                                                   String particleId = c.getArgument("particleId", String.class);
                                                   DoubleCoordinates pos = c.getArgument("position", DoubleCoordinates.class);
                                                   DoubleCoordinates motion = c.getArgument("motion", DoubleCoordinates.class);
                                                   int data = c.getArgument("data", Integer.class);
                                                   double maxDist = c.getArgument("maxDistance", Double.class);
                                                   spawnParticle(
                                                      (CommandSource)c.getSource(),
                                                      pos.getX((CommandSource)c.getSource()),
                                                      pos.getY((CommandSource)c.getSource(), true),
                                                      pos.getZ((CommandSource)c.getSource()),
                                                      motion.getX(0.0),
                                                      motion.getY(0.0),
                                                      motion.getZ(0.0),
                                                      particleId,
                                                      data,
                                                      maxDist
                                                   );
                                                   return 1;
                                                }
                                             )
                                       )
                                 )
                           )
                     ))
                  .then(((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("target", ArgumentTypeEntity.entities()).executes(c -> {
                     String particleId = c.getArgument("particleId", String.class);
                     EntitySelector selector = c.getArgument("target", EntitySelector.class);

                     for (Entity e : selector.get((CommandSource)c.getSource())) {
                        spawnParticle((CommandSource)c.getSource(), e.x, e.y, e.z, null, null, null, particleId, null, null);
                     }

                     return 1;
                  })).then(((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("motion", ArgumentTypeVec3.vec3d()).executes(c -> {
                     String particleId = c.getArgument("particleId", String.class);
                     DoubleCoordinates motion = c.getArgument("motion", DoubleCoordinates.class);
                     EntitySelector selector = c.getArgument("target", EntitySelector.class);

                     for (Entity e : selector.get((CommandSource)c.getSource())) {
                        spawnParticle((CommandSource)c.getSource(), e.x, e.y, e.z, motion.getX(0.0), motion.getY(0.0), motion.getZ(0.0), particleId, null, null);
                     }

                     return 1;
                  })).then(((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("data", ArgumentTypeInteger.integer()).executes(c -> {
                     String particleId = c.getArgument("particleId", String.class);
                     DoubleCoordinates motion = c.getArgument("motion", DoubleCoordinates.class);
                     int data = c.getArgument("data", Integer.class);
                     EntitySelector selector = c.getArgument("target", EntitySelector.class);

                     for (Entity e : selector.get((CommandSource)c.getSource())) {
                        spawnParticle((CommandSource)c.getSource(), e.x, e.y, e.z, motion.getX(0.0), motion.getY(0.0), motion.getZ(0.0), particleId, data, null);
                     }

                     return 1;
                  })).then(ArgumentBuilderRequired.argument("maxDistance", ArgumentTypeDouble.doubleArg()).executes(c -> {
                     String particleId = c.getArgument("particleId", String.class);
                     DoubleCoordinates motion = c.getArgument("motion", DoubleCoordinates.class);
                     int data = c.getArgument("data", Integer.class);
                     double maxDist = c.getArgument("maxDistance", Double.class);
                     EntitySelector selector = c.getArgument("target", EntitySelector.class);

                     for (Entity e : selector.get((CommandSource)c.getSource())) {
                        spawnParticle(
                           (CommandSource)c.getSource(), e.x, e.y, e.z, motion.getX(0.0), motion.getY(0.0), motion.getZ(0.0), particleId, data, maxDist
                        );
                     }

                     return 1;
                  })))))
            )
      );
   }

   public static void spawnParticle(
      CommandSource commandSource,
      double x,
      double y,
      double z,
      @Nullable Double motionX,
      @Nullable Double motionY,
      @Nullable Double motionZ,
      String particleId,
      @Nullable Integer data,
      @Nullable Double maxDistance
   ) {
      commandSource.getWorld()
         .spawnParticle(
            particleId,
            x,
            y,
            z,
            motionX == null ? 0.0 : motionX,
            motionY == null ? 0.0 : motionY,
            motionZ == null ? 0.0 : motionZ,
            data == null ? 0 : data,
            maxDistance == null ? 16.0 : maxDistance
         );
   }
}
