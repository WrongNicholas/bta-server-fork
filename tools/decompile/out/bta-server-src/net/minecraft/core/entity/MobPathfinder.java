package net.minecraft.core.entity;

import java.util.List;
import net.minecraft.core.entity.animal.MobFireflyCluster;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pathfinder.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MobPathfinder extends Mob {
   private static final int MAX_TURN = 30;
   @Nullable
   protected Path pathToEntity;
   @Nullable
   protected Entity target;
   @Nullable
   protected Entity closestFireflyEntity;
   protected boolean hasAttacked = false;

   public MobPathfinder(@Nullable World world) {
      super(world);
   }

   protected boolean isMovementCeased() {
      return false;
   }

   @Override
   protected void updateAI() {
      if (this.world != null) {
         this.hasAttacked = this.isMovementCeased();
         float sightRadius = 16.0F;
         if (this.target == null) {
            this.target = this.findPlayerToAttack();
            if (this.target != null) {
               this.pathToEntity = this.world.getPathToEntity(this, this.target, sightRadius);
            }
         } else if (!this.target.isAlive()) {
            this.target = null;
         } else {
            float distanceToEntity = this.target.distanceTo(this);
            if (this.canEntityBeSeen(this.target)) {
               this.attackEntity(this.target, distanceToEntity);
            } else {
               this.attackBlockedEntity(this.target, distanceToEntity);
            }
         }

         if (this.hasAttacked || this.target == null || this.pathToEntity != null && this.random.nextInt(20) != 0) {
            if (!this.hasAttacked
               && this.closestFireflyEntity == null
               && (this.pathToEntity == null && this.random.nextInt(80) == 0 || this.random.nextInt(80) == 0)) {
               this.roamRandomPath();
            }
         } else {
            this.pathToEntity = this.world.getPathToEntity(this, this.target, sightRadius);
         }

         int i = MathHelper.floor(this.bb.minY + 0.5);
         boolean inWater = this.isInWater();
         boolean inLava = this.isInLava();
         this.xRot = 0.0F;
         if (this.pathToEntity != null && this.random.nextInt(100) != 0) {
            Vec3 coordsForNextPath = this.pathToEntity.getPos(this);
            double d = this.bbWidth * 2.0F;

            while (coordsForNextPath != null && coordsForNextPath.distanceToSquared(this.x, coordsForNextPath.y, this.z) < d * d) {
               this.pathToEntity.next();
               if (this.pathToEntity.isDone()) {
                  this.closestFireflyEntity = null;
                  coordsForNextPath = null;
                  this.pathToEntity = null;
               } else {
                  coordsForNextPath = this.pathToEntity.getPos(this);
               }
            }

            this.isJumping = false;
            if (coordsForNextPath != null) {
               d = coordsForNextPath.x - this.x;
               double z1 = coordsForNextPath.z - this.z;
               double y1 = coordsForNextPath.y - i;
               float f2 = (float)(Math.atan2(z1, d) * 180.0 / Math.PI) - 90.0F;
               float f3 = f2 - this.yRot;
               this.moveForward = this.moveSpeed;

               while (f3 < -180.0F) {
                  f3 += 360.0F;
               }

               while (f3 >= 180.0F) {
                  f3 -= 360.0F;
               }

               if (f3 > 30.0F) {
                  f3 = 30.0F;
               }

               if (f3 < -30.0F) {
                  f3 = -30.0F;
               }

               this.yRot += f3;
               if (this.hasAttacked && this.target != null) {
                  double d4 = this.target.x - this.x;
                  double d5 = this.target.z - this.z;
                  float f5 = this.yRot;
                  this.yRot = (float)(Math.atan2(d5, d4) * 180.0 / Math.PI) - 90.0F;
                  float f4 = (f5 - this.yRot + 90.0F) * (float) Math.PI / 180.0F;
                  this.moveStrafing = -MathHelper.sin(f4) * this.moveForward * 1.0F;
                  this.moveForward = MathHelper.cos(f4) * this.moveForward * 1.0F;
               }

               if (y1 > 0.0) {
                  this.isJumping = true;
               }
            }

            if (this.target != null) {
               this.lookAt(this.target, 30.0F, 30.0F);
            }

            if (this.horizontalCollision && !this.hasPath()) {
               this.isJumping = true;
            }

            if (this.random.nextFloat() < 0.8F && (inWater || inLava)) {
               this.isJumping = true;
            }
         } else {
            super.updateAI();
            this.pathToEntity = null;
         }
      }
   }

   @Nullable
   protected Entity getClosestFireflyToEntity(int x, int y, int z, float radius) {
      if (this.world == null) {
         return null;
      } else {
         List<MobFireflyCluster> nearbyFireflies = this.world
            .getEntitiesWithinAABB(
               MobFireflyCluster.class, AABB.getTemporaryBB(this.x, this.y, this.z, this.x + 1.0, this.y + 1.0, this.z + 1.0).grow(radius, 4.0, radius)
            );
         double closestDistance = -1.0;
         MobFireflyCluster cluster = null;

         for (int i = 0; i < nearbyFireflies.size(); i++) {
            MobFireflyCluster clusterInList = nearbyFireflies.get(i);
            double currentDistance = clusterInList.distanceToSqr(x, y, z);
            if ((radius < 0.0 || currentDistance < radius * radius) && (closestDistance == -1.0 || currentDistance < closestDistance)) {
               closestDistance = currentDistance;
               cluster = clusterInList;
            }
         }

         return cluster;
      }
   }

   protected void roamRandomPath() {
      if (this.world != null) {
         boolean canMoveToPoint = false;
         int x = -1;
         int y = -1;
         int z = -1;
         float bestPathWeight = -99999.0F;

         for (int l = 0; l < 10; l++) {
            int x1 = MathHelper.floor(this.x + this.random.nextInt(13) - 6.0);
            int y1 = MathHelper.floor(this.y + this.random.nextInt(7) - 3.0);
            int z1 = MathHelper.floor(this.z + this.random.nextInt(13) - 6.0);
            float currentPathWeight = this.getBlockPathWeight(x1, y1, z1);
            if (currentPathWeight > bestPathWeight) {
               bestPathWeight = currentPathWeight;
               x = x1;
               y = y1;
               z = z1;
               canMoveToPoint = true;
            }
         }

         if (canMoveToPoint) {
            this.pathToEntity = this.world.getEntityPathToXYZ(this, x, y, z, 10.0F);
         }
      }
   }

   protected void attackEntity(@NotNull Entity entity, float distance) {
   }

   protected void attackBlockedEntity(@NotNull Entity entity, float f) {
   }

   protected float getBlockPathWeight(int x, int y, int z) {
      return 0.0F;
   }

   protected Entity findPlayerToAttack() {
      return null;
   }

   @Override
   public boolean canSpawnHere() {
      if (this.world == null) {
         return false;
      } else {
         int x = MathHelper.floor(this.x);
         int y = MathHelper.floor(this.bb.minY);
         int z = MathHelper.floor(this.z);
         return super.canSpawnHere() && this.getBlockPathWeight(x, y, z) >= 0.0F;
      }
   }

   public boolean hasPath() {
      return this.pathToEntity != null;
   }

   public void setPathToEntity(@Nullable Path path) {
      this.pathToEntity = path;
   }

   @Nullable
   public Entity getTarget() {
      return this.target;
   }

   public void setTarget(@Nullable Entity target) {
      this.target = target;
   }
}
