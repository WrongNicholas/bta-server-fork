package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.MaterialColor;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemToolSword;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicPumpkin extends BlockLogicVeryRotatable {
   private final boolean isCarvable;

   public BlockLogicPumpkin(Block<?> block, boolean isCarvable) {
      super(block, Material.vegetable);
      block.setTicking(true);
      this.isCarvable = isCarvable;
      block.withOverrideColor(MaterialColor.paintedOrange);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (!this.isCarvable) {
         return false;
      } else {
         ItemStack heldItem = player.getHeldItem();
         if (heldItem != null && heldItem.getItem() instanceof ItemToolSword) {
            heldItem.damageItem(1, player);
            world.setBlockAndMetadataWithNotify(
               x,
               y,
               z,
               Blocks.PUMPKIN_CARVED_IDLE.id(),
               player.getHorizontalPlacementDirection(Direction.getHorizontalDirection(player).getSide()).getOpposite().getId()
            );
            return true;
         } else {
            return false;
         }
      }
   }
}
