package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.Nullable;

public class BlockLogicPortal extends BlockLogicTransparent implements IPainted {
   public static final int MAX_PORTAL_SIZE = 32;
   public static final int MASK_COLOR = 240;
   public static final int MASK_ROTATION = 3;
   public static final int MASK_COLOR_SET = 8;
   public static final int ORIENTATION_X = 0;
   public static final int ORIENTATION_Z = 1;
   public Dimension targetDimension;
   public Block<?> portalFrame;
   public Block<?> portalTrigger;

   public BlockLogicPortal(Block<?> block, Dimension targetDimension, Block<?> portalMaterial, Block<?> portalTrigger) {
      super(block, Material.portal);
      this.targetDimension = targetDimension;
      this.portalFrame = portalMaterial;
      this.portalTrigger = portalTrigger;
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      if ((meta & 1) > 0) {
         float f1 = 0.125F;
         float f3 = 0.5F;
         return AABB.getTemporaryBB(0.5F - f1, 0.0, 0.5F - f3, 0.5F + f1, 1.0, 0.5F + f3);
      } else {
         float f = 0.5F;
         float f2 = 0.125F;
         return AABB.getTemporaryBB(0.5F - f, 0.0, 0.5F - f2, 0.5F + f, 1.0, 0.5F + f2);
      }
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   public int[] getPortalDims(World world, int x, int y, int z, boolean expectMiddle) {
      return this.getPortalDims(world, x, y, z, false, expectMiddle);
   }

   public int[] getPortalDims(World world, int x, int y, int z, boolean swapOrientation, boolean expectMiddle) {
      int maxDims = 32;
      int ox = x;
      int oy = y;
      int oz = z;
      int maxBoundA = 0;
      int maxBoundY = 0;
      boolean foundBottom = false;
      boolean foundOrientation = false;
      boolean orientation = false;
      if (world.dimension.portalBlock != null && world.dimension.portalBlock != this.block) {
         return null;
      } else {
         for (int ry = 0; ry > -maxDims; ry--) {
            if (world.getBlockId(x, y + ry, z) == this.portalFrame.id()) {
               foundBottom = true;
               y += ry;
               break;
            }
         }

         if (!foundBottom) {
            return null;
         } else {
            y++;
            if (!swapOrientation) {
               for (int rx = 0; rx > -maxDims; rx--) {
                  if (world.getBlockId(x + rx, y, z) == this.portalFrame.id()) {
                     foundOrientation = true;
                     orientation = false;
                     x += rx;
                     break;
                  }
               }

               if (!foundOrientation) {
                  for (int rz = 0; rz > -maxDims; rz--) {
                     if (world.getBlockId(x, y, z + rz) == this.portalFrame.id()) {
                        foundOrientation = true;
                        orientation = true;
                        z += rz;
                        break;
                     }
                  }
               }
            } else {
               for (int rzx = 0; rzx > -maxDims; rzx--) {
                  if (world.getBlockId(x, y, z + rzx) == this.portalFrame.id()) {
                     foundOrientation = true;
                     orientation = true;
                     z += rzx;
                     break;
                  }
               }

               if (!foundOrientation) {
                  for (int rxx = 0; rxx > -maxDims; rxx--) {
                     if (world.getBlockId(x + rxx, y, z) == this.portalFrame.id()) {
                        foundOrientation = true;
                        orientation = false;
                        x += rxx;
                        break;
                     }
                  }
               }
            }

            if (!foundOrientation) {
               return null;
            } else {
               if (!orientation) {
                  for (int rxxx = 1; rxxx < maxDims; rxxx++) {
                     if (world.getBlockId(x + rxxx, y, z) == this.portalFrame.id()) {
                        maxBoundA = rxxx;
                        break;
                     }
                  }
               } else {
                  for (int rzxx = 1; rzxx < maxDims; rzxx++) {
                     if (world.getBlockId(x, y, z + rzxx) == this.portalFrame.id()) {
                        maxBoundA = rzxx;
                        break;
                     }
                  }
               }

               if (maxBoundA == 0) {
                  return swapOrientation ? null : this.getPortalDims(world, ox, oy, oz, true, expectMiddle);
               } else {
                  y--;

                  for (int ryx = 1; ryx < maxDims; ryx++) {
                     if (y + ryx >= world.getHeightBlocks()) {
                        return swapOrientation ? null : this.getPortalDims(world, ox, oy, oz, true, expectMiddle);
                     }

                     if (world.getBlockId(x + (!orientation ? 1 : 0), y + ryx, z + (orientation ? 1 : 0)) == this.portalFrame.id()) {
                        maxBoundY = ryx;
                        break;
                     }
                  }

                  if (maxBoundY == 0) {
                     return swapOrientation ? null : this.getPortalDims(world, ox, oy, oz, true, expectMiddle);
                  } else {
                     for (int ra = 0; ra <= maxBoundA; ra++) {
                        for (int ryx = 0; ryx <= maxBoundY; ryx++) {
                           if (ra != 0 && ra != maxBoundA && ryx != 0 && ryx != maxBoundY) {
                              int id = world.getBlockId(x + (!orientation ? ra : 0), y + ryx, z + (orientation ? ra : 0));
                              if (id != 0 && id != this.portalTrigger.id() && id != this.block.id()) {
                                 return swapOrientation ? null : this.getPortalDims(world, ox, oy, oz, true, expectMiddle);
                              }
                           } else if (world.getBlockId(x + (!orientation ? ra : 0), y + ryx, z + (orientation ? ra : 0)) != this.portalFrame.id()) {
                              return swapOrientation ? null : this.getPortalDims(world, ox, oy, oz, true, expectMiddle);
                           }
                        }
                     }

                     if (expectMiddle) {
                        for (int ra = 1; ra < maxBoundA; ra++) {
                           for (int ryxx = 1; ryxx < maxBoundY; ryxx++) {
                              int id = world.getBlockId(x + (!orientation ? ra : 0), y + ryxx, z + (orientation ? ra : 0));
                              if (id != this.block.id()) {
                                 return swapOrientation ? null : this.getPortalDims(world, ox, oy, oz, true, true);
                              }
                           }
                        }
                     }

                     return new int[]{orientation ? 1 : 0, x, y, z, maxBoundA, maxBoundY};
                  }
               }
            }
         }
      }
   }

   public boolean tryToCreatePortal(World world, int x, int y, int z, @Nullable DyeColor color) {
      if (color == null) {
         color = DyeColor.PURPLE;
      }

      int[] bounds = this.getPortalDims(world, x, y, z, false);
      if (bounds == null) {
         return false;
      } else {
         x = bounds[1];
         y = bounds[2];
         z = bounds[3];
         world.noNeighborUpdate = true;

         for (int ra = 1; ra < bounds[4]; ra++) {
            for (int ry = 1; ry < bounds[5]; ry++) {
               int _x = x + (bounds[0] == 0 ? ra : 0);
               int _y = y + ry;
               int _z = z + (bounds[0] == 1 ? ra : 0);
               world.setBlockAndMetadata(_x, _y, _z, this.block.id(), bounds[0] & 1);
            }
         }

         int _x = x + (bounds[0] == 0 ? 1 : 0);
         int _y = y + 1;
         int _z = z + (bounds[0] == 1 ? 1 : 0);
         world.setBlockMetadata(_x, _y, _z, bounds[0] & 15 | 2);
         this.setColor(world, _x, _y, _z, color);
         world.markBlocksDirty(
            x + (bounds[0] == 0 ? 1 : 0),
            y + 1,
            z + (bounds[0] == 1 ? 1 : 0),
            x + (bounds[0] == 0 ? bounds[4] : 0),
            y + bounds[5],
            z + (bounds[0] == 1 ? bounds[4] : 0)
         );
         world.noNeighborUpdate = false;
         return true;
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      int[] bounds = this.getPortalDims(world, x, y, z, true);
      if (bounds == null) {
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return null;
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      if (entity instanceof EntityItem && ((EntityItem)entity).item != null && ((EntityItem)entity).item.itemID == Items.DYE.id) {
         EntityItem entityItem = (EntityItem)entity;
         DyeColor color = DyeColor.colorFromItemMeta(entityItem.item.getMetadata());
         if (this.getColor(world, x, y, z) != color) {
            this.setColor(world, x, y, z, color);
            entityItem.item.stackSize--;
            if (entityItem.item.stackSize <= 0) {
               entityItem.remove();
               return;
            }
         }
      }

      if (entity.vehicle == null && entity.passenger == null) {
         entity.handlePortal(this.block.id(), this.getColor(world, x, y, z));
      }
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      int meta = world.getBlockMetadata(x, y, z);
      if ((meta & 2) > 0 && rand.nextInt(20) == 0) {
         world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.5, z + 0.5, "portal.portal", 0.5F, rand.nextFloat() * 0.4F + 0.8F);
      }

      for (int l = 0; l < 4; l++) {
         double px = (double)x + rand.nextFloat();
         double py = (double)y + rand.nextFloat();
         double pz = (double)z + rand.nextFloat();
         int i1 = rand.nextInt(2) * 2 - 1;
         double xd = (rand.nextDouble() - 0.5) * 0.5;
         double yd = (rand.nextDouble() - 0.5) * 0.5;
         double zd = (rand.nextDouble() - 0.5) * 0.5;
         if (world.getBlockId(x - 1, y, z) != this.block.id() && world.getBlockId(x + 1, y, z) != this.block.id()) {
            px = x + 0.5 + 0.25 * i1;
            xd = rand.nextFloat() * 2.0 * i1;
         } else {
            pz = z + 0.5 + 0.25 * i1;
            zd = rand.nextFloat() * 2.0 * i1;
         }

         world.spawnParticle("portal", px, py, pz, xd, yd, zd, this.fromMetadata(meta).blockMeta);
      }
   }

   @Override
   public DyeColor fromMetadata(int meta) {
      return (meta & 8) == 0 ? DyeColor.PURPLE : DyeColor.colorFromBlockMeta((meta & 240) >> 4);
   }

   @Override
   public int toMetadata(DyeColor color) {
      return color.blockMeta << 4 & 240;
   }

   @Override
   public int stripColorFromMetadata(int meta) {
      return meta & -241;
   }

   @Override
   public void removeDye(World world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockAndMetadataWithNotify(x, y, z, Blocks.STAIRS_PLANKS_OAK.id(), this.stripColorFromMetadata(meta));
   }

   @Override
   public DyeColor getColor(World world, int x, int y, int z) {
      return IPainted.super.getColor(world, x, y, z);
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      int[] bounds = this.getPortalDims(world, x, y, z, true);
      if (bounds == null) {
         world.setBlockWithNotify(x, y, z, 0);
      } else {
         int px = bounds[1];
         int py = bounds[2];
         int pz = bounds[3];

         for (int ra = 1; ra < bounds[4]; ra++) {
            for (int ry = 1; ry < bounds[5]; ry++) {
               int _x = px + (bounds[0] == 0 ? ra : 0);
               int _y = py + ry;
               int _z = pz + (bounds[0] == 1 ? ra : 0);
               this.setColorRaw(world, _x, _y, _z, color);
            }
         }
      }
   }

   private void setColorRaw(World world, int x, int y, int z, DyeColor color) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockMetadataWithNotify(x, y, z, this.stripColorFromMetadata(meta) | this.toMetadata(color) | 8);
   }
}
