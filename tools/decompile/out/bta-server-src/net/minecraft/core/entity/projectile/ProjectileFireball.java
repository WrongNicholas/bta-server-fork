package net.minecraft.core.entity.projectile;

import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.monster.MobGhast;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;

public class ProjectileFireball extends Projectile {
   public static final double FIREBALL_SPEED = 1.0;

   public ProjectileFireball(World world) {
      super(world);
      this.setSize(1.0F, 1.0F);
   }

   public ProjectileFireball(World world, double x, double y, double z, double vX, double vY, double vZ) {
      super(world);
      this.setSize(1.0F, 1.0F);
      this.moveTo(x, y, z, this.yRot, this.xRot);
      this.setPos(x, y, z);
      this.setVelocity(vX, vY, vZ, 1.0);
   }

   public ProjectileFireball(World world, Mob owner, double vX, double vY, double vZ) {
      super(world);
      this.setSize(1.0F, 1.0F);
      this.moveTo(owner.x, owner.y, owner.z, owner.yRot, owner.xRot);
      this.setPos(this.x, this.y, this.z);
      this.owner = owner;
      this.heightOffset = 0.0F;
      vX += (this.random.nextGaussian() - this.random.nextGaussian()) * 0.8;
      vY += this.random.nextGaussian() * 0.4;
      vZ += (this.random.nextGaussian() - this.random.nextGaussian()) * 0.8;
      this.setVelocity(vX, vY, vZ, 1.0);
   }

   private void setVelocity(double vX, double vY, double vZ, double speed) {
      double velocity = MathHelper.sqrt(vX * vX + vY * vY + vZ * vZ);
      if (velocity != 0.0) {
         this.xd = vX / velocity * speed;
         this.yd = vY / velocity * speed;
         this.zd = vZ / velocity * speed;
      } else {
         this.xd = 0.0;
         this.yd = 0.0;
         this.zd = 0.0;
      }
   }

   @Override
   protected void initProjectile() {
      this.damage = 0;
      this.defaultGravity = 0.0F;
      this.defaultProjectileSpeed = 1.0F;
   }

   @Override
   public void tick() {
      this.remainingFireTicks = 10;
      this.world.spawnParticle("flame", this.x, this.y, this.z, this.xd * 0.05, this.yd * 0.05 - 0.1, this.zd * 0.05, 0);
      this.world
         .spawnParticle(
            "flame", this.x + this.xd * 0.5, this.y + this.yd * 0.5, this.z + this.zd * 0.5, this.xd * 0.05, this.yd * 0.05 - 0.1, this.zd * 0.05, 0
         );
      super.tick();
   }

   @Override
   public void onHit(HitResult result) {
      if (this.tickCount > 5) {
         if (!this.world.isClientSide) {
            if (result.entity != null) {
               if (result.entity instanceof MobGhast) {
                  result.entity.hurt(this.owner, 20, DamageType.COMBAT);
               } else {
                  result.entity.hurt(this.owner, this.damage, DamageType.COMBAT);
               }

               if (result.entity instanceof Mob && this.owner instanceof Player) {
                  ((Player)this.owner).addStat(Achievements.HIT_FIREBALL, 1);
               }
            }

            this.world.createExplosion(this.owner, this.x, this.y + this.bbHeight / 2.0F, this.z, 1.5F, true, false);
         }

         this.remove();
      }
   }

   @Override
   public void afterTick() {
      super.afterTick();
      this.world.spawnParticle("largesmoke", this.x, this.y, this.z, 0.0, 0.0, 0.0, 0);
   }

   @Override
   public boolean isPickable() {
      return true;
   }

   @Override
   public float getPickRadius() {
      return 1.0F;
   }

   @Override
   public boolean hurt(Entity entity, int i, DamageType type) {
      this.markHurt();
      if (entity != null) {
         Vec3 lookAngle = entity.getLookAngle();
         if (entity instanceof Mob) {
            this.owner = (Mob)entity;
         }

         if (lookAngle != null) {
            this.setVelocity(lookAngle.x, lookAngle.y, lookAngle.z, 1.0);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void lerpMotion(double xd, double yd, double zd) {
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
   }
}
