package net.minecraft.core.item;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.helper.UUIDHelper;
import net.minecraft.core.world.World;

public class ItemFlag extends Item {
   public ItemFlag(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.setMaxStackSize(1);
   }

   @Override
   public String getLanguageKey(ItemStack itemstack) {
      return !this.hasFlagBeenDrawnOn(itemstack) && !this.doesFlagContainDyes(itemstack) ? super.getLanguageKey(itemstack) : this.getKey() + ".modified";
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack stack, Player entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (world.getBlockId(blockX, blockY, blockZ) == Blocks.FLAG.id() && !entityplayer.isSneaking()) {
         TileEntityFlag flag = (TileEntityFlag)world.getTileEntity(blockX, blockY, blockZ);
         if (flag != null) {
            CompoundTag flagData = stack.getData().getCompoundOrDefault("FlagData", new CompoundTag());
            if (this.hasFlagBeenDrawnOn(stack) && this.doesFlagContainDyes(stack)) {
               entityplayer.sendStatusMessage(I18n.getInstance().translateKey("flag.overwrite"));
               return false;
            }

            flag.copyFlagNBT(flagData);
            UUIDHelper.writeToTag(flagData, entityplayer.uuid, "OwnerUUID");
            stack.getData().putCompound("FlagData", flagData);
            entityplayer.sendStatusMessage(I18n.getInstance().translateKey("flag.copied"));
            return true;
         }
      }

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
            world.playBlockSoundEffect(entityplayer, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, Blocks.FLAG, EnumBlockSoundEffectType.PLACE);
            world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, Blocks.FLAG.id(), 0);
            stack.consumeItem(entityplayer);
            TileEntityFlag tileEntityFlag = (TileEntityFlag)world.getTileEntity(blockX, blockY, blockZ);
            if (tileEntityFlag != null) {
               CompoundTag flagData = stack.getData().getCompoundOrDefault("FlagData", null);
               if (flagData != null) {
                  tileEntityFlag.readFlagNBT(flagData);
               }

               if (!world.isClientSide && tileEntityFlag.owner == null) {
                  entityplayer.displayFlagEditorScreen(tileEntityFlag);
               }
            }

            return true;
         }
      }
   }

   public boolean hasFlagBeenDrawnOn(ItemStack stack) {
      CompoundTag flagData = stack.getData().getCompound("FlagData");
      if (flagData == null) {
         return false;
      } else {
         byte[] colors = flagData.getByteArray("Colors");
         if (colors == null) {
            return false;
         } else {
            for (int i = 0; i < colors.length; i++) {
               if (colors[i] != 0) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean doesFlagContainDyes(ItemStack stack) {
      CompoundTag flagData = stack.getData().getCompound("FlagData");
      if (flagData == null) {
         return false;
      } else {
         ListTag items = flagData.getList("Items");
         return items != null && items.tagCount() > 0;
      }
   }
}
