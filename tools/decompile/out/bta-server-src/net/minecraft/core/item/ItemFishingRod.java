package net.minecraft.core.item;

import net.minecraft.core.entity.EntityFishingBobber;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;

public class ItemFishingRod extends Item {
   public ItemFishingRod(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.setMaxDamage(196);
      this.setMaxStackSize(1);
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      if (entityplayer.bobberEntity != null) {
         int damage = entityplayer.bobberEntity.yoink();
         itemstack.damageItem(damage, entityplayer);
      } else {
         world.playSoundAtEntity(entityplayer, entityplayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!world.isClientSide) {
            world.entityJoinedWorld(new EntityFishingBobber(world, entityplayer));
         }
      }

      entityplayer.swingItem();
      return itemstack;
   }
}
