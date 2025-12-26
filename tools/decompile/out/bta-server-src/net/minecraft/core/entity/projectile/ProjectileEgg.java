package net.minecraft.core.entity.projectile;

import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobChicken;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;

public class ProjectileEgg extends Projectile {
   public ProjectileEgg(World world) {
      super(world);
      this.modelItem = Items.EGG_CHICKEN;
   }

   public ProjectileEgg(World world, Mob owner) {
      super(world, owner);
      this.modelItem = Items.EGG_CHICKEN;
   }

   public ProjectileEgg(World world, double x, double y, double z) {
      super(world, x, y, z);
      this.modelItem = Items.EGG_CHICKEN;
   }

   @Override
   public void onHit(HitResult hitResult) {
      if (!this.world.isClientSide && this.random.nextInt(8) == 0) {
         byte byte0 = 1;
         if (this.random.nextInt(32) == 0) {
            byte0 = 4;
         }

         for (int k = 0; k < byte0; k++) {
            MobChicken entitychicken = new MobChicken(this.world);
            entitychicken.moveTo(this.x, this.y, this.z, this.yRot, 0.0F);
            this.world.entityJoinedWorld(entitychicken);
         }
      }

      super.onHit(hitResult);
   }
}
