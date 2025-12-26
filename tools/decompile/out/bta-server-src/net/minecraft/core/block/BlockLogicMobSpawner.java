package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityMobSpawner;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class BlockLogicMobSpawner extends BlockLogicMobSpawnerDeactivated {
   public BlockLogicMobSpawner(Block<?> block) {
      super(block);
      block.withEntity(TileEntityMobSpawner::new);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (player.getGamemode() != Gamemode.creative) {
         return false;
      } else {
         player.displayMobPickerScreen(x, y, z);
         return true;
      }
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      TileEntityMobSpawner tileEntity = (TileEntityMobSpawner)world.getTileEntity(x, y, z);
      tileEntity.setMobId("none");
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      if (!world.isClientSide) {
         TileEntityMobSpawner tileEntityMobSpawner = (TileEntityMobSpawner)world.getTileEntity(x, y, z);
         if (tileEntityMobSpawner == null) {
            return;
         }

         int amountToDrop = world.rand.nextInt(10) + 10;

         for (int l = 0; l < amountToDrop; l++) {
            String mobInSpawner = tileEntityMobSpawner.getMobId();
            if (mobInSpawner != null) {
               ItemStack itemstack;
               switch (mobInSpawner) {
                  case "minecraft:zombie":
                     itemstack = new ItemStack(Items.CLOTH);
                     break;
                  case "minecraft:skeleton":
                     if (world.rand.nextInt(2) == 0) {
                        itemstack = new ItemStack(Items.BONE);
                     } else {
                        itemstack = new ItemStack(Items.AMMO_ARROW);
                     }
                     break;
                  case "minecraft:zombie_armored":
                     itemstack = new ItemStack(Items.CHAINLINK);
                     break;
                  case "minecraft:spider":
                     itemstack = new ItemStack(Items.STRING);
                     break;
                  case "minecraft:snowman":
                     itemstack = new ItemStack(Items.AMMO_SNOWBALL);
                     break;
                  default:
                     itemstack = null;
               }

               if (itemstack != null) {
                  float f = world.rand.nextFloat() * 0.8F + 0.1F;
                  float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
                  float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

                  while (itemstack.stackSize > 0) {
                     int i1 = world.rand.nextInt(21) + 10;
                     if (i1 > itemstack.stackSize) {
                        i1 = itemstack.stackSize;
                     }

                     itemstack.stackSize -= i1;
                     EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.itemID, i1, itemstack.getMetadata()));
                     float f3 = 0.05F;
                     entityitem.xd = (float)world.rand.nextGaussian() * f3;
                     entityitem.yd = (float)world.rand.nextGaussian() * f3 + 0.2F;
                     entityitem.zd = (float)world.rand.nextGaussian() * f3;
                     world.entityJoinedWorld(entityitem);
                  }
               }
            }
         }
      }

      super.onBlockRemoved(world, x, y, z, data);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return dropCause == EnumDropCause.SILK_TOUCH ? new ItemStack[]{new ItemStack(Blocks.MOBSPAWNER_DEACTIVATED)} : null;
   }
}
