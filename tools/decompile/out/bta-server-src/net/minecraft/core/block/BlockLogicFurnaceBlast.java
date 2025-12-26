package net.minecraft.core.block;

import com.mojang.logging.LogUtils;
import java.util.Random;
import net.minecraft.core.Global;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityFurnaceBlast;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.slf4j.Logger;

public class BlockLogicFurnaceBlast extends BlockLogicFurnace {
   private static final Logger LOGGER = LogUtils.getLogger();

   public BlockLogicFurnaceBlast(Block<?> block, boolean flag) {
      super(block, flag);
      block.withEntity(TileEntityFurnaceBlast::new);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case PICK_BLOCK:
         case EXPLOSION:
         case PROPER_TOOL:
         case SILK_TOUCH:
            return new ItemStack[]{new ItemStack(Blocks.FURNACE_BLAST_IDLE)};
         default:
            return null;
      }
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (!world.isClientSide) {
         TileEntityFurnaceBlast furnaceBlast = (TileEntityFurnaceBlast)world.getTileEntity(x, y, z);
         player.displayFurnaceScreen(furnaceBlast);
      }

      return true;
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (this.isActive) {
         int l = world.getBlockMetadata(x, y, z);
         double posX = x + 0.5;
         double posY = y + rand.nextFloat() * 6.0F / 16.0;
         double posZ = z + 0.5;
         double f3 = 0.52;
         float f4 = rand.nextFloat() * 0.6F - 0.3F;
         if (l == 4) {
            world.spawnParticle("smoke", posX - f3, posY, posZ + f4, 0.0, 0.0, 0.0, 0);
            world.spawnParticle("largeSmoke", posX - f3, posY, posZ + f4, 0.0, 0.0, 0.0, 0);
         } else if (l == 5) {
            world.spawnParticle("smoke", posX + f3, posY, posZ + f4, 0.0, 0.0, 0.0, 0);
            world.spawnParticle("largeSmoke", posX + f3, posY, posZ + f4, 0.0, 0.0, 0.0, 0);
         } else if (l == 2) {
            world.spawnParticle("smoke", posX + f4, posY, posZ - f3, 0.0, 0.0, 0.0, 0);
            world.spawnParticle("largeSmoke", posX + f4, posY, posZ - f3, 0.0, 0.0, 0.0, 0);
         } else if (l == 3) {
            world.spawnParticle("smoke", posX + f4, posY, posZ + f3, 0.0, 0.0, 0.0, 0);
            world.spawnParticle("largeSmoke", posX + f4, posY, posZ + f3, 0.0, 0.0, 0.0, 0);
         }
      }
   }

   public static void updateFurnaceBlockState(boolean lit, World world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      TileEntity tileEntity = world.getTileEntity(x, y, z);
      if (tileEntity == null) {
         String msg = "Blast Furnace is missing Tile Entity at x: " + x + " y: " + y + " z: " + z + ", block will be removed!";
         if (Global.BUILD_CHANNEL.isUnstableBuild()) {
            throw new RuntimeException(msg);
         } else {
            world.setBlockWithNotify(x, y, z, 0);
            LOGGER.warn(msg);
         }
      } else {
         keepFurnaceInventory = true;
         if (lit) {
            world.setBlockWithNotify(x, y, z, Blocks.FURNACE_BLAST_ACTIVE.id());
         } else {
            world.setBlockWithNotify(x, y, z, Blocks.FURNACE_BLAST_IDLE.id());
         }

         keepFurnaceInventory = false;
         world.setBlockMetadataWithNotify(x, y, z, meta);
         tileEntity.validate();
         world.setTileEntity(x, y, z, tileEntity);
      }
   }
}
