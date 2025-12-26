package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class BlockLogicRotatable extends BlockLogic {
   public static final int MASK_DIRECTION = 7;

   public BlockLogicRotatable(Block<?> block, Material material) {
      super(block, material);
   }

   public static Direction getDirectionFromMeta(int meta) {
      return Direction.getDirectionById(meta & 7);
   }

   public static int setDirection(int meta, Direction direction) {
      return meta & -8 | direction.getId();
   }

   @Override
   public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
      world.setBlockMetadataWithNotify(x, y, z, mob.getHorizontalPlacementDirection(side).getOpposite().getId());
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      if (!side.isHorizontal()) {
         side = Side.SOUTH;
      }

      world.setBlockMetadataWithNotify(x, y, z, setDirection(0, side.getDirection()));
   }

   public static void setDefaultDirection(World world, int x, int y, int z) {
      if (!world.isClientSide) {
         int bN = world.getBlockId(x, y, z - 1);
         int bS = world.getBlockId(x, y, z + 1);
         int bW = world.getBlockId(x - 1, y, z);
         int bE = world.getBlockId(x + 1, y, z);
         Direction direction = Direction.NORTH;
         if (Blocks.solid[bN] && !Blocks.solid[bS]) {
            direction = Direction.SOUTH;
         }

         if (Blocks.solid[bS] && !Blocks.solid[bN]) {
            direction = Direction.NORTH;
         }

         if (Blocks.solid[bW] && !Blocks.solid[bE]) {
            direction = Direction.EAST;
         }

         if (Blocks.solid[bE] && !Blocks.solid[bW]) {
            direction = Direction.WEST;
         }

         world.setBlockMetadataWithNotify(x, y, z, setDirection(world.getBlockMetadata(x, y, z), direction));
      }
   }
}
