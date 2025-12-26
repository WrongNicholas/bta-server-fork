package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;

public class BlockLogicSpikes extends BlockLogic {
   public static final int MASK_POWERED = 1;

   public BlockLogicSpikes(Block<?> block, Material mat) {
      super(block, mat);
      float thickness = 0.1875F;
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, thickness, 1.0);
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      if (!world.isClientSide) {
         int meta = world.getBlockMetadata(x, y, z);
         if (world.hasNeighborSignal(x, y, z)) {
            world.setBlockMetadataWithNotify(x, y, z, meta | 1);
         }
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(Blocks.SPIKES)};
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (!world.isClientSide) {
         int meta = world.getBlockMetadata(x, y, z);
         boolean wasPowered = (meta & 1) != 0;
         boolean isPowered = world.hasNeighborSignal(x, y, z);
         if (isPowered != wasPowered) {
            if (isPowered) {
               world.playBlockEvent(null, 1007, x, y, z, 0);
               world.setBlockMetadataWithNotify(x, y, z, 1);
            } else {
               world.playBlockEvent(null, 1006, x, y, z, 0);
               world.setBlockMetadataWithNotify(x, y, z, 0);
            }
         }
      }
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      float thickness = 0.1875F;
      return AABB.getTemporaryBB(x, y, z, x + 1, y + thickness, z + 1);
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      int metaData = world.getBlockMetadata(x, y, z);
      if (isSpikesUp(metaData) && entity instanceof Mob) {
         if (entity instanceof Player && entity.isSneaking()) {
            if (entity.tickCount % 20 == 0) {
               entity.hurt(null, 1, DamageType.GENERIC);
            }

            entity.yd = 0.0;
            return;
         }

         if (entity.tickCount % 10 == 0) {
            entity.hurt(null, 2, DamageType.GENERIC);
         }

         entity.stuckInSpikes = true;
      }
   }

   @Override
   public boolean collidesWithEntity(Entity entity, World world, int x, int y, int z) {
      return !(entity instanceof EntityItem);
   }

   public static boolean isSpikesUp(int i) {
      return (i & 1) == 0;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }
}
