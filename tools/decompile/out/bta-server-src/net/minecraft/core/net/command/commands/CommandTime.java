package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeSeason;
import net.minecraft.core.net.command.arguments.ArgumentTypeTime;
import net.minecraft.core.world.LevelListener;
import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Season;
import net.minecraft.core.world.season.SeasonManagerCycle;
import net.minecraft.core.world.season.Seasons;

public class CommandTime implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("time")
            .requires(CommandSource::hasAdmin)
            .then(
               ((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal("query")
                        .then(ArgumentBuilderLiteral.literal("daytime").executes(c -> {
                           CommandSource source = (CommandSource)c.getSource();
                           source.sendTranslatableMessage("command.commands.time.query", ((CommandSource)c.getSource()).getWorld().getWorldTime() % 24000L);
                           return 1;
                        })))
                     .then(ArgumentBuilderLiteral.literal("gametime").executes(c -> {
                        CommandSource source = (CommandSource)c.getSource();
                        source.sendTranslatableMessage("command.commands.time.query", source.getWorld().getWorldTime() % 2147483647L);
                        return 1;
                     })))
                  .then(ArgumentBuilderLiteral.literal("day").executes(c -> {
                     CommandSource source = (CommandSource)c.getSource();
                     source.sendTranslatableMessage("command.commands.time.query", source.getWorld().getWorldTime() / 24000L % 2147483647L);
                     return 1;
                  }))
            )
            .then(
               ((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal(
                                    "set"
                                 )
                                 .then(ArgumentBuilderRequired.argument("time", ArgumentTypeTime.time()).executes(c -> {
                                    CommandSource source = (CommandSource)c.getSource();
                                    int time = c.getArgument("time", Integer.class);
                                    setWorldTime(source, source.getWorld(), time);
                                    return 1;
                                 })))
                              .then(ArgumentBuilderLiteral.literal("day").executes(c -> {
                                 CommandSource source = (CommandSource)c.getSource();
                                 setDayTime(source, source.getWorld(), 1000L);
                                 return 1;
                              })))
                           .then(ArgumentBuilderLiteral.literal("noon").executes(c -> {
                              CommandSource source = (CommandSource)c.getSource();
                              setDayTime(source, source.getWorld(), 6000L);
                              return 1;
                           })))
                        .then(ArgumentBuilderLiteral.literal("night").executes(c -> {
                           CommandSource source = (CommandSource)c.getSource();
                           setDayTime(source, source.getWorld(), 13000L);
                           return 1;
                        })))
                     .then(ArgumentBuilderLiteral.literal("midnight").executes(c -> {
                        CommandSource source = (CommandSource)c.getSource();
                        setDayTime(source, source.getWorld(), 18000L);
                        return 1;
                     })))
                  .then(
                     ((ArgumentBuilderRequired)((ArgumentBuilderRequired)ArgumentBuilderRequired.<CommandSource, String>argument(
                                 "season", ArgumentTypeSeason.season()
                              )
                              .requires(c -> c.getWorld().getSeasonManager() instanceof SeasonManagerCycle))
                           .executes(
                              c -> {
                                 CommandSource source = (CommandSource)c.getSource();
                                 String seasonId = c.getArgument("season", String.class);
                                 Season targetSeason = Seasons.getSeason(seasonId);
                                 SeasonManagerCycle seasonManager = (SeasonManagerCycle)source.getWorld().getSeasonManager();
                                 int time = 0;

                                 for (Season season : seasonManager.getSeasons()) {
                                    if (season == targetSeason) {
                                       break;
                                    }

                                    time += seasonManager.getSeasonLengthTicks(season);
                                 }

                                 setWorldTime(
                                    source,
                                    source.getWorld(),
                                    source.getWorld().getWorldTime() - source.getWorld().getWorldTime() % seasonManager.getYearLengthTicks() + time
                                 );
                                 return 1;
                              }
                           ))
                        .then(
                           ArgumentBuilderRequired.argument("day", ArgumentTypeInteger.integer())
                              .executes(
                                 c -> {
                                    CommandSource source = (CommandSource)c.getSource();
                                    String seasonId = c.getArgument("season", String.class);
                                    Season targetSeason = Seasons.getSeason(seasonId);
                                    int day = c.getArgument("day", Integer.class);
                                    SeasonManagerCycle seasonManager = (SeasonManagerCycle)source.getWorld().getSeasonManager();
                                    int time = 0;

                                    for (Season season : seasonManager.getSeasons()) {
                                       if (season == targetSeason) {
                                          break;
                                       }

                                       time += seasonManager.getSeasonLengthTicks(season);
                                    }

                                    time += day * 24000;
                                    setWorldTime(
                                       source,
                                       source.getWorld(),
                                       source.getWorld().getWorldTime() - source.getWorld().getWorldTime() % seasonManager.getYearLengthTicks() + time
                                    );
                                    return 1;
                                 }
                              )
                        )
                  )
            )
            .then(ArgumentBuilderLiteral.literal("add").then(ArgumentBuilderRequired.argument("time", ArgumentTypeTime.time()).executes(c -> {
               CommandSource source = (CommandSource)c.getSource();
               int time = c.getArgument("time", Integer.class);
               addWorldTime(source, source.getWorld(), time);
               return 1;
            })))
      );
   }

   private static void setDayTime(CommandSource source, World world, long time) {
      setWorldTime(source, world, world.getWorldTime() - world.getWorldTime() % 24000L + time);
   }

   private static void setWorldTime(CommandSource source, World world, long time) {
      world.setWorldTime(time);

      for (LevelListener listener : world.listeners) {
         listener.allChanged(true, true);
      }

      source.sendTranslatableMessage("command.commands.time.set", time);
   }

   private static void addWorldTime(CommandSource source, World world, long time) {
      setWorldTime(source, world, world.getWorldTime() + time);
   }
}
