package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeFloat;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.arguments.ArgumentTypeSoundCategory;
import net.minecraft.core.net.command.arguments.ArgumentTypeSoundId;
import net.minecraft.core.net.command.arguments.ArgumentTypeVec3;
import net.minecraft.core.net.command.helpers.DoubleCoordinates;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;

public class CommandPlaySound implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("playsound")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderRequired.argument("soundId", ArgumentTypeSoundId.soundId())
                  .then(
                     ((ArgumentBuilderRequired)((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("type", ArgumentTypeSoundCategory.soundCategory())
                              .executes(c -> {
                                 String soundId = c.getArgument("soundId", String.class);
                                 SoundCategory category = c.getArgument("type", SoundCategory.class);
                                 Player player = ((CommandSource)c.getSource()).getSender();
                                 World world = ((CommandSource)c.getSource()).getWorld();
                                 if (player != null) {
                                    world.playSoundEffect(player, category, player.x, player.y, player.z, soundId, 1.0F, 1.0F);
                                    return 1;
                                 } else {
                                    return 0;
                                 }
                              }))
                           .then(((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("position", ArgumentTypeVec3.vec3d()).executes(c -> {
                              String soundId = c.getArgument("soundId", String.class);
                              SoundCategory category = c.getArgument("type", SoundCategory.class);
                              DoubleCoordinates coordinates = c.getArgument("position", DoubleCoordinates.class);
                              Vec3 coords = ((CommandSource)c.getSource()).getCoordinates(true);
                              double sourceX = coords == null ? 0.0 : coords.x;
                              double sourceY = coords == null ? 0.0 : coords.y;
                              double sourceZ = coords == null ? 0.0 : coords.z;
                              World world = ((CommandSource)c.getSource()).getWorld();
                              world.playSoundEffect(
                                 null, category, coordinates.getX(sourceX), coordinates.getY(sourceY), coordinates.getZ(sourceZ), soundId, 1.0F, 1.0F
                              );
                              return 1;
                           })).then(((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("volume", ArgumentTypeFloat.floatArg()).executes(c -> {
                              String soundId = c.getArgument("soundId", String.class);
                              SoundCategory category = c.getArgument("type", SoundCategory.class);
                              DoubleCoordinates coordinates = c.getArgument("position", DoubleCoordinates.class);
                              float volume = c.getArgument("volume", Float.class);
                              Vec3 coords = ((CommandSource)c.getSource()).getCoordinates(true);
                              double sourceX = coords == null ? 0.0 : coords.x;
                              double sourceY = coords == null ? 0.0 : coords.y;
                              double sourceZ = coords == null ? 0.0 : coords.z;
                              World world = ((CommandSource)c.getSource()).getWorld();
                              world.playSoundEffect(
                                 null, category, coordinates.getX(sourceX), coordinates.getY(sourceY), coordinates.getZ(sourceZ), soundId, volume, 1.0F
                              );
                              return 1;
                           })).then(ArgumentBuilderRequired.argument("pitch", ArgumentTypeFloat.floatArg()).executes(c -> {
                              String soundId = c.getArgument("soundId", String.class);
                              SoundCategory category = c.getArgument("type", SoundCategory.class);
                              DoubleCoordinates coordinates = c.getArgument("position", DoubleCoordinates.class);
                              float volume = c.getArgument("volume", Float.class);
                              float pitch = c.getArgument("pitch", Float.class);
                              Vec3 coords = ((CommandSource)c.getSource()).getCoordinates(true);
                              double sourceX = coords == null ? 0.0 : coords.x;
                              double sourceY = coords == null ? 0.0 : coords.y;
                              double sourceZ = coords == null ? 0.0 : coords.z;
                              World world = ((CommandSource)c.getSource()).getWorld();
                              world.playSoundEffect(
                                 null, category, coordinates.getX(sourceX), coordinates.getY(sourceY), coordinates.getZ(sourceZ), soundId, volume, pitch
                              );
                              return 1;
                           })))))
                        .then(((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("target", ArgumentTypeEntity.entities()).executes(c -> {
                           String soundId = c.getArgument("soundId", String.class);
                           SoundCategory category = c.getArgument("type", SoundCategory.class);
                           EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);
                           List<? extends Entity> targets = entitySelector.get((CommandSource)c.getSource());
                           World world = ((CommandSource)c.getSource()).getWorld();

                           for (Entity e : targets) {
                              world.playSoundEffect(null, category, e.x, e.y, e.z, soundId, 1.0F, 1.0F);
                           }

                           return 1;
                        })).then(((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("volume", ArgumentTypeFloat.floatArg()).executes(c -> {
                           String soundId = c.getArgument("soundId", String.class);
                           SoundCategory category = c.getArgument("type", SoundCategory.class);
                           EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);
                           List<? extends Entity> targets = entitySelector.get((CommandSource)c.getSource());
                           float volume = c.getArgument("volume", Float.class);
                           World world = ((CommandSource)c.getSource()).getWorld();

                           for (Entity e : targets) {
                              world.playSoundEffect(null, category, e.x, e.y, e.z, soundId, volume, 1.0F);
                           }

                           return 1;
                        })).then(ArgumentBuilderRequired.argument("pitch", ArgumentTypeFloat.floatArg()).executes(c -> {
                           String soundId = c.getArgument("soundId", String.class);
                           SoundCategory category = c.getArgument("type", SoundCategory.class);
                           EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);
                           List<? extends Entity> targets = entitySelector.get((CommandSource)c.getSource());
                           float volume = c.getArgument("volume", Float.class);
                           float pitch = c.getArgument("pitch", Float.class);
                           World world = ((CommandSource)c.getSource()).getWorld();

                           for (Entity e : targets) {
                              world.playSoundEffect(null, category, e.x, e.y, e.z, soundId, volume, pitch);
                           }

                           return 1;
                        }))))
                  )
            )
      );
   }
}
