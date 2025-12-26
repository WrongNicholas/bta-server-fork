package net.minecraft.core.item;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;

public class ItemHandCannonUnloaded extends Item {
   public ItemHandCannonUnloaded(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.setMaxDamage(100);
      this.maxStackSize = 1;
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      if (entityplayer.inventory.consumeInventoryItem(Items.AMMO_CHARGE_EXPLOSIVE.id)) {
         world.playSoundAtEntity(entityplayer, entityplayer, "random.click", 1.0F, 1.9F / (itemRand.nextFloat() * 0.2F + 0.4F));
         return new ItemStack(Items.HANDCANNON_LOADED, 1, itemstack.getMetadata(), itemstack.getData());
      } else {
         return itemstack;
      }
   }
}
