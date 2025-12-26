package net.minecraft.core.item.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemBlockLamp<T extends BlockLogic> extends ItemBlock<T> {
   public ItemBlockLamp(Block<T> block) {
      super(block);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   @Override
   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      return stack.getMetadata();
   }

   @Override
   public String getLanguageKey(ItemStack itemstack) {
      return super.getKey() + "." + DyeColor.colorFromBlockMeta(itemstack.getMetadata()).colorID;
   }
}
