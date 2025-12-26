package net.minecraft.core.entity.animal;

import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;

public class MobSquid extends MobWaterAnimal {
   public float xBodyRot = 0.0F;
   public float xBodyRotO = 0.0F;
   public float zBodyRot = 0.0F;
   public float zBodyRotO = 0.0F;
   public float tentacleMovement = 0.0F;
   public float oldTentacleMovement = 0.0F;
   public float tentacleAngle = 0.0F;
   public float oldTentacleAngle = 0.0F;
   private float _speed = 0.0F;
   private float tentacleSpeed = 0.0F;
   private float rotateSpeed = 0.0F;
   private float tx = 0.0F;
   private float ty = 0.0F;
   private float tz = 0.0F;

   public MobSquid(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "squid");
      this.setSize(0.95F, 0.95F);
      this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
      this.mobDrops.add(new WeightedRandomLootObject(Items.DYE.getDefaultStack(), 1, 3));
   }

   @Override
   protected String getHurtSound() {
      return null;
   }

   @Override
   protected String getDeathSound() {
      return null;
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.xBodyRotO = this.xBodyRot;
      this.zBodyRotO = this.zBodyRot;
      this.oldTentacleMovement = this.tentacleMovement;
      this.oldTentacleAngle = this.tentacleAngle;
      this.tentacleMovement = this.tentacleMovement + this.tentacleSpeed;
      if (this.tentacleMovement > (float) (Math.PI * 2)) {
         this.tentacleMovement = 0.0F;
         if (this.random.nextInt(10) == 9) {
            this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
         }
      }

      if (this.isInWater() && !this.isSpecial()) {
         if (this.tentacleMovement < (float) Math.PI) {
            float f = this.tentacleMovement / (float) Math.PI;
            this.tentacleAngle = MathHelper.sin(f * f * (float) Math.PI) * (float) Math.PI * 0.25F;
            if (f > 0.75) {
               this._speed = 1.0F;
               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this._speed *= 0.9F;
            this.rotateSpeed *= 0.99F;
         }

         if (!this.isMultiplayerEntity) {
            this.xd = this.tx * this._speed;
            this.yd = this.ty * this._speed;
            this.zd = this.tz * this._speed;
         }

         float horizontalSpeed = MathHelper.sqrt(this.xd * this.xd + this.zd * this.zd);
         this.yBodyRot = this.yBodyRot + (-((float)Math.atan2(this.xd, this.zd)) * 180.0F / (float) Math.PI - this.yBodyRot) * 0.1F;
         this.yRot = this.yBodyRot;
         this.zBodyRot = this.zBodyRot + (float) Math.PI * this.rotateSpeed * 1.5F;
         this.xBodyRot = this.xBodyRot + (-((float)Math.atan2(horizontalSpeed, this.yd)) * 180.0F / (float) Math.PI - this.xBodyRot) * 0.1F;
      } else {
         this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.tentacleMovement)) * (float) Math.PI * 0.25F;
         if (!this.isSpecial()) {
            if (!this.isMultiplayerEntity) {
               this.xd = 0.0;
               this.yd -= 0.08;
               this.yd *= 0.98;
               this.zd = 0.0;
            }

            this.xBodyRot = this.xBodyRot + (-90.0F - this.xBodyRot) * 0.02F;
         }
      }
   }

   @Override
   public void trySuffocate() {
      if (!this.isSpecial()) {
         if (this.isAlive() && !this.isUnderLiquid(Material.water) && this.canBreatheUnderwater()) {
            this.airSupply--;
            if (this.airSupply == -20) {
               this.airSupply = 0;
               this.hurt(null, 2, DamageType.DROWN);
            }

            this.remainingFireTicks = 0;
         } else {
            this.airSupply = this.airMaxSupply;
         }
      }
   }

   @Override
   public void moveEntityWithHeading(float moveStrafing, float moveForward) {
      if (this.isSpecial()) {
         super.moveEntityWithHeading(moveStrafing, moveForward);
      } else {
         this.move(this.xd, this.yd, this.zd);
      }
   }

   @Override
   protected void updateAI() {
      if (this.isSpecial()) {
         Player closest = this.world.getClosestPlayer(this.x, this.y, this.z, 10.0);
         if (closest != null) {
            this.setTarget(closest);
         }

         super.updateAI();
      } else {
         if (this.random.nextInt(50) == 0 || !this.wasInWater || this.tx == 0.0F && this.ty == 0.0F && this.tz == 0.0F) {
            float f = this.random.nextFloat() * (float) Math.PI * 2.0F;
            this.tx = MathHelper.cos(f) * 0.2F;
            Block<?> upwardsBlock = this.world.getBlock((int)this.x, (int)this.y + 3, (int)this.z);
            Block<?> downwardsBlock = this.world.getBlock((int)this.x, (int)this.y - 3, (int)this.z);
            boolean isTouchingBlock = this.isTouchingBlock();
            if (isTouchingBlock
               && this.isInWater()
               && !this.world.isAirBlock((int)this.x, (int)(this.y + 1.0), (int)this.z)
               && !this.world.isAirBlock((int)this.x, (int)(this.y + 2.0), (int)this.z)) {
               this.ty = this.random.nextFloat() * 0.2F;
            } else if (upwardsBlock == null || upwardsBlock.getMaterial() != Material.water) {
               this.ty = -0.15F + this.random.nextFloat() * 0.2F;
            } else if (downwardsBlock != null && downwardsBlock.getMaterial() == Material.water) {
               this.ty = -0.1F + this.random.nextFloat() * 0.2F;
            } else {
               this.ty = -0.05F + this.random.nextFloat() * 0.4F;
            }

            this.tz = MathHelper.sin(f) * 0.2F;
         }

         this.tryToDespawn();
      }
   }

   public boolean isTouchingBlock() {
      int minX = MathHelper.floor(this.bb.minX - 0.25);
      int minY = MathHelper.floor(this.bb.minY - 0.25);
      int minZ = MathHelper.floor(this.bb.minZ - 0.25);
      int maxX = MathHelper.floor(this.bb.maxX + 0.25);
      int maxY = MathHelper.floor(this.bb.maxY + 0.25);
      int maxZ = MathHelper.floor(this.bb.maxZ + 0.25);
      if (this.world.areBlocksLoaded(minX, minY, minZ, maxX, maxY, maxZ)) {
         for (int _x = minX; _x <= maxX; _x++) {
            for (int _y = minY; _y <= maxY; _y++) {
               for (int _z = minZ; _z <= maxZ; _z++) {
                  Block<?> block = this.world.getBlock(_x, _y, _z);
                  if (block != null && block.getMaterial() != Material.water) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   @Override
   protected boolean makeStepSound() {
      return false;
   }

   public boolean isSpecial() {
      return "Useless7695".equals(this.nickname);
   }
}
