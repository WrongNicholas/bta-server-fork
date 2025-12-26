package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.world.World;

public class BlockLogicMesh extends BlockLogicTransparent {
   public BlockLogicMesh(Block<?> block) {
      super(block, Material.metal);
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean canPlaceOnSurface() {
      return true;
   }

   @Override
   public boolean collidesWithEntity(Entity entity, World world, int x, int y, int z) {
      return !(entity instanceof EntityItem);
   }
}
