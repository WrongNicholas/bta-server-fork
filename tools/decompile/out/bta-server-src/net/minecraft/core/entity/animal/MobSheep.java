package net.minecraft.core.entity.animal;

import com.mojang.nbt.tags.CompoundTag;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemToolShears;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobSheep extends MobAnimal {
   public static final int DATA_WOOL_STATE_ID = 16;
   public static final int MASK_WOOL_COLOR = 15;
   public static final int MASK_SHEARED = 16;
   public static final int DATA_EATING_ID = 17;
   public static final float[][] FLEECE_COLOR_TABLE = new float[][]{
      {1.0F, 1.0F, 1.0F},
      {0.95F, 0.7F, 0.2F},
      {0.9F, 0.5F, 0.85F},
      {0.6F, 0.7F, 0.95F},
      {0.9F, 0.9F, 0.2F},
      {0.5F, 0.8F, 0.1F},
      {0.95F, 0.7F, 0.8F},
      {0.3F, 0.3F, 0.3F},
      {0.6F, 0.6F, 0.6F},
      {0.3F, 0.6F, 0.7F},
      {0.7F, 0.4F, 0.9F},
      {0.2F, 0.4F, 0.8F},
      {0.5F, 0.4F, 0.3F},
      {0.4F, 0.5F, 0.2F},
      {0.8F, 0.3F, 0.3F},
      {0.1F, 0.1F, 0.1F}
   };
   private int growthTimer;
   public int timeSheepEating;
   public int prevTimeSheepEating;

   public MobSheep(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "sheep");
      this.setSize(0.9F, 1.3F);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(16, (byte)0, Byte.class);
      this.entityData.define(17, (byte)0, Byte.class);
   }

   @Override
   public void spawnInit() {
      this.setFleeceColor(getRandomFleeceColor(this.random));
   }

   @Override
   protected void dropDeathItems() {
      super.dropDeathItems();
      if (!this.getSheared()) {
         this.dropItem(new ItemStack(Blocks.WOOL.id(), 1, this.getFleeceColor().blockMeta), 0.0F);
      }
   }

   @Override
   public boolean interact(@NotNull Player player) {
      if (super.interact(player)) {
         return true;
      } else {
         ItemStack itemstack = player.inventory.getCurrentItem();
         if (itemstack != null && itemstack.getItem() instanceof ItemToolShears && !this.getSheared()) {
            if (!this.world.isClientSide) {
               this.setSheared(true);
               int count = 2 + this.random.nextInt(3);

               for (int j = 0; j < count; j++) {
                  EntityItem entityitem = this.dropItem(new ItemStack(Blocks.WOOL.id(), 1, this.getFleeceColor().blockMeta), 1.0F);
                  entityitem.yd = entityitem.yd + this.random.nextFloat() * 0.05F;
                  entityitem.xd = entityitem.xd + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F;
                  entityitem.zd = entityitem.zd + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F;
               }
            }

            itemstack.damageItem(1, player);
            if (itemstack.stackSize <= 0) {
               player.destroyCurrentEquippedItem();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public void onItemInteract(ItemStack itemStack) {
      if (itemStack.getItem() instanceof ItemToolShears && !this.getSheared()) {
         if (!this.world.isClientSide) {
            this.setSheared(true);
            int count = 2 + this.random.nextInt(3);

            for (int j = 0; j < count; j++) {
               EntityItem entityItem = this.dropItem(new ItemStack(Blocks.WOOL.id(), 1, this.getFleeceColor().blockMeta), 1.0F);
               entityItem.yd = entityItem.yd + this.random.nextFloat() * 0.05F;
               entityItem.xd = entityItem.xd + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F;
               entityItem.zd = entityItem.zd + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F;
            }
         }

         itemStack.damageItem(1, null);
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("Sheared", this.getSheared());
      tag.putByte("Color", (byte)this.getFleeceColor().blockMeta);
      tag.putShort("GrowthTimer", (short)this.growthTimer);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.setSheared(tag.getBoolean("Sheared"));
      this.setFleeceColor(DyeColor.colorFromBlockMeta(tag.getByte("Color")));
      this.setGrowthTimer(tag.getShort("GrowthTimer"));
   }

   @Override
   protected boolean isMovementBlocked() {
      return super.isMovementBlocked() || this.getIsSheepEating();
   }

   public boolean getIsSheepEating() {
      return this.entityData.getByte(17) != 0;
   }

   protected void setIsSheepEating(boolean value) {
      this.entityData.set(17, (byte)(value ? 1 : 0));
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getSheared()) {
         this.growthTimer++;
      }

      if (this.growthTimer > 400) {
         int blockX = MathHelper.floor(this.x);
         int blockY = MathHelper.floor(this.y);
         int blockZ = MathHelper.floor(this.z);
         Block<?> blockBelow = this.world.getBlock(blockX, blockY - 1, blockZ);
         this.growthTimer = 0;
         if ((blockBelow == Blocks.GRASS || blockBelow == Blocks.GRASS_RETRO) && !this.world.isClientSide) {
            this.setIsSheepEating(true);
         }

         this.timeSheepEating = 0;
         this.prevTimeSheepEating = 0;
      }

      if (this.getIsSheepEating()) {
         int blockX = MathHelper.floor(this.x);
         int blockY = MathHelper.floor(this.y);
         int blockZ = MathHelper.floor(this.z);
         Block<?> blockBelow = this.world.getBlock(blockX, blockY - 1, blockZ);
         if (this.timeSheepEating >= 5 && this.timeSheepEating <= 35 && this.timeSheepEating % 5 == 0 && !this.world.isClientSide) {
            this.world.playBlockSoundEffect(null, this.x + 0.5, this.y + 0.5, this.z + 0.5, Blocks.GRASS, EnumBlockSoundEffectType.DIG);
         }

         this.prevTimeSheepEating = this.timeSheepEating++;
         if (this.prevTimeSheepEating == 35 && (blockBelow == Blocks.GRASS || blockBelow == Blocks.GRASS_RETRO) && !this.world.isClientSide) {
            this.world.playBlockEvent(null, 2001, (int)this.x, (int)this.y - 1, (int)this.z, this.world.getBlockId((int)this.x, (int)this.y - 1, (int)this.z));
            this.world.setBlockWithNotify(blockX, blockY - 1, blockZ, Blocks.DIRT.id());
            this.setSheared(false);
         }

         if (this.prevTimeSheepEating >= 40) {
            this.prevTimeSheepEating = 0;
            this.timeSheepEating = 0;
            if (!this.world.isClientSide) {
               this.setIsSheepEating(false);
            }
         }
      }
   }

   @Override
   public String getLivingSound() {
      return "mob.sheep";
   }

   @Override
   protected String getHurtSound() {
      return "mob.sheep";
   }

   @Override
   protected String getDeathSound() {
      return "mob.sheep";
   }

   public DyeColor getFleeceColor() {
      return DyeColor.colorFromBlockMeta(this.entityData.getByte(16) & 15);
   }

   public void setFleeceColor(DyeColor color) {
      byte woolState = this.entityData.getByte(16);
      this.entityData.set(16, (byte)(woolState & -16 | color.blockMeta & 15));
   }

   public boolean getSheared() {
      return (this.entityData.getByte(16) & 16) != 0;
   }

   public void setSheared(boolean flag) {
      byte woolState = this.entityData.getByte(16);
      if (flag) {
         this.entityData.set(16, (byte)(woolState | 16));
      } else {
         this.entityData.set(16, (byte)(woolState & -17));
      }
   }

   public static DyeColor getRandomFleeceColor(Random random) {
      int i = random.nextInt(100);
      if (i < 5) {
         return DyeColor.BLACK;
      } else if (i < 10) {
         return DyeColor.GRAY;
      } else if (i < 15) {
         return DyeColor.SILVER;
      } else if (i < 18) {
         return DyeColor.BROWN;
      } else {
         return random.nextInt(500) != 0 ? DyeColor.WHITE : DyeColor.PINK;
      }
   }

   @Override
   public boolean isFavouriteItem(ItemStack itemStack) {
      return itemStack != null && itemStack.itemID < Blocks.blocksList.length
         ? Blocks.blocksList[itemStack.itemID].hasTag(BlockTags.SHEEPS_FAVOURITE_BLOCK)
         : false;
   }

   public void setGrowthTimer(int growthTimer) {
      this.growthTimer = growthTimer;
   }
}
