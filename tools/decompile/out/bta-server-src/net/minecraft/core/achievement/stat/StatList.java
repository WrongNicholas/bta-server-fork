package net.minecraft.core.achievement.stat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public abstract class StatList {
   protected static Map<NamespaceID, Stat> statMap = new HashMap<>();
   public static List<Stat> registeredStats = new ArrayList<>();
   public static List<Stat> basicStats = new ArrayList<>();
   public static List<Stat> usedItemStats = new ArrayList<>();
   public static final String STAT_USED = "stat_used";
   public static final String STAT_DEPLETED = "stat_broken";
   public static final String STAT_CRAFTED = "stat_crafted";
   public static final String STAT_PICKED_UP = "stat_picked_up";
   public static final String STAT_MINED = "stat_mined";
   public static Stat startGameStat = new StatBasic(NamespaceID.getPermanent("minecraft", "start_game"), "stat.startGame").setClientside().registerStat();
   public static Stat createWorldStat = new StatBasic(NamespaceID.getPermanent("minecraft", "create_world"), "stat.createWorld").setClientside().registerStat();
   public static Stat loadWorldStat = new StatBasic(NamespaceID.getPermanent("minecraft", "load_world"), "stat.loadWorld").setClientside().registerStat();
   public static Stat joinMultiplayerStat = new StatBasic(NamespaceID.getPermanent("minecraft", "join_multiplayer"), "stat.joinMultiplayer")
      .setClientside()
      .registerStat();
   public static Stat leaveGameStat = new StatBasic(NamespaceID.getPermanent("minecraft", "leave_game"), "stat.leaveGame").setClientside().registerStat();
   public static Stat minutesPlayedStat = new StatBasic(NamespaceID.getPermanent("minecraft", "playtime"), "stat.playOneMinute", Stat.statTypeTime)
      .setClientside()
      .registerStat();
   public static Stat distanceWalkedStat = new StatBasic(NamespaceID.getPermanent("minecraft", "travel_walk"), "stat.walkOneCm", Stat.statTypeDistance)
      .setClientside()
      .registerStat();
   public static Stat distanceSwumStat = new StatBasic(NamespaceID.getPermanent("minecraft", "travel_swim"), "stat.swimOneCm", Stat.statTypeDistance)
      .setClientside()
      .registerStat();
   public static Stat distanceFallenStat = new StatBasic(NamespaceID.getPermanent("minecraft", "travel_fall"), "stat.fallOneCm", Stat.statTypeDistance)
      .setClientside()
      .registerStat();
   public static Stat distanceClimbedStat = new StatBasic(NamespaceID.getPermanent("minecraft", "travel_climb"), "stat.climbOneCm", Stat.statTypeDistance)
      .setClientside()
      .registerStat();
   public static Stat distanceFlownStat = new StatBasic(NamespaceID.getPermanent("minecraft", "travel_fly"), "stat.flyOneCm", Stat.statTypeDistance)
      .setClientside()
      .registerStat();
   public static Stat distanceDoveStat = new StatBasic(NamespaceID.getPermanent("minecraft", "travel_dive"), "stat.diveOneCm", Stat.statTypeDistance)
      .setClientside()
      .registerStat();
   public static Stat distanceByMinecartStat = new StatBasic(
         NamespaceID.getPermanent("minecraft", "travel_minecart"), "stat.minecartOneCm", Stat.statTypeDistance
      )
      .setClientside()
      .registerStat();
   public static Stat distanceByBoatStat = new StatBasic(NamespaceID.getPermanent("minecraft", "travel_boat"), "stat.boatOneCm", Stat.statTypeDistance)
      .setClientside()
      .registerStat();
   public static Stat distanceByPigStat = new StatBasic(NamespaceID.getPermanent("minecraft", "travel_pig"), "stat.pigOneCm", Stat.statTypeDistance)
      .setClientside()
      .registerStat();
   public static Stat jumpStat = new StatBasic(NamespaceID.getPermanent("minecraft", "jump"), "stat.jump").setClientside().registerStat();
   public static Stat dropStat = new StatBasic(NamespaceID.getPermanent("minecraft", "drop"), "stat.drop").setClientside().registerStat();
   public static Stat damageDealtStat = new StatBasic(NamespaceID.getPermanent("minecraft", "damage_dealt"), "stat.damageDealt").registerStat();
   public static Stat damageTakenStat = new StatBasic(NamespaceID.getPermanent("minecraft", "damage_taken"), "stat.damageTaken").registerStat();
   public static Stat deathsStat = new StatBasic(NamespaceID.getPermanent("minecraft", "deaths"), "stat.deaths").registerStat();
   public static Stat mobKillsStat = new StatBasic(NamespaceID.getPermanent("minecraft", "kills_mob"), "stat.mobKills").registerStat();
   public static Stat playerKillsStat = new StatBasic(NamespaceID.getPermanent("minecraft", "kills_player"), "stat.playerKills").registerStat();
   public static Stat fishCaughtStat = new StatBasic(NamespaceID.getPermanent("minecraft", "caught_fish"), "stat.fishCaught").registerStat();
   public static Map<NamespaceID, Stat> mobEncounterStats;
   public static Map<Integer, Integer> replacementMap = new HashMap<>();

   public static void initReplaceMap() {
      for (Item item : Item.itemsList) {
         if (item != null) {
            Item parent = item.getStatParent();
            if (parent != null) {
               if (item.id == parent.id) {
                  throw new RuntimeException("Attempted to set '" + item.namespaceID + "' parent to itself!");
               }

               replacementMap.put(item.id, parent.id);
            }
         }
      }
   }

   public static void init() {
      initReplaceMap();
      Achievements.init();
      HashSet<Integer> craftable = new HashSet<>();

      for (RecipeEntryCrafting<?, ?> r : Registries.RECIPES.getAllCraftingRecipes()) {
         if (r.getOutput() instanceof ItemStack) {
            craftable.add(((ItemStack)r.getOutput()).itemID);
         } else if (r.getOutput() instanceof IItemConvertible) {
            craftable.add(((Item)r.getOutput()).asItem().id);
         }
      }

      for (RecipeEntryFurnace rx : Registries.RECIPES.getAllFurnaceRecipes()) {
         if (rx.getOutput() != null) {
            craftable.add(rx.getOutput().itemID);
         }
      }

      for (RecipeEntryBlastFurnace rxx : Registries.RECIPES.getAllBlastFurnaceRecipes()) {
         if (rxx.getOutput() != null) {
            craftable.add(rxx.getOutput().itemID);
         }
      }

      for (RecipeEntryTrommel rxxx : Registries.RECIPES.getAllTrommelRecipes()) {
         if (rxxx.getOutput() != null) {
            WeightedRandomBag<WeightedRandomLootObject> bag = rxxx.getOutput();

            for (WeightedRandomLootObject o : bag.getEntries()) {
               ItemStack stack = o.getItemStack();
               if (stack != null) {
                  craftable.add(stack.itemID);
               }
            }
         }
      }

      for (Integer i : craftable) {
         Item.itemsList[i].createStat("stat_crafted", "stat.craftItem");
      }

      mobEncounterStats = new HashMap<>();

      for (Entry<NamespaceID, Class<? extends Entity>> entry : EntityDispatcher.idToClassMap.entrySet()) {
         mobEncounterStats.put(
            entry.getKey(),
            new StatMob(NamespaceID.getPermanent("minecraft", "encounter_" + entry.getKey().toString().replace(":", "_")), "stat.encounterMob", entry.getKey())
               .registerStat()
         );
      }
   }

   @Nullable
   public static Stat getStat(NamespaceID id) {
      return statMap.get(id);
   }

   public static @Unmodifiable Collection<Stat> getAllStats() {
      return Collections.unmodifiableCollection(statMap.values());
   }

   public static class StatConverter {
      public static final int OFFSET_OFFSET = 65536;
      public static final int OFFSET_MINED = 16777216;
      public static final int OFFSET_CRAFTED = 16842752;
      public static final int OFFSET_USED = 16908288;
      public static final int OFFSET_DEPLETED = 16973824;
      public static final int OFFSET_PICKED_UP = 17039360;
      public static final int OFFSET_MOB = 17104896;
      public static final int OFFSET_ACHIEVEMENT = 5242880;

      @Nullable
      public static Stat getStatFromDeprecatedID(int id) {
         switch (id) {
            case 1000:
               return StatList.startGameStat;
            case 1001:
               return StatList.createWorldStat;
            case 1002:
               return StatList.loadWorldStat;
            case 1003:
               return StatList.joinMultiplayerStat;
            case 1004:
               return StatList.leaveGameStat;
            case 1100:
               return StatList.minutesPlayedStat;
            case 2000:
               return StatList.distanceWalkedStat;
            case 2001:
               return StatList.distanceSwumStat;
            case 2002:
               return StatList.distanceFallenStat;
            case 2003:
               return StatList.distanceClimbedStat;
            case 2004:
               return StatList.distanceFlownStat;
            case 2005:
               return StatList.distanceDoveStat;
            case 2006:
               return StatList.distanceByMinecartStat;
            case 2007:
               return StatList.distanceByBoatStat;
            case 2008:
               return StatList.distanceByPigStat;
            case 2010:
               return StatList.jumpStat;
            case 2011:
               return StatList.dropStat;
            case 2020:
               return StatList.damageDealtStat;
            case 2021:
               return StatList.damageTakenStat;
            case 2022:
               return StatList.deathsStat;
            case 2023:
               return StatList.mobKillsStat;
            case 2024:
               return StatList.playerKillsStat;
            case 2025:
               return StatList.fishCaughtStat;
            default:
               if (16777216 <= id && id < 16842752) {
                  Item i = Item.getItem(id - 16777216);
                  return i == null ? null : i.getStat("stat_mined");
               } else if (16842752 <= id && id < 16908288) {
                  Item i = Item.getItem(id - 16842752);
                  return i == null ? null : i.getStat("stat_crafted");
               } else if (16908288 <= id && id < 16973824) {
                  Item i = Item.getItem(id - 16908288);
                  return i == null ? null : i.getStat("stat_used");
               } else if (16973824 <= id && id < 17039360) {
                  Item i = Item.getItem(id - 16973824);
                  return i == null ? null : i.getStat("stat_broken");
               } else if (17039360 <= id && id < 17104896) {
                  Item i = Item.getItem(id - 17039360);
                  return i == null ? null : i.getStat("stat_picked_up");
               } else if (17104896 <= id && id < 17170432) {
                  Class<? extends Entity> c = EntityDispatcher.Legacy.getClassFromID(id - 17104896);
                  NamespaceID entityID = EntityDispatcher.idForClass(c);
                  return StatList.mobEncounterStats.get(entityID);
               } else {
                  if (5242880 <= id && id < 16777216) {
                     int achievementID = id - 5242880;
                     switch (achievementID) {
                        case 0:
                           return Achievements.OPEN_INVENTORY;
                        case 1:
                           return Achievements.MINE_WOOD;
                        case 2:
                           return Achievements.BUILD_WORKBENCH;
                        case 3:
                           return Achievements.BUILD_PICKAXE;
                        case 4:
                           return Achievements.BUILD_FURNACE;
                        case 5:
                           return Achievements.ACQUIRE_IRON;
                        case 6:
                           return Achievements.BUILD_HOE;
                        case 7:
                           return Achievements.MAKE_BREAD;
                        case 8:
                           return Achievements.BAKE_CAKE;
                        case 9:
                           return Achievements.BUILD_BETTER_PICKAXE;
                        case 10:
                           return Achievements.COOK_FISH;
                        case 11:
                           return Achievements.ON_A_RAIL;
                        case 12:
                           return Achievements.BUILD_SWORD;
                        case 13:
                           return Achievements.KILL_ENEMY;
                        case 14:
                           return Achievements.KILL_COW;
                        case 15:
                           return Achievements.FLY_PIG;
                        case 16:
                           return Achievements.GET_SENSED;
                        case 17:
                           return Achievements.CRAFT_HANDCANNON;
                        case 18:
                           return Achievements.COLLECT_STONE;
                        case 19:
                           return Achievements.CRUSH_BLOCKS;
                        case 20:
                           return Achievements.REPAIR_ARMOR;
                        case 21:
                           return Achievements.GET_CHAINMAIL;
                        case 22:
                           return Achievements.OPEN_GUIDEBOOK;
                        case 23:
                           return Achievements.CAUGHT_EM_ALL;
                     }
                  }

                  return null;
               }
         }
      }
   }
}
