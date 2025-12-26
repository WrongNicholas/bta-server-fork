package net.minecraft.core.entity.vehicle;

import com.mojang.nbt.tags.CompoundTag;
import java.util.List;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.util.helper.BlockParticleHelper;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class EntityBoat extends Entity {
   private static final double MAX_BOAT_SPEED = 0.8;
   private static final double ACCELERATION = 0.0065;
   private static final float MAX_ROTATION_SPEED = 5.0F;
   private static final float MIN_ROTATION_SPEED = 3.0F;
   private static final double MAX_PACKET_SPEED_CHANGE = 0.052;
   private static final double MAX_PACKET_ROTATION_CHANGE = 20.0;
   public int boatCurrentDamage;
   public int boatTimeSinceHit;
   public int boatRockDirection;
   private int lerpTicks;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private float lerpYRot;
   private float boatPitch;
   private double velocityX;
   private double velocityY;
   private double velocityZ;
   private float pendingYRot;
   private double pendingXDChange;
   private double pendingZDChange;
   private ItemStack flag = null;
   public TileEntityFlag tileEntityFlag = null;

   public EntityBoat(World world) {
      super(world);
      this.boatCurrentDamage = 0;
      this.boatTimeSinceHit = 0;
      this.boatRockDirection = 1;
      this.blocksBuilding = true;
      this.setSize(1.5F, 0.6F);
      this.heightOffset = this.bbHeight / 2.0F;
      this.sendAdditionalData = true;
   }

   private void createTileEntity() {
      if (this.flag != null) {
         this.tileEntityFlag = new TileEntityFlag();
         CompoundTag flagData = this.flag.getData().getCompoundOrDefault("FlagData", null);
         if (flagData != null) {
            this.tileEntityFlag.readFlagNBT(flagData);
         }
      }
   }

   @Override
   protected boolean makeStepSound() {
      return false;
   }

   @Override
   protected void defineSynchedData() {
   }

   @Override
   public AABB getBb() {
      return this.bb;
   }

   @Override
   public boolean isPushable() {
      return true;
   }

   public EntityBoat(World world, double d, double d1, double d2) {
      this(world);
      this.setPos(d, d1 + this.heightOffset, d2);
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      this.xo = d;
      this.yo = d1;
      this.zo = d2;
   }

   @Override
   public double getRideHeight() {
      return this.bbHeight * 0.0 - 0.3;
   }

   @Override
   public boolean hurt(Entity entity, int i, DamageType type) {
      int numParticles = this.random.nextInt(8) + 1;

      for (int q = 0; q < numParticles; q++) {
         this.world
            .spawnParticle(
               "block", this.x, this.y, this.z, this.xd, this.yd, this.zd, BlockParticleHelper.encodeBlockData(Blocks.PLANKS_OAK.id(), 0, Side.BOTTOM)
            );
      }

      if (!this.world.isClientSide && !this.removed) {
         if (entity instanceof Player && ((Player)entity).getGamemode() == Gamemode.creative) {
            this.remove();
            if (this.flag != null) {
               this.dropItem(this.flag.copy(), 0.0F);
            }

            return true;
         } else {
            this.boatRockDirection = -this.boatRockDirection;
            this.boatTimeSinceHit = 10;
            this.boatCurrentDamage += i * 10;
            this.markHurt();
            if (this.boatCurrentDamage > 40) {
               if (this.passenger != null) {
                  this.passenger.startRiding(this);
               }

               if (entity instanceof Player) {
                  this.dropItem(Items.BOAT.id, 1, 0.0F);
               } else {
                  for (int j = 0; j < 3; j++) {
                     this.dropItem(Blocks.PLANKS_OAK.id(), 1, 0.0F);
                  }

                  for (int k = 0; k < 2; k++) {
                     this.dropItem(Items.STICK.id, 1, 0.0F);
                  }
               }

               if (this.flag != null) {
                  this.dropItem(this.flag.copy(), 0.0F);
               }

               this.remove();
            }

            return true;
         }
      } else {
         return true;
      }
   }

   @Override
   public void remove() {
      Entity entity = this.passenger;
      if (entity != null) {
         double bbCenterX = (this.bb.minX + this.bb.maxX) / 2.0;
         double bbCenterZ = (this.bb.minZ + this.bb.maxZ) / 2.0;
         this.passenger = null;
         entity.vehicle = null;
         entity.moveTo(bbCenterX, this.bb.maxY + 0.1, bbCenterZ, entity.yRot, entity.xRot);
      }

      super.remove();
   }

   @Override
   public void animateHurt() {
      this.boatRockDirection = -this.boatRockDirection;
      this.boatTimeSinceHit = 10;
      this.boatCurrentDamage = this.boatCurrentDamage + this.boatCurrentDamage * 10;
   }

   @Override
   public boolean isPickable() {
      return !this.removed;
   }

   @Override
   public void lerpTo(double x, double y, double z, float yRot, float xRot, int i) {
      this.lerpX = x;
      this.lerpY = y;
      this.lerpZ = z;
      this.boatPitch = xRot;
      this.lerpTicks = i;
      if (this.passenger == null || this.passenger.lerpVehicleMotion()) {
         this.lerpYRot = yRot;
         this.xd = this.velocityX;
         this.yd = this.velocityY;
         this.zd = this.velocityZ;
      }
   }

   @Override
   public void lerpMotion(double xd, double yd, double zd) {
      if (this.passenger == null || this.passenger.lerpVehicleMotion()) {
         this.velocityX = this.xd = xd;
         this.velocityY = this.yd = yd;
         this.velocityZ = this.zd = zd;
      }
   }

   @Override
   public void tick() {
      this.setRot(this.yRot, this.xRot);
      super.tick();
      if (this.boatTimeSinceHit > 0) {
         this.boatTimeSinceHit--;
      }

      if (this.boatCurrentDamage > 0) {
         this.boatCurrentDamage--;
      }

      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      int waterSections = 5;
      double bouyancy = 0.0;
      boolean inWater = false;

      for (int i = 0; i < 5; i++) {
         double minY = this.bb.minY + (this.bb.maxY - this.bb.minY) * i / 5.0 - 0.125;
         double maxY = this.bb.minY + (this.bb.maxY - this.bb.minY) * (i + 1) / 5.0 - 0.125;
         AABB axisalignedbb = AABB.getTemporaryBB(this.bb.minX, minY, this.bb.minZ, this.bb.maxX, maxY, this.bb.maxZ);
         if (this.world.isAABBInMaterial(axisalignedbb, Material.water)) {
            bouyancy += 0.2;
            inWater = true;
         }
      }

      if (this.world.isClientSide) {
         if (this.lerpTicks > 0) {
            double lerpX = this.x + (this.lerpX - this.x) / this.lerpTicks;
            double lerpY = this.y + (this.lerpY - this.y) / this.lerpTicks;
            double lerpZ = this.z + (this.lerpZ - this.z) / this.lerpTicks;
            this.xRot = this.xRot + (this.boatPitch - this.xRot) / this.lerpTicks;
            if (this.passenger == null || this.passenger.lerpVehicleMotion()) {
               float lerpYRot = MathHelper.normalizeRotation(this.lerpYRot - this.yRot);
               this.yRot = this.yRot + lerpYRot / this.lerpTicks;
            }

            this.lerpTicks--;
            this.setPos(lerpX, lerpY, lerpZ);
         }

         this.boatMovement();
         this.move(this.xd, this.yd, this.zd);
         this.setRot(this.yRot, this.xRot);
         this.xd *= 0.99;
         this.yd *= 0.95;
         this.zd *= 0.99;
      } else {
         if (bouyancy < 1.0) {
            this.yd += 0.04 * (bouyancy * 2.0 - 1.0);
         } else {
            if (this.yd < 0.0) {
               this.yd /= 2.0;
            }

            this.yd += 0.007;
         }

         this.boatMovement();
         if (this.passenger == null) {
            float nextYRot = this.yRot;
            if (Math.hypot(this.xd, this.zd) > 0.001) {
               nextYRot = (float)Math.toDegrees(Math.atan2(this.zd, this.xd)) + 180.0F;
            }

            float changeInYRot = MathHelper.normalizeRotation(nextYRot - this.yRot);
            changeInYRot = MathHelper.clamp(changeInYRot, -20.0F, 20.0F);
            this.yRot = MathHelper.normalizeRotation(this.yRot + changeInYRot);
            this.setRot(this.yRot, this.xRot);
         }

         this.move(this.xd, this.yd, this.zd);
         double horizontalSpeed = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
         if (inWater && horizontalSpeed > 0.15) {
            double d12 = Math.cos(this.yRot * Math.PI / 180.0);
            double d15 = Math.sin(this.yRot * Math.PI / 180.0);

            for (int i1 = 0; i1 < 1.0 + horizontalSpeed * 60.0; i1++) {
               double d18 = this.random.nextFloat() * 2.0F - 1.0F;
               double d20 = (this.random.nextInt(2) * 2 - 1) * 0.6;
               if (this.random.nextBoolean()) {
                  double posX = this.x - d12 * d18 * 0.8 + d15 * d20;
                  double posZ = this.z - d15 * d18 * 0.8 - d12 * d20;
                  this.world.spawnParticle("splash", posX, this.y - 0.125, posZ, this.xd, this.yd, this.zd, 0);
               } else {
                  double posX = this.x + d12 + d15 * d18 * 0.7;
                  double posZ = this.z + d15 - d12 * d18 * 0.7;
                  this.world.spawnParticle("splash", posX, this.y - 0.125, posZ, this.xd, this.yd, this.zd, 0);
               }
            }
         }

         if (this.passenger == null || !this.passenger.deferVehicleBehavior()) {
            this.xd *= 0.99;
            this.yd *= 0.95;
            this.zd *= 0.99;
         }

         this.xRot = 0.0F;
         List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.bb.grow(0.2, 0.0, 0.2));
         if (list != null && !list.isEmpty()) {
            for (Entity entity : list) {
               if (entity != this.passenger && entity.isPushable() && entity instanceof EntityBoat) {
                  entity.push(this);
               }
            }
         }

         for (int ix = 0; ix < 4; ix++) {
            int blockX = MathHelper.floor(this.x + (ix % 2 - 0.5) * 0.8);
            int blockY = MathHelper.floor(this.y);
            int blockZ = MathHelper.floor(this.z + (ix / 2 - 0.5) * 0.8);
            Block<?> block = this.world.getBlock(blockX, blockY, blockZ);
            if (block != null && block.hasTag(BlockTags.BOAT_BREAKS)) {
               this.world.setBlockWithNotify(blockX, blockY, blockZ, 0);
            }
         }

         if (this.passenger != null && this.passenger.removed) {
            this.passenger = null;
         }
      }
   }

   public void boatMovement() {
      double boatRad = Math.toRadians(this.yRot + 90.0F);
      double vectorRad = Math.atan2(this.xd, this.zd);
      if (this.passenger != null) {
         boolean isBackwards = Math.cos(-boatRad - vectorRad) < 0.0;
         double ang = vectorRad + boatRad + (isBackwards ? Math.PI : 0.0);
         double _xd = this.xd;
         this.xd = _xd * Math.cos(ang) - this.zd * Math.sin(ang);
         this.zd = _xd * Math.sin(ang) + this.zd * Math.cos(ang);
      }

      double currentBoatSpeed = Math.hypot(this.xd, this.zd);
      if (this.passenger != null) {
         this.passenger.handleSpecialVehicleControl();
      }

      this.yRot = this.yRot + this.pendingYRot;
      this.pendingYRot = 0.0F;
      double xdOff = this.pendingXDChange;
      double zdOff = this.pendingZDChange;
      this.pendingXDChange = 0.0;
      this.pendingZDChange = 0.0;
      double offsetMagnitude = Math.hypot(xdOff, zdOff);
      double vecAngle = Math.acos((this.xd * xdOff + this.zd * zdOff) / ((currentBoatSpeed + 1.0E-11) * (offsetMagnitude + 1.0E-11)));
      double multiplier = 1.0 + vecAngle / Math.PI;
      xdOff *= multiplier;
      zdOff *= multiplier;
      this.xd += xdOff;
      this.zd += zdOff;
      if (this.passenger != null) {
         this.passenger.sendSpecialVehiclePacket();
      }

      currentBoatSpeed = Math.hypot(this.xd, this.zd);
      if (currentBoatSpeed > 0.8) {
         this.xd = this.xd / currentBoatSpeed * 0.8;
         this.zd = this.zd / currentBoatSpeed * 0.8;
      }

      if (this.onGround) {
         this.xd *= 0.5;
         this.yd *= 0.5;
         this.zd *= 0.5;
      }
   }

   public void controlBoat(float forward, float strafe) {
      double currentBoatSpeed = Math.hypot(this.xd, this.zd);
      this.pendingYRot = (float)(strafe * (3.0 + 2.0 * Math.max((0.8 - currentBoatSpeed * 1.5) / 0.8, 0.0)));
      float velOff = (float)(forward * 0.0065);
      this.pendingXDChange = Math.cos(Math.toRadians(this.yRot)) * velOff;
      this.pendingZDChange = Math.sin(Math.toRadians(this.yRot)) * velOff;
   }

   public void handleControlDirect(float xd, float zd, float yRot) {
      this.yRot = (float)MathHelper.clamp((double)yRot, this.yRot - 20.0, this.yRot + 20.0);
      this.xd = MathHelper.clamp((double)xd, this.xd - 0.052, this.xd + 0.052);
      this.zd = MathHelper.clamp((double)zd, this.zd - 0.052, this.zd + 0.052);
   }

   @Override
   public void positionRider() {
      if (this.passenger != null) {
         double d = Math.cos(this.yRot * Math.PI / 180.0) * 0.4;
         double d1 = Math.sin(this.yRot * Math.PI / 180.0) * 0.4;
         this.passenger.setPos(this.x + d, this.y + this.getRideHeight() + this.passenger.getRidingHeight(), this.z + d1);
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      if (this.flag != null) {
         CompoundTag flagTag = new CompoundTag();
         this.flag.writeToNBT(flagTag);
         tag.put("Flag", flagTag);
      }
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      if (tag.containsKey("Flag")) {
         CompoundTag flagTag = tag.getCompound("Flag");
         this.flag = ItemStack.readItemStackFromNbt(flagTag);
         this.createTileEntity();
      }
   }

   @Override
   public float getShadowHeightOffs() {
      return 0.0F;
   }

   @Override
   public boolean interact(@NotNull Player player) {
      if (this.passenger != null && this.passenger instanceof Player && this.passenger != player) {
         return true;
      } else {
         if (!this.world.isClientSide) {
            if (player.getHeldItem() != null && player.getHeldItem().itemID == Items.FLAG.id && this.flag == null) {
               this.flag = player.getHeldItem().copy();
               player.inventory.setItem(player.inventory.getCurrentItemIndex(), null);
               this.createTileEntity();
               this.world.sendTrackedEntityDataPacket(this);
               return true;
            }

            player.startRiding(this);
         }

         return true;
      }
   }

   @Override
   public Entity ejectRider() {
      Entity entity = this.passenger;
      if (entity == null) {
         return null;
      } else {
         this.passenger = null;
         entity.vehicle = null;
         double bbCenterX = (this.bb.minX + this.bb.maxX) / 2.0;
         double bbCenterY = (this.bb.minY + this.bb.maxY) / 2.0;
         double bbCenterZ = (this.bb.minZ + this.bb.maxZ) / 2.0;
         boolean flag = false;

         for (int i = 0; i < 4; i++) {
            double length = 1.75;
            double angle = Math.toRadians(this.yRot - 90.0F) + (Math.PI / 2) * i;
            double checkX = bbCenterX + Math.cos(angle) * 1.75;
            double checkZ = bbCenterZ + Math.sin(angle) * 1.75;
            entity.moveTo(checkX, this.bb.maxY + 0.1, checkZ, entity.yRot, entity.xRot);
            if (this.world.getCubes(entity, entity.bb).isEmpty()) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            entity.moveTo(bbCenterX, this.bb.maxY + 0.1, bbCenterZ, entity.yRot, entity.xRot);
         }

         return entity;
      }
   }

   @Override
   protected void causeFallDamage(float distance) {
   }
}
