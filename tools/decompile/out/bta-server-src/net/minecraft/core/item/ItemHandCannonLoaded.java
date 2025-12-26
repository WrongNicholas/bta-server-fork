package net.minecraft.core.item;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.ProjectileCannonball;
import net.minecraft.core.world.World;

public class ItemHandCannonLoaded extends Item {
   public ItemHandCannonLoaded(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.setMaxDamage(100);
      this.maxStackSize = 1;
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      world.playSoundAtEntity(entityplayer, entityplayer, "random.bow", 0.3F, 1.0F / (itemRand.nextFloat() * -0.2F - 0.4F));
      if (!world.isClientSide) {
         itemstack.damageItem(1, entityplayer);
         world.entityJoinedWorld(new ProjectileCannonball(world, entityplayer));
         return itemstack.stackSize <= 0 ? null : new ItemStack(Items.HANDCANNON_UNLOADED, 1, itemstack.getMetadata(), itemstack.getData());
      } else {
         return itemstack;
      }
   }
}
