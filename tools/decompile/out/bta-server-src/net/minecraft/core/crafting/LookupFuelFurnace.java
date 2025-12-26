package net.minecraft.core.crafting;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.Items;

public class LookupFuelFurnace {
   public static final LookupFuelFurnace instance = new LookupFuelFurnace();
   protected final Map<Integer, Integer> fuelList = new HashMap<>();

   protected LookupFuelFurnace() {
      this.register();
   }

   protected void register() {
      this.addFuelEntry(Blocks.PLANKS_OAK.id(), 300);
      this.addFuelEntry(Blocks.PLANKS_OAK_PAINTED.id(), 300);
      this.addFuelEntry(Blocks.STAIRS_PLANKS_OAK.id(), 300);
      this.addFuelEntry(Blocks.STAIRS_PLANKS_PAINTED.id(), 300);
      this.addFuelEntry(Blocks.SLAB_PLANKS_OAK.id(), 150);
      this.addFuelEntry(Blocks.SLAB_PLANKS_PAINTED.id(), 150);
      this.addFuelEntry(Blocks.FENCE_PLANKS_OAK.id(), 300);
      this.addFuelEntry(Blocks.FENCE_PLANKS_OAK_PAINTED.id(), 300);
      this.addFuelEntry(Blocks.FENCE_GATE_PLANKS_OAK.id(), 300);
      this.addFuelEntry(Blocks.FENCE_GATE_PLANKS_OAK_PAINTED.id(), 300);
      this.addFuelEntry(Blocks.LADDER_OAK.id(), 300);
      this.addFuelEntry(Blocks.BOOKSHELF_PLANKS_OAK.id(), 300);
      this.addFuelEntry(Blocks.CHEST_PLANKS_OAK.id(), 300);
      this.addFuelEntry(Blocks.CHEST_PLANKS_OAK_PAINTED.id(), 300);
      this.addFuelEntry(Blocks.WORKBENCH.id(), 300);
      this.addFuelEntry(Blocks.PRESSURE_PLATE_PLANKS_OAK.id(), 300);
      this.addFuelEntry(Blocks.TRAPDOOR_PLANKS_OAK.id(), 300);
      this.addFuelEntry(Blocks.TRAPDOOR_PLANKS_PAINTED.id(), 300);
      this.addFuelEntry(Blocks.LOG_OAK.id(), 300);
      this.addFuelEntry(Blocks.LOG_BIRCH.id(), 300);
      this.addFuelEntry(Blocks.LOG_PINE.id(), 300);
      this.addFuelEntry(Blocks.LOG_CHERRY.id(), 300);
      this.addFuelEntry(Blocks.LOG_OAK_MOSSY.id(), 300);
      this.addFuelEntry(Blocks.LOG_EUCALYPTUS.id(), 300);
      this.addFuelEntry(Blocks.LOG_THORN.id(), 300);
      this.addFuelEntry(Blocks.LOG_PALM.id(), 300);
      this.addFuelEntry(Blocks.SAPLING_OAK.id(), 100);
      this.addFuelEntry(Blocks.SAPLING_PINE.id(), 100);
      this.addFuelEntry(Blocks.SAPLING_BIRCH.id(), 100);
      this.addFuelEntry(Blocks.SAPLING_CHERRY.id(), 100);
      this.addFuelEntry(Blocks.SAPLING_EUCALYPTUS.id(), 100);
      this.addFuelEntry(Blocks.SAPLING_SHRUB.id(), 100);
      this.addFuelEntry(Blocks.SAPLING_OAK_RETRO.id(), 100);
      this.addFuelEntry(Blocks.SAPLING_CACAO.id(), 100);
      this.addFuelEntry(Blocks.SAPLING_THORN.id(), 100);
      this.addFuelEntry(Blocks.SAPLING_PALM.id(), 100);
      this.addFuelEntry(Blocks.DEADBUSH.id(), 100);
      this.addFuelEntry(Blocks.SPINIFEX.id(), 100);
      this.addFuelEntry(Blocks.BLOCK_COAL.id(), 12800);
      this.addFuelEntry(Blocks.BLOCK_CHARCOAL.id(), 12800);
      this.addFuelEntry(Blocks.BLOCK_NETHER_COAL.id(), 25600);
      this.addFuelEntry(Items.STICK.id, 100);
      this.addFuelEntry(Items.TOOL_PICKAXE_WOOD.id, 500);
      this.addFuelEntry(Items.TOOL_SWORD_WOOD.id, 500);
      this.addFuelEntry(Items.TOOL_AXE_WOOD.id, 500);
      this.addFuelEntry(Items.TOOL_SHOVEL_WOOD.id, 500);
      this.addFuelEntry(Items.TOOL_HOE_WOOD.id, 500);
      this.addFuelEntry(Items.TOOL_BOW.id, 300);
      this.addFuelEntry(Items.TOOL_FISHINGROD.id, 300);
      this.addFuelEntry(Items.BOAT.id, 300);
      this.addFuelEntry(Items.DOOR_OAK.id, 300);
      this.addFuelEntry(Items.DOOR_OAK_PAINTED.id, 300);
      this.addFuelEntry(Items.SIGN.id, 300);
      this.addFuelEntry(Items.BOWL.id, 300);
      this.addFuelEntry(Items.TOOL_CALENDAR.id, 300);
      this.addFuelEntry(Items.COAL.id, 1600);
      this.addFuelEntry(Items.NETHERCOAL.id, 6400);
      this.addFuelEntry(Items.BUCKET_LAVA.id, 20000);
   }

   public void addFuelEntry(int id, int fuelYield) {
      this.fuelList.put(id, fuelYield);
   }

   public int getFuelYield(int id) {
      return this.fuelList.get(id) == null ? 0 : this.fuelList.get(id);
   }

   public Map<Integer, Integer> getFuelList() {
      return this.fuelList;
   }
}
