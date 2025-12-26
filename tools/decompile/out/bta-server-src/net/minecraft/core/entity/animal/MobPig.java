package net.minecraft.core.entity.animal;

import com.mojang.nbt.tags.CompoundTag;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.EntityLightning;
import net.minecraft.core.entity.monster.MobZombiePig;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobPig extends MobAnimal {
   public static final int DATA_SADDLE_ID = 16;
   public List<WeightedRandomLootObject> burningMobDrops = new ArrayList<>();

   public MobPig(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "pig");
      this.setSize(0.9F, 0.9F);
      this.mobDrops.add(new WeightedRandomLootObject(Items.FOOD_PORKCHOP_RAW.getDefaultStack(), 1, 2));
      this.burningMobDrops.add(new WeightedRandomLootObject(Items.FOOD_PORKCHOP_COOKED.getDefaultStack(), 1, 2));
   }

   @Override
   protected void defineSynchedData() {
      this.entityData.define(16, (byte)0, Byte.class);
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("Saddle", this.getSaddled());
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.setSaddled(tag.getBoolean("Saddle"));
   }

   @Override
   public String getLivingSound() {
      return "mob.pig";
   }

   @Override
   protected String getHurtSound() {
      return "mob.pig";
   }

   @Override
   protected String getDeathSound() {
      return "mob.pigdeath";
   }

   @Override
   public boolean interact(@NotNull Player player) {
      if (super.interact(player)) {
         return true;
      } else if (!this.getSaddled() || this.world.isClientSide || this.passenger != null && this.passenger != player) {
         return false;
      } else {
         player.startRiding(this);
         return true;
      }
   }

   @Override
   protected void dropDeathItems() {
      if (this.getSaddled()) {
         this.dropItem(Items.SADDLE.id, 1);
      }

      super.dropDeathItems();
   }

   @Override
   protected List<WeightedRandomLootObject> getMobDrops() {
      return this.remainingFireTicks > 0 ? this.burningMobDrops : this.mobDrops;
   }

   public boolean getSaddled() {
      return (this.entityData.getByte(16) & 1) != 0;
   }

   public void setSaddled(boolean flag) {
      if (flag) {
         this.entityData.set(16, (byte)1);
      } else {
         this.entityData.set(16, (byte)0);
      }
   }

   @Override
   public void thunderHit(EntityLightning bolt) {
      if (!this.world.isClientSide) {
         MobZombiePig entitypigzombie = new MobZombiePig(this.world);
         entitypigzombie.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
         this.world.entityJoinedWorld(entitypigzombie);
         this.remove();
      }
   }

   @Override
   protected void causeFallDamage(float distance) {
      super.causeFallDamage(distance);
      if (distance > 5.0F && this.passenger instanceof Player) {
         ((Player)this.passenger).triggerAchievement(Achievements.FLY_PIG);
      }
   }

   @Override
   public boolean isFavouriteItem(ItemStack itemStack) {
      return itemStack != null && itemStack.itemID < Blocks.blocksList.length
         ? Blocks.blocksList[itemStack.itemID].hasTag(BlockTags.PIGS_FAVOURITE_BLOCK)
         : false;
   }
}
