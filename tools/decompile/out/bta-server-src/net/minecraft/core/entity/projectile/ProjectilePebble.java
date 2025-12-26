package net.minecraft.core.entity.projectile;

import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;

public class ProjectilePebble extends Projectile {
   private boolean firstBounce = true;

   public ProjectilePebble(World world) {
      super(world);
   }

   public ProjectilePebble(World world, Mob owner) {
      super(world, owner);
   }

   public ProjectilePebble(World world, double x, double y, double z) {
      super(world, x, y, z);
   }

   @Override
   public void initProjectile() {
      super.initProjectile();
      this.damage = 1;
   }

   @Override
   public void onHit(HitResult hitResult) {
      if (hitResult.entity != null) {
         hitResult.entity.hurt(this.owner, this.damage, DamageType.COMBAT);
      }

      if (!this.world.isClientSide) {
         EntityItem item = new EntityItem(this.world, this.x, this.y, this.z, new ItemStack(Items.AMMO_PEBBLE, 1));
         this.world.entityJoinedWorld(item);
      }

      this.remove();
   }

   @Override
   public void afterTick() {
      if (this.isInWater() && Math.abs(this.xd) + Math.abs(this.zd) > 0.5) {
         float modifier = 1.0F;
         if (this.firstBounce) {
            this.firstBounce = false;
            modifier = 1.0F + this.random.nextFloat() * 0.75F;
         }

         this.yd = (Math.abs(this.xd) + Math.abs(this.zd)) / 8.0 * modifier;
      }

      super.afterTick();
   }
}
