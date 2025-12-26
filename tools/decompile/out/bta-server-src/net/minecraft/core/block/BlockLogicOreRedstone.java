package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeatureOre;

public class BlockLogicOreRedstone extends BlockLogic {
   public static WorldFeatureOre.OreMap variantMap = new WorldFeatureOre.OreMap();
   private final boolean illuminated;
   private Block<?> normalBlock;
   private Block<?> illuminatedBlock;

   public BlockLogicOreRedstone(Block<?> block, Block<?> parentBlock, Material material, boolean illuminated, Block<?> normalBlock, Block<?> illuminatedBlock) {
      super(block, material);
      if (illuminated) {
         block.setTicking(true);
      }

      this.illuminated = illuminated;
      this.normalBlock = normalBlock;
      this.illuminatedBlock = illuminatedBlock;
      if (parentBlock != null) {
         variantMap.put(parentBlock.id(), block.id());
      }
   }

   public void link(Block<?> normalBlock, Block<?> illuminatedBlock) {
     this.normalBlock = normalBlock;
     this.illuminatedBlock = illuminatedBlock;
   }

   @Override
   public int tickDelay() {
      return 30;
   }

   @Override
   public void onBlockLeftClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      this.lightRedstone(world, x, y, z);
      super.onBlockLeftClicked(world, x, y, z, player, side, xHit, yHit);
   }

   @Override
   public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
      this.lightRedstone(world, x, y, z);
      super.onEntityWalking(world, x, y, z, entity);
   }

   private void lightRedstone(World world, int x, int y, int z) {
      this.spawnParticles(world, x, y, z);
      if (!this.illuminated) {
         world.setBlockWithNotify(x, y, z, this.illuminatedBlock.id());
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (this.illuminated) {
         world.setBlockWithNotify(x, y, z, this.normalBlock.id());
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case SILK_TOUCH:
         case PICK_BLOCK:
            return new ItemStack[]{new ItemStack(this.normalBlock)};
         case EXPLOSION:
         case PROPER_TOOL:
         case PISTON_CRUSH:
            return new ItemStack[]{new ItemStack(Items.DUST_REDSTONE, 4 + world.rand.nextInt(2))};
         default:
            return null;
      }
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (this.illuminated) {
         this.spawnParticles(world, x, y, z);
      }
   }

   private void spawnParticles(World world, int x, int y, int z) {
      Random random = world.rand;
      int redstoneBrightness = 10;
      double d = 0.0625;

      for (int i = 0; i < 6; i++) {
         double px = (double)x + random.nextFloat();
         double py = (double)y + random.nextFloat();
         double pz = (double)z + random.nextFloat();
         if (i == 0 && !world.isBlockOpaqueCube(x, y + 1, z)) {
            py = y + 1 + d;
         }

         if (i == 1 && !world.isBlockOpaqueCube(x, y - 1, z)) {
            py = y - d;
         }

         if (i == 2 && !world.isBlockOpaqueCube(x, y, z + 1)) {
            pz = z + 1 + d;
         }

         if (i == 3 && !world.isBlockOpaqueCube(x, y, z - 1)) {
            pz = z - d;
         }

         if (i == 4 && !world.isBlockOpaqueCube(x + 1, y, z)) {
            px = x + 1 + d;
         }

         if (i == 5 && !world.isBlockOpaqueCube(x - 1, y, z)) {
            px = x - d;
         }

         if (px < x || px > x + 1 || py < 0.0 || py > y + 1 || pz < z || pz > z + 1) {
            world.spawnParticle("reddust", px, py, pz, 0.0, 0.0, 0.0, 10);
         }
      }
   }
}
