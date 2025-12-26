package net.minecraft.core.entity;

import com.mojang.nbt.tags.CompoundTag;
import java.util.List;
import net.minecraft.core.achievement.stat.StatList;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class EntityFishingBobber extends Entity {
   public static final int ID_BOBBER_BITFIELD = 2;
   private int xTile;
   private int yTile;
   private int zTile;
   public Player owner;
   private int ticksInAir = 0;
   private int ticksCatchable = 0;
   public Entity hookedEntity = null;
   private int lerpSteps;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private double lerpYRot;
   private double lerpXRot;
   private double velocityX;
   private double velocityY;
   private double velocityZ;

   public EntityFishingBobber(World world) {
      super(world);
      this.setSize(0.25F, 0.25F);
      this.ignoreFrustumCheck = true;
   }

   public EntityFishingBobber(World world, double x, double y, double z) {
      this(world);
      this.setPos(x, y, z);
      this.ignoreFrustumCheck = true;
   }

   public EntityFishingBobber(World world, Player owner) {
      super(world);
      this.ignoreFrustumCheck = true;
      this.owner = owner;
      this.owner.bobberEntity = this;
      this.setSize(0.25F, 0.25F);
      this.moveTo(owner.x, owner.y + 1.62 - owner.heightOffset, owner.z, owner.yRot, owner.xRot);
      this.x = this.x - MathHelper.cos(this.yRot / 180.0F * (float) Math.PI) * 0.16F;
      this.y -= 0.1;
      this.z = this.z - MathHelper.sin(this.yRot / 180.0F * (float) Math.PI) * 0.16F;
      this.setPos(this.x, this.y, this.z);
      this.heightOffset = 0.0F;
      float f = 0.4F;
      this.xd = -MathHelper.sin(this.yRot / 180.0F * (float) Math.PI) * MathHelper.cos(this.xRot / 180.0F * (float) Math.PI) * f;
      this.zd = MathHelper.cos(this.yRot / 180.0F * (float) Math.PI) * MathHelper.cos(this.xRot / 180.0F * (float) Math.PI) * f;
      this.yd = -MathHelper.sin(this.xRot / 180.0F * (float) Math.PI) * f;
      this.shoot(this.xd, this.yd, this.zd, 1.5F, 1.0F);
   }

   @Override
   protected void defineSynchedData() {
      this.entityData.define(2, (byte)0, Byte.class);
   }

   public boolean isInGround() {
      return this.entityData.getByte(2) != 0;
   }

   public void setInGround(boolean flag) {
      this.entityData.set(2, Byte.valueOf((byte)(flag ? 1 : 0)));
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double distance) {
      double d1 = this.bb.getSize() * 4.0;
      d1 *= 64.0;
      return distance < d1 * d1;
   }

   public void shoot(double x, double y, double z, float speed, float randomness) {
      float f2 = MathHelper.sqrt(x * x + y * y + z * z);
      x /= f2;
      y /= f2;
      z /= f2;
      x += this.random.nextGaussian() * 0.0075 * randomness;
      y += this.random.nextGaussian() * 0.0075 * randomness;
      z += this.random.nextGaussian() * 0.0075 * randomness;
      x *= speed;
      y *= speed;
      z *= speed;
      this.xd = x;
      this.yd = y;
      this.zd = z;
      float hAng = MathHelper.sqrt(x * x + z * z);
      this.yRotO = this.yRot = (float)(Math.atan2(x, z) * 180.0 / Math.PI);
      this.xRotO = this.xRot = (float)(Math.atan2(y, hAng) * 180.0 / Math.PI);
   }

   @Override
   public void lerpTo(double x, double y, double z, float yRot, float xRot, int i) {
      this.lerpX = x;
      this.lerpY = y;
      this.lerpZ = z;
      this.lerpYRot = yRot;
      this.lerpXRot = xRot;
      this.lerpSteps = i;
      this.xd = this.velocityX;
      this.yd = this.velocityY;
      this.zd = this.velocityZ;
   }

   @Override
   public void lerpMotion(double xd, double yd, double zd) {
      this.velocityX = this.xd = xd;
      this.velocityY = this.yd = yd;
      this.velocityZ = this.zd = zd;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.lerpSteps > 0) {
         double d = this.x + (this.lerpX - this.x) / this.lerpSteps;
         double d1 = this.y + (this.lerpY - this.y) / this.lerpSteps;
         double d2 = this.z + (this.lerpZ - this.z) / this.lerpSteps;
         double d4 = this.lerpYRot - this.yRot;

         while (d4 < -180.0) {
            d4 += 360.0;
         }

         while (d4 >= 180.0) {
            d4 -= 360.0;
         }

         this.yRot = (float)(this.yRot + d4 / this.lerpSteps);
         this.xRot = (float)(this.xRot + (this.lerpXRot - this.xRot) / this.lerpSteps);
         this.lerpSteps--;
         this.setPos(d, d1, d2);
         this.setRot(this.yRot, this.xRot);
      } else {
         if (!this.world.isClientSide) {
            ItemStack heldPlayerItem = this.owner.getCurrentEquippedItem();
            if (this.owner.removed
               || !this.owner.isAlive()
               || heldPlayerItem == null
               || heldPlayerItem.getItem() != Items.TOOL_FISHINGROD
               || this.distanceToSqr(this.owner) > 1024.0) {
               this.remove();
               this.owner.bobberEntity = null;
               return;
            }

            if (this.hookedEntity != null) {
               if (!this.hookedEntity.removed) {
                  this.x = this.hookedEntity.x;
                  this.y = this.hookedEntity.bb.minY + this.hookedEntity.bbHeight * 0.8;
                  this.z = this.hookedEntity.z;
                  if (this.hookedEntity instanceof MobPathfinder) {
                     ((MobPathfinder)this.hookedEntity).target = this.owner;
                  }

                  double dx = this.owner.x - this.x;
                  double dy = this.owner.y - this.y;
                  double dz = this.owner.z - this.z;
                  double distance = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
                  if (distance > 10.0) {
                     double scale = 0.01;
                     this.hookedEntity.xd += dx * scale;
                     this.hookedEntity.yd += dy * scale;
                     this.hookedEntity.zd += dz * scale;
                  }

                  return;
               }

               this.hookedEntity = null;
            }
         }

         if (this.isInGround()) {
            if (this.world.getBlockId(this.xTile, this.yTile, this.zTile) == Blocks.ROPE.id()) {
               this.x = this.xTile + 0.5;
               this.y = this.yTile + 0.5;
               this.z = this.zTile + 0.5;
               return;
            }

            this.setInGround(false);
            this.xd = this.xd * (this.random.nextFloat() * 0.2F);
            this.yd = this.yd * (this.random.nextFloat() * 0.2F);
            this.zd = this.zd * (this.random.nextFloat() * 0.2F);
            this.ticksInAir = 0;
            this.ticksCatchable = 0;
         }

         this.ticksInAir++;
         Vec3 currentPos = Vec3.getTempVec3(this.x, this.y, this.z);
         Vec3 nextPos = Vec3.getTempVec3(this.x + this.xd, this.y + this.yd, this.z + this.zd);
         HitResult clip = this.world.checkBlockCollisionBetweenPoints(currentPos, nextPos);
         currentPos = Vec3.getTempVec3(this.x, this.y, this.z);
         nextPos = Vec3.getTempVec3(this.x + this.xd, this.y + this.yd, this.z + this.zd);
         if (clip != null) {
            nextPos = Vec3.getTempVec3(clip.location.x, clip.location.y, clip.location.z);
            if (clip.hitType == HitResult.HitType.TILE && this.world.getBlockId(clip.x, clip.y, clip.z) == Blocks.ROPE.id()) {
               this.setInGround(true);
               this.xTile = clip.x;
               this.yTile = clip.y;
               this.zTile = clip.z;
            }
         }

         Entity entity = null;
         List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.bb.expand(this.xd, this.yd, this.zd).grow(1.0, 1.0, 1.0));
         double d3 = 0.0;

         for (Entity e : list) {
            if (e.isPickable() && (e != this.owner || this.ticksInAir >= 5)) {
               float f2 = 0.3F;
               AABB aabb = e.bb.grow(f2, f2, f2);
               HitResult newHitResult = aabb.clip(currentPos, nextPos);
               if (newHitResult != null) {
                  double d6 = currentPos.distanceTo(newHitResult.location);
                  if (d6 < d3 || d3 == 0.0) {
                     entity = e;
                     d3 = d6;
                  }
               }
            }
         }

         if (entity != null) {
            clip = new HitResult(entity);
         }

         if (clip != null && clip.entity != null && clip.entity.hurt(this.owner, 0, DamageType.COMBAT)) {
            this.hookedEntity = clip.entity;
         }

         this.move(this.xd, this.yd, this.zd);
         float f = MathHelper.sqrt(this.xd * this.xd + this.zd * this.zd);
         this.yRot = (float)(Math.atan2(this.xd, this.zd) * 180.0 / Math.PI);
         this.xRot = (float)(Math.atan2(this.yd, f) * 180.0 / Math.PI);

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
         float movementScale = 0.92F;
         if (this.onGround || this.horizontalCollision) {
            movementScale = 0.5F;
         }

         int k = 5;
         double d5 = 0.0;

         for (int l = 0; l < k; l++) {
            double d8 = this.bb.minY + (this.bb.maxY - this.bb.minY) * l / k - 0.125 + 0.125;
            double d9 = this.bb.minY + (this.bb.maxY - this.bb.minY) * (l + 1) / k - 0.125 + 0.125;
            AABB axisalignedbb1 = AABB.getTemporaryBB(this.bb.minX, d8, this.bb.minZ, this.bb.maxX, d9, this.bb.maxZ);
            if (this.world.isAABBInMaterial(axisalignedbb1, Material.water)) {
               d5 += 1.0 / k;
            }
         }

         if (d5 > 0.0) {
            if (this.ticksCatchable > 0) {
               this.ticksCatchable--;
            } else {
               int catchRate = 500;
               int rainRate = 0;
               int algaeRate = 0;
               if (this.world.canBlockBeRainedOn(MathHelper.floor(this.x), MathHelper.floor(this.y) + 1, MathHelper.floor(this.z))) {
                  rainRate = 200;
               }

               if (this.world.getBlockId(MathHelper.floor(this.x), MathHelper.floor(this.y) + 1, MathHelper.floor(this.z)) == Blocks.ALGAE.id()) {
                  algaeRate = 100;
               }

               catchRate = catchRate - rainRate - algaeRate;
               if (this.random.nextInt(catchRate) == 0) {
                  this.ticksCatchable = this.random.nextInt(30) + 10;
                  this.yd -= 0.2;
                  this.world.playSoundAtEntity(null, this, "random.splash", 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                  float f3 = MathHelper.floor(this.bb.minY);

                  for (int i1 = 0; i1 < 1.0F + this.bbWidth * 20.0F; i1++) {
                     double xOff = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
                     double zOff = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
                     this.world.spawnParticle("bubble", this.x + xOff, f3 + 1.0F, this.z + zOff, this.xd, this.yd - this.random.nextFloat() * 0.2F, this.zd, 0);
                  }

                  for (int j1 = 0; j1 < 1.0F + this.bbWidth * 20.0F; j1++) {
                     double xOff = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
                     double zOff = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
                     this.world.spawnParticle("splash", this.x + xOff, f3 + 1.0F, this.z + zOff, this.xd, this.yd, this.zd, 0);
                  }
               }
            }
         }

         if (this.ticksCatchable > 0) {
            this.yd = this.yd - this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat() * 0.2;
         }

         double d7 = d5 * 2.0 - 1.0;
         this.yd += 0.04 * d7;
         if (d5 > 0.0) {
            movementScale = (float)(movementScale * 0.9);
            this.yd *= 0.8;
         }

         this.xd *= movementScale;
         this.yd *= movementScale;
         this.zd *= movementScale;
         this.setPos(this.x, this.y, this.z);
      }
   }

   @Override
   public void remove() {
      if (this.hookedEntity instanceof MobPathfinder) {
         ((MobPathfinder)this.hookedEntity).target = null;
      }

      super.remove();
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
   }

   @Override
   public float getShadowHeightOffs() {
      return 0.0F;
   }

   public int yoink() {
      int damage = 0;
      if (this.isInGround()) {
         double dx = this.x - this.owner.x;
         double dy = this.y - this.owner.y;
         double dz = this.z - this.owner.z;
         double distance = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
         dx /= distance;
         dy /= distance;
         dz /= distance;
         double clamp = 0.6;
         dx = MathHelper.clamp(dx, -clamp, clamp);
         dy = MathHelper.clamp(dy, -clamp, clamp);
         dz = MathHelper.clamp(dz, -clamp, clamp);
         double scale = 2.0;
         this.owner.xd += dx * scale;
         this.owner.yd += dy * scale;
         this.owner.zd += dz * scale;
         damage = 5;
      }

      if (this.hookedEntity != null) {
         double dx = this.owner.x - this.x;
         double dy = this.owner.y - this.y;
         double dz = this.owner.z - this.z;
         double distance = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
         double scale = 0.1;
         this.hookedEntity.xd += dx * scale;
         this.hookedEntity.yd = this.hookedEntity.yd + (dy * scale + MathHelper.sqrt(distance) * 0.08);
         this.hookedEntity.zd += dz * scale;
         damage = 3;
      } else if (this.ticksCatchable > 0) {
         EntityItem entityitem = new EntityItem(this.world, this.x, this.y, this.z, new ItemStack(Items.FOOD_FISH_RAW));
         double dx = this.owner.x - this.x;
         double dy = this.owner.y - this.y;
         double dz = this.owner.z - this.z;
         double distance = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
         double scale = 0.1;
         entityitem.xd = dx * scale;
         entityitem.yd = dy * scale + MathHelper.sqrt(distance) * 0.08;
         entityitem.zd = dz * scale;
         this.world.entityJoinedWorld(entityitem);
         this.owner.addStat(StatList.fishCaughtStat, 1);
         damage = 1;
      }

      if (this.isInGround()) {
         damage = 2;
      }

      this.remove();
      this.owner.bobberEntity = null;
      return damage;
   }
}
