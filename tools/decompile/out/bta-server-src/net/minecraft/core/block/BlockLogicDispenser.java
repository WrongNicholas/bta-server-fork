package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntityDispenser;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.entity.projectile.ProjectileArrowGolden;
import net.minecraft.core.entity.projectile.ProjectileCannonball;
import net.minecraft.core.item.IDispensable;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicDispenser extends BlockLogicVeryRotatable {
   public static final int MASK_POWERED = 8;

   public BlockLogicDispenser(Block<?> block) {
      super(block, Material.stone);
      block.withEntity(TileEntityDispenser::new);
   }

   @Override
   public int tickDelay() {
      return 4;
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (world.isClientSide) {
         return true;
      } else {
         TileEntityDispenser tileEntityDispenser = (TileEntityDispenser)world.getTileEntity(x, y, z);
         player.displayDispenserScreen(tileEntityDispenser);
         return true;
      }
   }

   private void dispenseItem(World world, int x, int y, int z, Random random) {
      Direction direction = BlockLogicVeryRotatable.metaToDirection(world.getBlockMetadata(x, y, z));
      int xOffset = direction.getOffsetX();
      int yOffset = direction.getOffsetY();
      int zOffset = direction.getOffsetZ();
      TileEntityDispenser tileEntity = (TileEntityDispenser)world.getTileEntity(x, y, z);
      ItemStack itemStack = tileEntity.getRandomStackFromInventory();
      double px = x + xOffset * 0.6 + 0.5;
      double py = y + yOffset * 0.6 + 0.5;
      double pz = z + zOffset * 0.6 + 0.5;
      if (itemStack == null) {
         world.playBlockEvent(1001, x, y, z, 0);
      } else {
         if (itemStack.getItem() instanceof IDispensable) {
            IDispensable dispensable = (IDispensable)itemStack.getItem();
            dispensable.onDispensed(itemStack, world, px, py, pz, xOffset, yOffset, zOffset, random);
            world.playBlockEvent(1002, x, y, z, 0);
         } else if (itemStack.itemID != Items.AMMO_ARROW.id && itemStack.itemID != Items.AMMO_ARROW_GOLD.id) {
            if (itemStack.itemID == Items.AMMO_CHARGE_EXPLOSIVE.id) {
               ProjectileCannonball projectileCannonball = new ProjectileCannonball(world, px, py, pz);
               projectileCannonball.setHeading(xOffset, yOffset + 0.1, zOffset, 1.1F, 6.0F);
               world.entityJoinedWorld(projectileCannonball);
               world.playBlockEvent(1002, x, y, z, 0);
            } else {
               EntityItem item = new EntityItem(world, px, py - 0.3, pz, itemStack);
               double randOffset = random.nextDouble() * 0.1 + 0.2;
               item.xd = xOffset * randOffset;
               item.yd = yOffset + 0.2;
               item.zd = zOffset * randOffset;
               item.xd = item.xd + random.nextGaussian() * 0.0075 * 6.0;
               item.yd = item.yd + random.nextGaussian() * 0.0075 * 6.0;
               item.zd = item.zd + random.nextGaussian() * 0.0075 * 6.0;
               world.entityJoinedWorld(item);
               world.playBlockEvent(1000, x, y, z, 0);
            }
         } else {
            ProjectileArrow arrow = (ProjectileArrow)(itemStack.itemID == Items.AMMO_ARROW.id
               ? new ProjectileArrow(world, px, py, pz, 0)
               : new ProjectileArrowGolden(world, px, py, pz));
            arrow.setHeading(xOffset, yOffset + 0.1, zOffset, 1.1F, 6.0F);
            arrow.setDoesArrowBelongToPlayer(true);
            world.entityJoinedWorld(arrow);
            world.playBlockEvent(1002, x, y, z, 0);
         }

         world.playBlockEvent(2000, x, y, z, direction.getId());
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      boolean flag = world.hasNeighborSignal(x, y, z);
      int meta = world.getBlockMetadata(x, y, z);
      if (flag && (meta & 8) == 0) {
         world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay());
         world.setBlockMetadata(x, y, z, meta | 8);
      } else if (!flag) {
         world.setBlockMetadata(x, y, z, meta & -9);
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      this.dispenseItem(world, x, y, z, rand);
   }
}
