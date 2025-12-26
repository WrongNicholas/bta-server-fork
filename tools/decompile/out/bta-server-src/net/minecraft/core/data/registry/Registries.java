package net.minecraft.core.data.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.gamerule.GameRule;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeRegistry;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShaped;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShapeless;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingWithTool;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryDyeing;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryLabel;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryLabelDye;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryMapDuplication;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryRepairable;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryRepairableStackable;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryScrap;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryUndyeing;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypes;

public class Registries extends Registry<Registry<?>> {
   public static final Registry<WorldType> WORLD_TYPES = new Registry<>();
   public static final Registry<Biome> BIOMES = new Registry<>();
   public static final Registry<GameRule<?>> GAME_RULES = new Registry<>();
   public static final Registry<Class<? extends RecipeEntryBase<?, ?, ?>>> RECIPE_TYPES = new Registry<>();
   public static final Registry<List<ItemStack>> ITEM_GROUPS = new Registry<>();
   public static RecipeRegistry RECIPES;
   public static RecipeRegistry RECIPES_LOCAL_COPY;
   private static Registries INSTANCE;
   public static boolean init = false;

   public Registries() {
      if (!init) {
         init = true;
         INSTANCE = this;
         this.register("minecraft:world_types", WORLD_TYPES);
         this.register("minecraft:biomes", BIOMES);
         this.register("minecraft:recipe_types", RECIPE_TYPES);
         this.register("minecraft:item_groups", ITEM_GROUPS);
         this.init();
         this.register("minecraft:recipes", RECIPES);
      }
   }

