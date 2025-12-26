package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.enums.PlacementMode;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class BlockLogicAxisAligned extends BlockLogic {
   public BlockLogicAxisAligned(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
      Axis axis = mob.getPlacementDirection(side, PlacementMode.SIDE).getAxis();
      world.setBlockMetadataWithNotify(x, y, z, axisToMeta(axis));
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      Axis axis = side.getAxis();
      world.setBlockMetadataWithNotify(x, y, z, axisToMeta(axis));
   }

   public static int axisToMeta(Axis axis) {
      if (axis == Axis.X) {
         return 2;
      } else {
         return axis == Axis.Z ? 1 : 0;
      }
   }

   public static Axis metaToAxis(int meta) {
      if (meta == 2) {
         return Axis.X;
      } else {
         return meta == 1 ? Axis.Z : Axis.Y;
      }
   }
}
