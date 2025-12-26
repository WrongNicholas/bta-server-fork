package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicOverlayPebbles extends BlockLogic {
   public BlockLogicOverlayPebbles(Block<?> block, Material material) {
      super(block, material);
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      Block<?> block = world.getBlock(x, y - 1, z);
      return block != null && block.getMaterial().isSolid();
   }

   @Override
   public boolean canBlockStay(World world, int x, int y, int z) {
      return this.canPlaceBlockAt(world, x, y, z);
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (!this.canBlockStay(world, x, y, z)) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return dropCause == EnumDropCause.PICK_BLOCK
         ? new ItemStack[]{new ItemStack(Items.AMMO_PEBBLE, 1)}
         : new ItemStack[]{new ItemStack(Items.AMMO_PEBBLE, meta + 1)};
   }
}
