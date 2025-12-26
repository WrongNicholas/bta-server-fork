package net.minecraft.core.entity.monster;

import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.MobFlying;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.ProjectileFireball;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobGhast extends MobFlying implements Enemy {
   public static final int DATA_CHARGING = 16;
   public int courseChangeCooldown = 0;
   public double waypointX;
   public double waypointY;
   public double waypointZ;
   private Entity targetedEntity = null;
   private int aggroCooldown = 0;
   public int attackChargeO = 0;
   public int attackCharge = 0;

   public MobGhast(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "ghast");
      this.setSize(4.0F, 7.0F);
      this.fireImmune = true;
      this.scoreValue = 1000;
      this.bb.grow(2.0, 3.0, 2.0);
      this.mobDrops.add(new WeightedRandomLootObject(Items.SULPHUR.getDefaultStack(), 0, 2));
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(16, (byte)0, Byte.class);
   }

   @Override
   public String getEntityTexture() {
      return this.entityData.getByte(16) != 1
         ? super.getEntityTexture()
         : "/assets/minecraft/textures/entity/ghast_fire/" + this.getTextureReference() + ".png";
   }

   @NotNull
   @Override
   public String getDefaultEntityTexture() {
      return this.entityData.getByte(16) != 1 ? super.getEntityTexture() : "/assets/minecraft/textures/entity/ghast_fire/0.png";
   }

   @Override
   public void tick() {
      if (this.world.isClientSide) {
         byte i = this.entityData.getByte(16);
         if (i > 0 && this.attackCharge == 0) {
            this.world
               .playSoundAtEntity(null, this, "mob.ghast.charge", this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         }

         this.attackCharge += i;
         if (this.attackCharge < 0) {
            this.attackCharge = 0;
         }

         if (this.attackCharge >= 20) {
            this.attackCharge = 20;
         }

         if (this.attackCharge >= 20 && i == 0) {
            this.world
               .playSoundAtEntity(null, this, "mob.ghast.fireball", this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.attackCharge = -40;
         }
      }

      super.tick();
   }

   @Override
   protected void updateAI() {
      if (!this.world.isClientSide && !this.world.getDifficulty().canHostileMobsSpawn()) {
         this.remove();
      }

      this.tryToDespawn();
      this.attackChargeO = this.attackCharge;
      double d = this.waypointX - this.x;
      double d1 = this.waypointY - this.y;
      double d2 = this.waypointZ - this.z;
      double d3 = MathHelper.sqrt(d * d + d1 * d1 + d2 * d2);
      if (d3 < 1.0 || d3 > 60.0) {
         this.waypointX = this.x + (this.random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         this.waypointY = this.y + (this.random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         this.waypointZ = this.z + (this.random.nextFloat() * 2.0F - 1.0F) * 16.0F;
      }

      if (this.courseChangeCooldown-- <= 0) {
         this.courseChangeCooldown = this.courseChangeCooldown + this.random.nextInt(5) + 2;
         if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, d3)) {
            this.xd += d / d3 * 0.1;
            this.yd += d1 / d3 * 0.1;
            this.zd += d2 / d3 * 0.1;
         } else {
            this.waypointX = this.x;
            this.waypointY = this.y;
            this.waypointZ = this.z;
         }
      }

      if (this.targetedEntity != null && this.targetedEntity.removed) {
         this.targetedEntity = null;
      }

      if (this.targetedEntity == null || this.aggroCooldown-- <= 0) {
         this.targetedEntity = this.world.getClosestPlayerToEntity(this, 100.0);
         if (this.targetedEntity != null && !((Player)this.targetedEntity).getGamemode().areMobsHostile()) {
            this.targetedEntity = null;
         }

         if (this.targetedEntity != null) {
            this.aggroCooldown = 20;
         }
      }

      double d4 = 64.0;
      if (this.targetedEntity != null && this.targetedEntity.distanceToSqr(this) < d4 * d4) {
         double d8 = 4.0;
         Vec3 vec3 = this.getViewVector(1.0F);
         double dX = this.targetedEntity.x - this.x;
         double dY = this.targetedEntity.y - this.y;
         double dZ = this.targetedEntity.z - this.z;
         double dist = MathHelper.sqrt(dX * dX + dY * dY + dZ * dZ);
         double vX = dX + this.targetedEntity.xd * dist / 7.5 - vec3.x * d8;
         double vY = dY + this.targetedEntity.yd * dist / 7.5 - (this.bbHeight / 2.0F + 0.5);
         double vZ = dZ + this.targetedEntity.zd * dist / 7.5 - vec3.z * d8;
         this.yBodyRot = this.yRot = -((float)Math.atan2(vX, vZ)) * 180.0F / (float) Math.PI;
         if (this.canEntityBeSeen(this.targetedEntity)) {
            if (this.attackCharge == 10) {
               this.world
                  .playSoundAtEntity(null, this, "mob.ghast.charge", this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

            this.attackCharge++;
            if (this.attackCharge == 20) {
               this.world
                  .playSoundAtEntity(null, this, "mob.ghast.fireball", this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
               ProjectileFireball fireball = new ProjectileFireball(this.world, this, vX, vY, vZ);
               fireball.x = this.x + vec3.x * d8;
               fireball.y = this.y + this.bbHeight / 2.0F + 0.5;
               fireball.z = this.z + vec3.z * d8;
               this.world.entityJoinedWorld(fireball);
               this.attackCharge = -40;
            }
         } else if (this.attackCharge > 0) {
            this.attackCharge--;
         } else {
            this.targetedEntity = null;
         }
      } else {
         this.yBodyRot = this.yRot = -((float)Math.atan2(this.xd, this.zd)) * 180.0F / (float) Math.PI;
         if (this.attackCharge > 0) {
            this.attackCharge--;
         }
      }

      if (!this.world.isClientSide) {
         byte chargeData = this.entityData.getByte(16);
         byte chargeState = (byte)(this.attackCharge <= 10 ? 0 : 1);
         if (chargeData != chargeState) {
            this.entityData.set(16, chargeState);
         }
      }
   }

   private boolean isCourseTraversable(double d, double d1, double d2, double d3) {
      double d4 = (this.waypointX - this.x) / d3;
      double d5 = (this.waypointY - this.y) / d3;
      double d6 = (this.waypointZ - this.z) / d3;
      AABB axisalignedbb = this.bb.copy();

      for (int i = 1; i < d3; i++) {
         axisalignedbb.move(d4, d5, d6);
         if (this.world.getCubes(this, axisalignedbb).size() > 0) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean hurt(Entity attacker, int i, DamageType type) {
      if (super.hurt(attacker, i, type)) {
         if (this.passenger != attacker && this.vehicle != attacker) {
            if (attacker != this) {
               this.targetedEntity = attacker;
               this.aggroCooldown = 60;
            }

            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public String getLivingSound() {
      return "mob.ghast.moan";
   }

   @Override
   protected String getHurtSound() {
      return "mob.ghast.scream";
   }

   @Override
   protected String getDeathSound() {
      return "mob.ghast.death";
   }

   @Override
   protected float getSoundVolume() {
      return 10.0F;
   }

   @Override
   public boolean canSpawnHere() {
      return this.world.getDifficulty().canHostileMobsSpawn()
         && this.random.nextInt(20) == 0
         && BlockTags.NETHER_MOBS_SPAWN
            .appliesTo(this.world.getBlock(MathHelper.floor(this.x), MathHelper.floor(this.y - this.heightOffset) - 1, MathHelper.floor(this.z)))
         && super.canSpawnHere();
   }

   @Override
   public int getMaxSpawnedInChunk() {
      return 1;
   }

   @Override
   public float getBrightness(float partialTick) {
      return 1.0F;
   }

   @Override
   public int getLightmapCoord(float partialTick) {
      return this.world.getLightmapCoord(15, 15);
   }
}
