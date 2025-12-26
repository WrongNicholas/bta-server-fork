package net.minecraft.core.data.gamerule;

import net.minecraft.core.data.registry.Registries;

public abstract class GameRules {
   public static GameRuleBoolean KEEP_INVENTORY = register(new GameRuleBoolean("keepInventory", false));
   public static GameRuleBoolean ALLOW_SPRINTING = register(new GameRuleBoolean("allowSprinting", false));
   public static GameRuleBoolean ALLOW_SLEEPING = register(new GameRuleBoolean("allowSleeping", true));
   public static GameRuleBoolean DO_NIGHTMARES = register(new GameRuleBoolean("doNightmares", true));
   public static GameRuleBoolean DO_SEASONAL_GROWTH = register(new GameRuleBoolean("doSeasonalGrowth", true));
   public static GameRuleBoolean TREECAPITATOR = register(new GameRuleBoolean("treecapitator", false));
   public static GameRuleBoolean DWARF_MODE = register(new GameRuleBoolean("dwarfMode", false));
   public static GameRuleBoolean MOB_GRIEFING = register(new GameRuleBoolean("mobGriefing", true));
   public static GameRuleBoolean DO_DAY_CYCLE = register(new GameRuleBoolean("doDaylightCycle", true));
   public static GameRuleBoolean DO_WEATHER_CYCLE = register(new GameRuleBoolean("doWeatherCycle", true));
   public static GameRuleBoolean DO_FIRE_SPREAD = register(new GameRuleBoolean("doFireSpread", true));
   public static GameRuleBoolean INSTANT_HEALING = register(new GameRuleBoolean("instantHealing", false));

   public static <T extends GameRule<?>> T register(T gameRule) {
      Registries.GAME_RULES.register(gameRule.getKey(), gameRule);
      return gameRule;
   }

   public static void init() {
   }
}
