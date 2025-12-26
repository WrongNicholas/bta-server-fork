package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicNetherrackIgneous extends BlockLogic {
   public BlockLogicNetherrackIgneous(Block<?> block) {
      super(block, Material.netherrack);
      block.setTicking(true);
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      float f = 0.125F;
      return AABB.getTemporaryBB(x, y, z, x + 1, y + 1 - f, z + 1);
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      if (!(entity instanceof EntityItem)) {
         entity.fireHurt();
      }
   }

   @Override
   public int tickDelay() {
      return 20;
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay() + world.rand.nextInt(5) - world.rand.nextInt(5));
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay() + world.rand.nextInt(5) - world.rand.nextInt(5));
   }

   public boolean canMelt(World world, int x, int y, int z) {
      boolean canMelt = false;

      for (Side side : Side.sides) {
         Block<?> block = world.getBlock(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ());
         Material adjacentMaterial = block == null ? Material.air : block.getMaterial();
         if (adjacentMaterial == Material.water) {
            return false;
         }

         canMelt |= adjacentMaterial == Material.lava;
      }

      return canMelt;
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (this.canMelt(world, x, y, z)) {
         world.setBlockWithNotify(x, y, z, Blocks.FLUID_LAVA_FLOWING.id());
      }
   }
}
