package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.IBonemealable;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.Nullable;

public class BlockLogicFlower extends BlockLogic implements IBonemealable {
   public static final int MASK_PERMANENT = 128;
   public boolean killedByWeather = false;
   public boolean canBeBonemealed = false;

   public BlockLogicFlower(Block<?> block) {
      super(block, Material.plant);
      block.setTicking(true);
      float f = 0.2F;
      this.setBlockBounds(0.5F - f, 0.0, 0.5F - f, 0.5F + f, f * 3.0F, 0.5F + f);
   }

   public BlockLogicFlower setKilledByWeather() {
      this.killedByWeather = true;
      return this;
   }

   public BlockLogicFlower setBonemealable() {
      this.canBeBonemealed = true;
      return this;
   }

   @Override
   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      return 128;
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return super.canPlaceBlockAt(world, x, y, z) && this.mayPlaceOn(world.getBlockId(x, y - 1, z));
   }

   protected boolean mayPlaceOn(int blockId) {
      return Blocks.blocksList[blockId] == null ? false : Blocks.blocksList[blockId].hasTag(BlockTags.GROWS_FLOWERS);
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      super.onNeighborBlockChange(world, x, y, z, blockId);
      this.checkAlive(world, x, y, z);
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      this.checkAlive(world, x, y, z);
      if (world.getGameRuleValue(GameRules.DO_SEASONAL_GROWTH)
         && world.getSeasonManager().getCurrentSeason() != null
         && !isPermanent(world.getBlockMetadata(x, y, z))
         && world.getSeasonManager().getCurrentSeason().killFlowers
         && this.killedByWeather
         && rand.nextInt(256) == 0) {
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   protected final void checkAlive(World world, int x, int y, int z) {
      if (!this.canBlockStay(world, x, y, z)) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   @Override
   public boolean canBlockStay(World world, int x, int y, int z) {
      return (world.getFullBlockLightValue(x, y, z) >= 8 || world.canBlockSeeTheSky(x, y, z)) && this.mayPlaceOn(world.getBlockId(x, y - 1, z));
   }

   public static boolean isPermanent(int metadata) {
      return (metadata & 128) > 0;
   }

   public static int setPermanent(int metadata, boolean permanent) {
      return metadata & -129 | (permanent ? 128 : 0);
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
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean onBonemealUsed(
      ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      Random rand = world.rand;
      if (!world.isClientSide && this.canBeBonemealed) {
         if (player == null || player.getGamemode().consumeBlocks()) {
            itemstack.stackSize--;
         }

         label35:
         for (int j1 = 0; j1 < 128; j1++) {
            int k1 = blockX;
            int l1 = blockY;
            int i2 = blockZ;

            for (int j2 = 0; j2 < j1 / 16; j2++) {
               k1 += rand.nextInt(3) - 1;
               l1 += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
               i2 += rand.nextInt(3) - 1;
               int id1 = world.getBlockId(k1, l1 - 1, i2);
               if (!this.mayPlaceOn(id1)) {
                  continue label35;
               }
            }

            if (world.getBlockId(k1, l1, i2) == 0 && rand.nextFloat() > 0.75) {
               world.setBlockWithNotify(k1, l1, i2, this.block.id());
            }
         }

         return true;
      } else {
         return this.canBeBonemealed;
      }
   }
}
