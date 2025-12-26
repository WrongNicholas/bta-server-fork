package net.minecraft.core.entity.projectile;

import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;

public class ProjectileSnowball extends Projectile {
   public ProjectileSnowball(World world) {
      super(world);
      this.modelItem = Items.AMMO_SNOWBALL;
   }

   public ProjectileSnowball(World world, Mob owner) {
      super(world, owner);
      this.modelItem = Items.AMMO_SNOWBALL;
   }

   public ProjectileSnowball(World world, double x, double y, double z) {
      super(world, x, y, z);
      this.modelItem = Items.AMMO_SNOWBALL;
   }
}
