package net.minecraft.core.data.gamerule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.BlockLogicLog;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkPosition;

public class TreecapitatorHelper {
   public final World world;
   public final ChunkPosition basePosition;
   public final Player player;
   protected final Set<ChunkPosition> allBlocksSet = new HashSet<>();
   protected final List<ChunkPosition> allBlocksList = new ArrayList<>();
   protected int[] adjacentTreeBlocks;
   protected ChunkPosition lastBrokenBlock = null;

   public TreecapitatorHelper(World world, int x, int y, int z, Player player) {
      this.world = world;
      this.basePosition = new ChunkPosition(x, y, z);
      this.player = player;
   }

   public boolean chopTree() {
      int id = this.world.getBlockId(this.basePosition.x, this.basePosition.y, this.basePosition.z);
      if (!this.isLog(Blocks.getBlock(id))) {
         return false;
      } else {
         ItemStack item = this.player.getCurrentEquippedItem();
         if (item != null && item.isItemStackDamageable()) {
            TreecapitatorHelper.ItemList itemList = null;
            if (EntityItem.enableItemClumping) {
               itemList = new TreecapitatorHelper.ItemList();
            }

            this.allBlocksSet.clear();
            this.allBlocksList.clear();
            this.allBlocksSet.add(this.basePosition);
            this.allBlocksList.add(this.basePosition);
            List<ChunkPosition> list = new ArrayList<>();
            list.add(this.basePosition);
            Set<ChunkPosition> set = new HashSet<>();

            for (int attempt = 0; attempt < 256; attempt++) {
               for (int i = 0; i < list.size(); i++) {
                  this.addLogsAroundBlock(list.get(i), set, id);
               }

               if (set.size() == 0) {
                  break;
               }

               list.clear();
               list.addAll(set);
               this.allBlocksSet.addAll(set);
               this.allBlocksList.addAll(set);
               set.clear();
            }

            list.clear();
            list.addAll(this.allBlocksList);
            List<ChunkPosition> brokenBlocks = new ArrayList<>();
            int maxBrokenBlocks = 64;
            int broken = 0;

            while (list.size() > 0) {
               ChunkPosition pos = list.remove(list.size() - 1);
               if (this.breakBlock(pos, true, itemList)) {
                  brokenBlocks.add(pos);
                  this.lastBrokenBlock = pos;
                  item.damageItem(1, this.player);
                  if (item.stackSize <= 0) {
                     this.player.destroyCurrentEquippedItem();
                     break;
                  }
               }

               if (++broken >= 64) {
                  break;
               }
            }

            list.clear();
            list.addAll(brokenBlocks);
            this.allBlocksList.clear();
            this.allBlocksSet.clear();

            for (int attempt = 0; attempt < 8; attempt++) {
               for (int i = 0; i < list.size(); i++) {
                  this.addDecayingLeavesAroundBlock(list.get(i), set);
               }

               if (set.size() == 0) {
                  break;
               }

               list.clear();
               list.addAll(set);
               this.allBlocksSet.addAll(set);
               this.allBlocksList.addAll(set);
               set.clear();
            }

            for (int i = 0; i < this.allBlocksList.size(); i++) {
               ChunkPosition posx = this.allBlocksList.get(i);
               Block<?> block = this.world.getBlock(posx.x, posx.y, posx.z);
               if (block != null && block.getLogic() instanceof BlockLogicLeavesBase) {
                  if (this.player.getGamemode().dropBlockOnBreak()) {
                     ItemStack[] drops = block.getBreakResult(
                        this.world, EnumDropCause.PROPER_TOOL, posx.x, posx.y, posx.z, this.world.getBlockMetadata(posx.x, posx.y, posx.z), null
                     );
                     if (itemList != null) {
                        itemList.addAll(drops);
                     } else {
                        this.dropItems(drops, posx);
                     }
                  }

                  this.world.setBlockWithNotify(posx.x, posx.y, posx.z, 0);
               }
            }

            if (itemList != null) {
               ChunkPosition dropPos = this.lastBrokenBlock != null ? this.lastBrokenBlock : this.basePosition;
               itemList.dropAllItems(this.world, dropPos.x, dropPos.y, dropPos.z);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   protected void addLogsAroundBlock(ChunkPosition pos, Set<ChunkPosition> set, int id) {
      int radius = 1;

      for (int offX = -radius; offX <= radius; offX++) {
         for (int offY = 0; offY <= radius; offY++) {
            for (int offZ = -radius; offZ <= radius; offZ++) {
               if ((offX != 0 || offY != 0 || offZ != 0) && pos.y + offY >= this.basePosition.y) {
                  ChunkPosition pos1 = new ChunkPosition(pos.x + offX, pos.y + offY, pos.z + offZ);
                  if (!this.allBlocksSet.contains(pos1) && !set.contains(pos1) && this.world.getBlockId(pos1.x, pos1.y, pos1.z) == id) {
                     set.add(pos1);
                  }
               }
            }
         }
      }
   }

   protected void addDecayingLeavesAroundBlock(ChunkPosition pos, Set<ChunkPosition> set) {
      int radius = 1;

      for (int offX = -radius; offX <= radius; offX++) {
         for (int offY = -radius; offY <= radius; offY++) {
            for (int offZ = -radius; offZ <= radius; offZ++) {
               if ((offX != 0 || offY != 0 || offZ != 0) && pos.y + offY >= this.basePosition.y) {
                  ChunkPosition pos1 = new ChunkPosition(pos.x + offX, pos.y + offY, pos.z + offZ);
                  if (!this.allBlocksSet.contains(pos1) && !set.contains(pos1) && this.isDecayableLeaf(pos1)) {
                     set.add(pos1);
                  }
               }
            }
         }
      }
   }

   protected boolean breakBlock(ChunkPosition pos, boolean particle, TreecapitatorHelper.ItemList itemList) {
      Block<?> block = this.world.getBlock(pos.x, pos.y, pos.z);
      if (this.world.setBlockWithNotify(pos.x, pos.y, pos.z, 0)) {
         int meta = this.world.getBlockMetadata(pos.x, pos.y, pos.z);
         ItemStack[] drops = block.getBreakResult(this.world, EnumDropCause.PROPER_TOOL, pos.x, pos.y, pos.z, meta, null);
         if (this.player.getGamemode().dropBlockOnBreak()) {
            if (itemList != null) {
               itemList.addAll(drops);
            } else {
               this.dropItems(drops, pos);
            }
         }

         if (particle) {
            this.world.playBlockEvent(this.player, 2001, pos.x, pos.y, pos.z, block.id());
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean isLog(Block<?> block) {
      return block != null && block.getLogic() instanceof BlockLogicLog;
   }

   protected boolean isDecayableLeaf(ChunkPosition pos) {
      Block<?> block = this.world.getBlock(pos.x, pos.y, pos.z);
      if (block != null && block.getLogic() instanceof BlockLogicLeavesBase) {
         int meta = this.world.getBlockMetadata(pos.x, pos.y, pos.z);
         return BlockLogicLeavesBase.isPermanent(meta) ? false : !this.isConnectedToLog(pos, 4);
      } else {
         return false;
      }
   }

   protected boolean isLogInRange(ChunkPosition pos, int range) {
      for (int offX = -range; offX <= range; offX++) {
         for (int offY = -range; offY <= range; offY++) {
            for (int offZ = -range; offZ <= range; offZ++) {
               if (this.isLog(this.world.getBlock(pos.x + offX, pos.y + offY, pos.z + offZ))) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   protected boolean isConnectedToLog(ChunkPosition pos, int range) {
      int x = pos.x;
      int y = pos.y;
      int z = pos.z;
      byte byte1 = 32;
      int a = byte1 * byte1;
      int b = byte1 / 2;
      if (this.adjacentTreeBlocks == null) {
         this.adjacentTreeBlocks = new int[byte1 * byte1 * byte1];
      }

      int range1 = range + 1;
      if (this.world.areBlocksLoaded(x - range1, y - range1, z - range1, x + range1, y + range1, z + range1)) {
         for (int offX = -range; offX <= range; offX++) {
            for (int offY = -range; offY <= range; offY++) {
               for (int offZ = -range; offZ <= range; offZ++) {
                  Block<?> block = this.world.getBlock(x + offX, y + offY, z + offZ);
                  if (this.isLog(block)) {
                     this.adjacentTreeBlocks[(offX + b) * a + (offY + b) * byte1 + offZ + b] = 0;
                  } else if (block != null && block.getLogic() instanceof BlockLogicLeavesBase) {
                     this.adjacentTreeBlocks[(offX + b) * a + (offY + b) * byte1 + offZ + b] = -2;
                  } else {
                     this.adjacentTreeBlocks[(offX + b) * a + (offY + b) * byte1 + offZ + b] = -1;
                  }
               }
            }
         }

         for (int c = 1; c <= 4; c++) {
            for (int offX = -range; offX <= range; offX++) {
               for (int offY = -range; offY <= range; offY++) {
                  for (int offZx = -range; offZx <= range; offZx++) {
                     if (this.adjacentTreeBlocks[(offX + b) * a + (offY + b) * byte1 + offZx + b] == c - 1) {
                        if (this.adjacentTreeBlocks[(offX + b - 1) * a + (offY + b) * byte1 + offZx + b] == -2) {
                           this.adjacentTreeBlocks[(offX + b - 1) * a + (offY + b) * byte1 + offZx + b] = c;
                        }

                        if (this.adjacentTreeBlocks[(offX + b + 1) * a + (offY + b) * byte1 + offZx + b] == -2) {
                           this.adjacentTreeBlocks[(offX + b + 1) * a + (offY + b) * byte1 + offZx + b] = c;
                        }

                        if (this.adjacentTreeBlocks[(offX + b) * a + (offY + b - 1) * byte1 + offZx + b] == -2) {
                           this.adjacentTreeBlocks[(offX + b) * a + (offY + b - 1) * byte1 + offZx + b] = c;
                        }

                        if (this.adjacentTreeBlocks[(offX + b) * a + (offY + b + 1) * byte1 + offZx + b] == -2) {
                           this.adjacentTreeBlocks[(offX + b) * a + (offY + b + 1) * byte1 + offZx + b] = c;
                        }

                        if (this.adjacentTreeBlocks[(offX + b) * a + (offY + b) * byte1 + (offZx + b - 1)] == -2) {
                           this.adjacentTreeBlocks[(offX + b) * a + (offY + b) * byte1 + (offZx + b - 1)] = c;
                        }

                        if (this.adjacentTreeBlocks[(offX + b) * a + (offY + b) * byte1 + offZx + b + 1] == -2) {
                           this.adjacentTreeBlocks[(offX + b) * a + (offY + b) * byte1 + offZx + b + 1] = c;
                        }
                     }
                  }
               }
            }
         }
      }

      return this.adjacentTreeBlocks[b * a + b * byte1 + b] >= 0;
   }

   protected String toString(ChunkPosition pos) {
      return pos.x + ", " + pos.y + ", " + pos.z;
   }

   protected int compare(ChunkPosition pos1, ChunkPosition pos2) {
      return pos1.y != pos2.y
         ? pos2.y - pos1.y
         : Math.abs(pos2.x - this.basePosition.x)
            + Math.abs(pos2.z - this.basePosition.z)
            - (Math.abs(pos1.x - this.basePosition.x) + Math.abs(pos1.z - this.basePosition.z));
   }

   protected void dropItems(ItemStack[] items, ChunkPosition pos) {
      if (items != null) {
         for (int i = 0; i < items.length; i++) {
            ItemStack stack = items[i];

            while (stack.stackSize > 0) {
               this.world.dropItem(pos.x, pos.y, pos.z, stack.splitStack(1));
            }
         }
      }
   }

   protected class ItemList {
      private List<ItemStack> items = new ArrayList<>();

      public void addAll(ItemStack[] itemStacks) {
         if (itemStacks != null) {
            for (int i = 0; i < itemStacks.length; i++) {
               this.add(itemStacks[i]);
            }
         }
      }

      public void add(ItemStack itemStack) {
         if (itemStack != null) {
            for (int i = 0; i < this.items.size(); i++) {
               ItemStack stack = this.items.get(i);
               if (stack.canStackWith(itemStack)) {
                  stack.stackSize = stack.stackSize + itemStack.stackSize;
                  return;
               }
            }

            this.items.add(itemStack.copy());
         }
      }

      public int size() {
         return this.items.size();
      }

      public ItemStack get(int i) {
         return this.items.get(i);
      }

      public void dropAllItems(World world, int x, int y, int z) {
         for (int i = 0; i < this.items.size(); i++) {
            ItemStack stack = this.items.get(i);

            while (stack.stackSize > 0) {
               world.dropItem(x, y, z, stack.splitStack(Math.min(stack.stackSize, stack.getMaxStackSize())));
            }
         }
      }
   }
}
