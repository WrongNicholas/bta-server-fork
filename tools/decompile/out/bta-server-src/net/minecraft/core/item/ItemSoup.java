package net.minecraft.core.item;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;

public class ItemSoup extends ItemFood {
   public ItemSoup(String name, String namespaceId, int id, int healAmount, int ticksPerHeal) {
      super(name, namespaceId, id, healAmount, ticksPerHeal, false, 1);
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      ItemStack stack = super.onUseItem(itemstack, world, entityplayer);
      return stack.stackSize <= 0 ? Items.BOWL.getDefaultStack() : stack;
   }
}
