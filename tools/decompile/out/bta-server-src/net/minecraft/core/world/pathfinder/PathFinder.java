package net.minecraft.core.world.pathfinder;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicDoor;
import net.minecraft.core.block.BlockLogicTrapDoor;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.util.collection.IntHashMap;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.Nullable;

public class PathFinder {
   private final WorldSource worldSource;
   private final BinaryHeap openSet = new BinaryHeap();
   private final IntHashMap<Node> closedSet = new IntHashMap<>();
   private final Node[] neighbors = new Node[32];

   public PathFinder(WorldSource worldSource) {
      this.worldSource = worldSource;
   }

   @Nullable
   public Path findPath(Entity entity, Entity target, float distance) {
      return this.findPath(entity, target.x, target.bb.minY, target.z, distance);
   }

   @Nullable
   public Path findPath(Entity entity, int xt, int yt, int zt, float distance) {
      return this.findPath(entity, (double)(xt + 0.5F), (double)(yt + 0.5F), (double)(zt + 0.5F), distance);
   }

   @Nullable
   private Path findPath(Entity entity, double xt, double yt, double zt, float distance) {
      this.openSet.clear();
      this.closedSet.clear();
      Node pathpoint = this.getNode(MathHelper.floor(entity.bb.minX), MathHelper.floor(entity.bb.minY), MathHelper.floor(entity.bb.minZ));
      Node pathpoint1 = this.getNode(MathHelper.floor(xt - entity.bbWidth / 2.0F), MathHelper.floor(yt), MathHelper.floor(zt - entity.bbWidth / 2.0F));
      Node pathpoint2 = new Node(
         MathHelper.floor_float(entity.bbWidth + 1.0F), MathHelper.floor_float(entity.bbHeight + 1.0F), MathHelper.floor_float(entity.bbWidth + 1.0F)
      );
      return this.findPath(entity, pathpoint, pathpoint1, pathpoint2, distance);
   }

   @Nullable
   private Path findPath(Entity entity, Node pathpoint, Node pathpoint1, Node pathpoint2, float f) {
      pathpoint.g = 0.0F;
      pathpoint.h = pathpoint.distanceTo(pathpoint1);
      pathpoint.f = pathpoint.h;
      this.openSet.clear();
      this.openSet.insert(pathpoint);
      Node pathpoint3 = pathpoint;

      while (!this.openSet.isEmpty()) {
         Node pathpoint4 = this.openSet.pop();
         if (pathpoint4.equals(pathpoint1)) {
            return this.reconstructPath(pathpoint, pathpoint1);
         }

         if (pathpoint4.distanceTo(pathpoint1) < pathpoint3.distanceTo(pathpoint1)) {
            pathpoint3 = pathpoint4;
         }

         pathpoint4.closed = true;
         int i = this.getNeighbors(entity, pathpoint4, pathpoint2, pathpoint1, f);

         for (int j = 0; j < i; j++) {
            Node pathpoint5 = this.neighbors[j];
            float f1 = pathpoint4.g + pathpoint4.distanceTo(pathpoint5);
            if (!pathpoint5.inOpenSet() || f1 < pathpoint5.g) {
               pathpoint5.cameFrom = pathpoint4;
               pathpoint5.g = f1;
               pathpoint5.h = pathpoint5.distanceTo(pathpoint1);
               if (pathpoint5.inOpenSet()) {
                  this.openSet.changeCost(pathpoint5, pathpoint5.g + pathpoint5.h);
               } else {
                  pathpoint5.f = pathpoint5.g + pathpoint5.h;
                  this.openSet.insert(pathpoint5);
               }
            }
         }
      }

      return pathpoint3 == pathpoint ? null : this.reconstructPath(pathpoint, pathpoint3);
   }

