package net.minecraft.core.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.animal.MobFireflyCluster;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;

public class BlockLogicLanternFirefly extends BlockLogic {
   public static final int MASK_HANGING = 1;
   public static final int MASK_ANGLED = 2;
   private final Supplier<Item> itemSupplier;
   private final MobFireflyCluster.FireflyColor color;

   public BlockLogicLanternFirefly(Block<?> block, MobFireflyCluster.FireflyColor colour, @NotNull Supplier<Item> itemSupplier) {
      super(block, Material.glass);
      this.color = colour;
      this.itemSupplier = itemSupplier;
      this.setBlockBounds(0.3125, 0.0, 0.3125, 0.6875, 0.5, 0.6875);
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int metadata = world.getBlockMetadata(x, y, z);
      float offset = 0.0F;
      if ((metadata & 1) != 0) {
         offset = 0.3875F;
      }

      return AABB.getTemporaryBB(0.3125, offset, 0.3125, 0.6875, 0.5F + offset, 0.6875);
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
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (rand.nextInt(3) == 0) {
         world.spawnParticle(
            this.color.getParticleName(), (double)x + rand.nextFloat(), (double)y + rand.nextFloat(), (double)z + rand.nextFloat(), 0.0, 0.0, 0.0, 0
         );
      }
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      world.setBlockRaw(x, y, z, 0);
      world.playSoundAtEntity(player, player, "item.pickup", 1.0F, 1.0F);
      if (!world.isClientSide) {
         world.dropItem(x, y, z, new ItemStack(this.itemSupplier.get(), 1, 0));
      }

      return true;
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      if (side != Side.BOTTOM && (side == Side.TOP || !(yPlaced >= 0.5))
         || !world.isBlockNormalCube(x, y + 1, z) && !Blocks.hasTag(world.getBlockId(x, y + 1, z), BlockTags.CAN_HANG_OFF)) {
         if ((side == Side.TOP || side != Side.BOTTOM && yPlaced < 0.5) && world.canPlaceOnSurfaceOfBlock(x, y - 1, z)) {
            world.setBlockMetadataWithNotify(x, y, z, 0);
         } else {
            if (!world.isBlockNormalCube(x, y + 1, z) && !Blocks.hasTag(world.getBlockId(x, y + 1, z), BlockTags.CAN_HANG_OFF)) {
               world.setBlockMetadataWithNotify(x, y, z, 0);
            } else {
               world.setBlockMetadataWithNotify(x, y, z, 1);
            }
         }
      } else {
         world.setBlockMetadataWithNotify(x, y, z, 1);
      }
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return !world.isBlockNormalCube(x, y + 1, z) && !Blocks.hasTag(world.getBlockId(x, y + 1, z), BlockTags.CAN_HANG_OFF)
         ? world.canPlaceOnSurfaceOfBlock(x, y - 1, z)
         : true;
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (!this.canBlockStay(world, x, y, z)) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case PICK_BLOCK:
         case SILK_TOUCH:
         case WORLD:
            return new ItemStack[]{new ItemStack(this.itemSupplier.get())};
         default:
            return null;
      }
   }

   @Override
   public boolean canBlockStay(World world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      return (meta & 1) == 0
         ? world.canPlaceOnSurfaceOfBlock(x, y - 1, z)
         : world.isBlockNormalCube(x, y + 1, z) || Blocks.hasTag(world.getBlockId(x, y + 1, z), BlockTags.CAN_HANG_OFF);
   }

   @Override
   public void onBlockDestroyedByPlayer(World world, int x, int y, int z, Side side, int meta, Player player, Item item) {
      if (item == null || !item.isSilkTouch()) {
         if (!world.isClientSide) {
            MobFireflyCluster entity = new MobFireflyCluster(world);
            entity.setColor(this.color);
            entity.setFireflyCount(1);
            entity.setClusterSize(0);
            entity.setSizeBasedOnClusterSize(entity.getClusterSize());
            entity.setPos(
               x + 0.5F + (world.rand.nextFloat() - 0.5F) * 0.5F, y + world.rand.nextFloat() * 0.5F, z + 0.5F + (world.rand.nextFloat() - 0.5F) * 0.5F
            );
            world.entityJoinedWorld(entity);
         }
      }
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return 1;
   }
}
