package net.minecraft.core.entity;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.DoubleTag;
import com.mojang.nbt.tags.FloatTag;
import com.mojang.nbt.tags.ListTag;
import java.util.List;
import java.util.Random;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicFluid;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.piston.BlockLogicPistonHead;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.ICarriable;
import net.minecraft.core.world.IVehicle;
import net.minecraft.core.world.World;
import net.minecraft.core.world.data.SynchedEntityData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Entity implements IVehicle {
   public static final int DATA_SHARED_FLAGS_ID = 0;
   public static final int FLAG_ONFIRE = 0;
   public static final int FLAG_SNEAKING = 1;
   public static final int FLAG_RIDING = 2;
   public static final int FLAG_SPRINTING = 3;
   private static int entityCounter = 0;
   public int id = entityCounter++;
   public double viewScale = 1.0;
   public boolean blocksBuilding = false;
   @Nullable
   public Entity passenger;
   @Nullable
   public IVehicle vehicle;
   @Nullable
   public World world;
   public double xo;
   public double yo;
   public double zo;
   public double x;
   public double y;
   public double z;
   public double xd;
   public double yd;
   public double zd;
   public float yRot;
   public float xRot;
   public float yRotO;
   public float xRotO;
   public float pushTime;
   public int pushesThisTick;
   @NotNull
   public final AABB bb = AABB.getPermanentBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public boolean onGround = false;
   public boolean horizontalCollision;
   public boolean verticalCollision;
   public boolean collision = false;
   public boolean hurtMarked = false;
   public boolean hadNicknameSet;
   public boolean stuckInCobweb;
   public boolean stuckInSpikes;
   public boolean slide = true;
   public boolean removed = false;
   public float heightOffset = 0.0F;
   public float bbWidth = 0.6F;
   public float bbHeight = 1.825F;
   public float walkDistO = 0.0F;
   public float walkDist = 0.0F;
   public float fallDistance = 0.0F;
   private int nextStep = 1;
   public float ySlideOffset = 0.0F;
   public float footSize = 0.0F;
   public boolean noPhysics = false;
   public float pushthrough = 0.0F;
   @NotNull
   protected Random random = new Random();
   public int tickCount;
   public int fireImmuneTicks = 1;
   public int remainingFireTicks = 0;
   public int maxFireTicks;
   private int soundCooldown;
   protected int airMaxSupply = 300;
   protected boolean wasInWater = false;
   public int heartsFlashTime = 0;
   public int airSupply = 300;
   private boolean firstTick = true;
   protected boolean fireImmune;
   @NotNull
   protected SynchedEntityData entityData = new SynchedEntityData();
   public float entityBrightness = 0.0F;
   private double entityRiderPitchDelta;
   private double entityRiderYawDelta;
   public boolean addedToChunk = false;
   public int chunkCoordX;
   public int chunkCoordY;
   public int chunkCoordZ;
   public int serverPosX;
   public int serverPosY;
   public int serverPosZ;
   public boolean ignoreFrustumCheck;
   public boolean isWalking = false;
   protected boolean muteStepSounds = false;
   public float speed;
   public float flySpeed;
   public boolean sendAdditionalData = false;
   public boolean turnWithVehicle = true;

   public Entity(@Nullable World world) {
      this.world = world;
      this.setPos(0.0, 0.0, 0.0);
      this.entityData.define(0, (byte)0, Byte.class);
      this.defineSynchedData();
   }

   protected abstract void defineSynchedData();

   public void spawnInit() {
   }

   @NotNull
   public SynchedEntityData getEntityData() {
      return this.entityData;
   }

   @Override
   public boolean equals(@Nullable Object that) {
      return that instanceof Entity ? ((Entity)that).id == this.id : false;
   }

   @Override
   public int hashCode() {
      return this.id;
   }

   protected void resetPos() {
      if (this.world != null) {
         while (!(this.y <= 0.0)) {
            this.setPos(this.x, this.y, this.z);
            if (this.world.getCubes(this, this.bb).isEmpty()) {
               break;
            }

            this.y++;
         }

         this.xd = this.yd = this.zd = 0.0;
         this.xRot = 0.0F;
      }
   }

   public void remove() {
      this.removed = true;
   }

   public boolean showBoundingBoxOnHover() {
      return false;
   }

   protected void setSize(float width, float height) {
      this.bbWidth = width;
      this.bbHeight = height;
   }

   public void setRot(float yRot, float xRot) {
      this.yRot = yRot % 360.0F;
      this.xRot = xRot % 360.0F;
   }

   public void setPos(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
      float center = this.bbWidth / 2.0F;
      float heightOfMob = this.bbHeight;
      this.bb
         .set(
            x - center, y - this.heightOffset + this.ySlideOffset, z - center, x + center, y - this.heightOffset + this.ySlideOffset + heightOfMob, z + center
         );
   }

   public void interpolateTurn(float yRot, float xRot) {
      float xRotCurrent = this.xRot;
      float yRotCurrent = this.yRot;
      this.yRot = (float)(this.yRot + yRot * 0.15);
      this.xRot = (float)(this.xRot - xRot * 0.15);
      if (this.xRot < -90.0F) {
         this.xRot = -90.0F;
      }

      if (this.xRot > 90.0F) {
         this.xRot = 90.0F;
      }

      this.xRotO = this.xRotO + (this.xRot - xRotCurrent);
      this.yRotO = this.yRotO + (this.yRot - yRotCurrent);
   }

   public void tick() {
      this.baseTick();
   }

   public void baseTick() {
      if (this.world != null) {
         this.pushesThisTick = 0;
         this.pushTime *= 0.98F;
         if (this.pushTime < 0.001F) {
            this.pushTime = 0.0F;
         }

         if (this.vehicle != null && this.vehicle.isRemoved()) {
            this.vehicle = null;
         }

         this.tickCount++;
         this.walkDistO = this.walkDist;
         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         this.xRotO = this.xRot;
         this.yRotO = this.yRot;
         this.checkOnWater(true);
         if (this.world.isClientSide) {
            this.remainingFireTicks = 0;
         } else if (this.remainingFireTicks > 0) {
            if (this.fireImmune) {
               this.remainingFireTicks -= 4;
               if (this.remainingFireTicks < 0) {
                  this.remainingFireTicks = 0;
               }
            } else {
               if (this.remainingFireTicks % 20 == 0) {
                  this.hurt(null, 1, DamageType.FIRE);
               }

               this.remainingFireTicks--;
            }
         }

         if (this.soundCooldown > 0) {
            this.soundCooldown--;
         }

         if (this.isInLava()) {
            this.lavaHurt();
         }

         if (this.y < -64.0) {
            this.outOfWorld();
         }

         if (!this.world.isClientSide) {
            this.setSharedFlag(0, this.remainingFireTicks > 0);
            this.setSharedFlag(2, this.vehicle != null);
         }

         this.firstTick = false;
      }
   }

   protected void checkOnWater(boolean addVelocity) {
      if (this.world != null) {
         if (this.checkAndHandleWater(addVelocity)) {
            if (!this.wasInWater && !this.firstTick) {
               float f = MathHelper.sqrt(this.xd * this.xd * 0.2 + this.yd * this.yd + this.zd * this.zd * 0.2) * 0.2F;
               if (f > 1.0F) {
                  f = 1.0F;
               }

               if (!Global.isServer) {
                  this.world.playSoundAtEntity(null, this, "random.splash", f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
               } else if (!this.world.isClientSide && this.soundCooldown == 0) {
                  this.soundCooldown = 2;
                  this.world.playSoundAtEntity(this, this, "random.splash", f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
               }

               float f1 = MathHelper.floor(this.bb.minY);

               for (int i = 0; i < 1.0F + this.bbWidth * 20.0F; i++) {
                  double offX = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
                  double offZ = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
                  this.world.spawnParticle("bubble", this.x + offX, f1 + 1.0F, this.z + offZ, this.xd, this.yd - this.random.nextFloat() * 0.2F, this.zd, 0);
               }

               for (int j = 0; j < 1.0F + this.bbWidth * 20.0F; j++) {
                  double offX = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
                  double offZ = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
                  this.world.spawnParticle("splash", this.x + offX, f1 + 1.0F, this.z + offZ, this.xd, this.yd, this.zd, 0);
               }
            }

            this.fallDistance = 0.0F;
            this.wasInWater = true;
            this.remainingFireTicks = 0;
         } else {
            this.wasInWater = false;
         }
      }
   }

   public void lavaHurt() {
      if (!this.fireImmune) {
         this.hurt(null, 4, DamageType.FIRE);
         this.remainingFireTicks = 600;
         this.maxFireTicks = this.remainingFireTicks;
      }
   }

   public void fireHurt() {
      if (!this.fireImmune) {
         this.hurt(null, 1, DamageType.FIRE);
         this.remainingFireTicks = 300;
         this.maxFireTicks = this.remainingFireTicks;
      }
   }

   public void outOfWorld() {
      this.remove();
   }

   public boolean isFree(double x, double y, double z) {
      if (this.world == null) {
         return true;
      } else {
         AABB offsetBb = this.bb.cloneMove(x, y, z).grow(-0.01, -0.01, -0.01);
         List<AABB> list = this.world.getCubes(this, offsetBb);
         return !list.isEmpty() ? false : !this.world.getIsAnyLiquid(offsetBb);
      }
   }

   public void move(double xd, double yd, double zd) {
      if (!this.noPhysics && this.world != null) {
         this.ySlideOffset *= 0.4F;
         double d3 = this.x;
         double d4 = this.z;
         if (this.stuckInCobweb) {
            xd *= 0.25;
            this.xd = 0.0;
            zd *= 0.25;
            this.zd = 0.0;
            yd *= 0.05;
            this.yd = 0.0;
            this.stuckInCobweb = false;
         }

         if (this.stuckInSpikes) {
            xd *= 0.25;
            this.xd = 0.0;
            zd *= 0.25;
            this.zd = 0.0;
            this.yd = 0.0;
            this.stuckInSpikes = false;
         }

         double oldXd = xd;
         double oldYd = yd;
         double oldZd = zd;
         AABB bbCopy = this.bb.copy();
         double bbWidth = this.bb.maxX - this.bb.minX - 0.05;
         if (this.isSneaking()) {
            if (xd != 0.0) {
               AABB floor = AABB.getTemporaryBB(this.bb.minX, this.bb.minY - 1.0, this.bb.minZ, this.bb.maxX, this.bb.minY, this.bb.maxZ).expand(xd, 0.0, 0.0);
               List<AABB> collidingBoxes = this.world.getCubes(this, floor);
               if (!collidingBoxes.isEmpty()) {
                  double limitX = 0.0;

                  for (int i = 0; i < collidingBoxes.size(); i++) {
                     AABB bb1 = collidingBoxes.get(i);
                     if (xd < 0.0) {
                        limitX = Math.min(limitX, bb1.minX - bbWidth - this.bb.minX);
                     } else {
                        limitX = Math.max(limitX, bb1.maxX + bbWidth - this.bb.maxX);
                     }
                  }

                  if (xd < 0.0) {
                     oldXd = xd = Math.max(xd, limitX);
                  } else {
                     oldXd = xd = Math.min(xd, limitX);
                  }
               }
            }

            if (zd != 0.0) {
               AABB floor = AABB.getTemporaryBB(this.bb.minX, this.bb.minY - 1.0, this.bb.minZ, this.bb.maxX, this.bb.minY, this.bb.maxZ).expand(0.0, 0.0, zd);
               List<AABB> collidingBoxes = this.world.getCubes(this, floor);
               if (!collidingBoxes.isEmpty()) {
                  double limitZ = 0.0;

                  for (int ix = 0; ix < collidingBoxes.size(); ix++) {
                     AABB bb1 = collidingBoxes.get(ix);
                     if (zd < 0.0) {
                        limitZ = Math.min(limitZ, bb1.minZ - bbWidth - this.bb.minZ);
                     } else {
                        limitZ = Math.max(limitZ, bb1.maxZ + bbWidth - this.bb.maxZ);
                     }
                  }

                  if (zd < 0.0) {
                     oldZd = zd = Math.max(zd, limitZ);
                  } else {
                     oldZd = zd = Math.min(zd, limitZ);
                  }
               }
            }
         }

         List<AABB> cubes = this.world.getCubes(this, this.bb.expand(xd, yd, zd));

         for (int ixx = 0; ixx < cubes.size(); ixx++) {
            yd = cubes.get(ixx).clipYCollide(this.bb, yd);
         }

         this.bb.move(0.0, yd, 0.0);
         if (!this.slide && oldYd != yd) {
            zd = 0.0;
            yd = 0.0;
            xd = 0.0;
         }

         for (int ixx = 0; ixx < cubes.size(); ixx++) {
            xd = cubes.get(ixx).clipXCollide(this.bb, xd);
         }

         this.bb.move(xd, 0.0, 0.0);
         if (!this.slide && oldXd != xd) {
            zd = 0.0;
            yd = 0.0;
            xd = 0.0;
         }

         for (int ixx = 0; ixx < cubes.size(); ixx++) {
            zd = cubes.get(ixx).clipZCollide(this.bb, zd);
         }

         this.bb.move(0.0, 0.0, zd);
         if (!this.slide && oldZd != zd) {
            zd = 0.0;
            yd = 0.0;
            xd = 0.0;
         }

         boolean touchedGround = this.onGround || oldYd != yd && oldYd < 0.0;
         boolean touchedWall = oldXd != xd || oldZd != zd;
         if (this.footSize > 0.0F && touchedGround && touchedWall && (this.isSneaking() || this.ySlideOffset < 0.05F)) {
            double oldX = xd;
            double oldY = yd;
            double oldZ = zd;
            xd = oldXd;
            yd = this.footSize;
            zd = oldZd;
            AABB axisalignedbb1 = this.bb.copy();
            this.bb.setBB(bbCopy);
            cubes = this.world.getCubes(this, this.bb.expand(oldXd, 0.0, oldZd));

            for (int ixx = 0; ixx < cubes.size(); ixx++) {
               yd = cubes.get(ixx).clipYCollide(this.bb, yd);
            }

            this.bb.move(0.0, yd, 0.0);
            if (!this.slide && oldYd != yd) {
               zd = 0.0;
               yd = 0.0;
               xd = 0.0;
            }

            for (int ixx = 0; ixx < cubes.size(); ixx++) {
               xd = cubes.get(ixx).clipXCollide(this.bb, xd);
            }

            this.bb.move(xd, 0.0, 0.0);
            if (!this.slide && oldXd != xd) {
               zd = 0.0;
               yd = 0.0;
               xd = 0.0;
            }

            for (int ixx = 0; ixx < cubes.size(); ixx++) {
               zd = cubes.get(ixx).clipZCollide(this.bb, zd);
            }

            this.bb.move(0.0, 0.0, zd);
            if (!this.slide && oldZd != zd) {
               zd = 0.0;
               yd = 0.0;
               xd = 0.0;
            }

            if (!this.slide && oldYd != yd) {
               zd = 0.0;
               yd = 0.0;
               xd = 0.0;
            } else {
               yd = -this.footSize;

               for (AABB aabb : cubes) {
                  yd = aabb.clipYCollide(this.bb, yd);
               }

               this.bb.move(0.0, yd, 0.0);
            }

            cubes = this.world.getCubes(this, this.bb);
            if (oldX * oldX + oldZ * oldZ >= xd * xd + zd * zd || cubes.size() > 0) {
               xd = oldX;
               yd = oldY;
               zd = oldZ;
               this.bb.setBB(axisalignedbb1);
            }
         }

         if (!this.world.isClientSide || this instanceof Player || !(this instanceof Mob)) {
            this.x = (this.bb.minX + this.bb.maxX) / 2.0;
            this.y = this.bb.minY + this.heightOffset - this.ySlideOffset;
            this.z = (this.bb.minZ + this.bb.maxZ) / 2.0;
         }

         this.horizontalCollision = oldXd != xd || oldZd != zd;
         this.verticalCollision = oldYd != yd;
         this.onGround = oldYd != yd && oldYd < 0.0;
         this.collision = this.horizontalCollision || this.verticalCollision;
         this.checkOnWater(false);
         this.checkFallDamage(yd, this.onGround);
         if (oldXd != xd) {
            this.xd = 0.0;
         }

         if (oldYd != yd) {
            this.yd = 0.0;
         }

         if (oldZd != zd) {
            this.zd = 0.0;
         }

         double d10 = this.x - d3;
         double d12 = this.z - d4;
         if (this.makeStepSound() && !this.isSneaking() && this.vehicle == null) {
            this.walkDist = (float)(this.walkDist + MathHelper.sqrt(d10 * d10 + d12 * d12) * 0.6);
            AABB boundingBox = AABB.getPermanentBB(this.x - 0.01, this.bb.minY - 0.01, this.z - 0.01, this.x + 0.01, this.bb.minY + 0.01, this.z + 0.01);
            int blockX = MathHelper.floor(this.x);
            int blockY = MathHelper.floor(boundingBox.minY);
            int blockZ = MathHelper.floor(this.z);
            Block<?> blockWalkedOn = null;

            label252:
            for (int x = blockX - 1; x <= blockX + 1; x++) {
               for (int z = blockZ - 1; z <= blockZ + 1; z++) {
                  for (int y = blockY - 1; y < blockY + 1; y++) {
                     Block<?> block = this.world.getBlock(x, y, z);
                     if (block != null) {
                        this.world.collidingBoundingBoxes.clear();
                        block.getCollidingBoundingBoxes(this.world, x, y, z, boundingBox, this.world.collidingBoundingBoxes);
                        if (!this.world.collidingBoundingBoxes.isEmpty()) {
                           blockWalkedOn = block;
                           blockX = x;
                           blockY = y;
                           blockZ = z;
                           break label252;
                        }
                     }
                  }
               }
            }

            int blockYUp = MathHelper.floor(boundingBox.maxY);
            Block<?> blockAbove = this.world.getBlock(blockX, blockYUp, blockZ);
            if (blockAbove != null && blockAbove.hasTag(BlockTags.OVERRIDE_STEPSOUND)) {
               blockWalkedOn = blockAbove;
               blockY = blockYUp;
            }

            this.isWalking = this.walkDistO != this.walkDist;
            if (this.canInteract()) {
               int walkedSteps = (int)(this.walkDist - this.nextStep);
               if (walkedSteps > 0 && blockWalkedOn != null) {
                  this.nextStep += walkedSteps;
                  if (!this.muteStepSounds) {
                     this.world.playBlockSoundEffect(this, this.x, this.bb.minY, this.z, blockWalkedOn, EnumBlockSoundEffectType.STEP);
                     blockWalkedOn.onEntityWalking(this.world, blockX, blockY, blockZ, this);
                  }
               }
            }
         }

         int minX = MathHelper.floor(this.bb.minX + 0.001);
         int minY = MathHelper.floor(this.bb.minY + 0.001);
         int minZ = MathHelper.floor(this.bb.minZ + 0.001);
         int maxX = MathHelper.floor(this.bb.maxX - 0.001);
         int maxY = MathHelper.floor(this.bb.maxY - 0.001);
         int maxZ = MathHelper.floor(this.bb.maxZ - 0.001);
         if (this.world.areBlocksLoaded(minX, minY, minZ, maxX, maxY, maxZ)) {
            for (int _x = minX; _x <= maxX; _x++) {
               for (int _y = minY; _y <= maxY; _y++) {
                  for (int _z = minZ; _z <= maxZ; _z++) {
                     int blockId = this.world.getBlockId(_x, _y, _z);
                     if (blockId > 0) {
                        Blocks.blocksList[blockId].onEntityCollidedWithBlock(this.world, _x, _y, _z, this);
                     }
                  }
               }
            }
         }

         boolean inWaterOrRain = this.isInWaterOrRain();
         if (this.world.isBoundingBoxBurning(this.bb.getInsetBoundingBox(0.001, 0.001, 0.001))) {
            this.burn(1);
            if (!inWaterOrRain) {
               this.remainingFireTicks++;
               if (this.remainingFireTicks == 0) {
                  this.remainingFireTicks = 300;
               }

               this.maxFireTicks = this.remainingFireTicks;
            }
         } else if (this.remainingFireTicks <= 0) {
            this.remainingFireTicks = -this.fireImmuneTicks;
         }

         if (inWaterOrRain && this.remainingFireTicks > 0) {
            this.world.playSoundAtEntity(null, this, "random.fizz", 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
            this.remainingFireTicks = -this.fireImmuneTicks;
         }
      } else {
         this.onGround = false;
         this.bb.move(xd, yd, zd);
         this.x = (this.bb.minX + this.bb.maxX) / 2.0;
         this.y = this.bb.minY + this.heightOffset - this.ySlideOffset;
         this.z = (this.bb.minZ + this.bb.maxZ) / 2.0;
      }
   }

   public void fling(double xd, double yd, double zd, float pushTime) {
      this.pushesThisTick++;
      this.fallDistance = 0.0F;
      double scalar = Math.sqrt(this.pushesThisTick) - Math.sqrt(this.pushesThisTick - 1);
      this.xd += xd * scalar;
      this.yd += yd * scalar;
      this.zd += zd * scalar;
      if (pushTime > this.pushTime) {
         this.pushTime = pushTime;
      }
   }

   public boolean canInteract() {
      return true;
   }

   protected boolean makeStepSound() {
      return true;
   }

   protected void checkFallDamage(double yd, boolean onGround) {
      if (this.vehicle != null || this.noPhysics) {
         this.fallDistance = 0.0F;
      } else if (onGround) {
         if (this.fallDistance > 0.0F) {
            this.causeFallDamage(this.fallDistance);
            this.fallDistance = 0.0F;
         }
      } else if (yd < 0.0) {
         this.fallDistance -= (float)yd;
      }
   }

   public AABB getBb() {
      return null;
   }

   protected void burn(int damage) {
      if (!this.fireImmune) {
         this.hurt(null, damage, DamageType.FIRE);
      }
   }

   protected void causeFallDamage(float distance) {
      if (this.passenger != null) {
         this.passenger.causeFallDamage(distance);
      }
   }

   public boolean isInWaterOrRain() {
      return this.world == null
         ? false
         : this.wasInWater || this.world.canBlockBeRainedOn(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z));
   }

   public boolean isInWater() {
      return this.wasInWater;
   }

   public boolean checkAndHandleWater(boolean addVelocity) {
      return this.world == null
         ? false
         : this.world.handleMaterialAcceleration(this.bb.getInsetBoundingBox(0.001, 0.001, 0.001), Material.water, this, addVelocity);
   }

   public boolean isUnderLiquid(Material material) {
      if (this.world == null) {
         return false;
      } else {
         double d = this.y + this.getHeadHeight();
         int i = MathHelper.floor(this.x);
         int j = MathHelper.floor_float(MathHelper.floor(d));
         int k = MathHelper.floor(this.z);
         int l = this.world.getBlockId(i, j, k);
         if (l != 0 && Blocks.blocksList[l].getMaterial() == material) {
            float f = BlockLogicFluid.getWaterVolume(this.world.getBlockMetadata(i, j, k)) - 0.2F;
            float f1 = j + 1 - f;
            return d < f1;
         } else {
            return false;
         }
      }
   }

   public float getHeadHeight() {
      return 0.0F;
   }

   public boolean isInLava() {
      return this.world == null ? false : this.world.isMaterialInBB(this.bb, Material.lava);
   }

   public void moveRelative(float f, float f1, float f2) {
      float f3 = MathHelper.sqrt_float(f * f + f1 * f1);
      if (!(f3 < 0.01F)) {
         if (f3 < 1.0F) {
            f3 = 1.0F;
         }

         f3 = f2 / f3;
         f *= f3;
         f1 *= f3;
         float sinYaw = MathHelper.sin(this.yRot * (float) Math.PI / 180.0F);
         float cosYaw = MathHelper.cos(this.yRot * (float) Math.PI / 180.0F);
         this.xd += f * cosYaw - f1 * sinYaw;
         this.zd += f1 * cosYaw + f * sinYaw;
      }
   }

   public float getBrightness(float partialTick) {
      if (Global.accessor.isFullbrightEnabled()) {
         return 1.0F;
      } else {
         int i = MathHelper.floor(this.x);
         double d = (this.bb.maxY - this.bb.minY) * 0.66;
         int j = MathHelper.floor(this.y - this.heightOffset + d);
         int k = MathHelper.floor(this.z);
         if (this.world != null
            && this.world
               .areBlocksLoaded(
                  MathHelper.floor(this.bb.minX),
                  MathHelper.floor(this.bb.minY),
                  MathHelper.floor(this.bb.minZ),
                  MathHelper.floor(this.bb.maxX),
                  MathHelper.floor(this.bb.maxY),
                  MathHelper.floor(this.bb.maxZ)
               )) {
            float f1 = this.world.getLightBrightness(i, j, k);
            if (f1 < this.entityBrightness) {
               f1 = this.entityBrightness;
            }

            return f1;
         } else {
            return this.entityBrightness;
         }
      }
   }

   public int getLightmapCoord(float partialTick) {
      if (this.world == null) {
         return -1;
      } else {
         int blockX = MathHelper.floor(this.x);
         int blockY = MathHelper.floor(this.y - this.heightOffset + this.ySlideOffset + this.bbHeight * 0.66);
         int blockZ = MathHelper.floor(this.z);
         int skylight;
         int blocklight;
         if (this.world.getBlockLitInteriorSurface(blockX, blockY, blockZ)) {
            int bU = this.world.getSavedLightValue(LightLayer.Block, blockX, blockY + 1, blockZ);
            int bW = this.world.getSavedLightValue(LightLayer.Block, blockX + 1, blockY, blockZ);
            int bE = this.world.getSavedLightValue(LightLayer.Block, blockX - 1, blockY, blockZ);
            int bS = this.world.getSavedLightValue(LightLayer.Block, blockX, blockY, blockZ + 1);
            int bN = this.world.getSavedLightValue(LightLayer.Block, blockX, blockY, blockZ - 1);
            if (bW > bU) {
               bU = bW;
            }

            if (bE > bU) {
               bU = bE;
            }

            if (bS > bU) {
               bU = bS;
            }

            if (bN > bU) {
               bU = bN;
            }

            blocklight = bU;
            int sU = this.world.getSavedLightValue(LightLayer.Sky, blockX, blockY + 1, blockZ);
            int sW = this.world.getSavedLightValue(LightLayer.Sky, blockX + 1, blockY, blockZ);
            int sE = this.world.getSavedLightValue(LightLayer.Sky, blockX - 1, blockY, blockZ);
            int sS = this.world.getSavedLightValue(LightLayer.Sky, blockX, blockY, blockZ + 1);
            int sN = this.world.getSavedLightValue(LightLayer.Sky, blockX, blockY, blockZ - 1);
            if (sW > sU) {
               sU = sW;
            }

            if (sE > sU) {
               sU = sE;
            }

            if (sS > sU) {
               sU = sS;
            }

            if (sN > sU) {
               sU = sN;
            }

            skylight = sU;
         } else {
            skylight = this.world.getSavedLightValue(LightLayer.Sky, blockX, blockY, blockZ);
            blocklight = this.world.getSavedLightValue(LightLayer.Block, blockX, blockY, blockZ);
         }

         return this.world.getLightmapCoord(skylight, blocklight);
      }
   }

   public void setWorld(@Nullable World world) {
      this.world = world;
   }

   public void absMoveTo(double x, double y, double z, float yRot, float xRot) {
      this.xo = this.x = x;
      this.yo = this.y = y;
      this.zo = this.z = z;
      this.yRotO = this.yRot = yRot;
      this.xRotO = this.xRot = xRot;
      this.ySlideOffset = 0.0F;
      double d3 = this.yRotO - yRot;
      if (d3 < -180.0) {
         this.yRotO += 360.0F;
      }

      if (d3 >= 180.0) {
         this.yRotO -= 360.0F;
      }

      this.setPos(this.x, this.y, this.z);
      this.setRot(yRot, xRot);
   }

   public void moveTo(double x, double y, double z, float yRot, float xRot) {
      this.xo = this.x = x;
      this.yo = this.y = y + this.heightOffset;
      this.zo = this.z = z;
      this.yRot = yRot;
      this.xRot = xRot;
      this.setPos(this.x, this.y, this.z);
   }

   public float distanceTo(Entity entity) {
      float f = (float)(this.x - entity.x);
      float f1 = (float)(this.y - entity.y);
      float f2 = (float)(this.z - entity.z);
      return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
   }

   public double distanceToSqr(double d, double d1, double d2) {
      double d3 = this.x - d;
      double d4 = this.y - d1;
      double d5 = this.z - d2;
      return d3 * d3 + d4 * d4 + d5 * d5;
   }

   public double distanceTo(double d, double d1, double d2) {
      double d3 = this.x - d;
      double d4 = this.y - d1;
      double d5 = this.z - d2;
      return MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
   }

   public double distanceToSqr(Entity entity) {
      double d = this.x - entity.x;
      double d1 = this.y - entity.y;
      double d2 = this.z - entity.z;
      return d * d + d1 * d1 + d2 * d2;
   }

   public void playerTouch(Player player) {
   }

   public boolean collidesWith(Entity entity) {
      return true;
   }

   public boolean collidesWithBlock(Block<?> block, int metadata) {
      return !(this.pushTime > 0.5F)
         || !(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd > 1.0)
         || !Block.hasLogicClass(block, BlockLogicPistonHead.class);
   }

   public void push(Entity entity) {
      if (entity.passenger != this
         && entity.vehicle != this
         && !entity.noPhysics
         && entity.isPickable()
         && this.collidesWith(entity)
         && entity.collidesWith(this)) {
         double d = entity.x - this.x;
         double d1 = entity.z - this.z;
         double d2 = MathHelper.abs_max(d, d1);
         if (d2 >= 0.01) {
            d2 = MathHelper.sqrt(d2);
            d /= d2;
            d1 /= d2;
            double d3 = 1.0 / d2;
            if (d3 > 1.0) {
               d3 = 1.0;
            }

            d *= d3;
            d1 *= d3;
            d *= 0.05;
            d1 *= 0.05;
            d *= 1.0F - this.pushthrough;
            d1 *= 1.0F - this.pushthrough;
            this.push(-d, 0.0, -d1);
            entity.push(d, 0.0, d1);
         }
      }
   }

   public void push(double x, double y, double z) {
      this.xd += x;
      this.yd += y;
      this.zd += z;
   }

   protected void markHurt() {
      this.hurtMarked = true;
   }

   public boolean hurt(Entity attacker, int baseDamage, DamageType type) {
      this.markHurt();
      return false;
   }

   public boolean isPickable() {
      return false;
   }

   public boolean isSelectable() {
      return this.isPickable();
   }

   public boolean isPushable() {
      return false;
   }

   public void awardKillScore(Entity entity, int i) {
   }

   public boolean shouldRender(Vec3 vec3) {
      double d = this.x - vec3.x;
      double d1 = this.y - vec3.y;
      double d2 = this.z - vec3.z;
      double d3 = d * d + d1 * d1 + d2 * d2;
      return this.shouldRenderAtSqrDistance(d3);
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      double d1 = this.bb.getSize();
      d1 *= 64.0 * this.viewScale;
      return distance < d1 * d1;
   }

   @Nullable
   public String getEntityTexture() {
      return null;
   }

   public boolean save(@NotNull CompoundTag tag) {
      NamespaceID s = this.getEncodeId();
      if (!this.removed && s != null) {
         tag.putString("id", s.toString());
         this.saveWithoutId(tag);
         return true;
      } else {
         return false;
      }
   }

   public void saveWithoutId(@NotNull CompoundTag tag) {
      tag.put("Pos", this.newDoubleList(new double[]{this.x, this.y + this.ySlideOffset, this.z}));
      tag.put("Motion", this.newDoubleList(new double[]{this.xd, this.yd, this.zd}));
      tag.put("Rotation", this.newFloatList(new float[]{this.yRot, this.xRot}));
      tag.putFloat("FallDistance", this.fallDistance);
      tag.putShort("Fire", (short)this.remainingFireTicks);
      tag.putShort("Air", (short)this.airSupply);
      tag.putBoolean("OnGround", this.onGround);
      tag.putFloat("PushTime", this.pushTime);
      this.addAdditionalSaveData(tag);
   }

   public void load(@NotNull CompoundTag tag) {
      ListTag posTag = tag.getList("Pos");
      ListTag motionTag = tag.getList("Motion");
      ListTag rotationTag = tag.getList("Rotation");
      this.xd = ((DoubleTag)motionTag.tagAt(0)).getValue();
      this.yd = ((DoubleTag)motionTag.tagAt(1)).getValue();
      this.zd = ((DoubleTag)motionTag.tagAt(2)).getValue();
      if (MathHelper.abs((float)this.xd) > 10.0) {
         this.xd = 0.0;
      }

      if (MathHelper.abs((float)this.yd) > 10.0) {
         this.yd = 0.0;
      }

      if (MathHelper.abs((float)this.zd) > 10.0) {
         this.zd = 0.0;
      }

      this.xo = this.x = ((DoubleTag)posTag.tagAt(0)).getValue();
      this.yo = this.y = ((DoubleTag)posTag.tagAt(1)).getValue();
      this.zo = this.z = ((DoubleTag)posTag.tagAt(2)).getValue();
      this.yRotO = this.yRot = ((FloatTag)rotationTag.tagAt(0)).getValue();
      this.xRotO = this.xRot = ((FloatTag)rotationTag.tagAt(1)).getValue();
      this.fallDistance = tag.getFloat("FallDistance");
      this.remainingFireTicks = tag.getShort("Fire");
      this.maxFireTicks = this.remainingFireTicks;
      this.airSupply = tag.getShort("Air");
      this.onGround = tag.getBoolean("OnGround");
      this.pushTime = tag.getFloat("PushTime");
      this.setPos(this.x, this.y, this.z);
      this.setRot(this.yRot, this.xRot);
      this.readAdditionalSaveData(tag);
   }

   @Nullable
   protected final NamespaceID getEncodeId() {
      return EntityDispatcher.idForClass((Class<? extends Entity>)this.getClass());
   }

   public abstract void readAdditionalSaveData(@NotNull CompoundTag var1);

   public abstract void addAdditionalSaveData(@NotNull CompoundTag var1);

   @NotNull
   protected ListTag newDoubleList(double[] array) {
      ListTag list = new ListTag();

      for (double d : array) {
         list.addTag(new DoubleTag(d));
      }

      return list;
   }

   @NotNull
   protected ListTag newFloatList(float[] array) {
      ListTag list = new ListTag();

      for (float f : array) {
         list.addTag(new FloatTag(f));
      }

      return list;
   }

   public float getShadowHeightOffs() {
      return this.bbHeight / 2.0F;
   }

   @NotNull
   public EntityItem dropItem(int itemId, int stackSize) {
      return this.dropItem(itemId, stackSize, 0.0F);
   }

   @NotNull
   public EntityItem dropItem(int itemId, int stackSize, float verticalOffset) {
      return this.dropItem(new ItemStack(itemId, stackSize, 0), verticalOffset);
   }

   @NotNull
   public EntityItem dropItem(@NotNull ItemStack itemStack, float verticalOffset) {
      EntityItem entityitem = new EntityItem(this.world, this.x, this.y + verticalOffset, this.z, itemStack);
      entityitem.pickupDelay = 10;
      if (this.world != null) {
         this.world.entityJoinedWorld(entityitem);
      }

      return entityitem;
   }

   public boolean isAlive() {
      return !this.removed;
   }

   public boolean isInWall() {
      if (this.world == null) {
         return false;
      } else {
         for (int i = 0; i < 8; i++) {
            float f = ((i >> 0) % 2 - 0.5F) * this.bbWidth * 0.9F;
            float f1 = ((i >> 1) % 2 - 0.5F) * 0.1F;
            float f2 = ((i >> 2) % 2 - 0.5F) * this.bbWidth * 0.9F;
            int x = MathHelper.floor(this.x + f);
            int y = MathHelper.floor(this.y + this.getHeadHeight() + f1);
            int z = MathHelper.floor(this.z + f2);
            if (this.world.isBlockNormalCube(x, y, z)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean interact(@NotNull Player player) {
      return false;
   }

   public void rideTick() {
      if (this.vehicle != null) {
         if (!this.vehicle.isRemoved() && !this.isSneaking()) {
            this.xd = 0.0;
            this.yd = 0.0;
            this.zd = 0.0;
            this.tick();
            if (this.vehicle != null) {
               this.vehicle.positionRider();
               this.entityRiderYawDelta = MathHelper.normalizeRotation(this.entityRiderYawDelta + this.vehicle.getYRotDelta());
               this.entityRiderPitchDelta = MathHelper.normalizeRotation(this.entityRiderPitchDelta + this.vehicle.getXRotDelta());
               float maxRot = 10.0F;
               double yaw = MathHelper.clamp(this.entityRiderYawDelta / 2.0, -10.0, 10.0);
               double pitch = MathHelper.clamp(this.entityRiderPitchDelta / 2.0, -10.0, 10.0);
               this.entityRiderYawDelta -= yaw;
               if (this.turnWithVehicle) {
                  this.yRot = (float)(this.yRot + yaw);
               }
            }
         } else {
            this.vehicle.ejectRider();
         }
      }
   }

   @Override
   public void positionRider() {
      if (this.passenger != null) {
         this.passenger.setPos(this.x, this.y + this.getRideHeight() + this.passenger.getRidingHeight(), this.z);
      }
   }

   public double getRidingHeight() {
      return this.heightOffset;
   }

   public double getRideHeight() {
      return this.bbHeight * 0.75;
   }

   public void startRiding(IVehicle vehicle) {
      if (this.canRide()) {
         if (this.vehicle != vehicle) {
            this.entityRiderPitchDelta = 0.0;
            this.entityRiderYawDelta = 0.0;
            if (vehicle == null) {
               if (this.vehicle != null) {
                  this.vehicle.moveExitingEntity(this);
                  this.vehicle.setPassenger(null);
               }

               this.vehicle = null;
            } else {
               if (this.vehicle != null) {
                  this.vehicle.setPassenger(null);
               }

               if (vehicle.getPassenger() != null) {
                  vehicle.getPassenger().vehicle = null;
               }

               this.vehicle = vehicle;
               vehicle.setPassenger(this);
            }
         }
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
         entity.moveTo(this.x, this.bb.minY, this.z, entity.yRot, entity.xRot);
         return entity;
      }
   }

   public void lerpTo(double x, double y, double z, float yRot, float xRot, int i) {
      this.setPos(x, y, z);
      this.setRot(yRot, xRot);
      if (this.world != null) {
         List<AABB> cubes = this.world.getCubes(this, this.bb.getInsetBoundingBox(0.03125, 0.0, 0.03125));
         if (!cubes.isEmpty()) {
            double d3 = 0.0;

            for (AABB cube : cubes) {
               if (cube.maxY > d3) {
                  d3 = cube.maxY;
               }
            }

            y += d3 - this.bb.minY;
            this.setPos(x, y, z);
         }
      }
   }

   public float getPickRadius() {
      return 0.1F;
   }

   public Vec3 getLookAngle() {
      return null;
   }

   public void handlePortal(int portalBlockId, DyeColor portalColor) {
   }

   @Nullable
   public ItemStack[] getInventory() {
      return null;
   }

   public void lerpMotion(double xd, double yd, double zd) {
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
   }

   public void handleEntityEvent(byte byte0, float attackedAtYaw) {
   }

   public void animateHurt() {
   }

   public void setEquippedSlot(int slot, int itemID, int itemMeta, CompoundTag tag) {
   }

   public void setHeldObject(@Nullable ICarriable heldObject) {
   }

   public boolean isOnFire() {
      return !this.fireImmune && this.remainingFireTicks > 0 || this.getSharedFlag(0);
   }

   public boolean isPassenger() {
      return this.vehicle != null || this.getSharedFlag(2);
   }

   public boolean isSneaking() {
      return this.getSharedFlag(1);
   }

   public void setSneaking(boolean flag) {
      this.setSharedFlag(1, flag);
   }

   protected boolean getSharedFlag(int i) {
      return (this.entityData.getByte(0) & 1 << i) != 0;
   }

   protected void setSharedFlag(int id, boolean flag) {
      byte sharedFlags = this.entityData.getByte(0);
      if (flag) {
         this.entityData.set(0, (byte)(sharedFlags | 1 << id));
      } else {
         this.entityData.set(0, (byte)(sharedFlags & ~(1 << id)));
      }
   }

   public void thunderHit(EntityLightning bolt) {
      this.burn(5);
      this.remainingFireTicks++;
      if (this.remainingFireTicks == 0) {
         this.remainingFireTicks = 300;
      }

      this.maxFireTicks = this.remainingFireTicks;
   }

   public void killed(Mob mob) {
   }

   protected boolean checkInTile(double d, double d1, double d2) {
      if (this.world == null) {
         return false;
      } else {
         int i = MathHelper.floor(d);
         int j = MathHelper.floor(d1);
         int k = MathHelper.floor(d2);
         double d3 = d - i;
         double d4 = d1 - j;
         double d5 = d2 - k;
         if (this.world.isBlockNormalCube(i, j, k)) {
            boolean flag = !this.world.isBlockNormalCube(i - 1, j, k);
            boolean flag1 = !this.world.isBlockNormalCube(i + 1, j, k);
            boolean flag2 = !this.world.isBlockNormalCube(i, j - 1, k);
            boolean flag3 = !this.world.isBlockNormalCube(i, j + 1, k);
            boolean flag4 = !this.world.isBlockNormalCube(i, j, k - 1);
            boolean flag5 = !this.world.isBlockNormalCube(i, j, k + 1);
            byte byte0 = -1;
            double d6 = 9999.0;
            if (flag && d3 < d6) {
               d6 = d3;
               byte0 = 0;
            }

            if (flag1 && 1.0 - d3 < d6) {
               d6 = 1.0 - d3;
               byte0 = 1;
            }

            if (flag2 && d4 < d6) {
               d6 = d4;
               byte0 = 2;
            }

            if (flag3 && 1.0 - d4 < d6) {
               d6 = 1.0 - d4;
               byte0 = 3;
            }

            if (flag4 && d5 < d6) {
               d6 = d5;
               byte0 = 4;
            }

            if (flag5 && 1.0 - d5 < d6) {
               double d7 = 1.0 - d5;
               byte0 = 5;
            }

            float f = this.random.nextFloat() * 0.2F + 0.1F;
            if (byte0 == 0) {
               this.xd = -f;
            }

            if (byte0 == 1) {
               this.xd = f;
            }

            if (byte0 == 2) {
               this.yd = -f;
            }

            if (byte0 == 3) {
               this.yd = f;
            }

            if (byte0 == 4) {
               this.zd = -f;
            }

            if (byte0 == 5) {
               this.zd = f;
            }
         }

         return false;
      }
   }

   public boolean canRide() {
      return true;
   }

   public boolean isSprinting() {
      return this.getSharedFlag(3);
   }

   public void setSprinting(boolean sprinting) {
      this.setSharedFlag(3, sprinting);
   }

   @Override
   public boolean isRemoved() {
      return this.removed;
   }

   @Override
   public void setPassenger(@Nullable Entity passenger) {
      this.passenger = passenger;
   }

   @Nullable
   @Override
   public Entity getPassenger() {
      return this.passenger;
   }

   @Override
   public void moveExitingEntity(@NotNull Entity entity) {
      entity.moveTo(this.x, this.bb.minY + this.bbHeight, this.z, entity.yRot, entity.xRot);
   }

   @Override
   public float getYRotDelta() {
      return (float)MathHelper.deltaAngle(this.yRot, this.yRotO);
   }

   @Override
   public float getXRotDelta() {
      return (float)MathHelper.deltaAngle(this.xRot, this.xRotO);
   }

   public boolean lerpVehicleMotion() {
      return true;
   }

   public void handleSpecialVehicleControl() {
   }

   public void sendSpecialVehiclePacket() {
   }

   public boolean deferVehicleBehavior() {
      return false;
   }

   public static String getNameFromEntity(@NotNull Entity entity, boolean useNickname) {
      I18n translator = I18n.getInstance();
      if (entity instanceof Mob && useNickname && !((Mob)entity).nickname.isEmpty()) {
         return ((Mob)entity).nickname;
      } else if (entity instanceof Player) {
         return ((Player)entity).getDisplayName();
      } else if (EntityDispatcher.nameKeyForClass((Class<? extends Entity>)entity.getClass()) != null) {
         return translator.translateKey(EntityDispatcher.nameKeyForClass((Class<? extends Entity>)entity.getClass()));
      } else {
         NamespaceID dispatcherId = EntityDispatcher.idForClass((Class<? extends Entity>)entity.getClass());
         return dispatcherId != null ? dispatcherId.value() : entity.getClass().getSimpleName();
      }
   }
}
