package net.minecraft.core.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicDoor extends BlockLogic implements IPaintable {
   public static final int MASK_ROTATION = 3;
   public static final int MASK_OPENED = 4;
   public static final int MASK_HINGE = 8;
   public final boolean isTop;
   public final boolean requireTool;
   @Nullable
   public final Supplier<Item> droppedItem;

   public BlockLogicDoor(Block<?> block, Material material, boolean isTop, boolean requireTool, @Nullable Supplier<Item> droppedItem) {
      super(block, material);
      this.isTop = isTop;
      this.requireTool = requireTool;
      this.droppedItem = droppedItem;
      float f = 0.5F;
      if (isTop) {
         this.setBlockBounds(0.5F - f, -1.0, 0.5F - f, 0.5F + f, 1.0, 0.5F + f);
      } else {
         this.setBlockBounds(0.5F - f, 0.0, 0.5F - f, 0.5F + f, 2.0, 0.5F + f);
      }
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      super.onBlockPlacedOnSide(world, x, y, z, side, xPlaced, yPlaced);
      int meta = world.getBlockMetadata(x, y, z);
      if (world.hasNeighborSignal(x, y, z)) {
         world.setBlockMetadataWithNotify(x, y, z, meta | 4);
         if (this.isTop) {
            world.setBlockMetadataWithNotify(x, y - 1, z, meta | 4);
         } else {
            world.setBlockMetadataWithNotify(x, y + 1, z, meta | 4);
         }

         world.playBlockEvent(1003, x, y, z, 0);
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

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      return this.getBoundsForRotation(this.getRotation(world.getBlockMetadata(x, y, z)), false);
   }

   public AABB getBoundsForRotation(int rotation, boolean drawingSelection) {
      float top = 1.0F;
      float bottom = 0.0F;
      if (this.isTop && drawingSelection) {
         top = 1.0F;
         bottom = -1.0F;
      } else if (drawingSelection) {
         top = 2.0F;
         bottom = 0.0F;
      }

      float f = 0.1875F;
      switch (rotation) {
         case 0:
            return AABB.getTemporaryBB(0.0, bottom, 0.0, 1.0, top, f);
         case 1:
            return AABB.getTemporaryBB(1.0F - f, bottom, 0.0, 1.0, top, 1.0);
         case 2:
            return AABB.getTemporaryBB(0.0, bottom, 1.0F - f, 1.0, top, 1.0);
         case 3:
            return AABB.getTemporaryBB(0.0, bottom, 0.0, f, top, 1.0);
         default:
            return AABB.getTemporaryBB(0.0, bottom, 0.0, 1.0, top, 1.0);
      }
   }

   @Override
   public void onBlockLeftClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      if (!Item.hasTag(player.getCurrentEquippedItem(), ItemTags.PREVENT_LEFT_CLICK_INTERACTIONS)) {
         this.onBlockRightClicked(world, x, y, z, player, side, xHit, yHit);
      }
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, @Nullable Player player, Side side, double xPlaced, double yPlaced) {
      if (this.material != Material.metal && this.material != Material.steel) {
         int l = world.getBlockMetadata(x, y, z);
         if (this.isTop) {
            Block<?> b;
            if ((b = Blocks.blocksList[world.getBlockId(x, y - 1, z)]) != null && b.getLogic() instanceof BlockLogicDoor) {
               Blocks.blocksList[world.getBlockId(x, y - 1, z)].onBlockRightClicked(world, x, y - 1, z, player, side, xPlaced, yPlaced);
            }

            return true;
         } else {
            Block<?> b;
            if ((b = Blocks.blocksList[world.getBlockId(x, y + 1, z)]) != null && b.getLogic() instanceof BlockLogicDoor) {
               world.setBlockMetadataWithNotify(x, y + 1, z, l ^ 4);
            }

            world.setBlockMetadataWithNotify(x, y, z, l ^ 4);
            world.markBlocksDirty(x, y - 1, z, x, y, z);
            world.playBlockEvent(player, 1003, x, y, z, 0);
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
      this.onBlockRightClicked(world, x, y, z, null, direction.getSide(), 0.5, 0.5);
   }

   public void onPoweredBlockChange(World world, int x, int y, int z, boolean isPowered) {
      int meta = world.getBlockMetadata(x, y, z);
      if ((meta & 8) > 0) {
         isPowered = !isPowered;
      }

      if (this.isTop) {
         Block<?> b;
         if ((b = Blocks.blocksList[world.getBlockId(x, y - 1, z)]) != null && b.getLogic() instanceof BlockLogicDoor) {
            ((BlockLogicDoor)b.getLogic()).onPoweredBlockChange(world, x, y - 1, z, isPowered);
         }
      } else {
         boolean isOpen = (world.getBlockMetadata(x, y, z) & 4) > 0;
         if (isOpen != isPowered) {
            Block<?> b;
            if ((b = Blocks.blocksList[world.getBlockId(x, y + 1, z)]) != null && b.getLogic() instanceof BlockLogicDoor) {
               world.setBlockMetadataWithNotify(x, y + 1, z, meta ^ 4);
            }

            world.setBlockMetadataWithNotify(x, y, z, meta ^ 4);
            world.markBlocksDirty(x, y - 1, z, x, y, z);
            world.playBlockEvent(null, 1003, x, y, z, 0);
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (this.isTop) {
         Block<?> otherBlock = Blocks.blocksList[world.getBlockId(x, y - 1, z)];
         if (otherBlock == null || !(otherBlock.getLogic() instanceof BlockLogicDoor)) {
            world.setBlockWithNotify(x, y, z, 0);
         }

         if (otherBlock != null && blockId > 0 && Blocks.blocksList[blockId].isSignalSource()) {
            otherBlock.onNeighborBlockChange(world, x, y - 1, z, blockId);
         }
      } else {
         boolean flag = false;
         Block<?> otherBlockx = Blocks.blocksList[world.getBlockId(x, y + 1, z)];
         if (otherBlockx == null || !(otherBlockx.getLogic() instanceof BlockLogicDoor)) {
            world.setBlockWithNotify(x, y, z, 0);
            flag = true;
         }

         if (!world.canPlaceOnSurfaceOfBlock(x, y - 1, z)) {
            this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
            world.setBlockWithNotify(x, y, z, 0);
            flag = true;
            if (otherBlockx != null && otherBlockx.getLogic() instanceof BlockLogicDoor) {
               world.setBlockWithNotify(x, y + 1, z, 0);
            }
         }

         if (flag) {
            if (!world.isClientSide) {
            }
         } else if (blockId > 0 && Blocks.blocksList[blockId].isSignalSource()) {
            boolean flag1 = world.hasNeighborSignal(x, y, z) || world.hasNeighborSignal(x, y + 1, z);
            this.onPoweredBlockChange(world, x, y, z, flag1);
         }
      }
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      if (this.isTop) {
         if (world.getBlockLogic(x, y - 1, z, BlockLogicDoor.class) != null) {
            world.setBlockWithNotify(x, y - 1, z, 0);
         }
      } else if (world.getBlockLogic(x, y + 1, z, BlockLogicDoor.class) != null) {
         world.setBlockWithNotify(x, y + 1, z, 0);
      }
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      int myId = world.getBlockId(x, y, z);
      int myMetadata = world.getBlockMetadata(x, y, z);
      int upId = world.getBlockId(x, y + 1, z);
      if (myId == Blocks.DOOR_PLANKS_OAK_BOTTOM.id() && upId == Blocks.DOOR_PLANKS_OAK_BOTTOM.id()) {
         world.setBlockAndMetadata(x, y + 1, z, Blocks.DOOR_PLANKS_OAK_TOP.id(), myMetadata);
      } else if (myId == Blocks.DOOR_IRON_BOTTOM.id() && upId == Blocks.DOOR_IRON_BOTTOM.id()) {
         world.setBlockAndMetadata(x, y + 1, z, Blocks.DOOR_IRON_TOP.id(), myMetadata);
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      if (this.droppedItem == null) {
         return null;
      } else if (this.requireTool && dropCause != EnumDropCause.IMPROPER_TOOL) {
         return new ItemStack[]{new ItemStack(this.droppedItem.get())};
      } else {
         return !this.requireTool ? new ItemStack[]{new ItemStack(this.droppedItem.get())} : null;
      }
   }

   public int getRotation(int i) {
      return (i & 4) != 0 ? i & 3 : i - 1 & 3;
   }

   public static boolean isOpen(int i) {
      boolean hasRightHinge = (i & 8) != 0;
      boolean opened = (i & 4) != 0;
      return !hasRightHinge && opened || hasRightHinge && !opened;
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return y >= world.getHeightBlocks() - 1
         ? false
         : world.canPlaceOnSurfaceOfBlock(x, y - 1, z) && super.canPlaceBlockAt(world, x, y, z) && super.canPlaceBlockAt(world, x, y + 1, z);
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return 1;
   }

   @Override
   public boolean canBePainted() {
      return this.material == Material.wood;
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      int meta = world.getBlockMetadata(x, y, z);
      if (this.isTop) {
         world.setBlockAndMetadataRaw(x, y, z, Blocks.DOOR_PLANKS_PAINTED_TOP.id(), meta);
         ((BlockLogicDoorPainted)Blocks.DOOR_PLANKS_PAINTED_TOP.getLogic()).setColor(world, x, y, z, color);
         world.setBlockAndMetadataRaw(x, y - 1, z, Blocks.DOOR_PLANKS_PAINTED_BOTTOM.id(), meta);
         ((BlockLogicDoorPainted)Blocks.DOOR_PLANKS_PAINTED_BOTTOM.getLogic()).setColor(world, x, y - 1, z, color);
      } else {
         world.setBlockAndMetadataRaw(x, y, z, Blocks.DOOR_PLANKS_PAINTED_BOTTOM.id(), meta);
         ((BlockLogicDoorPainted)Blocks.DOOR_PLANKS_PAINTED_BOTTOM.getLogic()).setColor(world, x, y, z, color);
         world.setBlockAndMetadataRaw(x, y + 1, z, Blocks.DOOR_PLANKS_PAINTED_TOP.id(), meta);
         ((BlockLogicDoorPainted)Blocks.DOOR_PLANKS_PAINTED_TOP.getLogic()).setColor(world, x, y + 1, z, color);
      }
   }
}
