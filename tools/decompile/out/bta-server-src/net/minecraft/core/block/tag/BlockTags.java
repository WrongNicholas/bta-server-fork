package net.minecraft.core.block.tag;

import com.mojang.logging.LogUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.block.Block;
import net.minecraft.core.data.tag.Tag;
import org.slf4j.Logger;

public abstract class BlockTags {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static Tag<Block<?>> MINEABLE_BY_PICKAXE = Tag.of("mineable_by_pickaxe");
   public static Tag<Block<?>> MINEABLE_BY_AXE = Tag.of("mineable_by_axe");
   public static Tag<Block<?>> MINEABLE_BY_SHOVEL = Tag.of("mineable_by_shovel");
   public static Tag<Block<?>> MINEABLE_BY_HOE = Tag.of("mineable_by_hoe");
   public static Tag<Block<?>> MINEABLE_BY_SWORD = Tag.of("mineable_by_sword");
   public static Tag<Block<?>> MINEABLE_BY_SHEARS = Tag.of("mineable_by_shears");
   public static Tag<Block<?>> IS_WATER = Tag.of("is_water");
   public static Tag<Block<?>> IS_LAVA = Tag.of("is_lava");
   public static Tag<Block<?>> GROWS_FLOWERS = Tag.of("grows_flowers");
   public static Tag<Block<?>> GROWS_SUGAR_CANE = Tag.of("grows_sugar_cane");
   public static Tag<Block<?>> GROWS_CACTI = Tag.of("grows_cacti");
   public static Tag<Block<?>> GROWS_TREES = Tag.of("grows_trees");
   public static Tag<Block<?>> GROWS_SPINIFEX = Tag.of("grows_spinifex");
   public static Tag<Block<?>> BROKEN_BY_FLUIDS = Tag.of("broken_by_fluids");
   public static Tag<Block<?>> PLACE_OVERWRITES = Tag.of("place_overwrites");
   public static Tag<Block<?>> PASSIVE_MOBS_SPAWN = Tag.of("passive_mobs_spawn");
   public static Tag<Block<?>> FIREFLIES_CAN_SPAWN = Tag.of("fireflies_can_spawn");
   public static Tag<Block<?>> FENCES_CONNECT = Tag.of("fences_connect");
   public static Tag<Block<?>> NOT_IN_CREATIVE_MENU = Tag.of("not_in_creative_menu");
   public static Tag<Block<?>> SHEARS_DO_SILK_TOUCH = Tag.of("shears_do_silk_touch");
   public static Tag<Block<?>> SKATEABLE = Tag.of("skateable");
   public static Tag<Block<?>> CAVE_GEN_REPLACES_SURFACE = Tag.of("cave_gen_replaces_surface");
   public static Tag<Block<?>> CAVES_CUT_THROUGH = Tag.of("caves_cut_through");
   public static Tag<Block<?>> CAN_HANG_OFF = Tag.of("can_hang_off");
   public static Tag<Block<?>> OVERRIDE_STEPSOUND = Tag.of("override_stepsound");
   public static Tag<Block<?>> CHAINLINK_FENCES_CONNECT = Tag.of("chainlink_fences_connect");
   public static Tag<Block<?>> PREVENT_MOB_SPAWNS = Tag.of("prevent_mob_spawns");
   public static Tag<Block<?>> PLANTABLE_IN_JAR = Tag.of("plantable_in_jar");
   public static Tag<Block<?>> INFINITE_BURN = Tag.of("infinite_burn");
   public static Tag<Block<?>> BOAT_BREAKS = Tag.of("boat_breaks");
   public static Tag<Block<?>> EXTENDS_MOTION_SENSOR_RANGE = Tag.of("extends_motion_sensor_range");
   public static Tag<Block<?>> INSTANT_PICKUP = Tag.of("instant_pickup");
   public static Tag<Block<?>> SHEEPS_FAVOURITE_BLOCK = Tag.of("sheeps_favourite_block");
   public static Tag<Block<?>> PIGS_FAVOURITE_BLOCK = Tag.of("pigs_favourite_block");
   public static Tag<Block<?>> NETHER_MOBS_SPAWN = Tag.of("nether_mobs_spawn");
   public static Tag<Block<?>> PISTON_CRUSHING = Tag.of("piston_crushing");
   public static List<Tag<Block<?>>> TAG_LIST = new ArrayList<>();

   static {
      for (Field field : BlockTags.class.getDeclaredFields()) {
         if (field.getType().equals(Tag.class)) {
            try {
               TAG_LIST.add((Tag<Block<?>>)field.get(null));
            } catch (Exception var5) {
               LOGGER.error("Failed to add tag '{}'!", field.getName(), var5);
            }
         }
      }
   }
}
