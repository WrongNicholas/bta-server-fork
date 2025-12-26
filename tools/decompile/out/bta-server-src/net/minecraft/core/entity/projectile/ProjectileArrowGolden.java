package net.minecraft.core.entity.projectile;

import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;

public class ProjectileArrowGolden extends ProjectileArrow {
   public ProjectileArrowGolden(World world) {
      super(world, 2);
      this.noPhysics = true;
      this.stack = new ItemStack(Items.AMMO_ARROW_GOLD);
   }

   public ProjectileArrowGolden(World world, double x, double y, double z) {
      super(world, x, y, z, 2);
      this.noPhysics = true;
      this.stack = new ItemStack(Items.AMMO_ARROW_GOLD);
   }

   public ProjectileArrowGolden(World world, Mob owner, boolean doesArrowBelongToPlayer) {
      super(world, owner, doesArrowBelongToPlayer, 2);
      this.noPhysics = true;
      this.stack = new ItemStack(Items.AMMO_ARROW_GOLD);
   }

   @Override
   protected void initProjectile() {
      super.initProjectile();
      this.defaultGravity = 0.02F;
      this.defaultProjectileSpeed = 1.0F;
      this.damage = 2;
   }

   @Override
   public void playerTouch(Player player) {
      if (this.arrowBelongsToPlayer()) {
         super.playerTouch(player);
      }
   }
}
