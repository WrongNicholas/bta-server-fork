package net.minecraft.core.item;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.EntityPainting;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class ItemPainting extends Item {
   public ItemPainting(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      if (!world.isClientSide && !entityplayer.isSneaking()) {
         entityplayer.displayPaintingPickerScreen();
      }

      return itemstack;
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, Player entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      int sideHit = side.getId();
      if (sideHit == 0) {
         return false;
      } else if (sideHit == 1) {
         return false;
      } else {
         int direction = 0;
         if (sideHit == 4) {
            direction = 1;
         }

         if (sideHit == 3) {
            direction = 2;
         }

         if (sideHit == 5) {
            direction = 3;
         }

         EntityPainting entity = new EntityPainting(world, blockX, blockY, blockZ, direction, entityplayer.getSelectedArt().key);
         if (entity.canStay()) {
            if (!world.isClientSide) {
               world.entityJoinedWorld(entity);
            }

            world.playBlockSoundEffect(entityplayer, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, Blocks.PLANKS_OAK, EnumBlockSoundEffectType.PLACE);
            itemstack.consumeItem(entityplayer);
            return true;
         } else {
            return false;
         }
      }
   }
}
