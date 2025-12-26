package net.minecraft.core.entity.projectile;

import net.minecraft.core.entity.Mob;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;

public class ProjectileCannonball extends Projectile {
   public ProjectileCannonball(World world) {
      super(world);
   }

   public ProjectileCannonball(World world, Mob owner) {
      super(world, owner);
   }

   public ProjectileCannonball(World world, double x, double y, double z) {
      super(world, x, y, z);
   }

   @Override
   public void initProjectile() {
      this.defaultGravity = 0.09F;
      this.defaultProjectileSpeed = 0.88F;
   }

   @Override
   public void onHit(HitResult hitResult) {
      if (hitResult.hitType == HitResult.HitType.TILE) {
         this.world.createExplosion(this.owner, this.x, this.y + this.bbHeight / 2.0F, this.z, 1.5F, false, true);
         this.remove();
      }
   }

   @Override
   public void afterTick() {
      super.afterTick();
      this.world.spawnParticle("largesmoke", this.x, this.y, this.z, 0.0, 0.0, 0.0, 0);
   }
}
