package net.minecraft.core.achievement;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;

public abstract class Achievements {
   public static List<Achievement> achievementList = new ArrayList<>();
   public static Achievement OPEN_INVENTORY = new Achievement(NamespaceID.getPermanent("minecraft", "open_inventory"), "openInventory", Items.BOOK, null)
      .setClientsideAchievement()
      .registerAchievement();
   public static Achievement MINE_WOOD = new Achievement(NamespaceID.getPermanent("minecraft", "mine_wood"), "mineWood", Blocks.LOG_OAK, OPEN_INVENTORY)
      .registerAchievement();
   public static Achievement BUILD_WORKBENCH = new Achievement(
         NamespaceID.getPermanent("minecraft", "build_workbench"), "buildWorkBench", Blocks.WORKBENCH, MINE_WOOD
      )
      .registerAchievement();
   public static Achievement BUILD_PICKAXE = new Achievement(
         NamespaceID.getPermanent("minecraft", "build_pickaxe"), "buildPickaxe", Items.TOOL_PICKAXE_WOOD, BUILD_WORKBENCH
      )
      .registerAchievement();
   public static Achievement BUILD_FURNACE = new Achievement(
         NamespaceID.getPermanent("minecraft", "build_furnace"), "buildFurnace", Blocks.FURNACE_STONE_ACTIVE, BUILD_PICKAXE
      )
      .registerAchievement();
   public static Achievement ACQUIRE_IRON = new Achievement(
         NamespaceID.getPermanent("minecraft", "acquire_iron"), "acquireIron", Items.INGOT_IRON, BUILD_FURNACE
      )
      .registerAchievement();
   public static Achievement GET_DIAMONDS = new Achievement(NamespaceID.getPermanent("minecraft", "get_diamonds"), "getDiamonds", Items.DIAMOND, ACQUIRE_IRON)
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement BUILD_HOE = new Achievement(NamespaceID.getPermanent("minecraft", "build_hoe"), "buildHoe", Items.TOOL_HOE_WOOD, BUILD_WORKBENCH)
      .registerAchievement();
   public static Achievement MAKE_BREAD = new Achievement(NamespaceID.getPermanent("minecraft", "make_bread"), "makeBread", Items.FOOD_BREAD, BUILD_HOE)
      .registerAchievement();
   public static Achievement BAKE_CAKE = new Achievement(NamespaceID.getPermanent("minecraft", "bake_cake"), "bakeCake", Items.FOOD_CAKE, BUILD_HOE)
      .registerAchievement();
   public static Achievement CRAFT_ICECREAM = new Achievement(
         NamespaceID.getPermanent("minecraft", "craft_icecream"), "craftIcecream", Items.BUCKET_ICECREAM, BUILD_HOE
      )
      .registerAchievement();
   public static Achievement CRAFT_PUMPKIN_PIE = new Achievement(
         NamespaceID.getPermanent("minecraft", "craft_pumpkin_pie"), "craftPumpkinPie", Items.FOOD_PUMPKIN_PIE, BUILD_HOE
      )
      .registerAchievement();
   public static Achievement BUILD_BETTER_PICKAXE = new Achievement(
         NamespaceID.getPermanent("minecraft", "build_better_pickaxe"), "buildBetterPickaxe", Items.TOOL_PICKAXE_STONE, BUILD_PICKAXE
      )
      .registerAchievement();
   public static Achievement COOK_FISH = new Achievement(NamespaceID.getPermanent("minecraft", "cook_fish"), "cookFish", Items.FOOD_FISH_COOKED, BUILD_FURNACE)
      .registerAchievement();
   public static Achievement ON_A_RAIL = new Achievement(NamespaceID.getPermanent("minecraft", "on_a_rail"), "onARail", Blocks.RAIL, ACQUIRE_IRON)
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement BUILD_SWORD = new Achievement(
         NamespaceID.getPermanent("minecraft", "build_sword"), "buildSword", Items.TOOL_SWORD_WOOD, BUILD_WORKBENCH
      )
      .registerAchievement();
   public static Achievement ALL_ARMOR_TYPES = new Achievement(
         NamespaceID.getPermanent("minecraft", "all_armor_types"), "allArmorTypes", Items.ARMOR_CHESTPLATE_STEEL, null
      )
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement KILL_ENEMY = new Achievement(NamespaceID.getPermanent("minecraft", "kill_enemy"), "killEnemy", Items.BONE, BUILD_SWORD)
      .registerAchievement();
   public static Achievement KILL_COW = new Achievement(NamespaceID.getPermanent("minecraft", "kill_cow"), "killCow", Items.LEATHER, BUILD_SWORD)
      .registerAchievement();
   public static Achievement FLY_PIG = new Achievement(NamespaceID.getPermanent("minecraft", "fly_pig"), "flyPig", Items.SADDLE, KILL_COW)
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement GET_SENSED = new Achievement(
         NamespaceID.getPermanent("minecraft", "get_sensed"), "getSensed", Blocks.MOTION_SENSOR_ACTIVE, KILL_ENEMY
      )
      .registerAchievement();
   public static Achievement CRAFT_HANDCANNON = new Achievement(
         NamespaceID.getPermanent("minecraft", "craft_hand_cannon"), "craftHandCannon", Items.HANDCANNON_UNLOADED, BUILD_BETTER_PICKAXE
      )
      .registerAchievement();
   public static Achievement COLLECT_STONE = new Achievement(
         NamespaceID.getPermanent("minecraft", "collect_stone"), "collectStone", Blocks.COBBLE_LIMESTONE, BUILD_PICKAXE
      )
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement CRUSH_BLOCKS = new Achievement(
         NamespaceID.getPermanent("minecraft", "crush_blocks"), "crushBlocks", Blocks.ICE, BUILD_BETTER_PICKAXE
      )
      .registerAchievement();
   public static Achievement REPAIR_ARMOR = new Achievement(NamespaceID.getPermanent("minecraft", "repair_armor"), "repairArmor", Items.CHAINLINK, KILL_ENEMY)
      .registerAchievement();
   public static Achievement GET_CHAINMAIL = new Achievement(
         NamespaceID.getPermanent("minecraft", "get_chainmail"), "getChainmail", Items.ARMOR_HELMET_CHAINMAIL, REPAIR_ARMOR
      )
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement OPEN_GUIDEBOOK = new Achievement(
         NamespaceID.getPermanent("minecraft", "open_guidebook"), "openGuidebook", Blocks.BOOKSHELF_PLANKS_OAK, OPEN_INVENTORY
      )
      .setClientsideAchievement()
      .registerAchievement();
   public static Achievement CAUGHT_EM_ALL = new Achievement(
         NamespaceID.getPermanent("minecraft", "caught_em_all"), "caughtEmAll", Items.LANTERN_FIREFLY_GREEN, BUILD_WORKBENCH
      )
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement ALL_MUSIC_DISCS = new Achievement(
         NamespaceID.getPermanent("minecraft", "all_music_discs"), "allMusicDiscs", Items.RECORD_STAL, null
      )
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement TRIPLE_HIT = new Achievement(NamespaceID.getPermanent("minecraft", "triple_hit"), "tripleHit", Items.AMMO_ARROW_GOLD, KILL_ENEMY)
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement ENTER_NETHER = new Achievement(NamespaceID.getPermanent("minecraft", "enter_nether"), "enterNether", Blocks.OBSIDIAN, null)
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement GET_NETHERCOAL = new Achievement(
         NamespaceID.getPermanent("minecraft", "get_nethercoal"), "getNethercoal", Items.NETHERCOAL, ENTER_NETHER
      )
      .setType(Achievement.TYPE_NORMAL)
      .registerAchievement();
   public static Achievement LIGHT_SIGN = new Achievement(NamespaceID.getPermanent("minecraft", "light_sign"), "lightSign", Items.DUST_GLOWSTONE, ENTER_NETHER)
      .setType(Achievement.TYPE_NORMAL)
      .registerAchievement();
   public static Achievement SWIM_NETHER = new Achievement(NamespaceID.getPermanent("minecraft", "swim_nether"), "swimNether", Blocks.ICE, ENTER_NETHER)
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement HIT_FIREBALL = new Achievement(
         NamespaceID.getPermanent("minecraft", "hit_fireball"), "hitFireball", Items.AMMO_FIREBALL, ENTER_NETHER
      )
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement SLEEP_NETHER = new Achievement(NamespaceID.getPermanent("minecraft", "sleep_nether"), "sleepNether", Items.BED, ENTER_NETHER)
      .setType(Achievement.TYPE_SPECIAL)
      .registerAchievement();
   public static Achievement MOST_WANTED = new Achievement(
         NamespaceID.getPermanent("minecraft", "most_wanted"), "mostWanted", Items.TOOL_SWORD_GOLD, ENTER_NETHER
      )
      .setType(Achievement.TYPE_NORMAL)
      .registerAchievement();
   public static Achievement FAST_TRAVEL = new Achievement(NamespaceID.getPermanent("minecraft", "fast_travel"), "fastTravel", Blocks.GRASS, ENTER_NETHER)
      .setType(Achievement.TYPE_NORMAL)
      .registerAchievement();

   public static void init() {
   }
}
