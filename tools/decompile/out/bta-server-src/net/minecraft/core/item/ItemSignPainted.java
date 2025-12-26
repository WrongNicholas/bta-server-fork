package net.minecraft.core.item;

import net.minecraft.core.block.BlockLogicSignPainted;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntitySign;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemSignPainted extends ItemSign {
   public ItemSignPainted(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
   }

   @Override
   public boolean onUseItemOnBlock(
      @NotNull ItemStack itemstack,
      @Nullable Player player,
      @NotNull World world,
      int blockX,
      int blockY,
      int blockZ,
      @NotNull Side side,
      double xPlaced,
      double yPlaced
   ) {
      int sideHit = side.getId();
      if (side == Side.BOTTOM) {
         return false;
      } else if (!world.getBlockMaterial(blockX, blockY, blockZ).isSolid()) {
         return false;
      } else {
         if (!world.canPlaceInsideBlock(blockX, blockY, blockZ)) {
            blockX += side.getOffsetX();
            blockY += side.getOffsetY();
            blockZ += side.getOffsetZ();
         }

         if (blockY < 0 || blockY >= world.getHeightBlocks()) {
            return false;
         } else if (!Blocks.SIGN_POST_PLANKS_OAK_PAINTED.canPlaceBlockAt(world, blockX, blockY, blockZ)) {
            return false;
         } else {
            if (sideHit == 1) {
               int direction = MathHelper.floor((player.yRot + 180.0F) * 16.0F / 360.0F + 0.5) & 15;
               world.playBlockSoundEffect(
                  player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, Blocks.SIGN_POST_PLANKS_OAK_PAINTED, EnumBlockSoundEffectType.PLACE
               );
               world.setBlockAndMetadataWithNotify(
                  blockX,
                  blockY,
                  blockZ,
                  Blocks.SIGN_POST_PLANKS_OAK_PAINTED.id(),
                  ((BlockLogicSignPainted) Blocks.SIGN_POST_PLANKS_OAK_PAINTED.getLogic()).toMetadata(DyeColor.colorFromItemMeta(itemstack.getMetadata())) | direction
               );
            } else {
               world.playBlockSoundEffect(
                  player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, Blocks.SIGN_WALL_PLANKS_OAK_PAINTED, EnumBlockSoundEffectType.PLACE
               );
               world.setBlockAndMetadataWithNotify(
                  blockX,
                  blockY,
                  blockZ,
                  Blocks.SIGN_WALL_PLANKS_OAK_PAINTED.id(),
                  ((BlockLogicSignPainted) Blocks.SIGN_POST_PLANKS_OAK_PAINTED.getLogic()).toMetadata(DyeColor.colorFromItemMeta(itemstack.getMetadata())) | sideHit
               );
            }

            itemstack.consumeItem(player);
            TileEntitySign tileentitysign = (TileEntitySign)world.getTileEntity(blockX, blockY, blockZ);
            if (tileentitysign != null) {
               if (itemstack.getData().containsKey("tileEntityData")) {
                  tileentitysign.readFromNBT(itemstack.getData().getCompound("tileEntityData"));
               } else {
                  tileentitysign.setOwner(player);
                  player.displaySignEditorScreen(tileentitysign);
               }
            }

            return true;
         }
      }
   }

   @Override
   public String getLanguageKey(ItemStack itemstack) {
      return super.getKey() + "." + DyeColor.colorFromItemMeta(itemstack.getMetadata()).colorID;
   }
}
