package net.minecraft.core.item;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.entity.projectile.ProjectileArrowGolden;
import net.minecraft.core.entity.projectile.ProjectileArrowPurple;
import net.minecraft.core.world.World;

public class ItemBow extends Item {
   public ItemBow(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.maxStackSize = 1;
      this.setMaxDamage(384);
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      ItemStack quiverSlot = entityplayer.inventory.armorItemInSlot(2);
      if (quiverSlot != null && quiverSlot.itemID == Items.ARMOR_QUIVER.id && quiverSlot.getMetadata() < quiverSlot.getMaxDamage()) {
         entityplayer.inventory.armorItemInSlot(2).damageItem(1, entityplayer);
         itemstack.damageItem(1, entityplayer);
         world.playSoundAtEntity(entityplayer, entityplayer, "random.bow", 0.3F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!world.isClientSide) {
            world.entityJoinedWorld(new ProjectileArrow(world, entityplayer, true, 0));
         }
      } else if (quiverSlot != null && quiverSlot.itemID == Items.ARMOR_QUIVER_GOLD.id) {
         itemstack.damageItem(1, entityplayer);
         world.playSoundAtEntity(entityplayer, entityplayer, "random.bow", 0.3F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!world.isClientSide) {
            world.entityJoinedWorld(new ProjectileArrowPurple(world, entityplayer, false));
         }
      } else if (entityplayer.inventory.consumeInventoryItem(Items.AMMO_ARROW_GOLD.id)) {
         itemstack.damageItem(1, entityplayer);
         world.playSoundAtEntity(entityplayer, entityplayer, "random.bow", 0.3F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!world.isClientSide) {
            world.entityJoinedWorld(new ProjectileArrowGolden(world, entityplayer, true));
         }
      } else if (entityplayer.inventory.consumeInventoryItem(Items.AMMO_ARROW.id)) {
         itemstack.damageItem(1, entityplayer);
         world.playSoundAtEntity(entityplayer, entityplayer, "random.bow", 0.3F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!world.isClientSide) {
            world.entityJoinedWorld(new ProjectileArrow(world, entityplayer, true, 0));
         }
      }

      return itemstack;
   }
}
