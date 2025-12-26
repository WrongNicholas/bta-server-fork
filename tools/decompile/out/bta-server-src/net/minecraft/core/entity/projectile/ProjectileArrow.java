package net.minecraft.core.entity.projectile;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Mob;
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

public class ProjectileArrow extends Projectile {
   public static final int TYPE_NORMAL = 0;
   public static final int TYPE_PURPLE = 1;
   public static final int TYPE_GOLDEN = 2;
   protected int mobsHit = 0;
   protected int xTile = -1;
   protected int yTile = -1;
   protected int zTile = -1;
   protected int inTile = 0;
   public int shake = 0;
   protected int inData = 0;
   protected int arrowType;
   protected ItemStack stack = new ItemStack(Items.AMMO_ARROW);
   protected boolean inGround = false;
   protected boolean doesArrowBelongToPlayer = false;

   public ProjectileArrow(World world) {
      this(world, 0);
   }

   public ProjectileArrow(World world, int arrowType) {
      super(world);
      this.arrowType = arrowType;
   }

   public ProjectileArrow(World world, double d, double d1, double d2, int arrowType) {
      super(world, d, d1, d2);
      this.arrowType = arrowType;
   }

   public ProjectileArrow(World world, Mob entityliving, boolean doesArrowBelongToPlayer, int arrowType) {
      super(world, entityliving);
      this.setDoesArrowBelongToPlayer(doesArrowBelongToPlayer);
      this.arrowType = arrowType;
   }

   @Override
   protected void initProjectile() {
      super.initProjectile();
      this.damage = 5;
   }

   public void setDoesArrowBelongToPlayer(boolean flag) {
      this.doesArrowBelongToPlayer = flag;
   }

   public boolean arrowBelongsToPlayer() {
      return this.doesArrowBelongToPlayer;
   }

   public void setGrounded(boolean flag) {
      this.inGround = flag;
   }

   public boolean isGrounded() {
      return this.inGround;
   }

