package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class BlockLogicLamp extends BlockLogic implements IPainted {
   public boolean isActive;
   public boolean isInverted;

   public BlockLogicLamp(Block<?> block, boolean isActive, boolean isInverted) {
      super(block, Material.stone);
      this.isActive = isActive;
      this.isInverted = isInverted;
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      if (this.isInverted) {
         if (this.isActive && (world.hasDirectSignal(x, y, z) || world.hasNeighborSignal(x, y, z))) {
            world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_INVERTED_IDLE.id(), world.getBlockMetadata(x, y, z));
         }
      } else if (this.isActive && !world.hasDirectSignal(x, y, z) && !world.hasNeighborSignal(x, y, z)) {
         world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_IDLE.id(), world.getBlockMetadata(x, y, z));
      }
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      if (!world.isClientSide) {
         boolean hasSignal = world.hasDirectSignal(x, y, z) || world.hasNeighborSignal(x, y, z);
         if (this.isInverted) {
            if (hasSignal && this.isActive) {
               world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_INVERTED_IDLE.id(), world.getBlockMetadata(x, y, z));
            } else if (!hasSignal && !this.isActive) {
               world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_INVERTED_ACTIVE.id(), world.getBlockMetadata(x, y, z));
            }
         } else if (hasSignal && !this.isActive) {
            world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_ACTIVE.id(), world.getBlockMetadata(x, y, z));
         } else if (!hasSignal && this.isActive) {
            world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_IDLE.id(), world.getBlockMetadata(x, y, z));
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      boolean hasSignal = world.hasDirectSignal(x, y, z) || world.hasNeighborSignal(x, y, z);
      if (this.isInverted) {
         if (hasSignal && this.isActive) {
            world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_INVERTED_IDLE.id(), world.getBlockMetadata(x, y, z));
         } else if (!hasSignal && !this.isActive) {
            world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_INVERTED_ACTIVE.id(), world.getBlockMetadata(x, y, z));
         }
      } else if (hasSignal && !this.isActive) {
         world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_ACTIVE.id(), world.getBlockMetadata(x, y, z));
      } else if (!hasSignal && this.isActive) {
         world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_IDLE.id(), world.getBlockMetadata(x, y, z));
      }
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      if (player.getHeldItem() == null) {
         this.invertLamp(world, x, y, z);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
      this.invertLamp(world, x, y, z);
   }

   public void invertLamp(@NotNull World world, int x, int y, int z) {
      if (this.isInverted) {
         if (this.isActive) {
            world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_IDLE.id(), world.getBlockMetadata(x, y, z));
         } else {
            world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_ACTIVE.id(), world.getBlockMetadata(x, y, z));
         }
      } else if (this.isActive) {
         world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_INVERTED_IDLE.id(), world.getBlockMetadata(x, y, z));
      } else {
         world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAMP_INVERTED_ACTIVE.id(), world.getBlockMetadata(x, y, z));
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(Blocks.LAMP_IDLE, 1, meta)};
   }

   @Override
   public DyeColor fromMetadata(int meta) {
      return DyeColor.colorFromBlockMeta(meta & 15);
   }

   @Override
   public int toMetadata(DyeColor color) {
      return color.blockMeta;
   }

   @Override
   public int stripColorFromMetadata(int meta) {
      return 0;
   }

   @Override
   public void removeDye(World world, int x, int y, int z) {
      world.setBlockMetadataWithNotify(x, y, z, 0);
   }
}
