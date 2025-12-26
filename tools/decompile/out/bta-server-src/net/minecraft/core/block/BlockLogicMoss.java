package net.minecraft.core.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.IBonemealable;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeatureOre;
import org.jetbrains.annotations.Nullable;

public class BlockLogicMoss extends BlockLogic implements IBonemealable {
   public static WorldFeatureOre.OreMap variantMap = new WorldFeatureOre.OreMap();
   public static final Map<Block<?>, Block<?>> stoneToMossMap = new HashMap<>();
   public static final Map<Block<?>, Block<?>> mossToStoneMap = new HashMap<>();

   public BlockLogicMoss(Block<?> block, Block<?> parentBlock) {
      super(block, Material.moss);
      block.setTicking(true);
      variantMap.put(parentBlock.id(), block.id());
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (!world.isClientSide) {
         if (world.areBlocksLoaded(x, y, z, 16) && this.canMossSpread(world, x, y, z) && rand.nextInt(20) == 0) {
            this.spreadMossRandomly(world, x, y, z, rand, false);
         }
      }
   }

   public boolean spreadMossRandomly(World world, int x, int y, int z, Random random, boolean ignoreLight) {
      int mossX = x;
      int mossY = y;
      int mossZ = z;
      int side = random.nextInt(6);
      if (side == 0) {
         mossZ = z - 1;
      }

      if (side == 1) {
         mossX = x + 1;
      }

      if (side == 2) {
         mossZ++;
      }

      if (side == 3) {
         mossX--;
      }

      if (side == 4) {
         mossY = y + 1;
      }

      if (side == 5) {
         mossY--;
      }

      if (ignoreLight || !Block.isBuried(world, mossX, mossY, mossZ)) {
         Block<?> mossStone = getMossBlock(world.getBlockId(mossX, mossY, mossZ));
         if (mossStone != null) {
            world.setBlockWithNotify(mossX, mossY, mossZ, mossStone.id());
            return true;
         }
      }

      return false;
   }

   public boolean canMossSpread(World world, int x, int y, int z) {
      for (int i = 0; i < 6; i++) {
         Side side = Side.getSideById(i);
         if (world.getBlockLightValue(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ()) > 5
            || !world.isBlockLoaded(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ())) {
            return false;
         }
      }

      return true;
   }

   public static Block<?> getMossBlock(int blockId) {
      return blockId == 0 ? null : getMossBlock(Blocks.blocksList[blockId]);
   }

   public static Block<?> getMossBlock(Block<?> stoneBlock) {
      return stoneToMossMap.get(stoneBlock);
   }

   public static Block<?> getStoneBlock(int blockId) {
      return blockId == 0 ? null : getStoneBlock(Blocks.blocksList[blockId]);
   }

   public static Block<?> getStoneBlock(Block<?> mossBlock) {
      return mossToStoneMap.get(mossBlock);
   }

   public static void initMossMap() {
      stoneToMossMap.clear();
      mossToStoneMap.clear();
      stoneToMossMap.put(Blocks.STONE, Blocks.MOSS_STONE);
      stoneToMossMap.put(Blocks.GRANITE, Blocks.MOSS_GRANITE);
      stoneToMossMap.put(Blocks.LIMESTONE, Blocks.MOSS_LIMESTONE);
      stoneToMossMap.put(Blocks.BASALT, Blocks.MOSS_BASALT);
      stoneToMossMap.put(Blocks.COBBLE_STONE, Blocks.COBBLE_STONE_MOSSY);
      stoneToMossMap.put(Blocks.BRICK_STONE_POLISHED, Blocks.BRICK_STONE_POLISHED_MOSSY);
      stoneToMossMap.put(Blocks.LOG_OAK, Blocks.LOG_OAK_MOSSY);

      for (Entry<Block<?>, Block<?>> entry : stoneToMossMap.entrySet()) {
         mossToStoneMap.put(entry.getValue(), entry.getKey());
      }
   }

   @Override
   public boolean onBonemealUsed(
      ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (!world.isClientSide) {
         if (player == null || player.getGamemode().consumeBlocks()) {
            itemstack.stackSize--;
         }

         for (int j1 = 0; j1 < 32; j1++) {
            int k1 = blockX;
            int l1 = blockY;
            int i2 = blockZ;

            for (int j2 = 0; j2 < j1 / 16; j2++) {
               k1 += world.rand.nextInt(3) - 1;
               l1 += (world.rand.nextInt(3) - 1) * world.rand.nextInt(3) / 2;
               i2 += world.rand.nextInt(3) - 1;
            }

            if (!Block.isBuried(world, k1, l1, i2)
               && world.getBlockLightValue(k1, l1 + 1, i2) <= 5
               && world.getBlockLightValue(k1, l1 - 1, i2) <= 5
               && world.getBlockLightValue(k1 + 1, l1, i2) <= 5
               && world.getBlockLightValue(k1 - 1, l1, i2) <= 5
               && world.getBlockLightValue(k1, l1, i2 - 1) <= 5
               && world.getBlockLightValue(k1, l1, i2 + 1) <= 5) {
               int blockId = world.getBlockId(k1, l1, i2);
               if (blockId == Blocks.STONE.id()) {
                  world.setBlockWithNotify(k1, l1, i2, Blocks.MOSS_STONE.id());
               } else if (blockId == Blocks.LIMESTONE.id()) {
                  world.setBlockWithNotify(k1, l1, i2, Blocks.MOSS_LIMESTONE.id());
               } else if (blockId == Blocks.GRANITE.id()) {
                  world.setBlockWithNotify(k1, l1, i2, Blocks.MOSS_GRANITE.id());
               } else if (blockId == Blocks.BASALT.id()) {
                  world.setBlockWithNotify(k1, l1, i2, Blocks.MOSS_BASALT.id());
               } else if (blockId == Blocks.COBBLE_STONE.id()) {
                  world.setBlockWithNotify(k1, l1, i2, Blocks.COBBLE_STONE_MOSSY.id());
               } else if (blockId == Blocks.BRICK_STONE_POLISHED.id()) {
                  world.setBlockWithNotify(k1, l1, i2, Blocks.BRICK_STONE_POLISHED_MOSSY.id());
               } else if (blockId == Blocks.LOG_OAK.id()) {
                  world.setBlockWithNotify(k1, l1, i2, Blocks.LOG_OAK_MOSSY.id());
               }
            }
         }
      }

      return true;
   }
}
