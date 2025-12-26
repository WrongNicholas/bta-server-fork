package net.minecraft.core.item;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;

public class ItemBucketIceCream extends ItemFood {
   public ItemBucketIceCream(String name, String namespaceId, int id, int healAmount, int ticksPerHeal) {
      super(name, namespaceId, id, healAmount, ticksPerHeal, false, 1);
      this.maxStackSize = 1;
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      if (entityplayer.getHealth() < entityplayer.getMaxHealth()) {
         super.onUseItem(itemstack, world, entityplayer);
         return new ItemStack(Items.BUCKET);
      } else {
         return itemstack;
      }
   }
}
