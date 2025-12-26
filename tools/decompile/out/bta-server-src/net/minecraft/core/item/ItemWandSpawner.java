package net.minecraft.core.item;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;

public class ItemWandSpawner extends Item implements IDispensable {
   public ItemWandSpawner(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player player) {
      if (player.gamemode != Gamemode.creative) {
         player.sendTranslatedChatMessage("wand.wrongmode");
         return itemstack;
      } else {
         if (!player.isSneaking()) {
            double reachDistance = player.getGamemode().getBlockReachDistance();
            HitResult rayTraceResult = player.rayTrace(reachDistance, 1.0F, false, false);
            if (rayTraceResult != null) {
               if (!world.isClientSide) {
                  this.spawnEntity(itemstack, world, rayTraceResult.location.x, rayTraceResult.location.y, rayTraceResult.location.z);
               }

               return itemstack;
            }
         }

         player.displayWandMobPickerScreen(itemstack);
         return itemstack;
      }
   }

   private String getEntityId(ItemStack itemStack) {
      String id = itemStack.getData().getString("monster");
      if (id == null || id.isEmpty()) {
         id = "Pig";
      }

      return id;
   }

   public void spawnEntity(ItemStack itemStack, World world, double x, double y, double z) {
      Entity entity = EntityDispatcher.createEntityInWorld(this.getEntityId(itemStack), world);
      if (entity != null) {
         entity.setPos(x, y, z);
         entity.spawnInit();
         if (itemStack.hasCustomName() && entity instanceof Mob) {
            if (itemStack.hasCustomColor()) {
               ((Mob)entity).chatColor = itemStack.getCustomColor();
            }

            ((Mob)entity).setNickname(itemStack.getCustomName());
         }

         world.entityJoinedWorld(entity);
      }
   }

   @Override
   public void onDispensed(ItemStack itemStack, World world, double x, double y, double z, int xOffset, int yOffset, int zOffset, Random random) {
      this.spawnEntity(itemStack, world, x + xOffset * 0.5, y + yOffset * 0.5, z + zOffset * 0.5);
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
      this.spawnEntity(
         itemStack,
         world,
         blockX + offX + direction.getOffsetX() * 0.5,
         blockY + offY + direction.getOffsetY() * 0.5,
         blockZ + offZ + direction.getOffsetZ() * 0.5
      );
   }
}
