package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class BlockLogicFullyRotatable extends BlockLogic {
   public static final int MASK_DIRECTION = 7;

   public BlockLogicFullyRotatable(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
      Direction direction = mob.getPlacementDirection(side).getOpposite();
      world.setBlockMetadataWithNotify(x, y, z, directionToMeta(direction));
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      world.setBlockMetadataWithNotify(x, y, z, directionToMeta(side.getDirection()));
   }

   public static int directionToMeta(Direction direction) {
      return direction.getId() & 7;
   }

   public static Direction metaToDirection(int meta) {
      return Direction.getDirectionById(meta & 7);
   }
}
