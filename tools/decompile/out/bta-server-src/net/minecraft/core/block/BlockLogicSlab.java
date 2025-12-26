package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.MaterialColor;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlockSlab;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;

public class BlockLogicSlab extends BlockLogic {
   public Block<?> modelBlock;
   public int modelBlockMetadata;

   public BlockLogicSlab(Block<?> block, Block<?> modelBlock) {
      this(block, modelBlock, 0);
      block.setBlockItem(() -> new ItemBlockSlab<>(block));
   }

   public BlockLogicSlab(Block<?> block, Block<?> modelBlock, int modelBlockMetadata) {
      super(block, Material.stone);
      this.modelBlock = modelBlock;
      this.modelBlockMetadata = modelBlockMetadata;
      block.withLightBlock(1);
   }

   @Override
   public void initializeBlock() {
      this.block.withHardness(this.modelBlock.blockHardness);
      this.block.withBlastResistance(this.modelBlock.blastResistance / 3.0F);
      this.block.withLightEmission(this.modelBlock.emission);
   }

   @NotNull
   @Override
   public Material getMaterial() {
      return this.modelBlock.getMaterial();
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int l = world.getBlockMetadata(x, y, z) & 3;
      switch (l) {
         case 0:
            return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
         case 1:
            return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         case 2:
         default:
            return AABB.getTemporaryBB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);
      }
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean renderAsNormalBlockOnCondition(WorldSource world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      return (meta & 3) == 1;
   }

   @Override
   public boolean canPlaceOnSurfaceOnCondition(World world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      return (meta & 3) != 0;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      ItemStack[] result = dropCause != EnumDropCause.IMPROPER_TOOL ? new ItemStack[]{new ItemStack(this.block)} : null;
      if (result != null) {
         for (ItemStack stack : result) {
            stack.setMetadata(meta & 240);
            stack.itemID = this.id();
            if ((meta & 3) == 1 && dropCause != EnumDropCause.PICK_BLOCK) {
               stack.stackSize = 2;
            } else {
               stack.stackSize = 1;
            }
         }
      }

      return result;
   }

   @Override
   public int tickDelay() {
      return this.modelBlock.tickDelay();
   }

   @Override
   public void handleEntityInside(World world, int x, int y, int z, Entity entity, Vec3 entityVelocity) {
      this.modelBlock.handleEntityInside(world, x, y, z, entity, entityVelocity);
   }

   @Override
   public boolean isCollidable() {
      return this.modelBlock.isCollidable();
   }

   @Override
   public boolean canCollideCheck(int meta, boolean shouldCollideWithFluids) {
      return this.modelBlock.canCollideCheck(meta, shouldCollideWithFluids);
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return world.getBlockId(x, y - 1, z) == this.id() || world.getBlockId(x, y + 1, z) == this.id() || super.canPlaceBlockAt(world, x, y, z);
   }

   @Override
   public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
      this.modelBlock.onEntityWalking(world, x, y, z, entity);
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      this.modelBlock.updateTick(world, x, y, z, rand);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      return this.modelBlock.onBlockRightClicked(world, x, y, z, player, side, xPlaced, yPlaced);
   }

   @Override
   public void onBlockDestroyedByExplosion(World world, int x, int y, int z) {
      this.modelBlock.onBlockDestroyedByExplosion(world, x, y, z);
   }

   @Override
   public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
      Direction dir = mob.getVerticalPlacementDirection(side, yPlaced);
      if (dir == Direction.DOWN) {
         world.setBlockMetadataWithNotify(x, y, z, 0);
      }

      if (dir == Direction.UP) {
         world.setBlockMetadataWithNotify(x, y, z, 2);
      }
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      if (side == Side.TOP) {
         world.setBlockMetadataWithNotify(x, y, z, 2);
      } else {
         world.setBlockMetadataWithNotify(x, y, z, 0);
      }
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      this.onNeighborBlockChange(world, x, y, z, 0);
      this.modelBlock.onBlockPlacedByWorld(world, x, y, z);
   }

   @Override
   public float getBlastResistance(Entity entity) {
      return this.modelBlock.getBlastResistance(entity);
   }

   @Override
   public float getBlockBrightness(WorldSource blockAccess, int x, int y, int z) {
      return this.modelBlock.getBlockBrightness(blockAccess, x, y, z);
   }

   @Override
   public void onBlockLeftClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      this.modelBlock.onBlockLeftClicked(world, x, y, z, player, side, xHit, yHit);
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      this.modelBlock.animationTick(world, x, y, z, rand);
   }

   @NotNull
   @Override
   public MaterialColor getMaterialColor() {
      return this.modelBlock.getMaterialColor();
   }
}
