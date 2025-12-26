package net.minecraft.core.entity;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;

public class MobFlying extends Mob {
   public MobFlying(World world) {
      super(world);
   }

   @Override
   protected void causeFallDamage(float distance) {
   }

   @Override
   public void moveEntityWithHeading(float moveStrafing, float moveForward) {
      if (this.isInWater()) {
         this.moveRelative(moveStrafing, moveForward, 0.02F);
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.8;
         this.yd *= 0.8;
         this.zd *= 0.8;
      } else if (this.isInLava()) {
         this.moveRelative(moveStrafing, moveForward, 0.02F);
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.5;
         this.yd *= 0.5;
         this.zd *= 0.5;
      } else {
         float f2 = 0.91F;
         if (this.onGround) {
            f2 = 0.5460001F;
            int i = this.world.getBlockId(MathHelper.floor(this.x), MathHelper.floor(this.bb.minY) - 1, MathHelper.floor(this.z));
            if (i > 0) {
               f2 = Blocks.blocksList[i].friction * 0.91F;
            }
         }

         float f3 = 0.1627714F / (f2 * f2 * f2);
         this.moveRelative(moveStrafing, moveForward, this.onGround ? 0.1F * f3 : 0.02F);
         f2 = 0.91F;
         if (this.onGround) {
            f2 = 0.5460001F;
            int j = this.world.getBlockId(MathHelper.floor(this.x), MathHelper.floor(this.bb.minY) - 1, MathHelper.floor(this.z));
            if (j > 0) {
               f2 = Blocks.blocksList[j].friction * 0.91F;
            }
         }

         this.move(this.xd, this.yd, this.zd);
         this.xd *= f2;
         this.yd *= f2;
         this.zd *= f2;
      }

      this.walkAnimSpeedO = this.walkAnimSpeed;
      double d = this.x - this.xo;
      double d1 = this.z - this.zo;
      float f4 = MathHelper.sqrt(d * d + d1 * d1) * 4.0F;
      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      this.walkAnimSpeed = this.walkAnimSpeed + (f4 - this.walkAnimSpeed) * 0.4F;
      this.walkAnimPos = this.walkAnimPos + this.walkAnimSpeed;
   }

   @Override
   public boolean canClimb() {
      return false;
   }
}