   private int getNeighbors(Entity entity, Node pathpoint, Node pathpoint1, Node pathpoint2, float f) {
      int i = 0;
      int j = 0;
      if (this.isFree(entity, pathpoint.x, pathpoint.y + 1, pathpoint.z, pathpoint1) == 1) {
         j = 1;
      }

      Node pathpoint3 = this.getNode(entity, pathpoint.x, pathpoint.y, pathpoint.z + 1, pathpoint1, j);
      Node pathpoint4 = this.getNode(entity, pathpoint.x - 1, pathpoint.y, pathpoint.z, pathpoint1, j);
      Node pathpoint5 = this.getNode(entity, pathpoint.x + 1, pathpoint.y, pathpoint.z, pathpoint1, j);
      Node pathpoint6 = this.getNode(entity, pathpoint.x, pathpoint.y, pathpoint.z - 1, pathpoint1, j);
      if (pathpoint3 != null && !pathpoint3.closed && pathpoint3.distanceTo(pathpoint2) < f) {
         this.neighbors[i++] = pathpoint3;
      }

      if (pathpoint4 != null && !pathpoint4.closed && pathpoint4.distanceTo(pathpoint2) < f) {
         this.neighbors[i++] = pathpoint4;
      }

      if (pathpoint5 != null && !pathpoint5.closed && pathpoint5.distanceTo(pathpoint2) < f) {
         this.neighbors[i++] = pathpoint5;
      }

      if (pathpoint6 != null && !pathpoint6.closed && pathpoint6.distanceTo(pathpoint2) < f) {
         this.neighbors[i++] = pathpoint6;
      }

      return i;
   }

   private Node getNode(Entity entity, int x, int y, int z, Node pathpoint, int l) {
      Node pathpoint1 = null;
      if (this.isFree(entity, x, y, z, pathpoint) == 1) {
         pathpoint1 = this.getNode(x, y, z);
      }

      if (pathpoint1 == null && l > 0 && this.isFree(entity, x, y + l, z, pathpoint) == 1) {
         pathpoint1 = this.getNode(x, y + l, z);
         y += l;
      }

      if (pathpoint1 != null) {
         int i1 = 0;
         int j1 = 0;

         while (y > 0 && (j1 = this.isFree(entity, x, y - 1, z, pathpoint)) == 1) {
            if (++i1 >= 4) {
               return null;
            }

            if (--y > 0) {
               pathpoint1 = this.getNode(x, y, z);
            }
         }

         if (j1 == -2) {
            return null;
         }
      }

      return pathpoint1;
   }

   private Node getNode(int x, int y, int z) {
      int l = Node.createHash(x, y, z);
      Node pathpoint = this.closedSet.get(l);
      if (pathpoint == null) {
         pathpoint = new Node(x, y, z);
         this.closedSet.put(l, pathpoint);
      }

      return pathpoint;
   }

   private int isFree(Entity entity, int x, int y, int z, Node pathpoint) {
      for (int x1 = x; x1 < x + pathpoint.x; x1++) {
         for (int y1 = y; y1 < y + pathpoint.y; y1++) {
            for (int z1 = z; z1 < z + pathpoint.z; z1++) {
               int blockId = this.worldSource.getBlockId(x1, y1, z1);
               if (blockId > 0) {
                  if (Block.hasLogicClass(Blocks.blocksList[blockId], BlockLogicDoor.class)) {
                     int blockMetadata = this.worldSource.getBlockMetadata(x1, y1, z1);
                     if (!BlockLogicDoor.isOpen(blockMetadata)) {
                        return 0;
                     }
                  } else {
                     if (entity instanceof MobCreeper) {
                        int blockMetadata = this.worldSource.getBlockMetadata(x1, y1, z1);
                        if (Block.hasLogicClass(Blocks.blocksList[blockId], BlockLogicTrapDoor.class)) {
                           boolean isTopClosedTrapdoor = !BlockLogicTrapDoor.isTrapdoorOpen(blockMetadata) && BlockLogicTrapDoor.isUpperHalf(blockMetadata);
                           if (isTopClosedTrapdoor) {
                              return 1;
                           }
                        }
                     }

                     Material material = Blocks.blocksList[blockId].getMaterial();
                     if (material.blocksMotion()) {
                        return 0;
                     }

                     if (material == Material.water) {
                        return -1;
                     }

                     if (material == Material.lava) {
                        return -2;
                     }
                  }
               }
            }
         }
      }

      return 1;
   }

   private Path reconstructPath(Node pathpoint, Node pathpoint1) {
      int i = 1;

      for (Node pathpoint2 = pathpoint1; pathpoint2.cameFrom != null; pathpoint2 = pathpoint2.cameFrom) {
         i++;
      }

      Node[] apathpoint = new Node[i];
      Node pathpoint3 = pathpoint1;

      for (apathpoint[--i] = pathpoint1; pathpoint3.cameFrom != null; apathpoint[--i] = pathpoint3) {
         pathpoint3 = pathpoint3.cameFrom;
      }

      return new Path(apathpoint);
   }
}
