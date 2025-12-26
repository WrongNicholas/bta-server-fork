package net.minecraft.core.entity.vehicle;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.IntTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.ShortTag;
import com.mojang.nbt.tags.Tag;
import java.util.List;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRail;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.block.logic.RailDirection;
import net.minecraft.core.block.motion.CarriedBlock;
import net.minecraft.core.crafting.LookupFuelFurnace;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.inventory.InventorySorter;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityMinecart extends Entity implements Container {
   public static final byte PASSENGER_CART = 0;
   public static final byte CHEST_CART = 1;
   public static final byte FURNACE_CART = 2;
   public static final int TYPE_MASK_COLOR = 240;
   public static final int TYPE_MASK_PAINTED_CHEST = 1;
   public static final int DATA_TYPE = 10;
   public static final int DATA_META = 11;
   public static final int DATA_FUEL = 12;
   private static final int[][][] EXITS = new int[][][]{
      {{0, 0, -1}, {0, 0, 1}},
      {{-1, 0, 0}, {1, 0, 0}},
      {{-1, -1, 0}, {1, 0, 0}},
      {{-1, 0, 0}, {1, -1, 0}},
      {{0, 0, -1}, {0, -1, 1}},
      {{0, -1, -1}, {0, 0, 1}},
      {{0, 0, 1}, {1, 0, 0}},
      {{0, 0, 1}, {-1, 0, 0}},
      {{0, 0, -1}, {-1, 0, 0}},
      {{0, 0, -1}, {1, 0, 0}}
   };
   private ItemStack[] items = new ItemStack[36];
   public int currentDamage = 0;
   public int timeSinceHit = 0;
   public int hurtDirection = 1;
   public boolean flipped = false;
   public double xPush;
   public double zPush;
   private int lerpTicks;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private double lerpYRot;
   private double lerpXRot;
   private double lerpXD;
   private double lerpYD;
   private double lerpZD;

   public EntityMinecart(World world) {
      super(world);
      this.blocksBuilding = true;
      this.setSize(0.98F, 0.7F);
      this.heightOffset = this.bbHeight / 2.0F;
   }

   public EntityMinecart(World world, double x, double y, double z, int type) {
      this(world);
      this.setPos(x, y + this.heightOffset, z);
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      this.xo = x;
      this.yo = y;
      this.zo = z;
      this.setType((byte)type);
   }

   @Override
   public float getShadowHeightOffs() {
      return 0.0F;
   }

   @Override
   protected boolean makeStepSound() {
      return false;
   }

   @Override
   protected void defineSynchedData() {
      this.entityData.define(10, (byte)0, Byte.class);
      this.entityData.define(11, 0, Integer.class);
      this.entityData.define(12, 0, Integer.class);
   }

   public void setType(byte type) {
      this.entityData.set(10, type);
   }

   public byte getType() {
      return this.entityData.getByte(10);
   }

   public void setMeta(int meta) {
      this.entityData.set(11, meta);
   }

   public int getMeta() {
      return this.entityData.getInt(11);
   }

   public void setFuel(int fuel) {
      this.entityData.set(12, fuel);
   }

   public int getFuel() {
      return this.entityData.getInt(12);
   }

   @Override
   public boolean isPushable() {
      return true;
   }

   @Override
   public boolean isPickable() {
      return !this.removed;
   }

   @Override
   public double getRideHeight() {
      return this.bbHeight * 0.0 - 0.3;
   }

   @Override
   public Entity ejectRider() {
      Entity entity = this.passenger;
      if (entity == null) {
         return null;
      } else {
         this.passenger = null;
         entity.vehicle = null;
         int blockX = MathHelper.floor(this.x);
         int blockY = MathHelper.floor(this.y);
         int blockZ = MathHelper.floor(this.z);
         if (this.isSafe(blockX - 1, blockY, blockZ)) {
            entity.moveTo(blockX - 0.5, blockY, blockZ + 0.5, entity.yRot, entity.xRot);
         } else if (this.isSafe(blockX + 1, blockY, blockZ)) {
            entity.moveTo(blockX + 1.5, blockY, blockZ + 0.5, entity.yRot, entity.xRot);
         } else if (this.isSafe(blockX, blockY, blockZ - 1)) {
            entity.moveTo(blockX + 0.5, blockY, blockZ - 0.5, entity.yRot, entity.xRot);
         } else if (this.isSafe(blockX, blockY, blockZ + 1)) {
            entity.moveTo(blockX + 0.5, blockY, blockZ + 1.5, entity.yRot, entity.xRot);
         } else {
            entity.moveTo(blockX + 0.5, blockY + 1, blockZ + 0.5, entity.yRot, entity.xRot);
         }

         return entity;
      }
   }

   private boolean isSafe(int x, int y, int z) {
      return !this.world.isBlockNormalCube(x, y, z) && !this.world.isBlockNormalCube(x, y + 1, z);
   }

   @Override
   public boolean hurt(Entity entity, int baseDamage, DamageType type) {
      if (!this.world.isClientSide && !this.removed) {
         if (entity instanceof Player && ((Player)entity).getGamemode() == Gamemode.creative) {
            this.remove();
            return true;
         } else {
            this.hurtDirection = -this.hurtDirection;
            this.timeSinceHit = 10;
            this.markHurt();
            this.currentDamage += baseDamage * 10;
            if (this.currentDamage > 40) {
               if (this.passenger != null) {
                  this.passenger.startRiding(this);
               }

               if (!this.world.isClientSide) {
                  this.dropItem(Items.MINECART.id, 1, 0.0F);
                  switch (this.getType()) {
                     case 1:
                        if ((this.getMeta() & 1) != 0) {
                           this.dropItem(new ItemStack(Blocks.CHEST_PLANKS_OAK_PAINTED, 1, this.getMeta() & 240), 0.0F);
                        } else {
                           this.dropItem(Blocks.CHEST_PLANKS_OAK.id(), 1, 0.0F);
                        }
                        break;
                     case 2:
                        this.dropItem(Blocks.FURNACE_STONE_IDLE.id(), 1, 0.0F);
                  }
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
   public void animateHurt() {
      this.hurtDirection = -this.hurtDirection;
      this.timeSinceHit = 10;
      this.currentDamage = this.currentDamage + this.currentDamage * 10;
   }

   @Override
   public void remove() {
      if (!this.world.isClientSide) {
         for (int j = 0; j < this.getContainerSize(); j++) {
            ItemStack itemStack = this.getItem(j);
            if (itemStack != null) {
               float range = 0.7F;
               double x1 = this.random.nextFloat() * 0.7F + 0.15F;
               double y1 = this.random.nextFloat() * 0.7F + 0.15F;
               double z1 = this.random.nextFloat() * 0.7F + 0.15F;
               EntityItem entityItem = new EntityItem(this.world, this.x + x1, this.y + y1, this.z + z1, itemStack);
               entityItem.pickupDelay = 10;
               this.world.entityJoinedWorld(entityItem);
               entityItem.xd *= 0.5;
               entityItem.yd *= 0.5;
               entityItem.zd *= 0.5;
               entityItem.pickupDelay = 0;
            }
         }
      }

      this.ejectRider();
      super.remove();
   }

   @Override
   public void tick() {
      if (this.timeSinceHit > 0) {
         this.timeSinceHit--;
      }

      if (this.currentDamage > 0) {
         this.currentDamage--;
      }

      this.pushesThisTick = 0;
      this.pushTime *= 0.98F;
      if (this.pushTime < 0.05F || this.pushTime < 0.25 && this.onGround) {
         this.pushTime = 0.0F;
      }

      double _xo = this.x;
      double _yo = this.y;
      double _zo = this.z;
      float _yRot = this.yRot;
      float _xRot = this.xRot;
      if (this.world.isClientSide) {
         if (this.lerpTicks > 0) {
            double newX = this.x + (this.lerpX - this.x) / this.lerpTicks;
            double newY = this.y + (this.lerpY - this.y) / this.lerpTicks;
            double newZ = this.z + (this.lerpZ - this.z) / this.lerpTicks;
            this.yRot = this.yRot + (float)(MathHelper.normalizeRotation(this.lerpYRot - this.yRot) / this.lerpTicks);
            this.xRot = this.xRot + (float)(MathHelper.normalizeRotation(this.lerpXRot - this.xRot) / this.lerpTicks);
            this.lerpTicks--;
            this.setPos(newX, newY, newZ);
            this.setRot(this.yRot, this.xRot);
         }
      } else {
         this.motionIteration();
         this.motionIteration();
      }

      this.xo = _xo;
      this.yo = _yo;
      this.zo = _zo;
      this.yRotO = _yRot;
      this.xRotO = _xRot;
   }

   public void motionIteration() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.yd -= 0.04;
      int blockX = MathHelper.floor(this.x);
      int blockY = MathHelper.floor(this.y);
      int blockZ = MathHelper.floor(this.z);
      if (BlockLogicRail.isRailBlockAt(this.world, blockX, blockY - 1, blockZ)) {
         blockY--;
      }

      double maxSpeed = 0.4;
      boolean didFurnacePush = false;
      double d5 = 0.0078125;
      Block<?> block = this.world.getBlock(blockX, blockY, blockZ);
      if (block != null && block.getLogic() instanceof BlockLogicRail) {
         BlockLogicRail rail = (BlockLogicRail)block.getLogic();
         Vec3 vec3d = this.getPos(this.x, this.y, this.z);
         this.y = blockY;
         boolean onPoweredPoweredRail = false;
         boolean onUnpoweredPoweredRail = false;
         if (block == Blocks.RAIL_POWERED) {
            onPoweredPoweredRail = (this.world.getBlockMetadata(blockX, blockY, blockZ) & 8) != 0;
            onUnpoweredPoweredRail = !onPoweredPoweredRail;
         }

         RailDirection railDirection = rail.getRailDirection(this.world, blockX, blockY, blockZ);
         if (railDirection.isSloped()) {
            this.y = blockY + 1;
         }

         if (railDirection == RailDirection.SLOPE_E) {
            this.xd -= 0.0078125;
         }

         if (railDirection == RailDirection.SLOPE_W) {
            this.xd += 0.0078125;
         }

         if (railDirection == RailDirection.SLOPE_N) {
            this.zd += 0.0078125;
         }

         if (railDirection == RailDirection.SLOPE_S) {
            this.zd -= 0.0078125;
         }

         int[][] nextRailTable = EXITS[railDirection.meta];
         double d9 = nextRailTable[1][0] - nextRailTable[0][0];
         double d10 = nextRailTable[1][2] - nextRailTable[0][2];
         double d11 = Math.sqrt(d9 * d9 + d10 * d10);
         double d12 = this.xd * d9 + this.zd * d10;
         if (d12 < 0.0) {
            d9 = -d9;
            d10 = -d10;
         }

         double vel = Math.hypot(this.xd, this.zd);
         this.xd = vel * d9 / d11;
         this.zd = vel * d10 / d11;
         if (onUnpoweredPoweredRail) {
            if (Math.hypot(this.xd, this.zd) < 0.03) {
               this.xd *= 0.0;
               this.yd *= 0.0;
               this.zd *= 0.0;
            } else {
               this.xd *= 0.5;
               this.yd *= 0.0;
               this.zd *= 0.5;
            }
         }

         double d18 = blockX + 0.5 + nextRailTable[0][0] * 0.5;
         double d19 = blockZ + 0.5 + nextRailTable[0][2] * 0.5;
         double d20 = blockX + 0.5 + nextRailTable[1][0] * 0.5;
         double d21 = blockZ + 0.5 + nextRailTable[1][2] * 0.5;
         d9 = d20 - d18;
         d10 = d21 - d19;
         double d17;
         if (d9 == 0.0) {
            d17 = this.z - blockZ;
         } else if (d10 == 0.0) {
            d17 = this.x - blockX;
         } else {
            double d22 = this.x - d18;
            double d24 = this.z - d19;
            d17 = (d22 * d9 + d24 * d10) * 2.0;
         }

         this.x = d18 + d9 * d17;
         this.z = d19 + d10 * d17;
         this.setPos(this.x, this.y + this.heightOffset, this.z);
         if (this.passenger != null && this.passenger instanceof Player && Math.hypot(this.xd, this.zd) < 0.01F) {
            this.xd = this.xd + this.passenger.xd * 0.05;
            this.zd = this.zd + this.passenger.zd * 0.05;
         }

         double _xd = this.xd;
         double _zd = this.zd;
         if (this.passenger != null) {
            _xd *= 0.75;
            _zd *= 0.75;
         }

         _xd = MathHelper.clamp(_xd, -0.4, 0.4);
         _zd = MathHelper.clamp(_zd, -0.4, 0.4);
         this.move(_xd, 0.0, _zd);
         if (nextRailTable[0][1] != 0 && MathHelper.floor(this.x) - blockX == nextRailTable[0][0] && MathHelper.floor(this.z) - blockZ == nextRailTable[0][2]) {
            this.setPos(this.x, this.y + nextRailTable[0][1], this.z);
         } else if (nextRailTable[1][1] != 0
            && MathHelper.floor(this.x) - blockX == nextRailTable[1][0]
            && MathHelper.floor(this.z) - blockZ == nextRailTable[1][2]) {
            this.setPos(this.x, this.y + nextRailTable[1][1], this.z);
         }

         if (this.passenger != null) {
            this.xd *= 0.997;
            this.yd *= 0.0;
            this.zd *= 0.997;
         } else {
            if (this.getType() == 2) {
               vel = Math.hypot(this.xPush, this.zPush);
               if (vel > 0.01) {
                  didFurnacePush = true;
                  this.xPush /= vel;
                  this.zPush /= vel;
                  this.xd *= 0.8;
                  this.yd *= 0.0;
                  this.zd *= 0.8;
                  double pushScalar = 0.04;
                  this.xd = this.xd + this.xPush * 0.04;
                  this.zd = this.zd + this.zPush * 0.04;
               } else {
                  this.xd *= 0.9;
                  this.yd *= 0.0;
                  this.zd *= 0.9;
               }
            }

            this.xd *= 0.96;
            this.yd *= 0.0;
            this.zd *= 0.96;
         }

         Vec3 vec3d1 = this.getPos(this.x, this.y, this.z);
         if (vec3d1 != null && vec3d != null) {
            double slopeSpeedOffset = (vec3d.y - vec3d1.y) * 0.05;
            vel = Math.hypot(this.xd, this.zd);
            if (vel > 0.0) {
               this.xd = this.xd / vel * (vel + slopeSpeedOffset);
               this.zd = this.zd / vel * (vel + slopeSpeedOffset);
            }

            this.setPos(this.x, vec3d1.y, this.z);
         }

         int newBlockX = MathHelper.floor(this.x);
         int newBlockZ = MathHelper.floor(this.z);
         if (newBlockX != blockX || newBlockZ != blockZ) {
            vel = Math.hypot(this.xd, this.zd);
            this.xd = vel * (newBlockX - blockX);
            this.zd = vel * (newBlockZ - blockZ);
         }

         if (this.getType() == 2) {
            double pushMagnitude = Math.hypot(this.xPush, this.zPush);
            if (pushMagnitude > 0.01 && Math.hypot(this.xd, this.zd) > 0.001) {
               this.xPush = this.xd;
               this.zPush = this.zd;
            }
         }

         if (onPoweredPoweredRail) {
            vel = Math.hypot(this.xd, this.zd);
            if (vel > 0.01) {
               double scalar = 0.06;
               this.xd = this.xd + this.xd / vel * 0.06;
               this.zd = this.zd + this.zd / vel * 0.06;
            } else if (railDirection == RailDirection.STRAIGHT_EW) {
               if (this.world.isBlockNormalCube(blockX - 1, blockY, blockZ)) {
                  this.xd = 0.02;
               } else if (this.world.isBlockNormalCube(blockX + 1, blockY, blockZ)) {
                  this.xd = -0.02;
               }
            } else if (railDirection == RailDirection.STRAIGHT_NS) {
               if (this.world.isBlockNormalCube(blockX, blockY, blockZ - 1)) {
                  this.zd = 0.02;
               } else if (this.world.isBlockNormalCube(blockX, blockY, blockZ + 1)) {
                  this.zd = -0.02;
               }
            }
         }
      } else {
         this.xd = MathHelper.clamp(this.xd, -0.4, 0.4);
         this.zd = MathHelper.clamp(this.zd, -0.4, 0.4);
         if (this.onGround) {
            this.xd *= 0.5;
            this.yd *= 0.5;
            this.zd *= 0.5;
         }

         this.move(this.xd, this.yd, this.zd);
         if (!this.onGround) {
            this.xd *= 0.95;
            this.yd *= 0.95;
            this.zd *= 0.95;
         }
      }

      this.xRot = 0.0F;
      double diffX = this.xo - this.x;
      double diffZ = this.zo - this.z;
      if (Math.hypot(diffX, diffZ) > 0.001) {
         this.yRot = (float)Math.toDegrees(Math.atan2(diffZ, diffX));
         if (this.flipped) {
            this.yRot += 180.0F;
         }
      }

      double diffYRot = MathHelper.normalizeRotation(this.yRot - this.yRotO);
      if (diffYRot < -170.0 || diffYRot >= 170.0) {
         this.yRot += 180.0F;
         this.flipped = !this.flipped;
      }

      this.setRot(this.yRot, this.xRot);
      List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.bb.expand(0.2, 0.0, 0.2));
      if (list != null && !list.isEmpty()) {
         for (Entity entity : list) {
            if (entity instanceof EntityMinecart && entity != this.passenger && entity.isPushable()) {
               entity.push(this);
            }
         }
      }

      if (this.passenger != null && this.passenger.removed) {
         this.passenger = null;
      }

      if (didFurnacePush && this.random.nextInt(4) == 0) {
         this.setFuel(this.getFuel() - 1);
         if (this.getFuel() < 0) {
            this.xPush = 0.0;
            this.zPush = 0.0;
         }

         this.world.spawnParticle("largesmoke", this.x, this.y + 0.8, this.z, 0.0, 0.0, 0.0, 0);
      }
   }

   public Vec3 getPosOffs(double x, double y, double z, double scalar) {
      int blockX = MathHelper.floor(x);
      int blockY = MathHelper.floor(y);
      int blockZ = MathHelper.floor(z);
      if (BlockLogicRail.isRailBlockAt(this.world, blockX, blockY - 1, blockZ)) {
         blockY--;
      }

      Block<?> block = this.world.getBlock(blockX, blockY, blockZ);
      if (block != null && block.getLogic() instanceof BlockLogicRail) {
         BlockLogicRail rail = (BlockLogicRail)block.getLogic();
         int meta = this.world.getBlockMetadata(blockX, blockY, blockZ);
         if (rail.getIsPowered()) {
            meta &= 7;
         }

         if (meta >= 2 && meta <= 5) {
            blockY++;
         }

         int[][] ai = EXITS[meta];
         double d4 = ai[1][0] - ai[0][0];
         double d5 = ai[1][2] - ai[0][2];
         double d6 = Math.sqrt(d4 * d4 + d5 * d5);
         d4 /= d6;
         d5 /= d6;
         double vecX = x + d4 * scalar;
         double vecZ = z + d5 * scalar;
         double vecY = blockY;
         if (ai[0][1] != 0 && MathHelper.floor(x) - blockX == ai[0][0] && MathHelper.floor(z) - blockZ == ai[0][2]) {
            vecY += ai[0][1];
         } else if (ai[1][1] != 0 && MathHelper.floor(x) - blockX == ai[1][0] && MathHelper.floor(z) - blockZ == ai[1][2]) {
            vecY += ai[1][1];
         }

         return this.getPos(vecX, vecY, vecZ);
      } else {
         return null;
      }
   }

   @Nullable
   public Vec3 getPos(double x, double y, double z) {
      int blockX = MathHelper.floor(x);
      int blockY = MathHelper.floor(y);
      int blockZ = MathHelper.floor(z);
      if (BlockLogicRail.isRailBlockAt(this.world, blockX, blockY - 1, blockZ)) {
         blockY--;
      }

      Block<?> block = this.world.getBlock(blockX, blockY, blockZ);
      if (!Block.hasLogicClass(block, BlockLogicRail.class)) {
         return null;
      } else {
         BlockLogicRail rail = (BlockLogicRail)block.getLogic();
         RailDirection direction = rail.getRailDirection(this.world, blockX, blockY, blockZ);
         int[][] offsetMap = EXITS[direction.meta];
         double x1 = blockX + 0.5 + offsetMap[0][0] * 0.5;
         double y1 = blockY + 0.5 + offsetMap[0][1] * 0.5;
         double z1 = blockZ + 0.5 + offsetMap[0][2] * 0.5;
         double x2 = blockX + 0.5 + offsetMap[1][0] * 0.5;
         double y2 = blockY + 0.5 + offsetMap[1][1] * 0.5;
         double z2 = blockZ + 0.5 + offsetMap[1][2] * 0.5;
         double railsDX = x2 - x1;
         double railsDY = (y2 - y1) * 2.0;
         double railsDZ = z2 - z1;
         double proximity;
         if (railsDX == 0.0) {
            proximity = z - blockZ;
         } else if (railsDZ == 0.0) {
            proximity = x - blockX;
         } else {
            double targetDX = x - x1;
            double targetDZ = z - z1;
            proximity = (targetDX * railsDX + targetDZ * railsDZ) * 2.0;
         }

         double vecX = x1 + railsDX * proximity;
         double vecY = y1 + railsDY * proximity;
         double vecZ = z1 + railsDZ * proximity;
         if (railsDY < 0.0) {
            vecY++;
         }

         if (railsDY > 0.0) {
            vecY += 0.5;
         }

         return Vec3.getTempVec3(vecX, vecY, vecZ);
      }
   }

   @Override
   public void lerpTo(double x, double y, double z, float yRot, float xRot, int ticks) {
      this.lerpX = x;
      this.lerpY = y;
      this.lerpZ = z;
      this.lerpYRot = yRot;
      this.lerpXRot = xRot;
      this.lerpTicks = 2;
      this.xd = this.lerpXD;
      this.yd = this.lerpYD;
      this.zd = this.lerpZD;
   }

   @Override
   public void lerpMotion(double xd, double yd, double zd) {
      this.lerpXD = this.xd = xd;
      this.lerpYD = this.yd = yd;
      this.lerpZD = this.zd = zd;
   }

   @Override
   public void push(Entity entity) {
      if (!this.world.isClientSide) {
         if (entity != this.passenger) {
            if (entity.isPushable()) {
               if (this.getType() == 0
                  && entity instanceof Mob
                  && !(entity instanceof Player)
                  && this.xd * this.xd + this.zd * this.zd > 0.01
                  && this.passenger == null
                  && entity.vehicle == null) {
                  entity.startRiding(this);
               }

               double diffX = entity.x - this.x;
               double diffZ = entity.z - this.z;
               double distSqr = diffX * diffX + diffZ * diffZ;
               if (distSqr >= 1.0E-4) {
                  double distance = Math.sqrt(distSqr);
                  double unitX = diffX / distance;
                  double unitZ = diffZ / distance;
                  double n7 = 1.0 / distance;
                  if (n7 > 1.0) {
                     n7 = 1.0;
                  }

                  double n8 = unitX * n7;
                  double n9 = unitZ * n7;
                  double n10 = n8 * 0.1;
                  double n11 = n9 * 0.1;
                  double n12 = n10 * (1.0F - this.pushthrough);
                  double n13 = n11 * (1.0F - this.pushthrough);
                  double xForce = n12 * 0.5;
                  double zForce = n13 * 0.5;
                  if (entity instanceof EntityMinecart) {
                     int thisType = this.getType();
                     int otherType = ((EntityMinecart)entity).getType();
                     if (otherType == 2 && thisType != 2) {
                        this.xd *= 0.2;
                        this.zd *= 0.2;
                        this.push(entity.xd - xForce, 0.0, entity.zd - zForce);
                        entity.xd *= 0.7;
                        entity.zd *= 0.7;
                     } else if (otherType != 2 && thisType == 2) {
                        entity.xd *= 0.2;
                        entity.zd *= 0.2;
                        entity.push(this.xd + xForce, 0.0, this.zd + zForce);
                        this.xd *= 0.7;
                        this.zd *= 0.7;
                     } else {
                        double avgXD = (entity.xd + this.xd) / 2.0;
                        double avgZD = (entity.zd + this.zd) / 2.0;
                        this.xd *= 0.2;
                        this.zd *= 0.2;
                        this.push(avgXD - xForce, 0.0, avgZD - zForce);
                        entity.xd *= 0.2;
                        entity.zd *= 0.2;
                        entity.push(avgXD + xForce, 0.0, avgZD + zForce);
                     }
                  } else {
                     this.push(-xForce, 0.0, -zForce);
                     entity.push(xForce / 4.0, 0.0, zForce / 4.0);
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean stillValid(Player player) {
      return this.isRemoved() ? false : player.distanceToSqr(this) <= 64.0;
   }

   @Override
   public boolean interact(@NotNull Player player) {
      switch (this.getType()) {
         case 0:
            if (!this.world.isClientSide && this.passenger == null && player.isSneaking() && player.getHeldObject() instanceof CarriedBlock) {
               CarriedBlock carriedBlock = (CarriedBlock)player.getHeldObject();
               if (carriedBlock.entity instanceof TileEntityChest) {
                  TileEntityChest chest = (TileEntityChest)carriedBlock.entity;
                  this.setType((byte)1);
                  if (carriedBlock.blockId == Blocks.CHEST_PLANKS_OAK_PAINTED.id()) {
                     this.setMeta(carriedBlock.metadata & 240 | 1);
                  } else {
                     this.setMeta(carriedBlock.metadata & 240);
                  }

                  for (int i = 0; i < this.getContainerSize(); i++) {
                     this.setItem(i, chest.getItem(i));
                     chest.setItem(i, null);
                  }

                  player.setHeldObject(null);
                  return true;
               }
            }

            if (this.passenger != null && this.passenger instanceof Player && this.passenger != player) {
               return true;
            }

            if (!this.world.isClientSide) {
               player.startRiding(this);
            }
            break;
         case 1:
            if (!this.world.isClientSide) {
               if (player.isSneaking() && player.inventory.getCurrentItem() == null && player.getHeldObject() == null) {
                  TileEntityChest tileEntityChest = new TileEntityChest();

                  for (int i = 0; i < this.getContainerSize(); i++) {
                     tileEntityChest.setItem(i, this.getItem(i));
                     this.setItem(i, null);
                  }

                  tileEntityChest.worldObj = null;
                  Block<?> block;
                  if ((this.getMeta() & 1) != 0) {
                     block = Blocks.CHEST_PLANKS_OAK_PAINTED;
                  } else {
                     block = Blocks.CHEST_PLANKS_OAK;
                  }

                  tileEntityChest.carriedBlock = tileEntityChest.getCarriedEntry(this.world, player, block, this.getMeta() & 240);
                  player.setHeldObject(tileEntityChest.carriedBlock);
                  this.setType((byte)0);
                  this.setMeta(0);
               } else {
                  player.displayChestScreen(this, this.x, this.y, this.z);
               }
            }
            break;
         case 2:
            ItemStack itemstack = player.inventory.getCurrentItem();
            if (itemstack != null && LookupFuelFurnace.instance.getFuelYield(itemstack.itemID) > 0 && itemstack.consumeItem(player)) {
               if (itemstack.stackSize <= 0) {
                  player.inventory.setItem(player.inventory.getCurrentItemIndex(), null);
               }

               this.setFuel(this.getFuel() + MathHelper.round(LookupFuelFurnace.instance.getFuelYield(itemstack.itemID) / 1600.0F * 1200.0F));
            }

            this.xPush = this.x - player.x;
            this.zPush = this.z - player.z;
      }

      return true;
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      tag.putInt("Type", this.getType());
      tag.putInt("TypeMeta", this.getMeta());
      switch (this.getType()) {
         case 1:
            ListTag nbttaglist = new ListTag();

            for (int i = 0; i < this.items.length; i++) {
               if (this.items[i] != null) {
                  CompoundTag nbttagcompound1 = new CompoundTag();
                  nbttagcompound1.putByte("Slot", (byte)i);
                  this.items[i].writeToNBT(nbttagcompound1);
                  nbttaglist.addTag(nbttagcompound1);
               }
            }

            tag.put("Items", nbttaglist);
            break;
         case 2:
            tag.putDouble("PushX", this.xPush);
            tag.putDouble("PushZ", this.zPush);
            tag.putInt("Fuel", this.getFuel());
      }
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      this.setType((byte)tag.getInteger("Type"));
      this.setMeta(tag.getInteger("TypeMeta"));
      switch (this.getType()) {
         case 1:
            ListTag nbttaglist = tag.getList("Items");
            this.items = new ItemStack[this.getContainerSize()];

            for (int i = 0; i < nbttaglist.tagCount(); i++) {
               CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
               int j = nbttagcompound1.getByte("Slot") & 255;
               if (j < this.items.length) {
                  this.items[j] = ItemStack.readItemStackFromNbt(nbttagcompound1);
               }
            }
            break;
         case 2:
            this.xPush = tag.getDouble("PushX");
            this.zPush = tag.getDouble("PushZ");
            Tag<?> t = tag.getTag("Fuel");
            if (t instanceof ShortTag) {
               this.setFuel(((ShortTag)t).getValue());
            } else if (t instanceof IntTag) {
               this.setFuel(((IntTag)t).getValue());
            }
      }
   }

   @Override
   public String getNameTranslationKey() {
      return "container.minecart.name";
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   @Override
   public int getContainerSize() {
      return 27;
   }

   @Nullable
   @Override
   public ItemStack getItem(int index) {
      return this.items[index];
   }

   @Nullable
   @Override
   public ItemStack removeItem(int index, int takeAmount) {
      if (this.items[index] != null) {
         if (this.items[index].stackSize <= takeAmount) {
            ItemStack itemstack = this.items[index];
            this.items[index] = null;
            return itemstack;
         } else {
            ItemStack itemstack1 = this.items[index].splitStack(takeAmount);
            if (this.items[index].stackSize <= 0) {
               this.items[index] = null;
            }

            return itemstack1;
         }
      } else {
         return null;
      }
   }

   @Override
   public void setItem(int index, @Nullable ItemStack itemstack) {
      this.items[index] = itemstack;
      if (itemstack != null && itemstack.stackSize > this.getMaxStackSize()) {
         itemstack.stackSize = this.getMaxStackSize();
      }
   }

   @Override
   public void setChanged() {
   }

   @Override
   public void sortContainer() {
      InventorySorter.sortInventory(this.items);
   }
}
