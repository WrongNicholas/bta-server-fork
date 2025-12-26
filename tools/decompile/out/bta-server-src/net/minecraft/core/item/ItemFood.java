package net.minecraft.core.item;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;

public class ItemFood extends Item {
   private final int healAmount;
   private final boolean isWolfsFavoriteMeat;
   private final int ticksPerHeal;

   public ItemFood(String name, String namespaceId, int id, int healAmount, int ticksPerHeal, boolean favouriteWolfMeat, int maxStackSize) {
      super(name, namespaceId, id);
      this.healAmount = healAmount;
      this.ticksPerHeal = ticksPerHeal;
      this.isWolfsFavoriteMeat = favouriteWolfMeat;
      this.maxStackSize = maxStackSize;
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      if (entityplayer.getHealth() < entityplayer.getMaxHealth()
         && entityplayer.getHealth() + entityplayer.getTotalHealingRemaining() < entityplayer.getMaxHealth()
         && itemstack.consumeItem(entityplayer)) {
         entityplayer.eatFood(this);
         world.playSoundAtEntity(
            entityplayer,
            entityplayer,
            this.ticksPerHeal >= 10 ? "random.bite_extended" : "random.bite",
            0.5F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F,
            1.1F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F
         );
      }

      return itemstack;
   }

   public int getHealAmount() {
      return this.healAmount;
   }

   public int getTicksPerHeal() {
      return this.ticksPerHeal;
   }

   public boolean getIsWolfsFavoriteMeat() {
      return this.isWolfsFavoriteMeat;
   }
}
