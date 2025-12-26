package net.minecraft.core.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.block.logic.RailDirection;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.chunk.ChunkPosition;

public class BlockLogicRail extends BlockLogic {
   public static final int MASK_DIRECTION = 7;
   public static final int MASK_POWERED = 8;
   public final boolean isPowered;

   public static boolean isRailBlockAt(World world, int x, int y, int z) {
      Block<?> b;
      return (b = world.getBlock(x, y, z)) != null && b.getLogic() instanceof BlockLogicRail;
   }

   public RailDirection getRailDirection(WorldSource world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      if (this.isPowered) {
         meta &= 7;
      }

      return RailDirection.getFromMeta(meta);
   }

   protected BlockLogicRail(Block<?> block, boolean isPowered) {
      super(block, Material.decoration);
      this.isPowered = isPowered;
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
   }

   public boolean getIsPowered() {
      return this.isPowered;
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      RailDirection direction = this.getRailDirection(world, x, y, z);
      return direction.isSloped() ? AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 0.725F, 1.0) : AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return world.canPlaceOnSurfaceOfBlock(x, y - 1, z);
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      this.performRailTurn(world, x, y, z, true);
      if (this.block == Blocks.RAIL_POWERED) {
         this.onNeighborBlockChange(world, x, y, z, this.id());
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (!world.isClientSide) {
         int meta = world.getBlockMetadata(x, y, z);
         boolean isPoweredFlagSet = (meta & 8) != 0;
         RailDirection railDirection = this.getRailDirection(world, x, y, z);
         if (!this.isValidState(world, x, y, z)) {
            this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
            world.setBlockWithNotify(x, y, z, 0);
         } else {
            Block<?> block = Blocks.getBlock(blockId);
            if (this.block == Blocks.RAIL_POWERED) {
               boolean gettingPower = world.hasNeighborSignal(x, y, z)
                  || world.hasNeighborSignal(x, y + 1, z)
                  || this.isConnectedPoweredRail1(world, x, y, z, true, 0)
                  || this.isConnectedPoweredRail1(world, x, y, z, false, 0);
               boolean changedMeta = false;
               if (gettingPower && !isPoweredFlagSet) {
                  world.setBlockMetadataWithNotify(x, y, z, railDirection.meta | 8);
                  changedMeta = true;
               } else if (!gettingPower && isPoweredFlagSet) {
                  world.setBlockMetadataWithNotify(x, y, z, railDirection.meta);
                  changedMeta = true;
               }

               if (changedMeta) {
                  world.notifyBlocksOfNeighborChange(x, y - 1, z, this.id());
                  if (railDirection.isSloped()) {
                     world.notifyBlocksOfNeighborChange(x, y + 1, z, this.id());
                  }
               }
            } else if (block != null && block.isSignalSource() && BlockLogicRail.Rail.getNAdjacentTracks(new BlockLogicRail.Rail(world, x, y, z)) == 3) {
               this.performRailTurn(world, x, y, z, false);
            }
         }
      }
   }

   private boolean isValidState(World world, int x, int y, int z) {
      if (!world.canPlaceOnSurfaceOfBlock(x, y - 1, z)) {
         return false;
      } else {
         RailDirection railDirection = this.getRailDirection(world, x, y, z);
         if (railDirection == RailDirection.SLOPE_E && !world.canPlaceOnSurfaceOfBlock(x + 1, y, z)) {
            return false;
         } else if (railDirection == RailDirection.SLOPE_W && !world.canPlaceOnSurfaceOfBlock(x - 1, y, z)) {
            return false;
         } else {
            return railDirection == RailDirection.SLOPE_N && !world.canPlaceOnSurfaceOfBlock(x, y, z - 1)
               ? false
               : railDirection != RailDirection.SLOPE_S || world.canPlaceOnSurfaceOfBlock(x, y, z + 1);
         }
      }
   }

   private void performRailTurn(World world, int x, int y, int z, boolean forceUpdate) {
      if (!world.isClientSide) {
         new BlockLogicRail.Rail(world, x, y, z).railTurn(world.hasNeighborSignal(x, y, z), forceUpdate);
      }
   }

