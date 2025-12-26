package net.minecraft.core.item;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRail;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.vehicle.EntityMinecart;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemMinecart extends Item {
   public final int minecartType;

   public ItemMinecart(String name, String namespaceId, int id, int type) {
      super(name, namespaceId, id);
      this.setMaxStackSize(1);
      this.minecartType = type;
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      Block<?> block = world.getBlock(blockX, blockY, blockZ);
      if (!Block.hasLogicClass(block, BlockLogicRail.class)) {
         return false;
      } else {
         if (!world.isClientSide) {
            world.entityJoinedWorld(new EntityMinecart(world, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, this.minecartType));
         }

         if (player == null || player.getGamemode().consumeBlocks()) {
            itemstack.stackSize--;
         }

         return true;
      }
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
      int x = blockX + direction.getOffsetX();
      int y = blockY + direction.getOffsetY();
      int z = blockZ + direction.getOffsetZ();
      this.onUseItemOnBlock(itemStack, null, world, x, y, z, direction.getSide(), 0.5, 0.5);
   }
}
