package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemFireStriker;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicBrazier extends BlockLogic {
   private final boolean burning;

   public BlockLogicBrazier(Block<?> block, boolean burning) {
      super(block, Material.metal);
      this.burning = burning;
   }

   public boolean isBurning() {
      return this.burning;
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(Blocks.BRAZIER_INACTIVE, 1)};
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      ItemStack heldItem = player.getHeldItem();
      if (heldItem != null && heldItem.getItem() instanceof ItemFireStriker && !this.burning) {
         Block<?> b;
         if (((b = world.getBlock(x + 1, y, z)) == null || !(b.getLogic() instanceof BlockLogicFluid))
            && ((b = world.getBlock(x - 1, y, z)) == null || !(b.getLogic() instanceof BlockLogicFluid))
            && ((b = world.getBlock(x, y, z + 1)) == null || !(b.getLogic() instanceof BlockLogicFluid))
            && ((b = world.getBlock(x, y, z - 1)) == null || !(b.getLogic() instanceof BlockLogicFluid))) {
            world.setBlockAndMetadataWithNotify(x, y, z, Blocks.BRAZIER_ACTIVE.id(), 0);
            heldItem.damageItem(1, player);
            world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.5, z + 0.5, "fire.ignite", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
            return true;
         } else {
            return false;
         }
      } else if (heldItem == null && this.burning) {
         world.setBlockAndMetadataWithNotify(x, y, z, Blocks.BRAZIER_INACTIVE.id(), 0);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
      if (this.burning) {
         world.setBlockAndMetadataWithNotify(x, y, z, Blocks.BRAZIER_INACTIVE.id(), 0);
      }
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      if (this.burning) {
         Blocks.FIRE.onBlockRemoved(world, x, y, z, data);
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (this.burning) {
         Block<?> block = Blocks.blocksList[blockId];
         if (block != null && block.getLogic() instanceof BlockLogicFluid) {
            world.setBlockAndMetadataWithNotify(x, y, z, Blocks.BRAZIER_INACTIVE.id(), 0);
         }
      }
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (this.burning) {
         Blocks.FIRE.animationTick(world, x, y, z, rand);
      }
   }
}