   @Override
   public void lerpMotion(double xd, double yd, double zd) {
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float f = MathHelper.sqrt(xd * xd + zd * zd);
         this.yRot = (float)(Math.atan2(xd, zd) * 180.0 / Math.PI);
         this.xRot = (float)(Math.atan2(yd, f) * 180.0 / Math.PI);
         this.xRotO = this.xRot;
         this.yRotO = this.yRot;
         this.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
         this.ticksInGround = 0;
      }
   }

   @Override
   public void tick() {
      if (this.shake > 0) {
         this.shake--;
      }

      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float f = MathHelper.sqrt(this.xd * this.xd + this.zd * this.zd);
         this.yRotO = this.yRot = (float)(Math.atan2(this.xd, this.zd) * 180.0 / Math.PI);
         this.xRotO = this.xRot = (float)(Math.atan2(this.yd, f) * 180.0 / Math.PI);
      }

      Block<?> block = this.world.getBlock(this.xTile, this.yTile, this.zTile);
      if (block != null) {
         AABB aabb = block.getCollisionBoundingBoxFromPool(this.world, this.xTile, this.yTile, this.zTile);
         if (aabb != null && aabb.contains(Vec3.getTempVec3(this.x, this.y, this.z))) {
            this.setGrounded(true);
         }
      }

      if (this.isGrounded()) {
         int id = this.world.getBlockId(this.xTile, this.yTile, this.zTile);
         int meta = this.world.getBlockMetadata(this.xTile, this.yTile, this.zTile);
         if (id == this.inTile && meta == this.inData) {
            this.ticksInGround++;
            if (this.ticksInGround == 1200) {
               this.remove();
            }
         } else {
            this.setGrounded(false);
            this.xd = this.xd * (this.random.nextFloat() * 0.2);
            this.yd = this.yd * (this.random.nextFloat() * 0.2);
            this.zd = this.zd * (this.random.nextFloat() * 0.2);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         }
      } else {
         if (this instanceof ProjectileArrowGolden) {
            this.world.spawnParticle("arrowtrail", this.x, this.y, this.z, this.xd * 0.05, this.yd * 0.05 - 0.1, this.zd * 0.05, 0);
            this.world
               .spawnParticle(
                  "arrowtrail", this.x + this.xd * 0.5, this.y + this.yd * 0.5, this.z + this.zd * 0.5, this.xd * 0.05, this.yd * 0.05 - 0.1, this.zd * 0.05, 0
               );
         }

         super.tick();
      }
   }

   @Override
   public HitResult getHitResult() {
      Vec3 oldPosition = Vec3.getTempVec3(this.x, this.y, this.z);
      Vec3 newPosition = Vec3.getTempVec3(this.x + this.xd, this.y + this.yd, this.z + this.zd);
      return this.world.checkBlockCollisionBetweenPoints(oldPosition, newPosition, false, true, false);
   }

   @Override
   public void onHit(HitResult hitResult) {
      if (hitResult.entity != null) {
         if (hitResult.entity.hurt(this.owner, this.damage, DamageType.COMBAT)) {
            if (this.isOnFire()) {
               hitResult.entity.fireHurt();
            }

            if (!this.world.isClientSide) {
               this.world.playSoundAtEntity(null, this, "random.drr", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            }

            if (!(this instanceof ProjectileArrowGolden)) {
               this.remove();
            } else if (this.owner instanceof Player && ++this.mobsHit >= 3) {
               ((Player)this.owner).addStat(Achievements.TRIPLE_HIT, 1);
            }
         } else if (!(this instanceof ProjectileArrowGolden)) {
            this.xd *= -0.1;
            this.yd *= -0.1;
            this.zd *= -0.1;
            this.yRot += 180.0F;
            this.yRotO += 180.0F;
            this.ticksInAir = 0;
         }
      } else {
         this.xTile = hitResult.x;
         this.yTile = hitResult.y;
         this.zTile = hitResult.z;
         this.inTile = this.world.getBlockId(this.xTile, this.yTile, this.zTile);
         this.inData = this.world.getBlockMetadata(this.xTile, this.yTile, this.zTile);
         this.xd = (float)(hitResult.location.x - this.x);
         this.yd = (float)(hitResult.location.y - this.y);
         this.zd = (float)(hitResult.location.z - this.z);
         float f1 = MathHelper.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
         this.x = this.x - this.xd / f1 * 0.05;
         this.y = this.y - this.yd / f1 * 0.05;
         this.z = this.z - this.zd / f1 * 0.05;
         this.inGroundAction();
      }
   }

   protected void inGroundAction() {
      if (this.world.isClientSide) {
         this.setGrounded(true);
         this.shake = 7;
      } else if (this.arrowBelongsToPlayer()) {
         this.world.playSoundAtEntity(null, this, "random.drr", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         this.setGrounded(true);
         this.shake = 7;
      } else {
         this.world.playSoundAtEntity(null, this, "random.drr", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

         for (int j = 0; j < 4; j++) {
            this.world.spawnParticle("item", this.x, this.y, this.z, 0.0, 0.0, 0.0, Items.AMMO_ARROW.id);
         }

         this.remove();
      }
   }

   public int getArrowType() {
      return this.arrowType;
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putShort("xTile", (short)this.xTile);
      tag.putShort("yTile", (short)this.yTile);
      tag.putShort("zTile", (short)this.zTile);
      tag.putShort("inTile", (short)this.inTile);
      tag.putByte("shake", (byte)this.shake);
      tag.putByte("inData", (byte)this.inData);
      tag.putByte("inGround", (byte)(this.isGrounded() ? 1 : 0));
      tag.putBoolean("player", this.arrowBelongsToPlayer());
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.xTile = tag.getShort("xTile");
      this.yTile = tag.getShort("yTile");
      this.zTile = tag.getShort("zTile");
      this.inTile = tag.getShort("inTile") & 16383;
      this.shake = tag.getByte("shake") & 255;
      this.inData = tag.getByte("inData") & 255;
      this.setGrounded(tag.getByte("inGround") == 1);
      this.setDoesArrowBelongToPlayer(tag.getBoolean("player"));
   }

   @Override
   public void playerTouch(Player player) {
      if (!this.world.isClientSide) {
         if (this.isGrounded() && this.arrowBelongsToPlayer() && this.shake <= 0) {
            player.inventory.insertItem(this.stack, true);
            if (this.stack.stackSize <= 0) {
               this.world.playSoundAtEntity(player, this, "item.pickup", 1.0F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 4.0F);
               player.onItemPickup(this, this.stack);
               this.remove();
            }
         }
      }
   }
}
