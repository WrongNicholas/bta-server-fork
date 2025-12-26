package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public abstract class BlockLogicFluid extends BlockLogic {
   public BlockLogicFluid(Block<?> block, Material material) {
      super(block, material);
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   }

   public static float getWaterVolume(int data) {
      data &= 15;
      if (data >= 8) {
         data = 0;
      }

      return (data + 1) / 8.0F;
   }

   protected int getDepth(WorldSource world, int x, int y, int z) {
      return world.getBlockMaterial(x, y, z) != this.material ? -1 : world.getBlockMetadata(x, y, z) & 15;
   }

   protected int getRenderedDepth(WorldSource blockAccess, int x, int y, int z) {
      if (blockAccess.getBlockMaterial(x, y, z) != this.material) {
         return -1;
      } else {
         int meta = blockAccess.getBlockMetadata(x, y, z) & 15;
         if (meta >= 8) {
            meta = 0;
         }

         return meta;
      }
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean canCollideCheck(int meta, boolean shouldCollideWithFluids) {
      return shouldCollideWithFluids && (meta & 15) == 0;
   }

   @Override
   public boolean getIsBlockSolid(WorldSource blockAccess, int x, int y, int z, Side side) {
      Material material = blockAccess.getBlockMaterial(x, y, z);
      if (material == this.material) {
         return false;
      } else if (material == Material.ice) {
         return false;
      } else {
         return side == Side.TOP ? true : super.getIsBlockSolid(blockAccess, x, y, z, side);
      }
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
   }

   private Vec3 getFlow(WorldSource world, int x, int y, int z) {
      Vec3 result = Vec3.getTempVec3(0.0, 0.0, 0.0);
      int depthLocal = this.getRenderedDepth(world, x, y, z);

      for (int i = 0; i < 4; i++) {
         int checkX = x;
         int checkZ = z;
         if (i == 0) {
            checkX = x - 1;
         }

         if (i == 1) {
            checkZ = z - 1;
         }

         if (i == 2) {
            checkX++;
         }

         if (i == 3) {
            checkZ++;
         }

         int depthCheck = this.getRenderedDepth(world, checkX, y, checkZ);
         if (depthCheck < 0) {
            if (!world.getBlockMaterial(checkX, y, checkZ).blocksMotion()) {
               depthCheck = this.getRenderedDepth(world, checkX, y - 1, checkZ);
               if (depthCheck >= 0) {
                  int mult = depthCheck - (depthLocal - 8);
                  result = result.add((checkX - x) * mult, (y - y) * mult, (checkZ - z) * mult);
               }
            }
         } else {
            int mult = depthCheck - depthLocal;
            result = result.add((checkX - x) * mult, (y - y) * mult, (checkZ - z) * mult);
         }
      }

      return result.normalize();
   }

   @Override
   public void handleEntityInside(World world, int x, int y, int z, Entity entity, Vec3 entityVelocity) {
      if (!entity.noPhysics) {
         Vec3 flowVector = this.getFlow(world, x, y, z);
         entityVelocity.x = entityVelocity.x + flowVector.x;
         entityVelocity.y = entityVelocity.y + flowVector.y;
         entityVelocity.z = entityVelocity.z + flowVector.z;
      }
   }

   @Override
   public int tickDelay() {
      if (this.material == Material.water) {
         return 5;
      } else {
         return this.material != Material.lava ? 0 : 30;
      }
   }

   @Override
   public float getBlockBrightness(WorldSource world, int x, int y, int z) {
      float f = world.getLightBrightness(x, y, z);
      float f1 = world.getLightBrightness(x, y + 1, z);
      return Math.max(f, f1);
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      super.updateTick(world, x, y, z, rand);
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (this.material == Material.water && rand.nextInt(64) == 0) {
         int data = world.getBlockMetadata(x, y, z) & 15;
         if (data > 0 && data < 8) {
            world.playSoundEffect(
               null, SoundCategory.WORLD_SOUNDS, x + 0.5F, y + 0.5F, z + 0.5F, "liquid.water", rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() * 1.0F + 0.5F
            );
         }
      }

      if (this.material == Material.lava
         && world.getBlockMaterial(x, y + 1, z) == Material.air
         && !world.isBlockOpaqueCube(x, y + 1, z)
         && rand.nextInt(100) == 0) {
         double xOff = (double)x + rand.nextFloat();
         double yOff = y + this.getBounds().maxY;
         double zOff = (double)z + rand.nextFloat();
         world.spawnParticle("lava", xOff, yOff, zOff, 0.0, 0.0, 0.0, 0);
      }
   }

   public static double getSlopeAngle(WorldSource blockAccess, int x, int y, int z, Material material) {
      Vec3 vec3 = null;
      if (material == Material.water) {
         vec3 = ((BlockLogicFluid) Blocks.FLUID_WATER_FLOWING.getLogic()).getFlow(blockAccess, x, y, z);
      }

      if (material == Material.lava) {
         vec3 = ((BlockLogicFluid) Blocks.FLUID_LAVA_FLOWING.getLogic()).getFlow(blockAccess, x, y, z);
      }

      return vec3 != null && (vec3.x != 0.0 || vec3.z != 0.0) ? Math.atan2(vec3.z, vec3.x) - (Math.PI / 2) : -1000.0;
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      this.checkForHarden(world, x, y, z);
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      this.checkForHarden(world, x, y, z);
   }

   private void checkForHarden(World world, int x, int y, int z) {
      if (world.getBlockId(x, y, z) == this.block.id()) {
         if (this.material == Material.lava
            && (
               world.getBlockMaterial(x, y, z - 1) == Material.water
                  || world.getBlockMaterial(x, y, z + 1) == Material.water
                  || world.getBlockMaterial(x - 1, y, z) == Material.water
                  || world.getBlockMaterial(x + 1, y, z) == Material.water
                  || world.getBlockMaterial(x, y + 1, z) == Material.water
            )) {
            int data = world.getBlockMetadata(x, y, z) & 15;
            if (data == 0) {
               if (world.dimension == Dimension.NETHER) {
                  world.setBlockWithNotify(x, y, z, Blocks.COBBLE_NETHERRACK_IGNEOUS.id());
               } else {
                  world.setBlockWithNotify(x, y, z, Blocks.OBSIDIAN.id());
               }
            } else if (data <= 2) {
               world.setBlockWithNotify(x, y, z, Blocks.COBBLE_GRANITE.id());
            } else if (data <= 4) {
               world.setBlockWithNotify(x, y, z, Blocks.COBBLE_STONE.id());
            } else {
               world.setBlockWithNotify(x, y, z, Blocks.COBBLE_BASALT.id());
            }

            this.fizz(world, x, y, z);
         }

         if (this.material == Material.water
            && (
               world.getBlockMaterial(x, y, z - 1) == Material.lava
                  || world.getBlockMaterial(x, y, z + 1) == Material.lava
                  || world.getBlockMaterial(x - 1, y, z) == Material.lava
                  || world.getBlockMaterial(x + 1, y, z) == Material.lava
                  || world.getBlockMaterial(x, y + 1, z) == Material.lava
            )) {
            int data = world.getBlockMetadata(x, y, z) & 15;
            if (data == 0) {
               world.setBlockWithNotify(x, y, z, Blocks.COBBLE_LIMESTONE.id());
               this.fizz(world, x, y, z);
            }
         }
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return null;
   }

   protected void fizz(World world, int x, int y, int z) {
      world.playSoundEffect(
         null, SoundCategory.WORLD_SOUNDS, x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F
      );

      for (int i = 0; i < 8; i++) {
         world.spawnParticle("largesmoke", x + Math.random(), y + 1.2, z + Math.random(), 0.0, 0.0, 0.0, 0);
      }
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      float f7 = getFluidHeight(world, x, y, z, this.material);
      float f8 = getFluidHeight(world, x, y, z + 1, this.material);
      float f9 = getFluidHeight(world, x + 1, y, z + 1, this.material);
      float f10 = getFluidHeight(world, x + 1, y, z, this.material);
      return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, Math.max((double)((f7 + f8 + f9 + f10) / 4.0F), 0.2), 1.0);
   }

   public static float getFluidHeight(WorldSource worldSource, int x, int y, int z, Material material) {
      int l = 0;
      float f = 0.0F;

      for (int i1 = 0; i1 < 4; i1++) {
         int j1 = x - (i1 & 1);
         int l1 = z - (i1 >> 1 & 1);
         if (worldSource.getBlockMaterial(j1, y + 1, l1) == material) {
            return 1.0F;
         }

         Material material1 = worldSource.getBlockMaterial(j1, y, l1);
         if (material1 == material) {
            int i2 = worldSource.getBlockMetadata(j1, y, l1);
            if (i2 >= 8 || i2 == 0) {
               f += getWaterVolume(i2) * 10.0F;
               l += 10;
            }

            f += getWaterVolume(i2);
            l++;
         } else if (!material1.isSolid()) {
            f++;
            l++;
         }
      }

      return Math.max(1.0F - f / l, 0.015F);
   }
}
