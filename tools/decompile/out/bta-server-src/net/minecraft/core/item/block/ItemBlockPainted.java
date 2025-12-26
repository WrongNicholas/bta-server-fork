package net.minecraft.core.item.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;

public class ItemBlockPainted<T extends BlockLogic> extends ItemBlock<T> {
   private final boolean upperMetadata;

   public ItemBlockPainted(Block<T> block, boolean upperMetadata) {
      super(block);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
      this.upperMetadata = upperMetadata;
   }

   @Override
   public String getLanguageKey(ItemStack itemstack) {
      return this.upperMetadata
         ? super.getKey() + "." + DyeColor.colorFromBlockMeta((itemstack.getMetadata() & 240) >> 4).colorID
         : super.getKey() + "." + DyeColor.colorFromBlockMeta(itemstack.getMetadata() & 15).colorID;
   }
}
