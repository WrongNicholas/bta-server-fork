package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicSoulSand extends BlockLogic {
   public BlockLogicSoulSand(Block<?> block) {
      super(block, Material.soulsand);
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      float f = 0.125F;
      return AABB.getTemporaryBB(x, y, z, x + 1, y + 1 - f, z + 1);
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      entity.xd *= 0.4;
      entity.zd *= 0.4;
   }
}
