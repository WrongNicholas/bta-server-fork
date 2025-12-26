package net.minecraft.core.block;

import com.mojang.logging.LogUtils;
import java.util.Random;
import net.minecraft.core.Global;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityTrommel;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.slf4j.Logger;

public class BlockLogicTrommel extends BlockLogicRotatable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private boolean isActive;
   public static boolean keepTrommelInventory = false;

   public BlockLogicTrommel(Block<?> block, Material material, boolean isActive) {
      super(block, material);
      this.isActive = isActive;
      block.withEntity(TileEntityTrommel::new);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case PICK_BLOCK:
         case EXPLOSION:
         case PROPER_TOOL:
         case SILK_TOUCH:
            return new ItemStack[]{new ItemStack(Blocks.TROMMEL_IDLE)};
         default:
            return null;
      }
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (!world.isClientSide) {
         TileEntityTrommel tileEntityTrommel = (TileEntityTrommel)world.getTileEntity(x, y, z);
         player.displayTrommelScreen(tileEntityTrommel);
      }

      return true;
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (this.isActive) {
         double xPos = (double)x + rand.nextFloat();
         double yPos = y + rand.nextFloat() * 0.5 + 1.0;
         double zPos = (double)z + rand.nextFloat();
         world.spawnParticle("smoke", xPos, yPos, zPos, 0.0, 0.0, 0.0, 0);
      }
   }

   public static void updateTrommelBlockState(boolean lit, World world, int x, int y, int z) {
      int l = world.getBlockMetadata(x, y, z);
      TileEntity tileEntity = world.getTileEntity(x, y, z);
      if (tileEntity == null) {
         String msg = "Trommel is missing Tile Entity at x: " + x + " y: " + y + " z: " + z + ", block will be removed!";
         if (Global.BUILD_CHANNEL.isUnstableBuild()) {
            throw new RuntimeException(msg);
         } else {
            world.setBlockWithNotify(x, y, z, 0);
            LOGGER.warn(msg);
         }
      } else {
         keepTrommelInventory = true;
         if (lit) {
            world.setBlockWithNotify(x, y, z, Blocks.TROMMEL_ACTIVE.id());
         } else {
            world.setBlockWithNotify(x, y, z, Blocks.TROMMEL_IDLE.id());
         }

         keepTrommelInventory = false;
         world.setBlockMetadataWithNotify(x, y, z, l);
         tileEntity.validate();
         world.setTileEntity(x, y, z, tileEntity);
      }
   }
}
