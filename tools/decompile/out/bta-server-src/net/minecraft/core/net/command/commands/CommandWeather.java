package net.minecraft.core.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeFloat;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeTime;
import net.minecraft.core.net.command.arguments.ArgumentTypeWeather;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.Weathers;

public class CommandWeather implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      CommandNode<CommandSource> command = dispatcher.register(
         ArgumentBuilderLiteral.<CommandSource>literal("weather")
            .requires(CommandSource::hasAdmin)
            .then(ArgumentBuilderLiteral.literal("clear").executes(c -> {
               CommandSource source = (CommandSource)c.getSource();
               World world = source.getWorld();
               Weather weather = Weathers.OVERWORLD_CLEAR;
               world.weatherManager.overrideWeather(weather);
               source.sendTranslatableMessage("command.commands.weather.success", weather.getTranslatedName());
               return 1;
            }))
            .then(((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("weather", ArgumentTypeWeather.weather()).executes(c -> {
               CommandSource source = (CommandSource)c.getSource();
               World world = source.getWorld();
               Weather weather = c.getArgument("weather", Weather.class);
               world.weatherManager.overrideWeather(weather);
               source.sendTranslatableMessage("command.commands.weather.success", weather.getTranslatedName());
               return 1;
            })).then(((ArgumentBuilderRequired)ArgumentBuilderRequired.argument("duration", ArgumentTypeTime.time()).executes(c -> {
               CommandSource source = (CommandSource)c.getSource();
               World world = source.getWorld();
               Weather weather = c.getArgument("weather", Weather.class);
               world.weatherManager.overrideWeather(weather, c.getArgument("duration", Integer.class).intValue());
               source.sendTranslatableMessage("command.commands.weather.success", weather.getTranslatedName());
               return 1;
            })).then(ArgumentBuilderRequired.argument("power", ArgumentTypeFloat.floatArg()).executes(c -> {
               CommandSource source = (CommandSource)c.getSource();
               World world = source.getWorld();
               Weather weather = c.getArgument("weather", Weather.class);
               world.weatherManager.overrideWeather(weather, c.getArgument("duration", Integer.class).intValue(), c.getArgument("power", Float.class));
               source.sendTranslatableMessage("command.commands.weather.success", weather.getTranslatedName());
               return 1;
            }))))
      );
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("w").requires(CommandSource::hasAdmin).redirect(command));
   }
}