   private boolean isConnectedPoweredRail1(World world, int x, int y, int z, boolean forward, int distance) {
      if (distance >= 8) {
         return false;
      } else {
         RailDirection railDirection = this.getRailDirection(world, x, y, z);
         Axis axis = Axis.NONE;
         int nextX;
         int nextY;
         int nextZ;
         if (forward) {
            nextX = x + railDirection.getNextRailX();
            nextY = y + railDirection.getNextRailY();
            nextZ = z + railDirection.getNextRailZ();
         } else {
            nextX = x + railDirection.getPrevRailX();
            nextY = y + railDirection.getPrevRailY();
            nextZ = z + railDirection.getPrevRailZ();
         }

         switch (railDirection) {
            case STRAIGHT_NS:
            case SLOPE_N:
            case SLOPE_S:
               axis = Axis.Z;
               break;
            case STRAIGHT_EW:
            case SLOPE_E:
            case SLOPE_W:
               axis = Axis.X;
         }

         return this.isConnectedPoweredRail2(world, nextX, nextY, nextZ, forward, distance, axis)
            ? true
            : this.isConnectedPoweredRail2(world, nextX, nextY - 1, nextZ, forward, distance, axis);
      }
   }

   private boolean isConnectedPoweredRail2(World world, int x, int y, int z, boolean forward, int distance, Axis axis) {
      int blockId = world.getBlockId(x, y, z);
      if (blockId == Blocks.RAIL_POWERED.id()) {
         int meta = world.getBlockMetadata(x, y, z);
         boolean isPoweredFlagSet = (meta & 8) != 0;
         RailDirection direction = this.getRailDirection(world, x, y, z);
         if (axis == Axis.X && (direction == RailDirection.STRAIGHT_NS || direction == RailDirection.SLOPE_N || direction == RailDirection.SLOPE_S)) {
            return false;
         }

         if (axis == Axis.Z && (direction == RailDirection.STRAIGHT_EW || direction == RailDirection.SLOPE_E || direction == RailDirection.SLOPE_W)) {
            return false;
         }

         if (isPoweredFlagSet) {
            if (!world.hasNeighborSignal(x, y, z) && !world.hasNeighborSignal(x, y + 1, z)) {
               return this.isConnectedPoweredRail1(world, x, y, z, forward, distance + 1);
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return 0;
   }

   public static class Rail {
      private final World world;
      private final int x;
      private final int y;
      private final int z;
      private final boolean isPoweredRail;
      private final List<ChunkPosition> connectedTracks = new ArrayList<>();

      public Rail(World world, int x, int y, int z) {
         this.world = world;
         this.x = x;
         this.y = y;
         this.z = z;
         Block<?> block = world.getBlock(x, y, z);
         this.isPoweredRail = block != null && block.getLogic() instanceof BlockLogicRail && ((BlockLogicRail)block.getLogic()).isPowered;
         int meta = world.getBlockMetadata(x, y, z) & (this.isPoweredRail ? 7 : 255);
         this.setConnections(RailDirection.getFromMeta(meta));
      }

      private void setConnections(RailDirection direction) {
         this.connectedTracks.clear();
         this.connectedTracks.add(new ChunkPosition(this.x + direction.getNextRailX(), this.y + direction.getNextRailY(), this.z + direction.getNextRailZ()));
         this.connectedTracks.add(new ChunkPosition(this.x + direction.getPrevRailX(), this.y + direction.getPrevRailY(), this.z + direction.getPrevRailZ()));
      }

      private void checkAndRemoveMissingRails() {
         for (int i = 0; i < this.connectedTracks.size(); i++) {
            BlockLogicRail.Rail rail = this.getMinecartTrackLogic(this.connectedTracks.get(i));
            if (rail != null && rail.isConnectedTo(this)) {
               this.connectedTracks.set(i, new ChunkPosition(rail.x, rail.y, rail.z));
            } else {
               this.connectedTracks.remove(i--);
            }
         }
      }

      private boolean isMinecartTrack(int x, int y, int z) {
         if (BlockLogicRail.isRailBlockAt(this.world, x, y, z)) {
            return true;
         } else {
            return BlockLogicRail.isRailBlockAt(this.world, x, y + 1, z) ? true : BlockLogicRail.isRailBlockAt(this.world, x, y - 1, z);
         }
      }

      private BlockLogicRail.Rail getMinecartTrackLogic(ChunkPosition chunkposition) {
         if (BlockLogicRail.isRailBlockAt(this.world, chunkposition.x, chunkposition.y, chunkposition.z)) {
            return new BlockLogicRail.Rail(this.world, chunkposition.x, chunkposition.y, chunkposition.z);
         } else if (BlockLogicRail.isRailBlockAt(this.world, chunkposition.x, chunkposition.y + 1, chunkposition.z)) {
            return new BlockLogicRail.Rail(this.world, chunkposition.x, chunkposition.y + 1, chunkposition.z);
         } else {
            return BlockLogicRail.isRailBlockAt(this.world, chunkposition.x, chunkposition.y - 1, chunkposition.z)
               ? new BlockLogicRail.Rail(this.world, chunkposition.x, chunkposition.y - 1, chunkposition.z)
               : null;
         }
      }

      private boolean isConnectedTo(BlockLogicRail.Rail rail) {
         for (ChunkPosition chunkposition : this.connectedTracks) {
            if (chunkposition.x == rail.x && chunkposition.z == rail.z) {
               return true;
            }
         }

         return false;
      }

      private boolean isInTrack(int x, int y, int z) {
         for (ChunkPosition chunkposition : this.connectedTracks) {
            if (chunkposition.x == x && chunkposition.z == z) {
               return true;
            }
         }

         return false;
      }

      private int getAdjacentTracks() {
         int count = 0;
         if (this.isMinecartTrack(this.x, this.y, this.z - 1)) {
            count++;
         }

         if (this.isMinecartTrack(this.x, this.y, this.z + 1)) {
            count++;
         }

         if (this.isMinecartTrack(this.x - 1, this.y, this.z)) {
            count++;
         }

         if (this.isMinecartTrack(this.x + 1, this.y, this.z)) {
            count++;
         }

         return count;
      }

      private boolean handleKeyPress(BlockLogicRail.Rail rail) {
         return this.isConnectedTo(rail) ? true : this.connectedTracks.size() != 2;
      }

      private boolean canTrackTurnTo(int x, int y, int z) {
         BlockLogicRail.Rail rail = this.getMinecartTrackLogic(new ChunkPosition(x, y, z));
         if (rail == null) {
            return false;
         } else {
            rail.checkAndRemoveMissingRails();
            return rail.handleKeyPress(this);
         }
      }

      public void railTurn(boolean isPowered, boolean forceUpdate) {
         boolean flagNorth = this.canTrackTurnTo(this.x, this.y, this.z - 1);
         boolean flagSouth = this.canTrackTurnTo(this.x, this.y, this.z + 1);
         boolean flagWest = this.canTrackTurnTo(this.x - 1, this.y, this.z);
         boolean flagEast = this.canTrackTurnTo(this.x + 1, this.y, this.z);
         RailDirection railType = RailDirection.NONE;
         if ((flagNorth || flagSouth) && !flagWest && !flagEast) {
            railType = RailDirection.STRAIGHT_NS;
         }

         if ((flagWest || flagEast) && !flagNorth && !flagSouth) {
            railType = RailDirection.STRAIGHT_EW;
         }

         if (!this.isPoweredRail) {
            if (flagSouth && flagEast && !flagNorth && !flagWest) {
               railType = RailDirection.TURN_ES;
            }

            if (flagSouth && flagWest && !flagNorth && !flagEast) {
               railType = RailDirection.TURN_WS;
            }

            if (flagNorth && flagWest && !flagSouth && !flagEast) {
               railType = RailDirection.TURN_WN;
            }

            if (flagNorth && flagEast && !flagSouth && !flagWest) {
               railType = RailDirection.TURN_EN;
            }
         }

         if (railType == RailDirection.NONE) {
            if (flagNorth || flagSouth) {
               railType = RailDirection.STRAIGHT_NS;
            }

            if (flagWest || flagEast) {
               railType = RailDirection.STRAIGHT_EW;
            }

            if (!this.isPoweredRail) {
               if (isPowered) {
                  if (flagSouth && flagEast) {
                     railType = RailDirection.TURN_ES;
                  }

                  if (flagWest && flagSouth) {
                     railType = RailDirection.TURN_WS;
                  }

                  if (flagEast && flagNorth) {
                     railType = RailDirection.TURN_EN;
                  }

                  if (flagNorth && flagWest) {
                     railType = RailDirection.TURN_WN;
                  }
               } else {
                  if (flagNorth && flagWest) {
                     railType = RailDirection.TURN_WN;
                  }

                  if (flagEast && flagNorth) {
                     railType = RailDirection.TURN_EN;
                  }

                  if (flagWest && flagSouth) {
                     railType = RailDirection.TURN_WS;
                  }

                  if (flagSouth && flagEast) {
                     railType = RailDirection.TURN_ES;
                  }
               }
            }
         }

         if (railType == RailDirection.STRAIGHT_NS) {
            if (BlockLogicRail.isRailBlockAt(this.world, this.x, this.y + 1, this.z - 1)) {
               railType = RailDirection.SLOPE_N;
            }

            if (BlockLogicRail.isRailBlockAt(this.world, this.x, this.y + 1, this.z + 1)) {
               railType = RailDirection.SLOPE_S;
            }
         }

         if (railType == RailDirection.STRAIGHT_EW) {
            if (BlockLogicRail.isRailBlockAt(this.world, this.x + 1, this.y + 1, this.z)) {
               railType = RailDirection.SLOPE_E;
            }

            if (BlockLogicRail.isRailBlockAt(this.world, this.x - 1, this.y + 1, this.z)) {
               railType = RailDirection.SLOPE_W;
            }
         }

         if (railType == RailDirection.NONE) {
            railType = RailDirection.STRAIGHT_EW;
         }

         this.setConnections(railType);
         int newMeta = railType.meta;
         if (this.isPoweredRail) {
            newMeta |= this.world.getBlockMetadata(this.x, this.y, this.z) & 8;
         }

         if (forceUpdate || this.world.getBlockMetadata(this.x, this.y, this.z) != newMeta) {
            this.world.setBlockMetadataWithNotify(this.x, this.y, this.z, newMeta);

            for (ChunkPosition connectedTrack : this.connectedTracks) {
               BlockLogicRail.Rail rail = this.getMinecartTrackLogic(connectedTrack);
               if (rail != null) {
                  rail.checkAndRemoveMissingRails();
                  if (rail.handleKeyPress(this)) {
                     rail.updateRailState(this);
                  }
               }
            }
         }
      }

      private void updateRailState(BlockLogicRail.Rail otherLogic) {
         this.connectedTracks.add(new ChunkPosition(otherLogic.x, otherLogic.y, otherLogic.z));
         boolean flagNorth = this.isInTrack(this.x, this.y, this.z - 1);
         boolean flagSouth = this.isInTrack(this.x, this.y, this.z + 1);
         boolean flagWest = this.isInTrack(this.x - 1, this.y, this.z);
         boolean flagEast = this.isInTrack(this.x + 1, this.y, this.z);
         RailDirection railDirection = RailDirection.NONE;
         if (flagNorth || flagSouth) {
            railDirection = RailDirection.STRAIGHT_NS;
         }

         if (flagWest || flagEast) {
            railDirection = RailDirection.STRAIGHT_EW;
         }

         if (!this.isPoweredRail) {
            if (flagSouth && flagEast && !flagNorth && !flagWest) {
               railDirection = RailDirection.TURN_ES;
            }

            if (flagSouth && flagWest && !flagNorth && !flagEast) {
               railDirection = RailDirection.TURN_WS;
            }

            if (flagNorth && flagWest && !flagSouth && !flagEast) {
               railDirection = RailDirection.TURN_WN;
            }

            if (flagNorth && flagEast && !flagSouth && !flagWest) {
               railDirection = RailDirection.TURN_EN;
            }
         }

         if (railDirection == RailDirection.STRAIGHT_NS) {
            if (BlockLogicRail.isRailBlockAt(this.world, this.x, this.y + 1, this.z - 1)) {
               railDirection = RailDirection.SLOPE_N;
            }

            if (BlockLogicRail.isRailBlockAt(this.world, this.x, this.y + 1, this.z + 1)) {
               railDirection = RailDirection.SLOPE_S;
            }
         }

         if (railDirection == RailDirection.STRAIGHT_EW) {
            if (BlockLogicRail.isRailBlockAt(this.world, this.x + 1, this.y + 1, this.z)) {
               railDirection = RailDirection.SLOPE_E;
            }

            if (BlockLogicRail.isRailBlockAt(this.world, this.x - 1, this.y + 1, this.z)) {
               railDirection = RailDirection.SLOPE_W;
            }
         }

         if (railDirection == RailDirection.NONE) {
            railDirection = RailDirection.STRAIGHT_NS;
         }

         int newMeta = railDirection.meta;
         if (this.isPoweredRail) {
            newMeta |= this.world.getBlockMetadata(this.x, this.y, this.z) & 8;
         }

         this.world.setBlockMetadataWithNotify(this.x, this.y, this.z, newMeta);
      }

      public static int getNAdjacentTracks(BlockLogicRail.Rail rail) {
         return rail.getAdjacentTracks();
      }
   }
}