   private void init() {
      WorldTypes.init();
      Biomes.init();
      GameRules.init();
      RECIPE_TYPES.register("minecraft:crafting/shaped", RecipeEntryCraftingShaped.class);
      RECIPE_TYPES.register("minecraft:crafting/shapeless", RecipeEntryCraftingShapeless.class);
      RECIPE_TYPES.register("minecraft:crafting/label", RecipeEntryLabel.class);
      RECIPE_TYPES.register("minecraft:crafting/label_dye", RecipeEntryLabelDye.class);
      RECIPE_TYPES.register("minecraft:crafting/scrap", RecipeEntryScrap.class);
      RECIPE_TYPES.register("minecraft:crafting/repairable", RecipeEntryRepairable.class);
      RECIPE_TYPES.register("minecraft:crafting/repairable_stackable", RecipeEntryRepairableStackable.class);
      RECIPE_TYPES.register("minecraft:crafting/uses_tool", RecipeEntryCraftingWithTool.class);
      RECIPE_TYPES.register("minecraft:crafting/map_duplication", RecipeEntryMapDuplication.class);
      RECIPE_TYPES.register("minecraft:crafting/undyeing", RecipeEntryUndyeing.class);
      RECIPE_TYPES.register("minecraft:crafting/dyeing", RecipeEntryDyeing.class);
      RECIPE_TYPES.register("minecraft:smelting", RecipeEntryFurnace.class);
      RECIPE_TYPES.register("minecraft:smelting/blast", RecipeEntryBlastFurnace.class);
      RECIPE_TYPES.register("minecraft:trommeling", RecipeEntryTrommel.class);
      ITEM_GROUPS.register("minecraft:stones", stackListOf(Blocks.STONE, Blocks.BASALT, Blocks.LIMESTONE, Blocks.GRANITE, Blocks.PERMAFROST, Blocks.NETHERRACK));
      ITEM_GROUPS.register(
         "minecraft:cobblestones",
         stackListOf(
            Blocks.COBBLE_STONE, Blocks.COBBLE_BASALT, Blocks.COBBLE_LIMESTONE, Blocks.COBBLE_GRANITE, Blocks.COBBLE_PERMAFROST, Blocks.COBBLE_NETHERRACK
         )
      );
      ITEM_GROUPS.register("minecraft:grasses", stackListOf(Blocks.GRASS, Blocks.GRASS_RETRO));
      ITEM_GROUPS.register("minecraft:dirt", stackListOf(Blocks.DIRT, Blocks.DIRT_SCORCHED));
      ITEM_GROUPS.register(
         "minecraft:trommel_dirt",
         stackListOf(
            Blocks.DIRT, Blocks.DIRT_SCORCHED, Blocks.GRASS, Blocks.GRASS_RETRO, Blocks.GRASS_SCORCHED, Blocks.PATH_DIRT, Blocks.FARMLAND_DIRT, Blocks.MUD
         )
      );
      ITEM_GROUPS.register("minecraft:moss_stones", stackListOf(Blocks.MOSS_STONE, Blocks.MOSS_BASALT, Blocks.MOSS_LIMESTONE, Blocks.MOSS_GRANITE));
      ITEM_GROUPS.register(
         "minecraft:logs",
         stackListOf(
            Blocks.LOG_OAK,
            Blocks.LOG_PINE,
            Blocks.LOG_BIRCH,
            Blocks.LOG_CHERRY,
            Blocks.LOG_EUCALYPTUS,
            Blocks.LOG_OAK_MOSSY,
            Blocks.LOG_THORN,
            Blocks.LOG_PALM
         )
      );
      ITEM_GROUPS.register(
         "minecraft:leaves",
         stackListOf(
            Blocks.LEAVES_OAK,
            Blocks.LEAVES_OAK_RETRO,
            Blocks.LEAVES_PINE,
            Blocks.LEAVES_BIRCH,
            Blocks.LEAVES_CHERRY,
            Blocks.LEAVES_EUCALYPTUS,
            Blocks.LEAVES_SHRUB
         )
      );
      ITEM_GROUPS.register(
         "minecraft:coal_ores",
         stackListOf(Blocks.ORE_COAL_STONE, Blocks.ORE_COAL_BASALT, Blocks.ORE_COAL_LIMESTONE, Blocks.ORE_COAL_GRANITE, Blocks.ORE_COAL_PERMAFROST)
      );
      ITEM_GROUPS.register(
         "minecraft:iron_ores",
         stackListOf(Blocks.ORE_IRON_STONE, Blocks.ORE_IRON_BASALT, Blocks.ORE_IRON_LIMESTONE, Blocks.ORE_IRON_GRANITE, Blocks.ORE_IRON_PERMAFROST)
      );
      ITEM_GROUPS.register(
         "minecraft:gold_ores",
         stackListOf(Blocks.ORE_GOLD_STONE, Blocks.ORE_GOLD_BASALT, Blocks.ORE_GOLD_LIMESTONE, Blocks.ORE_GOLD_GRANITE, Blocks.ORE_GOLD_PERMAFROST)
      );
      ITEM_GROUPS.register(
         "minecraft:lapis_ores",
         stackListOf(Blocks.ORE_LAPIS_STONE, Blocks.ORE_LAPIS_BASALT, Blocks.ORE_LAPIS_LIMESTONE, Blocks.ORE_LAPIS_GRANITE, Blocks.ORE_LAPIS_PERMAFROST)
      );
      ITEM_GROUPS.register(
         "minecraft:redstone_ores",
         stackListOf(
            Blocks.ORE_REDSTONE_STONE,
            Blocks.ORE_REDSTONE_BASALT,
            Blocks.ORE_REDSTONE_LIMESTONE,
            Blocks.ORE_REDSTONE_GRANITE,
            Blocks.ORE_REDSTONE_PERMAFROST,
            Blocks.ORE_REDSTONE_GLOWING_STONE,
            Blocks.ORE_REDSTONE_GLOWING_BASALT,
            Blocks.ORE_REDSTONE_GLOWING_LIMESTONE,
            Blocks.ORE_REDSTONE_GLOWING_GRANITE,
            Blocks.ORE_REDSTONE_GLOWING_PERMAFROST
         )
      );
      ITEM_GROUPS.register(
         "minecraft:diamond_ores",
         stackListOf(
            Blocks.ORE_DIAMOND_STONE, Blocks.ORE_DIAMOND_BASALT, Blocks.ORE_DIAMOND_LIMESTONE, Blocks.ORE_DIAMOND_GRANITE, Blocks.ORE_DIAMOND_PERMAFROST
         )
      );
      ITEM_GROUPS.register("minecraft:nethercoal_ores", stackListOf(Blocks.ORE_NETHERCOAL_NETHERRACK));
      List<ItemStack> plankStackList = new ArrayList<>();
      List<ItemStack> signStackList = new ArrayList<>();
      List<ItemStack> doorStackList = new ArrayList<>();
      List<ItemStack> chestStackList = new ArrayList<>();
      List<ItemStack> stairsStackList = new ArrayList<>();
      List<ItemStack> slabsStackList = new ArrayList<>();
      List<ItemStack> fencesStackList = new ArrayList<>();
      List<ItemStack> fenceGatesStackList = new ArrayList<>();
      List<ItemStack> trapdoorStackList = new ArrayList<>();
      List<ItemStack> woolStackList = new ArrayList<>();
      List<ItemStack> lampStackList = new ArrayList<>();
      List<ItemStack> invertedLampStackList = new ArrayList<>();
      List<ItemStack> woodenButtonsList = new ArrayList<>();
      List<ItemStack> buttonsList = new ArrayList<>();
      List<ItemStack> woodenPressurePlatesList = new ArrayList<>();
      List<ItemStack> pressurePlatesList = new ArrayList<>();
      plankStackList.add(Blocks.PLANKS_OAK.getDefaultStack());
      signStackList.add(Items.SIGN.getDefaultStack());
      doorStackList.add(Items.DOOR_OAK.getDefaultStack());
      chestStackList.add(Blocks.CHEST_PLANKS_OAK.getDefaultStack());
      stairsStackList.add(Blocks.STAIRS_PLANKS_OAK.getDefaultStack());
      slabsStackList.add(Blocks.SLAB_PLANKS_OAK.getDefaultStack());
      fencesStackList.add(Blocks.FENCE_PLANKS_OAK.getDefaultStack());
      fenceGatesStackList.add(Blocks.FENCE_GATE_PLANKS_OAK.getDefaultStack());
      trapdoorStackList.add(Blocks.TRAPDOOR_PLANKS_OAK.getDefaultStack());
      woodenButtonsList.add(Blocks.BUTTON_PLANKS.getDefaultStack());
      buttonsList.add(Blocks.BUTTON_STONE.getDefaultStack());
      woodenPressurePlatesList.add(Blocks.PRESSURE_PLATE_PLANKS_OAK.getDefaultStack());
      pressurePlatesList.add(Blocks.PRESSURE_PLATE_STONE.getDefaultStack());
      pressurePlatesList.add(Blocks.PRESSURE_PLATE_COBBLE_STONE.getDefaultStack());

      for (DyeColor color : DyeColor.values()) {
         plankStackList.add(new ItemStack(Blocks.PLANKS_OAK_PAINTED, 1, color.blockMeta));
         signStackList.add(new ItemStack(Items.SIGN_PAINTED, 1, color.itemMeta));
         doorStackList.add(new ItemStack(Items.DOOR_OAK_PAINTED, 1, color.itemMeta));
         chestStackList.add(new ItemStack(Blocks.CHEST_PLANKS_OAK_PAINTED, 1, color.blockMeta << 4));
         stairsStackList.add(new ItemStack(Blocks.STAIRS_PLANKS_PAINTED, 1, color.blockMeta << 4));
         slabsStackList.add(new ItemStack(Blocks.SLAB_PLANKS_PAINTED, 1, color.blockMeta << 4));
         fencesStackList.add(new ItemStack(Blocks.FENCE_PLANKS_OAK_PAINTED, 1, color.blockMeta));
         fenceGatesStackList.add(new ItemStack(Blocks.FENCE_GATE_PLANKS_OAK_PAINTED, 1, color.blockMeta << 4));
         trapdoorStackList.add(new ItemStack(Blocks.TRAPDOOR_PLANKS_PAINTED, 1, color.blockMeta << 4));
         woolStackList.add(new ItemStack(Blocks.WOOL, 1, color.blockMeta));
         lampStackList.add(new ItemStack(Blocks.LAMP_IDLE, 1, color.blockMeta));
         invertedLampStackList.add(new ItemStack(Blocks.LAMP_INVERTED_ACTIVE, 1, color.blockMeta));
         woodenButtonsList.add(new ItemStack(Blocks.BUTTON_PLANKS_PAINTED, 1, color.blockMeta << 4));
         woodenPressurePlatesList.add(new ItemStack(Blocks.PRESSURE_PLATE_PLANKS_OAK_PAINTED, 1, color.blockMeta << 4));
      }

      buttonsList.addAll(woodenButtonsList);
      pressurePlatesList.addAll(woodenPressurePlatesList);
      ITEM_GROUPS.register("minecraft:planks", plankStackList);
      ITEM_GROUPS.register("minecraft:signs", signStackList);
      ITEM_GROUPS.register("minecraft:doors", doorStackList);
      ITEM_GROUPS.register("minecraft:chests", chestStackList);
      ITEM_GROUPS.register("minecraft:wools", woolStackList);
      ITEM_GROUPS.register("minecraft:lamps", lampStackList);
      ITEM_GROUPS.register("minecraft:lamps_inverted", invertedLampStackList);
      ITEM_GROUPS.register("minecraft:stairs_planks", stairsStackList);
      ITEM_GROUPS.register("minecraft:slabs_planks", slabsStackList);
      ITEM_GROUPS.register("minecraft:fences_planks", fencesStackList);
      ITEM_GROUPS.register("minecraft:fence_gates_planks", fenceGatesStackList);
      ITEM_GROUPS.register("minecraft:trapdoor_planks", trapdoorStackList);
      ITEM_GROUPS.register("minecraft:buttons_planks", woodenButtonsList);
      ITEM_GROUPS.register("minecraft:buttons", buttonsList);
      ITEM_GROUPS.register("minecraft:pressureplates_planks", woodenPressurePlatesList);
      ITEM_GROUPS.register("minecraft:pressureplates", pressurePlatesList);
      ITEM_GROUPS.register(
         "minecraft:tool_swords",
         stackListOf(
            new ItemStack(Items.TOOL_SWORD_WOOD, 1, -1),
            new ItemStack(Items.TOOL_SWORD_STONE, 1, -1),
            new ItemStack(Items.TOOL_SWORD_IRON, 1, -1),
            new ItemStack(Items.TOOL_SWORD_GOLD, 1, -1),
            new ItemStack(Items.TOOL_SWORD_DIAMOND, 1, -1),
            new ItemStack(Items.TOOL_SWORD_STEEL, 1, -1)
         )
      );
      if (RECIPES == null) {
         new RecipeRegistry();
      }
   }

   public static List<ItemStack> stackListOf(Object... items) {
      ArrayList<ItemStack> list = new ArrayList<>();

      for (Object item : items) {
         if (item instanceof Block) {
            list.add(new ItemStack((Block<?>)item));
         } else if (item instanceof Item) {
            list.add(new ItemStack((Item)item));
         } else if (item instanceof ItemStack) {
            list.add(new ItemStack((ItemStack)item));
         } else if (item instanceof Collection) {
            list.addAll(stackListOf(((Collection)item).toArray(new Object[0])));
         }
      }

      return list;
   }

   public static Registries getInstance() {
      return INSTANCE;
   }
}
