package net.minecraft.core.entity.projectile;

import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;

public class ProjectileArrowPurple extends ProjectileArrow {
   public ProjectileArrowPurple(World world) {
      super(world, 1);
   }

   public ProjectileArrowPurple(World world, double x, double y, double z) {
      super(world, x, y, z, 1);
   }

   public ProjectileArrowPurple(World world, Mob owner, boolean doesArrowBelongToPlayer) {
      super(world, owner, doesArrowBelongToPlayer, 1);
   }

   @Override
   protected void initProjectile() {
      super.initProjectile();
      this.defaultGravity = 0.03F;
      this.defaultProjectileSpeed = 0.99F;
      this.damage = 2;
   }

   @Override
   protected void inGroundAction() {
      this.world.playSoundAtEntity(null, this, "random.drr", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

      for (int j = 0; j < 4; j++) {
         this.world.spawnParticle("item", this.x, this.y, this.z, 0.0, 0.0, 0.0, Items.AMMO_ARROW_PURPLE.id);
      }

      this.remove();
   }
}
