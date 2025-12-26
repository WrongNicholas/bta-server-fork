package net.minecraft.core.entity.monster;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.Weathers;
import org.jetbrains.annotations.NotNull;

public class MobSkeleton extends MobMonster {
   public static final int DATA_ATTACK_TIME = 15;
   private static final ItemStack defaultHeldItem = new ItemStack(Items.TOOL_BOW, 1);

   public MobSkeleton(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "skeleton");
      this.scoreValue = 500;
      this.mobDrops.add(new WeightedRandomLootObject(Items.AMMO_ARROW.getDefaultStack(), 0, 2));
      this.mobDrops.add(new WeightedRandomLootObject(Items.BONE.getDefaultStack(), 0, 2));
   }

   @Override
   public String getLivingSound() {
      return "mob.skeleton";
   }

   @Override
   protected String getHurtSound() {
      return "mob.skeletonhurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.skeletonhurt";
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(15, this.attackTime, Integer.class);
   }

   @Override
   public void onLivingUpdate() {
      if (this.world.isClientSide) {
         this.attackTime = this.entityData.getInt(15);
      } else {
         this.entityData.set(15, this.attackTime);
      }

      if (this.world.isDaytime()) {
         float f = this.getBrightness(1.0F);
         Weather weather;
         if (f > 0.5F
            && this.world.canBlockSeeTheSky(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z))
            && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F
            && (weather = this.world.getCurrentWeather()) != null
            && !weather.isDamp
            && (weather != Weathers.OVERWORLD_FOG || this.world.weatherManager.getWeatherPower() < 0.75F)) {
            this.remainingFireTicks = 300;
         }
      }

      super.onLivingUpdate();
   }

   @Override
   protected void attackEntity(@NotNull Entity entity, float distance) {
      if (distance < 10.0F) {
         double d = entity.x - this.x;
         double d1 = entity.z - this.z;
         if (this.attackTime == 0) {
            if (!this.world.isClientSide) {
               ProjectileArrow arrow = new ProjectileArrow(this.world, this, false, 0);
               double d2 = entity.y + entity.getHeadHeight() - 0.2 - arrow.y;
               float f1 = MathHelper.sqrt(d * d + d1 * d1) * 0.2F;
               this.world.playSoundAtEntity(null, this, "random.bow", 1.0F, 1.0F / (this.random.nextFloat() * 0.4F + 0.8F));
               arrow.setHeading(d, d2 + f1, d1, 0.6F, 12.0F);
               this.world.entityJoinedWorld(arrow);
            }

            this.attackTime = 30;
         }

         this.yRot = (float)(Math.atan2(d1, d) * 180.0 / Math.PI) - 90.0F;
         this.hasAttacked = true;
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
   }

   @Override
   public ItemStack getHeldItem() {
      return defaultHeldItem;
   }
}
