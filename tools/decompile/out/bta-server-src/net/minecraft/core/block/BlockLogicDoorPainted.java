package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;

public class BlockLogicDoorPainted extends BlockLogicDoor implements IPainted {
   public BlockLogicDoorPainted(Block<?> block, Material material, boolean isTop) {
      super(block, material, isTop, false, null);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(Items.DOOR_OAK_PAINTED, 1, 15 - (meta >> 4 & 15))};
   }

   @Override
   public DyeColor fromMetadata(int meta) {
      return DyeColor.colorFromBlockMeta(meta >> 4 & 15);
   }

   @Override
   public int toMetadata(DyeColor color) {
      return color.blockMeta << 4;
   }

   @Override
   public int stripColorFromMetadata(int meta) {
      return meta & 15;
   }

   @Override
   public void removeDye(World world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockAndMetadataWithNotify(x, y, z, this.isTop ? Blocks.DOOR_PLANKS_OAK_TOP.id() : Blocks.DOOR_PLANKS_OAK_BOTTOM.id(), meta & 15);
      if (this.isTop) {
         world.setBlockAndMetadataWithNotify(x, y - 1, z, Blocks.DOOR_PLANKS_OAK_BOTTOM.id(), meta & 15);
      } else {
         world.setBlockAndMetadataWithNotify(x, y + 1, z, Blocks.DOOR_PLANKS_OAK_TOP.id(), meta & 15);
      }
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockMetadataWithNotify(x, y, z, meta & 15 | this.toMetadata(color));
      if (this.isTop) {
         world.setBlockMetadataWithNotify(x, y - 1, z, meta & 15 | this.toMetadata(color));
      } else {
         world.setBlockMetadataWithNotify(x, y + 1, z, meta & 15 | this.toMetadata(color));
      }
   }
}
