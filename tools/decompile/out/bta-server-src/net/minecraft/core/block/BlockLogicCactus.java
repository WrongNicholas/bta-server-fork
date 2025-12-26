package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.vehicle.EntityBoat;
import net.minecraft.core.entity.vehicle.EntityMinecart;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.IBonemealable;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicCactus extends BlockLogic implements IBonemealable {
   public BlockLogicCactus(Block<?> block) {
      super(block, Material.cactus);
      block.setTicking(true);
      float f = 0.0625F;
      this.setBlockBounds(f, 0.0, f, 1.0F - f, 1.0, 1.0F - f);
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      this.onNeighborBlockChange(world, x, y, z, 0);
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (world.isAirBlock(x, y + 1, z)) {
         int cactusHeight = 1;

         while (world.getBlockId(x, y - cactusHeight, z) == this.block.id()) {
            cactusHeight++;
         }

         if (cactusHeight < 3) {
            int i1 = world.getBlockMetadata(x, y, z);
            if (i1 == 15) {
               world.setBlockWithNotify(x, y + 1, z, this.block.id());
               world.setBlockMetadataWithNotify(x, y, z, 0);
            } else {
               world.setBlockMetadataWithNotify(x, y, z, i1 + 1);
            }
         }
      }
   }

   public void growCactusOnTop(World world, int x, int y, int z) {
      int l = 1;

      while (world.getBlockId(x, y + l, z) == this.block.id()) {
         l++;
      }

      if (world.isAirBlock(x, y + l, z)) {
         world.setBlockWithNotify(x, y + l, z, this.block.id());
      }
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return !super.canPlaceBlockAt(world, x, y, z) ? false : this.canBlockStay(world, x, y, z);
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (!this.canBlockStay(world, x, y, z)) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   @Override
   public boolean canBlockStay(World world, int x, int y, int z) {
      if (world.getBlockMaterial(x - 1, y, z).isSolid()) {
         return false;
      } else if (world.getBlockMaterial(x + 1, y, z).isSolid()) {
         return false;
      } else if (world.getBlockMaterial(x, y, z - 1).isSolid()) {
         return false;
      } else if (world.getBlockMaterial(x, y, z + 1).isSolid()) {
         return false;
      } else {
         int l = world.getBlockId(x, y - 1, z);
         return Blocks.hasTag(l, BlockTags.GROWS_CACTI);
      }
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      if (entity instanceof Mob || entity instanceof EntityMinecart || entity instanceof EntityBoat) {
         entity.hurt(null, 1, DamageType.COMBAT);
      }
   }

   @Override
   public boolean onBonemealUsed(
      ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (!world.isClientSide) {
         this.growCactusOnTop(world, blockX, blockY, blockZ);
         if (player == null || player.getGamemode().consumeBlocks()) {
            itemstack.stackSize--;
         }
      }

      return true;
   }
}
