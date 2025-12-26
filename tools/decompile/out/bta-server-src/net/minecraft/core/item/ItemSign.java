package net.minecraft.core.item;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntitySign;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class ItemSign extends Item {
   public ItemSign(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, Player entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
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
         } else if (!Blocks.SIGN_POST_PLANKS_OAK.canPlaceBlockAt(world, blockX, blockY, blockZ)) {
            return false;
         } else {
            if (sideHit == 1) {
               world.playBlockSoundEffect(
                  entityplayer, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, Blocks.SIGN_POST_PLANKS_OAK, EnumBlockSoundEffectType.PLACE
               );
               world.setBlockAndMetadataWithNotify(
                  blockX, blockY, blockZ, Blocks.SIGN_POST_PLANKS_OAK.id(), MathHelper.floor((entityplayer.yRot + 180.0F) * 16.0F / 360.0F + 0.5) & 15
               );
            } else {
               world.playBlockSoundEffect(
                  entityplayer, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, Blocks.SIGN_WALL_PLANKS_OAK, EnumBlockSoundEffectType.PLACE
               );
               world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, Blocks.SIGN_WALL_PLANKS_OAK.id(), sideHit);
            }

            itemstack.consumeItem(entityplayer);
            TileEntitySign tileentitysign = (TileEntitySign)world.getTileEntity(blockX, blockY, blockZ);
            if (tileentitysign != null) {
               tileentitysign.setOwner(entityplayer);
               entityplayer.displaySignEditorScreen(tileentitysign);
            }

            return true;
         }
      }
   }
}
