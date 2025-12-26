package net.minecraft.core.item;

import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobPig;
import net.minecraft.core.entity.player.Player;

public class ItemSaddle extends Item {
   public ItemSaddle(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.maxStackSize = 1;
   }

   @Override
   public boolean useItemOnEntity(ItemStack itemstack, Mob mob, Player player) {
      if (mob instanceof MobPig && itemstack.consumeItem(player)) {
         MobPig entitypig = (MobPig)mob;
         if (!entitypig.getSaddled()) {
            entitypig.setSaddled(true);
            return true;
         }
      }

      return false;
   }
}
