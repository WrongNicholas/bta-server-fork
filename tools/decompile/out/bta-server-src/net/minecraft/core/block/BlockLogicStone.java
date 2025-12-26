package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

public class BlockLogicStone extends BlockLogic {
   private final Block<?> cobble;

   public BlockLogicStone(Block<?> block, Block<?> cobbleStone, Material material) {
      super(block, material);
      this.cobble = cobbleStone;
   }

   @Override
   public void harvestBlock(World world, Player player, int x, int y, int z, int meta, TileEntity tileEntity) {
      super.harvestBlock(world, player, x, y, z, meta, tileEntity);
      if (player.getStat(Blocks.STONE.getStat("stat_mined")) > 0
         && player.getStat(Blocks.BASALT.getStat("stat_mined")) > 0
         && player.getStat(Blocks.GRANITE.getStat("stat_mined")) > 0
         && player.getStat(Blocks.LIMESTONE.getStat("stat_mined")) > 0
         && player.getStat(Blocks.PERMAFROST.getStat("stat_mined")) > 0) {
         player.triggerAchievement(Achievements.COLLECT_STONE);
      }
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      Chunk chunk = world.getChunkFromBlockCoords(x, z);
      if (y <= 32 && chunk.getChunkRandom(987234911L).nextInt(10) == 0 && world.getBlockId(x, y - 1, z) == 0 && rand.nextInt(50) == 0) {
         world.spawnParticle("slimechunk", x, y, z, 0.0, 0.0, 0.0, 0);
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case PISTON_CRUSH:
         case WORLD:
         case EXPLOSION:
         case PROPER_TOOL:
            return new ItemStack[]{new ItemStack(this.cobble)};
         case PICK_BLOCK:
         case SILK_TOUCH:
            return new ItemStack[]{new ItemStack(this)};
         default:
            return null;
      }
   }
}
