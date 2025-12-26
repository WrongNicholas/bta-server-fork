package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicActivator extends BlockLogicVeryRotatable {
   public static final int MASK_POWERED = 8;

   public BlockLogicActivator(Block<?> block) {
      super(block, Material.netherrack);
      block.withEntity(TileEntityActivator::new);
   }

   @Override
   public int tickDelay() {
      return 4;
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (!world.isClientSide) {
         TileEntityActivator tileEntityActivator = (TileEntityActivator)world.getTileEntity(x, y, z);
         player.displayActivatorScreen(tileEntityActivator);
      }

      return true;
   }

   private void useItem(World world, int x, int y, int z, Random random) {
      Direction direction = BlockLogicRotatable.getDirectionFromMeta(world.getBlockMetadata(x, y, z));
      int xOffset = direction.getOffsetX();
      int yOffset = direction.getOffsetY();
      int zOffset = direction.getOffsetZ();
      TileEntityActivator activator = (TileEntityActivator)world.getTileEntity(x, y, z);
      if (!activator.locked(activator.stackSelector)) {
         ItemStack itemStack = activator.getNextStack();
         double px = xOffset * 0.6 + 0.5;
         double py = yOffset * 0.6 + 0.5;
         double pz = zOffset * 0.6 + 0.5;
         if (itemStack == null) {
            Block<?> block = world.getBlock(x + xOffset, y + yOffset, z + zOffset);
            if (block != null) {
               block.onActivatorInteract(world, x + xOffset, y + yOffset, z + zOffset, activator, direction);
            } else {
               world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x, y, z, "tile.activator.click", 1.0F, 1.0F);
            }
         } else {
            Item item = itemStack.getItem();
            item.onUseByActivator(itemStack, activator, world, random, x, y, z, px, py, pz, direction);
            activator.nullDeadItems();
            world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x, y, z, "tile.activator.use", 1.0F, 1.0F);
            world.playBlockEvent(2000, x, y, z, direction.getId());
         }
      } else {
         activator.shiftSelector();
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      int meta = world.getBlockMetadata(x, y, z);
      boolean hasSignal = this.getNeighborSignal(world, x, y, z, BlockLogicRotatable.getDirectionFromMeta(meta));
      boolean isPowered = (meta & 8) != 0;
      if (hasSignal && !isPowered) {
         world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay());
         world.setBlockMetadataWithNotify(x, y, z, meta | 8);
      } else if (!hasSignal && isPowered) {
         world.setBlockMetadataWithNotify(x, y, z, meta & -9);
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      this.useItem(world, x, y, z, rand);
   }

   private boolean getNeighborSignal(World world, int x, int y, int z, Direction direction) {
      if (direction != Direction.DOWN && world.getSignal(x, y - 1, z, Side.BOTTOM)) {
         return true;
      } else if (direction != Direction.UP && world.getSignal(x, y + 1, z, Side.TOP)) {
         return true;
      } else if (direction != Direction.NORTH && world.getSignal(x, y, z - 1, Side.NORTH)) {
         return true;
      } else if (direction != Direction.SOUTH && world.getSignal(x, y, z + 1, Side.SOUTH)) {
         return true;
      } else if (direction != Direction.EAST && world.getSignal(x + 1, y, z, Side.EAST)) {
         return true;
      } else {
         return direction != Direction.WEST && world.getSignal(x - 1, y, z, Side.WEST) ? true : world.getSignal(x, y, z, Side.BOTTOM);
      }
   }
}
