package net.minecraft.core.entity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class EntityPrimedTNT extends Entity {
   public int fuse = 0;

   public EntityPrimedTNT(World world) {
      super(world);
      this.blocksBuilding = true;
      this.setSize(0.98F, 0.98F);
      this.heightOffset = this.bbHeight / 2.0F;
   }

   public EntityPrimedTNT(World world, double x, double y, double z) {
      this(world);
      this.setPos(x, y, z);
      float f = (float)(Math.random() * Math.PI * 2.0);
      this.xd = -MathHelper.sin(f * (float) Math.PI / 180.0F) * 0.02F;
      this.yd = 0.2;
      this.zd = -MathHelper.cos(f * (float) Math.PI / 180.0F) * 0.02F;
      this.fuse = 80;
      this.xo = x;
      this.yo = y;
      this.zo = z;
   }

   @Override
   protected void defineSynchedData() {
   }

   @Override
   protected boolean makeStepSound() {
      return false;
   }

   @Override
   public boolean isPickable() {
      return !this.removed;
   }

   @Override
   public void tick() {
      this.checkOnWater(true);
      this.checkOnWater(true);
      this.pushTime *= 0.98F;
      if (this.pushTime < 0.05F || this.pushTime < 0.25 && this.onGround) {
         this.pushTime = 0.0F;
      }

      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.yd -= 0.04;
      this.move(this.xd, this.yd, this.zd);
      this.xd *= 0.98;
      this.yd *= 0.98;
      this.zd *= 0.98;
      if (this.onGround) {
         this.xd *= 0.7;
         this.zd *= 0.7;
         this.yd *= -0.5;
      }

      if (this.fuse-- <= 0) {
         if (!this.world.isClientSide) {
            this.remove();
            this.world.createExplosion(null, this.x, this.y + 0.5, this.z, 4.0F);
         } else {
            this.remove();
         }
      } else {
         this.world.spawnParticle("smoke", this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0, 0);
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      tag.putByte("Fuse", (byte)this.fuse);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      this.fuse = tag.getByte("Fuse");
   }

   @Override
   public float getShadowHeightOffs() {
      return 0.0F;
   }
}
