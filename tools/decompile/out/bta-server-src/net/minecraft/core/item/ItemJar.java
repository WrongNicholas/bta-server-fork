package net.minecraft.core.item;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.animal.MobFireflyCluster;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemJar extends ItemPlaceable {
   protected ItemJar(String name, String namespaceId, int id) {
      super(name, namespaceId, id, Blocks.JAR_GLASS);
      this.setMaxStackSize(64);
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack stack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (player != null && player.isSneaking()) {
         return super.onUseItemOnBlock(stack, player, world, blockX, blockY, blockZ, side, xPlaced, yPlaced);
      } else {
         if (player != null) {
            int count = stack.stackSize;
            ItemStack used = this.onUseItem(stack, world, player);
            if (used != null && used.stackSize != count) {
               return true;
            }
         }

         return super.onUseItemOnBlock(stack, player, world, blockX, blockY, blockZ, side, xPlaced, yPlaced);
      }
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, @NotNull Player entityplayer) {
      if (entityplayer.isSneaking()) {
         return itemstack;
      } else {
         for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(entityplayer, entityplayer.bb.grow(2.0, 2.0, 2.0))) {
            if (entity instanceof MobFireflyCluster) {
               MobFireflyCluster fireflyCluster = (MobFireflyCluster)entity;
               MobFireflyCluster.FireflyColor colour = fireflyCluster.getColor();
               boolean didFillJar;
               if (colour == MobFireflyCluster.FireflyColor.BLUE) {
                  didFillJar = fillJar(entityplayer, new ItemStack(Items.LANTERN_FIREFLY_BLUE, 1));
               } else if (colour == MobFireflyCluster.FireflyColor.ORANGE) {
                  didFillJar = fillJar(entityplayer, new ItemStack(Items.LANTERN_FIREFLY_ORANGE, 1));
               } else if (colour == MobFireflyCluster.FireflyColor.RED) {
                  didFillJar = fillJar(entityplayer, new ItemStack(Items.LANTERN_FIREFLY_RED, 1));
               } else {
                  didFillJar = fillJar(entityplayer, new ItemStack(Items.LANTERN_FIREFLY_GREEN, 1));
               }

               if (!world.isClientSide && didFillJar) {
                  fireflyCluster.setFireflyCount(fireflyCluster.getFireflyCount() - 1);
                  if (fireflyCluster.getFireflyCount() <= 0) {
                     fireflyCluster.remove();
                  }
               }

               return itemstack;
            }
         }

         return itemstack;
      }
   }

   public static boolean fillJar(Player player, ItemStack itemToGive) {
      if (player.inventory.getCurrentItem().stackSize <= 1) {
         player.inventory.setItem(player.inventory.getCurrentItemIndex(), itemToGive);
      } else {
         player.inventory.insertItem(itemToGive, true);
      }

      if (itemToGive.stackSize < 1) {
         player.swingItem();
         player.inventory.getCurrentItem().consumeItem(player);
         return true;
      } else {
         return false;
      }
   }
}
