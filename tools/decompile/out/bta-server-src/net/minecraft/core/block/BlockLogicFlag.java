package net.minecraft.core.block;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicFlag extends BlockLogic {
   public BlockLogicFlag(Block<?> block, Material material) {
      super(block, material);
      float f = 0.125F;
      float f1 = 1.0F;
      this.setBlockBounds(0.5F - f, 0.0, 0.5F - f, 0.5F + f, f1, 0.5F + f);
      block.withEntity(TileEntityFlag::new);
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
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
      ItemStack stack = new ItemStack(Items.FLAG);
      if (tileEntity != null) {
         CompoundTag compound = new CompoundTag();
         ((TileEntityFlag)tileEntity).writeFlagNBT(compound);
         stack.getData().putCompound("FlagData", compound);
      }

      return new ItemStack[]{stack};
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (!world.isClientSide) {
         TileEntityFlag flag = (TileEntityFlag)world.getTileEntity(x, y, z);
         if (flag.owner != null && !player.uuid.equals(flag.owner)) {
            return false;
         } else {
            int heldItemId = player.getHeldItem() != null ? player.getHeldItem().itemID : 0;
            if (heldItemId != Items.MAP.id && heldItemId != Items.FLAG.id) {
               player.displayFlagEditorScreen(flag);
               return true;
            } else {
               return false;
            }
         }
      } else {
         return true;
      }
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return 1;
   }
}
