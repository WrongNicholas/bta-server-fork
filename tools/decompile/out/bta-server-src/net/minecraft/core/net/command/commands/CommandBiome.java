package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeBiome;
import net.minecraft.core.net.command.arguments.ArgumentTypeIntegerCoordinates;
import net.minecraft.core.net.command.helpers.Coordinates2D;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.biome.Biome;

public class CommandBiome implements CommandManager.CommandRegistry {
   private static final DynamicCommandExceptionType FAILURE = new DynamicCommandExceptionType(
      arg -> () -> I18n.getInstance().translateKeyAndFormat("command.commands.biome.locate.exception_failure", arg)
   );

   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("biome")
            .requires(CommandSource::hasAdmin)
            .then(
               ArgumentBuilderLiteral.literal("get")
                  .then(
                     ArgumentBuilderRequired.argument("position", ArgumentTypeIntegerCoordinates.intCoordinates())
                        .executes(
                           c -> {
                              IntegerCoordinates position = c.getArgument("position", IntegerCoordinates.class);
                              Biome biome = ((CommandSource)c.getSource())
                                 .getWorld()
                                 .getBlockBiome(
                                    position.getX((CommandSource)c.getSource()),
                                    position.getY((CommandSource)c.getSource(), true),
                                    position.getZ((CommandSource)c.getSource())
                                 );
                              ((CommandSource)c.getSource())
                                 .sendTranslatableMessage(
                                    "command.commands.biome.get.success",
                                    position.getX((CommandSource)c.getSource()),
                                    position.getY((CommandSource)c.getSource(), true),
                                    position.getZ((CommandSource)c.getSource()),
                                    Registries.BIOMES.getKey(biome)
                                 );
                              return 1;
                           }
                        )
                  )
            )
            .then(
               ArgumentBuilderLiteral.literal("locate")
                  .then(
                     ArgumentBuilderRequired.argument("biome", ArgumentTypeBiome.biome())
                        .executes(
                           c -> {
                              Biome biome = c.getArgument("biome", Biome.class);
                              Coordinates2D biomeLocation = getBiomeCoords(biome, (CommandSource)c.getSource());
                              if (biomeLocation == null) {
                                 throw FAILURE.create(Registries.BIOMES.getKey(biome));
                              } else {
                                 Vec3 sourcePos = ((CommandSource)c.getSource()).getBlockCoordinates();
                                 if (sourcePos == null) {
                                    ((CommandSource)c.getSource())
                                       .sendTranslatableMessage(
                                          "command.commands.biome.locate.success",
                                          Registries.BIOMES.getKey(biome),
                                          biomeLocation.getX((CommandSource)c.getSource()),
                                          biomeLocation.getZ((CommandSource)c.getSource())
                                       );
                                 } else {
                                    int distance = (int)sourcePos.distanceTo(
                                       Vec3.getTempVec3(
                                          biomeLocation.getX((CommandSource)c.getSource()), sourcePos.y, biomeLocation.getZ((CommandSource)c.getSource())
                                       )
                                    );
                                    ((CommandSource)c.getSource())
                                       .sendTranslatableMessage(
                                          distance == 1
                                             ? "command.commands.biome.locate.success_in_world_single"
                                             : "command.commands.biome.locate.success_in_world_multiple",
                                          Registries.BIOMES.getKey(biome),
                                          biomeLocation.getX((CommandSource)c.getSource()),
                                          biomeLocation.getZ((CommandSource)c.getSource()),
                                          distance
                                       );
                                 }

                                 return 1;
                              }
                           }
                        )
                  )
            )
      );
   }

   private static Coordinates2D getBiomeCoords(Biome biome, CommandSource source) {
      int[] xPattern = new int[]{0, 1, 0, -1};
      int[] zPattern = new int[]{1, 0, -1, 0};
      Vec3 sourcePos = source.getBlockCoordinates();
      if (sourcePos == null) {
         sourcePos = Vec3.getTempVec3(0.0, 0.0, 0.0);
      }

      int runLength = 2;
      int chunkX = (int)(sourcePos.x / 16.0);
      int y = (int)sourcePos.y;
      int chunkZ = (int)(sourcePos.z / 16.0);
      int passes = 1024;
      if (source.getWorld().getBlockBiome((int)sourcePos.x, y, (int)sourcePos.z) == biome) {
         return new Coordinates2D((int)sourcePos.x, (int)sourcePos.z);
      } else {
         chunkX--;
         chunkZ--;

         for (int pass = 0; pass < passes; pass++) {
            for (int i = 0; i < 4; i++) {
               for (int j = runLength - 1; j >= 0; j--) {
                  chunkX += xPattern[i];
                  chunkZ += zPattern[i];
                  if (source.getWorld().getBlockBiome(chunkX * 16, y, chunkZ * 16) == biome) {
                     return new Coordinates2D(chunkX * 16, chunkZ * 16);
                  }
               }
            }

            runLength++;
         }

         return null;
      }
   }
}
