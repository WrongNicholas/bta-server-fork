package net.minecraft.core.entity.projectile;

import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;

public class ProjectileCannonballBouncy extends ProjectileCannonball {
   private int fuse = 50;

   public ProjectileCannonballBouncy(World world, Mob owner) {
      super(world, owner);
      this.x = this.x + this.xd;
      this.y = this.y + this.yd;
      this.z = this.z + this.zd;
      this.setHeading(this.xd, this.yd, this.zd, 0.75F, 0.0F);
      this.defaultGravity = 0.06F;
   }

   public ProjectileCannonballBouncy(World world, double x, double y, double z) {
      super(world, x, y, z);
      this.defaultGravity = 0.06F;
   }

   @Override
   public void tick() {
      this.baseTick();
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float f = MathHelper.sqrt(this.xd * this.xd + this.zd * this.zd);
         this.yRotO = this.yRot = (float)(Math.atan2(this.xd, this.zd) * 180.0 / Math.PI);
         this.xRotO = this.xRot = (float)(Math.atan2(this.yd, f) * 180.0 / Math.PI);
      }

      float velocity = MathHelper.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
      Vec3 currentPos = Vec3.getTempVec3(this.x, this.y, this.z);
      Vec3 nextPos = Vec3.getTempVec3(this.x + this.xd, this.y + this.yd - 0.25, this.z + this.zd);
      HitResult hit = this.world.checkBlockCollisionBetweenPoints(currentPos, nextPos, false, true, false);
      if (hit != null && hit.hitType == HitResult.HitType.TILE) {
         float bounceAxisScale = 0.4F;
         float otherAxisScale = 0.6F;
         float minVelocity = 0.2F;
         Side side = hit.side;
         if (side == Side.TOP && (Math.abs(this.yd) < 0.25 || velocity < minVelocity)) {
            this.yd = 0.0;
            this.y = hit.y + 1.0F + 0.25F;
            this.xd = 0.0;
            this.zd = 0.0;
            return;
         }

         if (side.getAxis() == Axis.Y) {
            this.yd = -this.yd * bounceAxisScale;
            this.xd *= otherAxisScale;
            this.zd *= otherAxisScale;
            this.world.playSoundAtEntity(null, this, "random.explode", 0.25F * velocity, 2.0F);
         }

         if (side.getAxis() == Axis.X) {
            this.xd = -this.xd * bounceAxisScale;
            this.yd *= otherAxisScale;
            this.zd *= otherAxisScale;
            this.world.playSoundAtEntity(null, this, "random.explode", 0.25F * velocity, 2.0F);
         }

         if (side.getAxis() == Axis.Z) {
            this.zd = -this.zd * bounceAxisScale;
            this.xd *= otherAxisScale;
            this.yd *= otherAxisScale;
            this.world.playSoundAtEntity(null, this, "random.explode", 0.25F * velocity, 2.0F);
         }
      }

      List<Entity> collidingEntities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.bb.cloneMove(this.xd, this.yd, this.zd));
      if ((collidingEntities == null || collidingEntities.size() <= 0) && this.fuse > 0
         || collidingEntities != null && collidingEntities.size() == 1 && collidingEntities.get(0) == this.owner && this.tickCount < 4) {
         this.x = this.x + this.xd;
         this.y = this.y + this.yd;
         this.z = this.z + this.zd;
         float velocityHorizontal = MathHelper.sqrt(this.xd * this.xd + this.zd * this.zd);
         this.yRot = (float)(Math.atan2(this.xd, this.zd) * 180.0 / Math.PI);
         this.xRot = (float)(Math.atan2(this.yd, velocityHorizontal) * 180.0 / Math.PI);

         while (this.xRot - this.xRotO < -180.0F) {
            this.xRotO -= 360.0F;
         }

         while (this.xRot - this.xRotO >= 180.0F) {
            this.xRotO += 360.0F;
         }

         while (this.yRot - this.yRotO < -180.0F) {
            this.yRotO -= 360.0F;
         }

         while (this.yRot - this.yRotO >= 180.0F) {
            this.yRotO += 360.0F;
         }

         this.xRot = this.xRotO + (this.xRot - this.xRotO) * 0.2F;
         this.yRot = this.yRotO + (this.yRot - this.yRotO) * 0.2F;
         float deceleration = 0.99F;
         float gravity = 0.06F;
         if (this.isInWater()) {
            for (int i = 0; i < 4; i++) {
               double particleDistance = 0.25;
               this.world
                  .spawnParticle(
                     "bubble",
                     this.x - this.xd * particleDistance,
                     this.y - this.yd * particleDistance,
                     this.z - this.zd * particleDistance,
                     this.xd,
                     this.yd,
                     this.zd,
                     0
                  );
            }

            deceleration = 0.8F;
         }

         this.world.spawnParticle("largesmoke", this.x, this.y, this.z, 0.0, 0.0, 0.0, 0);
         this.xd *= deceleration;
         this.yd *= deceleration;
         this.zd *= deceleration;
         this.yd -= gravity;
         this.setPos(this.x, this.y, this.z);
         this.fuse--;
      } else {
         this.world.createExplosion(this.owner, this.x, this.y + this.bbHeight / 2.0F, this.z, 1.5F, false, true);
         this.remove();
      }
   }
}
