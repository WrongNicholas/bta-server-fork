package net.minecraft.core.crafting;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.Items;

public class LookupFuelFurnaceBlast extends LookupFuelFurnace {
   public static final LookupFuelFurnaceBlast instance = new LookupFuelFurnaceBlast();

   protected LookupFuelFurnaceBlast() {
   }

   @Override
   protected void register() {
      this.addFuelEntry(Blocks.BLOCK_NETHER_COAL.id(), 12800);
      this.addFuelEntry(Blocks.BLOCK_OLIVINE.id(), 1600);
      this.addFuelEntry(Items.NETHERCOAL.id, 1600);
      this.addFuelEntry(Items.OLIVINE.id, 200);
   }
}
