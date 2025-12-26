package net.minecraft.core.entity.monster;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobScorpion extends MobMonster {
   public static final int DATA_UNKNOWN = 16;

   public MobScorpion(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "scorpion");
      this.setSize(1.0F, 0.2F);
      this.moveSpeed = 0.8F;
      this.scoreValue = 200;
      this.mobDrops.add(new WeightedRandomLootObject(Items.STRING.getDefaultStack(), 0, 2));
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(16, (byte)1, Byte.class);
   }

   @Override
   public void spawnInit() {
      super.defineSynchedData();
      if (this.world.getDifficulty().canHostileMobsSpawn() && this.random.nextInt(100 / this.world.getDifficulty().id()) == 0) {
         MobSkeleton entityskeleton = new MobSkeleton(this.world);
         entityskeleton.moveTo(this.x, this.y, this.z, this.yRot, 0.0F);
         this.world.entityJoinedWorld(entityskeleton);
         entityskeleton.startRiding(this);
      }
   }

   @Override
   public double getRideHeight() {
      return this.bbHeight * 0.75 - 0.5;
   }

   @Override
   protected boolean makeStepSound() {
      return false;
   }

   @Override
   protected Entity findPlayerToAttack() {
      float f = this.getBrightness(1.0F);
      if (f < 0.5F) {
         double d = 16.0;
         Player p = this.world.getClosestPlayerToEntity(this, d);
         return p != null && !p.gamemode.areMobsHostile() ? null : p;
      } else {
         return null;
      }
   }

   @Override
   public String getLivingSound() {
      return "mob.spider";
   }

   @Override
   protected String getHurtSound() {
      return "mob.spider";
   }

   @Override
   protected String getDeathSound() {
      return "mob.spiderdeath";
   }

   @Override
   protected void attackEntity(@NotNull Entity entity, float distance) {
      float f1 = this.getBrightness(1.0F);
      if (f1 > 0.5F && this.random.nextInt(100) == 0) {
         this.target = null;
      } else {
         if (!(distance > 2.0F) || !(distance < 6.0F) || this.random.nextInt(10) != 0) {
            super.attackEntity(entity, distance);
         } else if (this.onGround) {
            double d = entity.x - this.x;
            double d1 = entity.z - this.z;
            float f2 = MathHelper.sqrt(d * d + d1 * d1);
            this.xd = d / f2 * 0.5 * 0.8F + this.xd * 0.2;
            this.zd = d1 / f2 * 0.5 * 0.8F + this.zd * 0.2;
            this.yd = 0.4F;
         }
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
}
