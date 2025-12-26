package net.minecraft.core.block;

import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntitySign;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicSign extends BlockLogic implements IPaintable {
   public static final int MASK_SIDE = 15;
   public static final int SIDE_NORTH = 2;
   public static final int SIDE_SOUTH = 3;
   public static final int SIDE_WEST = 4;
   public static final int SIDE_EAST = 5;
   public final boolean isFreeStanding;

   public BlockLogicSign(Block<?> block, boolean isFreeStanding) {
      super(block, Material.wood);
      this.isFreeStanding = isFreeStanding;
      float f = 0.25F;
      float f1 = 1.0F;
      this.setBlockBounds(0.5F - f, 0.0, 0.5F - f, 0.5F + f, f1, 0.5F + f);
      block.withEntity(TileEntitySign::new);
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      if (this.isFreeStanding) {
         return this.getBounds();
      } else {
         float bottom = 0.28125F;
         float top = 0.78125F;
         float width = 1.0F;
         float thickness = 0.125F;
         switch (world.getBlockMetadata(x, y, z) & 15) {
            case 2:
               return AABB.getTemporaryBB(0.0, bottom, 1.0F - thickness, width, top, 1.0);
            case 3:
               return AABB.getTemporaryBB(0.0, bottom, 0.0, width, top, thickness);
            case 4:
               return AABB.getTemporaryBB(1.0F - thickness, bottom, 0.0, 1.0, top, width);
            case 5:
               return AABB.getTemporaryBB(0.0, bottom, 0.0, thickness, top, width);
            default:
               return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         }
      }
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
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      boolean isUnstable = false;
      if (this.isFreeStanding) {
         if (!world.getBlockMaterial(x, y - 1, z).isSolid()) {
            isUnstable = true;
         }
      } else {
         switch (world.getBlockMetadata(x, y, z) & 15) {
            case 2:
               isUnstable = !world.getBlockMaterial(x, y, z + 1).isSolid();
               break;
            case 3:
               isUnstable = !world.getBlockMaterial(x, y, z - 1).isSolid();
               break;
            case 4:
               isUnstable = !world.getBlockMaterial(x + 1, y, z).isSolid();
               break;
            case 5:
               isUnstable = !world.getBlockMaterial(x - 1, y, z).isSolid();
               break;
            default:
               isUnstable = true;
         }
      }

      if (isUnstable) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      }

      super.onNeighborBlockChange(world, x, y, z, blockId);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(Items.SIGN)};
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      TileEntitySign signEntity = (TileEntitySign)world.getTileEntity(x, y, z);
      if (signEntity != null && player != null) {
         if (player.getHeldItem() != null && player.getHeldItem().itemID == Items.DUST_GLOWSTONE.id && !signEntity.isGlowing()) {
            signEntity.setGlowing(true);
            if (player.getGamemode().consumeBlocks()) {
               player.getHeldItem().stackSize--;
            }

            player.addStat(Achievements.LIGHT_SIGN, 1);
            return true;
         }

         if (player.getHeldItem() != null && (player.getHeldItem().itemID == Items.DYE.id || player.getHeldItem().itemID == Items.PAINTBRUSH.id)) {
            return false;
         }

         if (signEntity.isEditableBy(player)) {
            player.displaySignEditorScreen(signEntity);
            return true;
         }
      }

      return false;
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return 1;
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      world.setBlockRaw(x, y, z, this.isFreeStanding ? Blocks.SIGN_POST_PLANKS_OAK_PAINTED.id() : Blocks.SIGN_WALL_PLANKS_OAK_PAINTED.id());
      world.setBlockMetadataWithNotify(x, y, z, color.blockMeta << 4 | world.getBlockMetadata(x, y, z) & 15);
   }
}
