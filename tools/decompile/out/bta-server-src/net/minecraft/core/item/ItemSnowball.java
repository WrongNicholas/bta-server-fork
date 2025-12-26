package net.minecraft.core.item;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.ProjectileSnowball;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;

public class ItemSnowball extends Item implements IDispensable {
   public ItemSnowball(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.maxStackSize = 64;
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      itemstack.consumeItem(entityplayer);
      world.playSoundAtEntity(entityplayer, entityplayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      if (!world.isClientSide) {
         world.entityJoinedWorld(new ProjectileSnowball(world, entityplayer));
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
      ProjectileSnowball projectileSnowball = new ProjectileSnowball(world, blockX + offX, blockY + offY, blockZ + offZ);
      projectileSnowball.setHeading(
         direction.getOffsetX() * 0.6, direction.getOffsetY() == 0 ? 0.1 : direction.getOffsetY() * 0.6, direction.getOffsetZ() * 0.6F, 1.1F, 6.0F
      );
      world.entityJoinedWorld(projectileSnowball);
      itemStack.stackSize--;
   }

   @Override
   public void onDispensed(ItemStack itemStack, World world, double x, double y, double z, int xOffset, int yOffset, int zOffset, Random random) {
      ProjectileSnowball entitysnowball = new ProjectileSnowball(world, x, y, z);
      entitysnowball.setHeading(xOffset, yOffset + 0.1, zOffset, 1.1F, 6.0F);
      world.entityJoinedWorld(entitysnowball);
   }
}
