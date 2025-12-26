package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.IBonemealable;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.Nullable;

public class BlockLogicFarmland extends BlockLogic implements IBonemealable {
   public static final int MASK_FERTILIZED = 16;
   public static final int MASK_WET = 15;

   public BlockLogicFarmland(Block<?> block) {
      super(block, Material.dirt);
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);
      block.withLightBlock(255);
      block.setTicking(true);
   }

   public static boolean isWet(int data) {
      return getWet(data) > 0;
   }

   public static int getWet(int data) {
      return data & 15;
   }

   public static int setWet(int data, int amount) {
      return amount & 15 | data & -16;
   }

   public static boolean isFertilized(int data) {
      return (data & 16) != 0;
   }

   public static int setFertilized(int data, boolean fertilized) {
      return fertilized ? data | 16 : data & -17;
   }

   @Override
   public int tickDelay() {
      return 60;
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return AABB.getTemporaryBB(x, y, z, x + 1, y + 1, z + 1);
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      Material material = world.getBlockMaterial(x, y + 1, z);
      return material == Material.vegetable ? AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0) : AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case SILK_TOUCH:
         case PICK_BLOCK:
            return new ItemStack[]{new ItemStack(this)};
         default:
            return isWet(meta) ? new ItemStack[]{new ItemStack(Blocks.MUD)} : new ItemStack[]{new ItemStack(Blocks.DIRT)};
      }
   }

   private boolean isCropsNearby(World world, int x, int y, int z) {
      int radius = 0;

      for (int ix = x - 0; ix <= x + 0; ix++) {
         for (int iz = z - 0; iz <= z + 0; iz++) {
            if (world.getBlockId(ix, y + 1, iz) == Blocks.CROPS_WHEAT.id()) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   private boolean isWaterNearby(World world, int x, int y, int z, int range) {
      for (int x1 = x - range; x1 <= x + range; x1++) {
         for (int y1 = y - range; y1 <= y + range; y1++) {
            for (int z1 = z - range; z1 <= z + range; z1++) {
               if (Blocks.hasTag(world.getBlockId(x1, y1, z1), BlockTags.IS_WATER)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Override
   public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
      if (world.rand.nextInt(4) == 0 && entity instanceof Player) {
         if (((Player)entity).inventory.armorInventory[0] != null && ((Player)entity).inventory.armorInventory[0].getItem() == Items.ARMOR_BOOTS_LEATHER) {
            return;
         }

         if (isWet(world.getBlockMetadata(x, y, z))) {
            world.setBlockWithNotify(x, y, z, Blocks.MUD.id());
         } else {
            world.setBlockWithNotify(x, y, z, Blocks.DIRT.id());
         }
      }
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay() - world.rand.nextInt(10) + world.rand.nextInt(10));
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      super.onNeighborBlockChange(world, x, y, z, blockId);
      Material material = world.getBlockMaterial(x, y + 1, z);
      if (!material.isSolid() || material == Material.vegetable) {
         world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay() - world.rand.nextInt(10) + world.rand.nextInt(10));
      } else if (isWet(world.getBlockMetadata(x, y, z))) {
         world.setBlockWithNotify(x, y, z, Blocks.MUD.id());
      } else {
         world.setBlockWithNotify(x, y, z, Blocks.DIRT.id());
      }
   }

   private boolean canBecomeWet(World world, int x, int y, int z) {
      if (this.isWaterNearby(world, x, y, z, 1)) {
         return true;
      } else {
         if (this.isWaterNearby(world, x, y, z, 4)) {
            for (int x1 = x - 1; x1 <= x + 1; x1++) {
               for (int y1 = y - 1; y1 <= y + 1; y1++) {
                  for (int z1 = z - 1; z1 <= z + 1; z1++) {
                     int bID = world.getBlockId(x1, y1, z1);
                     if (bID == Blocks.MUD.id() || bID == Blocks.FARMLAND_DIRT.id() && isWet(world.getBlockMetadata(x1, y1, z1))) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (rand.nextInt(2) != 0) {
         world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay() - rand.nextInt(10) + rand.nextInt(10));
      } else {
         int data = world.getBlockMetadata(x, y, z);
         int hydration = getWet(data);
         if (!this.isWaterNearby(world, x, y, z, 4) && !world.canBlockBeRainedOn(x, y + 1, z)) {
            if (hydration > 0) {
               world.setBlockMetadataWithNotify(x, y, z, setWet(data, hydration - 1));
               world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay() - rand.nextInt(10) + rand.nextInt(10));
            } else if (!this.isCropsNearby(world, x, y, z)) {
               world.setBlockWithNotify(x, y, z, Blocks.DIRT.id());
            }
         } else if (hydration < 15 && this.canBecomeWet(world, x, y, z)) {
            world.setBlockMetadataWithNotify(x, y, z, setWet(data, hydration + 1));
            world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay() - rand.nextInt(10) + rand.nextInt(10));
         }
      }
   }

   @Override
   public boolean onBonemealUsed(
      ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      int data = world.getBlockMetadata(blockX, blockY, blockZ);
      if (!isFertilized(data) && itemstack.consumeItem(player)) {
         world.setBlockMetadataWithNotify(blockX, blockY, blockZ, setFertilized(data, true));
         return true;
      } else {
         return false;
      }
   }
}
