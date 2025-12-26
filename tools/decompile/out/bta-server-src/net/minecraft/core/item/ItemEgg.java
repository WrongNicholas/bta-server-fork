package net.minecraft.core.item;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.ProjectileEgg;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;

public class ItemEgg extends Item implements IDispensable {
   public ItemEgg(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.maxStackSize = 16;
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      itemstack.consumeItem(entityplayer);
      world.playSoundAtEntity(entityplayer, entityplayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      if (!world.isClientSide) {
         world.entityJoinedWorld(new ProjectileEgg(world, entityplayer));
      }

      return itemstack;
   }

   @Override
   public void onUseByActivator(
      ItemStack itemStack,
      TileEntityActivator activatorBlock,
      World world,
      Random random,
      int blockX,
      int blockY,
      int blockZ,
      double offX,
      double offY,
      double offZ,
      Direction direction
   ) {
      ProjectileEgg projectileEgg = new ProjectileEgg(world, blockX + offX, blockY + offY, blockZ + offZ);
      projectileEgg.setHeading(
         direction.getOffsetX() * 0.6, direction.getOffsetY() == 0 ? 0.1 : direction.getOffsetY() * 0.6, direction.getOffsetZ() * 0.6F, 1.1F, 6.0F
      );
      world.entityJoinedWorld(projectileEgg);
      itemStack.stackSize--;
   }

   @Override
   public void onDispensed(ItemStack itemStack, World world, double x, double y, double z, int xOffset, int yOffset, int zOffset, Random random) {
      ProjectileEgg projectileEgg = new ProjectileEgg(world, x, y, z);
      projectileEgg.setHeading(xOffset, yOffset + 0.1, zOffset, 1.1F, 6.0F);
      world.entityJoinedWorld(projectileEgg);
   }
}
